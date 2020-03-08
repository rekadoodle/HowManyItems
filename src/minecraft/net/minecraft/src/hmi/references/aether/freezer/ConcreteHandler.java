package net.minecraft.src.hmi.references.aether.freezer;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.references.aether.TabAether;

public class ConcreteHandler extends TabHandler {

	@Override
	public void loadTabs(BaseMod basemod) {
		mod_HowManyItems.addTab(new TabAether(basemod, TileEntityFreezer.class, new ArrayList<ItemStack>(Arrays.asList(new ItemStack(AetherBlocks.Icestone))), "/aether/gui/enchanter.png", AetherBlocks.Freezer));
		mod_HowManyItems.addGuiToBlock(GuiFreezer.class, new ItemStack(AetherBlocks.Freezer));
	}
}