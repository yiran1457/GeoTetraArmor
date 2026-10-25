package ovo.yiran.geotetraarmor.compat;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//? if with_ehp {
import net.yiran.extraholopage.ExtraHoloPage;
import ovo.yiran.geotetraarmor.compat.ehp.EHPCompat;
//?}

public class CompatHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        //? if with_ehp {
        if (ModList.get().isLoaded(ExtraHoloPage.MODID)) {
            EHPCompat.onClientCompat();
        }
        //?}
    }
}
