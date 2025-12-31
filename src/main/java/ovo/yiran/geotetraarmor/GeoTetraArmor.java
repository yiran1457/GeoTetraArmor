package ovo.yiran.geotetraarmor;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import ovo.yiran.geotetraarmor.compat.CompatHandler;
import ovo.yiran.geotetraarmor.items.ModularChestItem;
import ovo.yiran.geotetraarmor.items.ModularFeetItem;
import ovo.yiran.geotetraarmor.items.ModularHeadItem;
import ovo.yiran.geotetraarmor.items.ModularLegsItem;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.items.InitializableItem;

import java.util.ArrayList;
import java.util.List;

@Mod(GeoTetraArmor.MODID)
@SuppressWarnings("removal")
public class GeoTetraArmor {
    public static final String MODID = "geotetraarmor";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static List<InitializableItem> ITEM = new ArrayList<>();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<Item> HEAD = ITEMS.register("head", ModularHeadItem::new);
    public static final RegistryObject<Item> FEET = ITEMS.register("feet", ModularFeetItem::new);
    public static final RegistryObject<Item> CHEST = ITEMS.register("chest", ModularChestItem::new);
    public static final RegistryObject<Item> LEGS = ITEMS.register("legs", ModularLegsItem::new);

    public GeoTetraArmor() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                Config.SPEC
        );
        ITEMS.register(modEventBus);
        modEventBus.register(CompatHandler.class);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onBuildCreativeModeTab);
    }

    public void onCommonSetup(final FMLCommonSetupEvent event) {
        for (InitializableItem item : ITEM) {
            item.commonInit(TetraMod.packetHandler);
        }
        ITEM.clear();
        ITEM = null;
    }

    public void onBuildCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation("tetra", "default"))) {
            event.accept(HEAD.get().getDefaultInstance());
            event.accept(CHEST.get().getDefaultInstance());
            event.accept(LEGS.get().getDefaultInstance());
            event.accept(FEET.get().getDefaultInstance());
        }
    }
}
