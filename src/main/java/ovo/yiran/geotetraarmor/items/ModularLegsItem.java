package ovo.yiran.geotetraarmor.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.GuiModuleOffsets;

public class ModularLegsItem extends ModularArmorItem {
    public ModularLegsItem() {
        super(EquipmentSlot.LEGS,"modular_legs");
        majorModuleKeys = new String[]{"legs/left", "legs/right","legs/belt"};
        minorModuleKeys = new String[0];
        requiredModules = new String[]{"legs/left", "legs/right","legs/belt"};
    }

    @Override
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        //return new GuiModuleOffsets( -14, 0,4, 0);
        return new GuiModuleOffsets(-21, 13,11, 13,4,-2);
    }

}
