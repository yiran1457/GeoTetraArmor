package ovo.yiran.geotetraarmor.effects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.MAINHAND || event.getSlot() == EquipmentSlot.OFFHAND) return;
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) return;
        boolean canFly = entity.getPersistentData().getBoolean("gta:fly");
        if (ArmorEffectUtil.getArmorTotalEffectLevel(entity, FLY) > 0) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
            if (!canFly) {
                entity.getPersistentData().putBoolean("gta:fly", true);
            }
        } else {
            if (canFly) {
                entity.getPersistentData().putBoolean("gta:fly", false);
                // 不覆盖创造模式或其他 mod 提供的飞行能力
                if (!player.getAbilities().instabuild) {
                    player.getAbilities().mayfly = false;
                }
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }
}
