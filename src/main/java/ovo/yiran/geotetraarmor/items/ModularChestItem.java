package ovo.yiran.geotetraarmor.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.items.modular.IModularItem;

public class ModularChestItem extends ModularArmorItem {
    public ModularChestItem() {
        super(EquipmentSlot.CHEST, "modular_chest");
        majorModuleKeys = new String[]{"chest/right", "chest/base", "chest/left"};
        minorModuleKeys = new String[]{"chest/extra"};
        requiredModules = new String[]{"chest/base", "chest/left", "chest/right"};
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack= new ItemStack(this);
        IModularItem.putModuleInSlot(itemStack, "chest/left", "armor/chest/left/vanilla", "vanilla_chest_left/iron" );
        IModularItem.putModuleInSlot(itemStack, "chest/right", "armor/chest/right/vanilla", "vanilla_chest_right/iron" );
        IModularItem.putModuleInSlot(itemStack, "chest/base", "armor/chest/base/vanilla", "vanilla_chest_base/iron" );
        return itemStack;
    }

    @Override
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets(4, 0, 4, 18, -14, 0);
    }

    @Override
    public GuiModuleOffsets getMinorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets(-17, 21);
    }
}
