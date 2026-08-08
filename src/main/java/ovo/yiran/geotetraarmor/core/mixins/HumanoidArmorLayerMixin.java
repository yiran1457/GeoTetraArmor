package ovo.yiran.geotetraarmor.core.mixins;

import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Shadow
    protected abstract void setPartVisibility(A model, EquipmentSlot slot);

    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Unique
    private void geotetra$renderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Model model, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    private void test$renderArmorPiece(PoseStack poseStack, MultiBufferSource buffer, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, CallbackInfo ci) {
        ItemStack itemstack = livingEntity.getItemBySlot(slot);
        Item $$9 = itemstack.getItem();
        if ($$9 instanceof ModularArmorItem armoritem) {
            if (armoritem.getEquipmentSlot() == slot) {
                var renderers = armoritem.geoArmorRenderers(itemstack, livingEntity);
                if (!renderers.isEmpty()) {
                    this.getParentModel().copyPropertiesTo(p_model);
                    this.setPartVisibility(p_model, slot);
                    renderers.forEach(renderer -> {
                        renderer.prepForRender(livingEntity, itemstack, slot, p_model);
                        this.geotetra$renderModel(poseStack, buffer, packedLight, renderer, renderer.getGeoModel().getTextureResource(armoritem));
                    });
                }
            }
        }
    }
}
