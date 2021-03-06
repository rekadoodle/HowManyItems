package net.minecraft.src.hmi.references.aether;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.src.*;
import net.minecraft.src.hmi.AetherHandler;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.Utils;

public class ConcreteHandler extends AetherHandler {

	@Override
	public void loadTabs(BaseMod basemod) {
		mod_HowManyItems.addTab(new TabAether(basemod, TileEntityEnchanter.class, new ArrayList<ItemStack>(Arrays.asList(new ItemStack(AetherItems.AmbrosiumShard))), "/aether/gui/enchanter.png", AetherBlocks.Enchanter));
		mod_HowManyItems.addGuiToBlock(GuiEnchanter.class, new ItemStack(AetherBlocks.Enchanter));
		try
        {
			Class.forName("TileEntityFreezer");
			((TabHandler) Utils.getHandler("aether.freezer")).loadTabs(basemod);
        } catch (ClassNotFoundException e) { }
	}

	@Override
	public boolean isInventory(GuiScreen screen) {
		return screen instanceof GuiInventoryMoreSlots;
	}

	@Override
	public GuiScreen newInv(EntityPlayer player) {
		return new GuiInventoryMoreSlots(player);
	}
	
	
}