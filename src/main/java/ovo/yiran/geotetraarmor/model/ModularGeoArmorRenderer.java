package ovo.yiran.geotetraarmor.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import se.mickelus.tetra.module.data.ModuleModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.Objects;

public class ModularGeoArmorRenderer extends GeoArmorRenderer<ModularArmorItem> {
    public Color color;
    protected GeoBone belt = null;
    public ModularGeoArmorRenderer(ModuleModel moduleModel) {
        super(new ModularArmorModel<>(moduleModel));
        color = new Color((moduleModel.tint & 0x00FFFFFF) | 0xFF000000);
    }

    @Override
    public Color getRenderColor(ModularArmorItem animatable, float partialTick, int packedLight) {
        return color;
    }

    @Nullable
    public GeoBone getBeltBone() {
        return  this.model.getBone("armorBelt").orElse(null);
    }

    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        if (this.lastModel != bakedModel) {
            this.lastModel = bakedModel;
            this.head = this.getHeadBone();
            this.body = this.getBodyBone();
            this.rightArm = this.getRightArmBone();
            this.leftArm = this.getLeftArmBone();
            this.rightLeg = this.getRightLegBone();
            this.leftLeg = this.getLeftLegBone();
            this.rightBoot = this.getRightBootBone();
            this.leftBoot = this.getLeftBootBone();
            this.leftBoot = this.getLeftBootBone();
            this.belt = this.getBeltBone();
        }
    }

    public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
        this.setAllVisible(false);
        currentPart.visible = true;
        GeoBone bone = null;
        if (currentPart != model.hat && currentPart != model.head) {
            if (currentPart == model.body) {
                bone = this.body;
                if(this.belt!=null) {
                    this.belt.setHidden(false);
                }
            } else if (currentPart == model.leftArm) {
                bone = this.leftArm;
            } else if (currentPart == model.rightArm) {
                bone = this.rightArm;
            } else if (currentPart == model.leftLeg) {
                bone = currentSlot == EquipmentSlot.FEET ? this.leftBoot : this.leftLeg;
            } else if (currentPart == model.rightLeg) {
                bone = currentSlot == EquipmentSlot.FEET ? this.rightBoot : this.rightLeg;
            }
        } else {
            bone = this.head;
        }

        if (bone != null) {
            bone.setHidden(false);
        }

    }

    protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
        if (this.head != null) {
            ModelPart headPart = baseModel.head;
            RenderUtils.matchModelPartRot(headPart, this.head);
            this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
        }

        if (this.body != null) {
            ModelPart bodyPart = baseModel.body;
            RenderUtils.matchModelPartRot(bodyPart, this.body);
            this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
            if(this.belt != null) {
                RenderUtils.matchModelPartRot(bodyPart, this.belt);
                this.belt.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
            }
        }

        if (this.rightArm != null) {
            ModelPart rightArmPart = baseModel.rightArm;
            RenderUtils.matchModelPartRot(rightArmPart, this.rightArm);
            this.rightArm.updatePosition(rightArmPart.x + 5.0F, 2.0F - rightArmPart.y, rightArmPart.z);
        }

        if (this.leftArm != null) {
            ModelPart leftArmPart = baseModel.leftArm;
            RenderUtils.matchModelPartRot(leftArmPart, this.leftArm);
            this.leftArm.updatePosition(leftArmPart.x - 5.0F, 2.0F - leftArmPart.y, leftArmPart.z);
        }

        if (this.rightLeg != null) {
            ModelPart rightLegPart = baseModel.rightLeg;
            RenderUtils.matchModelPartRot(rightLegPart, this.rightLeg);
            this.rightLeg.updatePosition(rightLegPart.x + 2.0F, 12.0F - rightLegPart.y, rightLegPart.z);
            if (this.rightBoot != null) {
                RenderUtils.matchModelPartRot(rightLegPart, this.rightBoot);
                this.rightBoot.updatePosition(rightLegPart.x + 2.0F, 12.0F - rightLegPart.y, rightLegPart.z);
            }
        }

        if (this.leftLeg != null) {
            ModelPart leftLegPart = baseModel.leftLeg;
            RenderUtils.matchModelPartRot(leftLegPart, this.leftLeg);
            this.leftLeg.updatePosition(leftLegPart.x - 2.0F, 12.0F - leftLegPart.y, leftLegPart.z);
            if (this.leftBoot != null) {
                RenderUtils.matchModelPartRot(leftLegPart, this.leftBoot);
                this.leftBoot.updatePosition(leftLegPart.x - 2.0F, 12.0F - leftLegPart.y, leftLegPart.z);
            }
        }

    }

    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        super.applyBoneVisibilityBySlot(currentSlot);
        if (Objects.requireNonNull(currentSlot) == EquipmentSlot.LEGS) {
            this.setBoneVisible(this.belt, true);
        }
    }

    protected void setAllBonesVisible(boolean visible) {
        super.setAllBonesVisible(visible);
        this.setBoneVisible(this.belt, visible);
    }
}
