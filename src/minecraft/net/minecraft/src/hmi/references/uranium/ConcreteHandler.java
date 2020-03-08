package net.minecraft.src.hmi.references.uranium;

import java.util.ArrayList;

import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.tabs.Tab;
import net.minecraft.src.hmi.tabs.TabSmelting;

public class ConcreteHandler extends TabHandler {

	@Override
	public void loadTabs(BaseMod basemod) {
		ArrayList<ItemStack> fuels = new ArrayList<ItemStack>();
		fuels.add(new ItemStack (mod_Uranium.uraniumDust));
		fuels.add(new ItemStack (mod_Uranium.uraniumCoal));
		fuels.add(new ItemStack (mod_Uranium.skullUranium));
		for(Block block: Block.blocksList) {
			if(block != null && ModLoader.AddAllFuel(block.blockID) > 0) 
				fuels.add(new ItemStack(block));
		}
		for(Item item: Item.itemsList) {
			if(item != null && ModLoader.AddAllFuel(item.shiftedIndex) > 0) fuels.add(new ItemStack(item));
		}
		Tab reactorTab = new TabSmelting(basemod, ReactorRecipes.smelting().getSmeltingList(), fuels, "/uraniumTextures/reactorgui.png", mod_Uranium.reactorIdle);
		reactorTab.equivalentCraftingStations.add(new ItemStack(mod_Uranium.reactorActive));
		mod_HowManyItems.addTab(reactorTab);
		mod_HowManyItems.addGuiToBlock(GuiUraniumMod.class, new ItemStack(mod_Uranium.reactorIdle));
	}
}