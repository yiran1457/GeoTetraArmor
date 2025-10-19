package ovo.yiran.geotetraarmor.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.items.modular.IModularItem;

public class ModularLegsItem extends ModularArmorItem {
    public ModularLegsItem() {
        super(EquipmentSlot.LEGS, "modular_legs");
        majorModuleKeys = new String[]{"legs/left", "legs/right", "legs/belt"};
        minorModuleKeys = new String[]{"legs/extra"};
        requiredModules = new String[]{"legs/left", "legs/right", "legs/belt"};
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        IModularItem.putModuleInSlot(itemStack, "legs/belt", "armor/legs/belt/vanilla", "vanilla_legs_belt/iron");
        IModularItem.putModuleInSlot(itemStack, "legs/left", "armor/legs/left/vanilla", "vanilla_legs_left/iron");
        IModularItem.putModuleInSlot(itemStack, "legs/right", "armor/legs/right/vanilla", "vanilla_legs_right/iron");
        return itemStack;
    }

    @Override
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets(-21, 13, 11, 13, 4, -2);
    }

    @Override
    public GuiModuleOffsets getMinorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets(-14,0);
    }
}
