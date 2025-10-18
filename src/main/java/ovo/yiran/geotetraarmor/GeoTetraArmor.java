package ovo.yiran.geotetraarmor;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import ovo.yiran.geotetraarmor.items.*;
import se.mickelus.tetra.items.modular.IModularItem;

import java.util.HashMap;
import java.util.Map;

@Mod(GeoTetraArmor.MODID)
@SuppressWarnings("removal")
public class GeoTetraArmor {
    public static final String MODID = "geotetraarmor";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<Item> HEAD = ITEMS.register("head", ModularHeadItem::new);
    public static final RegistryObject<Item> FEET = ITEMS.register("feet", ModularFeetItem::new);
    public static final RegistryObject<Item> CHEST = ITEMS.register("chest", ModularChestItem::new);
    public static final RegistryObject<Item> LEGS = ITEMS.register("legs", ModularLegsItem::new);/*
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> HEAD.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(HEAD.get());
                output.accept(CHEST.get());
                output.accept(LEGS.get());
                output.accept(FEET.get());
            }).build());*/

    public GeoTetraArmor() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
       // CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(this::onBuildCreativeModeTab);
    }

    public void onBuildCreativeModeTab(BuildCreativeModeTabContentsEvent event){

        if (event.getTabKey() == ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation("tetra", "default"))) {
            event.accept(HEAD.get().getDefaultInstance());
            event.accept(CHEST.get().getDefaultInstance());
            event.accept(LEGS.get().getDefaultInstance());
            event.accept(FEET.get().getDefaultInstance());
        }
    }
}
