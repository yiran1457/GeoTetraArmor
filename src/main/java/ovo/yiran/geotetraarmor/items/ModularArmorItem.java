package ovo.yiran.geotetraarmor.items;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.logging.LogUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import ovo.yiran.geotetraarmor.GeoTetraArmor;
import ovo.yiran.geotetraarmor.model.ModularGeoArmorRenderer;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.data.ModuleModel;
import se.mickelus.tetra.module.schematic.RepairSchematic;
import se.mickelus.tetra.properties.AttributeHelper;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ModularArmorItem extends ItemModularHandheld implements GeoItem, Equipable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Predicate<ModuleModel> IS_GECKO_MODEL = moduleModel -> moduleModel.type.startsWith("gecko");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final EquipmentSlot slot;
    private final Cache<String, ImmutableList<ModuleModel>> modelCache;
    private final Cache<String, List<ModularGeoArmorRenderer>> rendererCache;

    public ModularArmorItem(EquipmentSlot slot, String repairName) {
        super(new Properties().stacksTo(1));
        GeoTetraArmor.ITEM.add(this);
        this.slot = slot;
        this.modelCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite(5L, TimeUnit.MINUTES).build();
        this.rendererCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite(5L, TimeUnit.MINUTES).build();
        SchematicRegistry.instance.registerSchematic(new RepairSchematic(this, repairName));
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

    public void applyDamage(int amount, ItemStack itemStack, @Nullable LivingEntity responsibleEntity, int slot) {
        int damage = itemStack.getDamageValue();
        int maxDamage = itemStack.getMaxDamage();
        if (!this.isBroken(damage, maxDamage)) {
            int reducedAmount = this.getReducedDamage(amount, itemStack, responsibleEntity);
            itemStack.hurtAndBreak(reducedAmount, responsibleEntity, (player) -> player.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, slot)));
            //打磨
            tickProgression(responsibleEntity, itemStack, reducedAmount);
            if (this.isBroken(damage + reducedAmount, maxDamage) && responsibleEntity != null && !responsibleEntity.level().isClientSide) {
                responsibleEntity.playSound(SoundEvents.SHIELD_BREAK, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void clearCaches() {
        rendererCache.invalidateAll();
        modelCache.invalidateAll();
        super.clearCaches();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack itemStack) {
        if (slot == this.slot && !isBroken(itemStack)) {
            return getAttributeModifiersCached(itemStack);
        }
        return AttributeHelper.emptyMap;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiersCached(ItemStack itemStack) {
        try {
            return this.getAttributeModifierCache().get(this.getDataCacheKey(itemStack), () ->
                    fixSlotIdentifiers(slot,
                            Optional.ofNullable(this.getAttributeModifiersCollapsed(itemStack))
                                    .orElseGet(ImmutableMultimap::of))
            );
        } catch (ExecutionException e) {
            LOGGER.error("Failed to get cached attribute modifiers for {}", itemStack, e);
            return this.getAttributeModifiersCollapsed(itemStack);
        }
    }

    private Multimap<Attribute, AttributeModifier> fixSlotIdentifiers(EquipmentSlot slot, Multimap<Attribute, AttributeModifier> modifiers) {
        return modifiers.entries()
                .stream()
                .collect(Multimaps.toMultimap(
                        Map.Entry::getKey,
                        (entry) -> {
                            String key = entry.getValue().getName() + slot.getName();
                            return new AttributeModifier(
                                    UUID.nameUUIDFromBytes(key.getBytes()),
                                    key,
                                    entry.getValue().getAmount(),
                                    entry.getValue().getOperation()
                            );
                        },
                        ArrayListMultimap::create));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> fixIdentifiers(Multimap<Attribute, AttributeModifier> modifiers) {
        return modifiers;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "Fly/Idle", 0, (state) -> {
            for (Object value : state.getExtraData().values()) {
                if (value instanceof Player player) {
                    if (player.getAbilities().flying) {
                        return state.setAndContinue(DefaultAnimations.FLY);
                    }
                }
            }
            return state.setAndContinue(DefaultAnimations.IDLE);
        }));
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public ImmutableList<ModuleModel> getModels(ItemStack itemStack, @Nullable LivingEntity entity) {
        return filterModels(getModelsCached(itemStack, entity), IS_GECKO_MODEL.negate());
    }

    public ImmutableList<ModuleModel> getGeckoModels(ItemStack itemStack, @Nullable LivingEntity entity) {
        return filterModels(getModelsCached(itemStack, entity), IS_GECKO_MODEL);
    }

    private ImmutableList<ModuleModel> getModelsCached(ItemStack itemStack, @Nullable LivingEntity entity) {
        try {
            return modelCache.get(this.getModelCacheKey(itemStack, entity), () -> computeModels(itemStack));
        } catch (ExecutionException e) {
            LOGGER.error("Failed to get cached models for {}", itemStack, e);
            return computeModels(itemStack);
        }
    }

    private ImmutableList<ModuleModel> computeModels(ItemStack itemStack) {
        return this.getAllModules(itemStack)
                .stream()
                .sorted(Comparator.comparing(ItemModule::getRenderLayer))
                .flatMap((itemModule) -> Arrays.stream(itemModule.getModels(itemStack)))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ModuleModel::getRenderLayer))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    private static ImmutableList<ModuleModel> filterModels(ImmutableList<ModuleModel> models, Predicate<ModuleModel> filter) {
        return models.stream()
                .filter(filter)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public List<ModularGeoArmorRenderer> geoArmorRenderers(ItemStack itemStack, @Nullable LivingEntity entity) {
        try {
            return rendererCache.get(this.getModelCacheKey(itemStack, entity), () -> this.getGeckoModels(itemStack, entity).stream().map(ModularGeoArmorRenderer::new).collect(Collectors.toList()));
        } catch (ExecutionException e) {
            LOGGER.error("Failed to create geo armor renderers for {}", itemStack, e);
            return ImmutableList.of();
        }
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return slot;
    }
}
