package ovo.yiran.geotetraarmor.effects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ovo.yiran.geotetraarmor.ArmorEffectUtil;
import ovo.yiran.geotetraarmor.GeoTetraArmor;
import se.mickelus.tetra.effect.ItemEffect;

@Mod.EventBusSubscriber(modid = GeoTetraArmor.MODID)
public class FlyEffect {
    public static final ItemEffect FLY = ItemEffect.get("gta:fly");

    @SubscribeEvent
    public static void onEquipmentChang(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.MAINHAND || event.getSlot() == EquipmentSlot.OFFHAND) return;
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        boolean canFly = event.getEntity().getPersistentData().getBoolean("gta:fly");
        if (ArmorEffectUtil.getArmorTotalEffectLevel(event.getEntity(), FLY) > 0) {
            ((ServerPlayer) event.getEntity()).getAbilities().mayfly = true;
            ((ServerPlayer) event.getEntity()).onUpdateAbilities();
            if (!canFly) {
                event.getEntity().getPersistentData().putBoolean("gta:fly", true);
            }
        }else {
            if (canFly) {
                event.getEntity().getPersistentData().putBoolean("gta:fly", false);
                ((ServerPlayer) event.getEntity()).getAbilities().mayfly = false;
                ((ServerPlayer) event.getEntity()).getAbilities().flying = false;
                ((ServerPlayer) event.getEntity()).onUpdateAbilities();
            }
        }
    }
}
