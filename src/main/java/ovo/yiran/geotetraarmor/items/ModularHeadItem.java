package ovo.yiran.geotetraarmor.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.items.modular.IModularItem;

public class ModularHeadItem extends ModularArmorItem {
    public ModularHeadItem() {
        super(EquipmentSlot.HEAD, "modular_head");
        majorModuleKeys = new String[]{"head/base"};
        minorModuleKeys = new String[]{"head/extra"};
        requiredModules = new String[]{"head/base"};
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        IModularItem.putModuleInSlot(itemStack, "head/base", "armor/head/base/vanilla", "vanilla_head_base/iron");
        return itemStack;
    }

    @Override
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        return super.getMajorGuiOffsets(itemStack);
    }

    @Override
    public GuiModuleOffsets getMinorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets(-16, 2);
    }
}
