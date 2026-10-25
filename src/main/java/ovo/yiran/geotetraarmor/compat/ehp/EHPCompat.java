//? if with_ehp {
package ovo.yiran.geotetraarmor.compat.ehp;

import net.minecraft.resources.ResourceLocation;
import net.yiran.extraholopage.api.ExtraHoloRegister;
import ovo.yiran.geotetraarmor.GeoTetraArmor;

public class EHPCompat {
    public static void onClientCompat() {

        ExtraHoloRegister.register(GeoTetraArmor.HEAD.get())
                .setTexture(new ResourceLocation(GeoTetraArmor.MODID, "textures/gui/gta.png"), 42, 42, 84, 0);
        ExtraHoloRegister.register(GeoTetraArmor.CHEST.get())
                .setTexture(new ResourceLocation(GeoTetraArmor.MODID, "textures/gui/gta.png"), 42, 42, 42, 0);
        ExtraHoloRegister.register(GeoTetraArmor.LEGS.get())
                .setTexture(new ResourceLocation(GeoTetraArmor.MODID, "textures/gui/gta.png"), 42, 42, 126, 0);
        ExtraHoloRegister.register(GeoTetraArmor.FEET.get())
                .setTexture(new ResourceLocation(GeoTetraArmor.MODID, "textures/gui/gta.png"), 42, 42, 0, 0);
    }
}
//?} else {
/*package ovo.yiran.geotetraarmor.compat.ehp;

/^*
 * ExtraHoloPage has no build for Tetra 6.10+ (its client mixins target the 6.9 GUI classes), so this compat is
 * compiled only for the legacy node and stays empty everywhere else.
 ^/
public class EHPCompat {
}
*///?}
