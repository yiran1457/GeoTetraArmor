package ovo.yiran.geotetraarmor.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.GuiModuleOffsets;

public class ModularFeetItem extends ModularArmorItem {
    public ModularFeetItem() {
        super(EquipmentSlot.FEET, "modular_feet");
        majorModuleKeys = new String[]{"feet/left", "feet/right"};
        minorModuleKeys = new String[0];
        requiredModules = new String[]{"feet/left", "feet/right"};
    }

    @Override
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets( -14, 18,4, 18);
    }

}
