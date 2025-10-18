package ovo.yiran.geotetraarmor.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.items.modular.IModularItem;

public class ModularFeetItem extends ModularArmorItem {
    public ModularFeetItem() {
        super(EquipmentSlot.FEET, "modular_feet");
        majorModuleKeys = new String[]{"feet/left", "feet/right"};
        minorModuleKeys = new String[0];
        requiredModules = new String[]{"feet/left", "feet/right"};
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        IModularItem.putModuleInSlot(itemStack, "feet/left", "armor/feet/left/vanilla", "vanilla_feet_left/iron");
        IModularItem.putModuleInSlot(itemStack, "feet/right", "armor/feet/right/vanilla", "vanilla_feet_right/iron");
        return itemStack;
    }

    @Override
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets(-14, 18, 4, 18);
    }

}
