package ovo.yiran.geotetraarmor.model;

import net.minecraft.resources.ResourceLocation;
import se.mickelus.tetra.module.Priority;
//? if <6.10 {
import se.mickelus.tetra.module.data.ModuleModel;
//?} else {
/*import se.mickelus.tetra.module.model.AbstractTextureModelData;
import se.mickelus.tetra.module.model.IModuleModel;
*///?}

/**
 * Bridges the module model type change between Tetra 6.9 (data.ModuleModel) and 6.10+ (model.IModuleModel).
 * All Tetra model handling in this mod must go through this class; it is the only place that knows the difference.
 */
public final class ModuleModelAccess {
    //? if <6.10 {
    public static final String GECKO_TYPE = "gecko";
    //?} else {
    /*public static final ResourceLocation GECKO_TYPE = new ResourceLocation("geotetraarmor", "gecko");
    *///?}

    private ModuleModelAccess() {
    }

    //? if <6.10 {
    public static boolean isGecko(Object model) {
        return model instanceof ModuleModel moduleModel && GECKO_TYPE.equals(moduleModel.type);
    }

    public static ResourceLocation location(Object model) {
        return ((ModuleModel) model).location;
    }

    public static int tint(Object model) {
        return ((ModuleModel) model).tint;
    }

    public static Priority renderLayer(Object model) {
        return ((ModuleModel) model).getRenderLayer();
    }
    //?} else {
    /*public static boolean isGecko(Object model) {
        return model instanceof IModuleModel moduleModel && GECKO_TYPE.equals(moduleModel.getType());
    }

    public static ResourceLocation location(Object model) {
        return ((AbstractTextureModelData) model).getLocation();
    }

    public static int tint(Object model) {
        return ((AbstractTextureModelData) model).getTint().getRaw();
    }

    public static Priority renderLayer(Object model) {
        return ((IModuleModel) model).getRenderLayer();
    }
    *///?}
}
