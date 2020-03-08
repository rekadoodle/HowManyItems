package net.minecraft.src.hmi.references.ee1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.Utils;
import net.minecraft.src.hmi.tabs.Tab;
import net.minecraft.src.hmi.tabs.TabGenericBlock;

public class ConcreteHandler extends TabHandler {

	@Override
	public void loadTabs(BaseMod basemod) {
		mod_HowManyItems.addEquivalentFurnace(new ItemStack(mod_EE.darkMatterFurnaceOn));
		mod_HowManyItems.addEquivalentFurnace(new ItemStack(mod_EE.darkMatterFurnaceOff));
		mod_HowManyItems.addGuiToBlock(GuiDarkMatterFurnace.class, new ItemStack(Block.stoneOvenIdle));
		
		HashMap glowAggRecipes = new HashMap();
		glowAggRecipes.put(new ItemStack(Item.redstone), new ItemStack(Item.lightStoneDust));
		glowAggRecipes.put(new ItemStack(Item.lightStoneDust), new ItemStack(Block.glowStone));
		glowAggRecipes.put(new ItemStack(Block.dirt), new ItemStack(Block.glowStone));
		glowAggRecipes.put(new ItemStack(Block.cobblestone), new ItemStack(Block.glowStone));
		glowAggRecipes.put(new ItemStack(Block.stone), new ItemStack(Block.glowStone));
		glowAggRecipes.put(new ItemStack(Block.netherrack), new ItemStack(Block.glowStone));
		glowAggRecipes.put(new ItemStack(Block.slowSand), new ItemStack(Block.glowStone));
		mod_HowManyItems.addTab(new TabGenericBlock(basemod, glowAggRecipes, mod_EE.glowStoneAggregator));
		mod_HowManyItems.addGuiToBlock(GuiAggregator.class, new ItemStack(mod_EE.glowStoneAggregator));
		
		HashMap obsAggRecipes = new HashMap();
		obsAggRecipes.put(new ItemStack[] {new ItemStack(Item.bucketLava), new ItemStack(Item.bucketWater)},  new ItemStack(Block.obsidian));
		obsAggRecipes.put(new ItemStack[] {new ItemStack(Item.redstone), new ItemStack(Item.coal), new ItemStack(Item.bucketEmpty), new ItemStack(Block.ice)},  new ItemStack(Block.obsidian));
		obsAggRecipes.put(new ItemStack[] {new ItemStack(Item.redstone), new ItemStack(mod_EE.volcanite), new ItemStack(mod_EE.evertide)},  new ItemStack(Block.obsidian));
		Tab obsAggTab = new TabGenericBlock(basemod, obsAggRecipes, 4, 1, mod_EE.obsAggregatorOff, 0);
		obsAggTab.equivalentCraftingStations.add(new ItemStack(mod_EE.obsAggregatorOn));
		mod_HowManyItems.addTab(obsAggTab);
		mod_HowManyItems.addGuiToBlock(GuiObsAgg.class, new ItemStack(mod_EE.obsAggregatorOff));
		
		HashMap locusRecipes = new HashMap();
		locusRecipes.put(new ItemStack[] {new ItemStack(Item.diamond), new ItemStack(mod_EE.mobiusFuel)}, new ItemStack(Item.diamond, 2));
		locusRecipes.put(new ItemStack[] {new ItemStack(Block.blockDiamond), new ItemStack(mod_EE.mobiusFuel)}, new ItemStack(Block.blockDiamond, 2));
		locusRecipes.put(new ItemStack[] {new ItemStack(mod_EE.darkMatter), new ItemStack(mod_EE.mobiusFuel)}, new ItemStack(mod_EE.darkMatter, 2));
		locusRecipes.put(new ItemStack[] {new ItemStack(mod_EE.darkMatterBlock), new ItemStack(mod_EE.mobiusFuel)}, new ItemStack(mod_EE.darkMatterBlock, 2));
		mod_HowManyItems.addTab(new TabGenericBlock(basemod, locusRecipes, 2, 1, mod_EE.dmLocus, 0));
		mod_HowManyItems.addGuiToBlock(GuiLocus.class, new ItemStack(mod_EE.dmLocus));
	}
}