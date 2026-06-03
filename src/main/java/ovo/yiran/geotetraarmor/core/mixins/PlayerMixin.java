package ovo.yiran.geotetraarmor.core.mixins;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import se.mickelus.tetra.event.ModularItemDamageEvent;

@Mixin(Player.class)
public class PlayerMixin {
    @Shadow
    @Final
    private Inventory inventory;

    @Inject(method = "hurtArmor", at = @At("HEAD"))
    public void onHurtArmor(DamageSource source, float amount, CallbackInfo ci) {
        if (!(amount <= 0.0F)) {
            amount /= 4.0F;
            if (amount < 1.0F) {
                amount = 1.0F;
            }

            for (int i = 0; i < 4; i++) {
                ItemStack itemstack = this.inventory.armor.get(i);
                if ((!source.is(DamageTypeTags.IS_FIRE) || !itemstack.getItem().isFireResistant()) && itemstack.getItem() instanceof ModularArmorItem armorItem) {
                    ModularItemDamageEvent event = new ModularItemDamageEvent((LivingEntity) (Object)this,itemstack,(int)amount);
                    MinecraftForge.EVENT_BUS.post(event);
                    armorItem.applyDamage(event.getAmount(),itemstack,this.inventory.player,i);
                }
            }
        }
    }
}
