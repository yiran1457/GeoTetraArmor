package ovo.yiran.geotetraarmor.compat;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.yiran.extraholopage.ExtraHoloPage;
import ovo.yiran.geotetraarmor.compat.ehp.EHPCompat;

public class CompatHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if(ModList.get().isLoaded(ExtraHoloPage.MODID)){
            EHPCompat.onClientCompat();
        }
    }
}
