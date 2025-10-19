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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Shadow
    protected abstract void setPartVisibility(A model, EquipmentSlot slot);

    @Shadow
    protected abstract boolean usesInnerModel(EquipmentSlot slot);

    @Shadow
    protected abstract void renderGlint(PoseStack poseStack, MultiBufferSource buffer, int packedLight, A model);

    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }


    private void renderModel(PoseStack p_289664_, MultiBufferSource p_289689_, int p_289681_, ModularArmorItem p_289650_, Model p_289658_, boolean p_289668_, float p_289678_, float p_289674_, float p_289693_, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = p_289689_.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        p_289658_.renderToBuffer(p_289664_, vertexconsumer, p_289681_, OverlayTexture.NO_OVERLAY, p_289678_, p_289674_, p_289693_, 1.0F);
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    private void test$renderArmorPiece(PoseStack poseStack, MultiBufferSource buffer, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, CallbackInfo ci) {
        ItemStack itemstack = livingEntity.getItemBySlot(slot);
        Item $$9 = itemstack.getItem();
        if ($$9 instanceof ModularArmorItem armoritem) {
            if (armoritem.getEquipmentSlot() == slot) {
                var z = armoritem.geoArmorRenderers(itemstack, livingEntity);
                        z.forEach(renderer -> {
                    this.getParentModel().copyPropertiesTo(p_model);
                    this.setPartVisibility(p_model, slot);

                    renderer.prepForRender(livingEntity, itemstack, slot, p_model);
                    boolean flag = this.usesInnerModel(slot);
                    this.renderModel(poseStack, buffer, packedLight, armoritem, renderer, flag, 1, 1, 1,renderer.getGeoModel().getTextureResource(armoritem) );

/*
                    if (itemstack.hasFoil()) {
                        this.renderGlint(poseStack, buffer, packedLight, (A) renderer);
                    }*/
                });
                /*
                armoritem.getGecModels(itemstack, livingEntity).forEach(moduleModel -> {
                            this.getParentModel().copyPropertiesTo(p_model);
                            this.setPartVisibility(p_model, slot);
                            var geoModel =  //new ModularArmorModel<>(moduleModel);
                                    new DefaultedItemGeoModel<ModularArmorItem>(
                                    new ResourceLocation(moduleModel.getTexture().getNamespace(), moduleModel.getTexture().getPath().substring(5))
                            );
                            GeoArmorRenderer<ModularArmorItem> renderer = new GeoArmorRenderer<ModularArmorItem>(
                                    geoModel//  new ModularArmorModel<>(moduleModel)
                            ) {
                                @Override
                                public software.bernie.geckolib.core.object.Color getRenderColor(ModularArmorItem animatable, float partialTick, int packedLight) {
                                    return new Color((moduleModel.getTint() & 0x00FFFFFF) | 0xFF000000);
                                }
                            };

                            renderer.prepForRender(livingEntity, itemstack, slot, p_model);
                            boolean flag = this.usesInnerModel(slot);
                            this.renderModel(poseStack, buffer, packedLight, armoritem, renderer, flag, 1, 1, 1,geoModel.getTextureResource(armoritem) );


                            if (itemstack.hasFoil()) {
                                this.renderGlint(poseStack, buffer, packedLight, (A) renderer);
                            }
                        }
                );*/
            }
        }
    }
}
