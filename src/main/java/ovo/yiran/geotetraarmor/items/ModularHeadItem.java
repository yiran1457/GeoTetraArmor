package ovo.yiran.geotetraarmor.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.GuiModuleOffsets;

public class ModularHeadItem extends ModularArmorItem {
    public ModularHeadItem() {
        super(EquipmentSlot.HEAD,"modular_head");
        majorModuleKeys = new String[]{"head/base"};
        minorModuleKeys = new String[]{"head/extra"};
        requiredModules = new String[]{"head/base"};
    }

    @Override
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        return super.getMajorGuiOffsets(itemStack);
    }

    @Override
    public GuiModuleOffsets getMinorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets(-16,2) ;
    }
}
