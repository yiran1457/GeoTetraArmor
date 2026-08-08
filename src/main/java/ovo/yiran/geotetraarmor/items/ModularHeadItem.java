package ovo.yiran.geotetraarmor.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import ovo.yiran.geotetraarmor.Config;
import se.mickelus.mutil.network.PacketHandler;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.items.modular.IModularItem;

public class ModularHeadItem extends ModularArmorItem {
    public ModularHeadItem() {
        super(EquipmentSlot.HEAD, "modular_head");
        majorModuleKeys = new String[]{"head/base"};
        minorModuleKeys = new String[]{"head/extra"};
        requiredModules = new String[]{"head/base"};
    }

    public void commonInit(PacketHandler packetHandler) {
        DataManager.instance.synergyData.onReload(() -> this.synergies = DataManager.instance.synergyData.getOrdered("armor/head"));
        this.honeBase = Config.HeadHoneBase.get();
        this.honeIntegrityMultiplier = Config.HeadHoneMultiplier.get();
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        IModularItem.putModuleInSlot(itemStack, "head/base", "armor/head/base/vanilla", "vanilla_head_base/iron");
        return itemStack;
    }

    @Override
    public GuiModuleOffsets getMinorGuiOffsets(ItemStack itemStack) {
        return new GuiModuleOffsets(-16, 2);
    }
}
