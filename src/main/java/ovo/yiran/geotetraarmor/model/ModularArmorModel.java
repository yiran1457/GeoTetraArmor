package ovo.yiran.geotetraarmor.model;

import net.minecraft.resources.ResourceLocation;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import software.bernie.geckolib.model.GeoModel;

public class ModularArmorModel<T extends ModularArmorItem> extends GeoModel<T> {
    private final ResourceLocation modelPath;
    private final ResourceLocation texturePath;
    private final ResourceLocation animationsPath;

    public ModularArmorModel(ResourceLocation location) {
        modelPath = buildFormattedModelPath(location);
        texturePath = buildFormattedTexturePath(location);
        animationsPath = buildFormattedAnimationPath(location);
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return modelPath;
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return texturePath;
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return animationsPath;
    }

    public static ResourceLocation buildFormattedModelPath(ResourceLocation basePath) {
        return new ResourceLocation(basePath.getNamespace(), "geo/" + basePath.getPath() + ".geo.json");
    }

    public static ResourceLocation buildFormattedAnimationPath(ResourceLocation basePath) {
        return new ResourceLocation(basePath.getNamespace(), "animations/" + basePath.getPath() + ".animation.json");
    }

    public static ResourceLocation buildFormattedTexturePath(ResourceLocation basePath) {
        return new ResourceLocation(basePath.getNamespace(), "textures/" + basePath.getPath() + ".png");
    }
}
