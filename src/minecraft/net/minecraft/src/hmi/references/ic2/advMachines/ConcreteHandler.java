package net.minecraft.src.hmi.references.ic2.advMachines;

import net.minecraft.src.*;
import net.minecraft.src.hmi.TabUtils;
import net.minecraft.src.hmi.references.ic2.AdvMachinesHandler;
import net.minecraft.src.hmi.tabs.TabSmelting;

public class ConcreteHandler extends AdvMachinesHandler {
	
	@Override
	public void init(TabSmelting maceratorTab, TabSmelting compressorTab) {
		maceratorTab.equivalentCraftingStations.add(new ItemStack(mod_IC2_AdvMachine.blockAdvMachine, 1, 0));
		mod_HowManyItems.addGuiToBlock(GuiRotary.class, new ItemStack(mod_IC2_AdvMachine.blockAdvMachine, 1, 0));
		
		compressorTab.equivalentCraftingStations.add(new ItemStack(mod_IC2_AdvMachine.blockAdvMachine, 1, 1));
		mod_HowManyItems.addGuiToBlock(GuiSingularity.class, new ItemStack(mod_IC2_AdvMachine.blockAdvMachine, 1, 1));
	}

}
