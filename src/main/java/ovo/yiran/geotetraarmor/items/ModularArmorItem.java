package ovo.yiran.geotetraarmor.items;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
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
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ModularArmorItem extends ItemModularHandheld implements GeoItem, Equipable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public EquipmentSlot slot;
    public Cache<String, List<ModularGeoArmorRenderer>> rendererCache;

    public ModularArmorItem(EquipmentSlot slot, String repairName) {
        super(new Properties().stacksTo(1));
        this.slot = slot;
        this.rendererCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite(5L, TimeUnit.MINUTES).build();
        SchematicRegistry.instance.registerSchematic(new RepairSchematic(this, repairName));
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

    public void applyDamage(int amount, ItemStack itemStack, @Nullable LivingEntity responsibleEntity,int slot) {
        int damage = itemStack.getDamageValue();
        int maxDamage = itemStack.getMaxDamage();
        if (!this.isBroken(damage, maxDamage)) {
            int reducedAmount = this.getReducedDamage(amount, itemStack, responsibleEntity);
            itemStack.hurtAndBreak(amount, responsibleEntity, (player) -> player.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR,slot)));
            //打磨
            tickProgression(responsibleEntity,itemStack,reducedAmount);
            if (this.isBroken(damage + reducedAmount, maxDamage) && !responsibleEntity.level().isClientSide) {
                itemStack.hurtAndBreak(amount, responsibleEntity, (player) -> player.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR,slot)));
                //不知道为什么，没声音
                responsibleEntity.playSound(SoundEvents.SHIELD_BREAK, 1.0F, 1.0F);
            }
        }

    }

    @Override
    public void clearCaches() {
        rendererCache.invalidateAll();
        super.clearCaches();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack itemStack) {
        if (slot == this.slot) {
            return getAttributeModifiersCached(itemStack);
        }
        return AttributeHelper.emptyMap;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiersCached(ItemStack itemStack) {
        try {
            return this.getAttributeModifierCache().get(this.getDataCacheKey(itemStack), () ->
                    Equip$fixIdentifiers(slot,
                            Optional.ofNullable(this.getAttributeModifiersCollapsed(itemStack))
                                    .orElseGet(ImmutableMultimap::of))
            );
        } catch (ExecutionException e) {
            e.printStackTrace();
            return this.getAttributeModifiersCollapsed(itemStack);
        }
    }

    public Multimap<Attribute, AttributeModifier> Equip$fixIdentifiers(EquipmentSlot slot, Multimap<Attribute, AttributeModifier> modifiers) {
        return modifiers.entries()
                .stream()
                .collect(Multimaps.toMultimap(
                        Map.Entry::getKey,
                        (entry) -> {
                            var key = entry.getValue().getName() + slot.getName();
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
            for (Object object : state.getExtraData().keySet()) {
                if (object instanceof DataTicket<?> dataTicket) {
                    if (dataTicket.id().equals("entity")) {
                        if (state.getData(dataTicket) instanceof Player player) {
                            if (player.getAbilities().flying) {
                                return state.setAndContinue(DefaultAnimations.FLY);
                            }
                        }
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
        return this.getAllModules(itemStack)
                .stream()
                .sorted(Comparator.comparing(ItemModule::getRenderLayer))
                .flatMap((itemModule) -> Arrays.stream(itemModule.getModels(itemStack)))
                .filter(Objects::nonNull)
                .filter(ModularArmorItem::modelNotGecko)
                .sorted(Comparator.comparing(ModuleModel::getRenderLayer))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));

    }

    public static boolean modelIsGecko(ModuleModel moduleModel) {
        return moduleModel.type.startsWith("gecko");
    }

    public static boolean modelNotGecko(ModuleModel moduleModel) {
        return !moduleModel.type.startsWith("gecko");
    }

    public ImmutableList<ModuleModel> getGecModels(ItemStack itemStack, @Nullable LivingEntity entity) {
        return this.getAllModules(itemStack)
                .stream()
                .sorted(Comparator.comparing(ItemModule::getRenderLayer))
                .flatMap((itemModule) -> Arrays.stream(itemModule.getModels(itemStack)))
                .filter(Objects::nonNull)
                .filter(ModularArmorItem::modelIsGecko)
                .sorted(Comparator.comparing(ModuleModel::getRenderLayer))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public List<ModularGeoArmorRenderer> geoArmorRenderers(ItemStack itemStack, @Nullable LivingEntity entity) {
        try {
            return rendererCache.get(this.getModelCacheKey(itemStack, entity), () -> this.getGecModels(itemStack, entity).stream().map(ModularGeoArmorRenderer::new).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return ImmutableList.of();
        }
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return slot;
    }
}
