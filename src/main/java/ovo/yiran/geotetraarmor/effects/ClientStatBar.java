package ovo.yiran.geotetraarmor.effects;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ovo.yiran.geotetraarmor.GeoTetraArmor;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterNone;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

@Mod.EventBusSubscriber(modid = GeoTetraArmor.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientStatBar {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        addStatBar(
                new GuiStatBar(
                        0, 0, 59,
                        "tetra.stats." + FlyEffect.FLY.getKey(),
                        0, 1, false, false, false, new StatGetterEffectLevel(FlyEffect.FLY),
                        LabelGetterBasic.integerLabel, new TooltipGetterNone("tetra.stats." + FlyEffect.FLY.getKey() + ".tooltip")
                )
        );
    }

    public static void addStatBar(GuiStatBar bar) {
        WorkbenchStatsGui.addBar(bar);
        HoloStatsGui.addBar(bar);
    }
}
