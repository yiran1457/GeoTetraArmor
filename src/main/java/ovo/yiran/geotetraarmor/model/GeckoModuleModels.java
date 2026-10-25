package ovo.yiran.geotetraarmor.model;

//? if >=6.10 {
/*import se.mickelus.tetra.module.model.GridTextureModelData;
import se.mickelus.tetra.module.model.ModuleModelRegistry;
*///?}

/**
 * Registers the "gecko" module model type for Tetra versions that resolve model types by name.
 * <p>
 * Tetra 6.9 accepts unknown model types and carries the string along, so nothing is needed there. Tetra 6.10+ resolves
 * model types through {@code ModuleModelRegistry} and rejects unknown ones, so "gecko" has to be registered before any
 * module data is deserialized. The registered model is a Tetra {@code GridTextureModelData} whose type is
 * {@code geotetraarmor:gecko}: that keeps Tetra's own material kneading (location suffix + tint) intact while still
 * letting this mod recognise its own models.
 */
public final class GeckoModuleModels {
    private GeckoModuleModels() {
    }

    public static void register() {
        //? if >=6.10 {
        /*ModuleModelRegistry.register("gecko", GeckoModuleModels::deserialize);
        ModuleModelRegistry.register(ModuleModelAccess.GECKO_TYPE.toString(), GeckoModuleModels::deserialize);
        *///?}
    }

    //? if >=6.10 {
    /*private static GridTextureModelData deserialize(JsonElement element) {
        JsonObject json = element.getAsJsonObject().deepCopy();
        json.addProperty("type", ModuleModelAccess.GECKO_TYPE.toString());
        return DataManager.gson.fromJson(json, GridTextureModelData.class);
    }
    *///?}
}
