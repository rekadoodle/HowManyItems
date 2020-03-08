package net.minecraft.src.hmi.references.ic2;

import java.util.ArrayList;
import java.util.HashMap;

import ic2.*;
import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.Utils;
import net.minecraft.src.hmi.tabs.Tab;
import net.minecraft.src.hmi.tabs.TabSmelting;

public class ConcreteHandler extends TabHandler {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void loadTabs(BaseMod basemod) {
		Block machine = mod_IC2.blockMachine;
		
		mod_HowManyItems.addEquivalentFurnace(new ItemStack(machine, 1, 1));
		mod_HowManyItems.addGuiToBlock(ic2.GuiIronFurnace.class, new ItemStack(Block.stoneOvenIdle));
		
		mod_HowManyItems.addEquivalentFurnace(new ItemStack(machine, 1, 2));
		mod_HowManyItems.addGuiToBlock(ic2.GuiElecFurnace.class, new ItemStack(Block.stoneOvenIdle));
		
		mod_HowManyItems.addEquivalentFurnace(new ItemStack(machine, 1, 13));
		mod_HowManyItems.addGuiToBlock(ic2.GuiInduction.class, new ItemStack(Block.stoneOvenIdle));
		
		ArrayList<ItemStack> fuels = new ArrayList<ItemStack>();
		fuels.add(new ItemStack(Item.redstone));
		fuels.add(new ItemStack(mod_IC2.itemBatSU));
		for(Item item: Item.itemsList) {
			if(item != null && item instanceof ItemBattery) fuels.add(new ItemStack(item));
		}
		
		TabSmelting maceratorTab = new TabSmelting(basemod, TileEntityMacerator.recipes, fuels, "/IC2sprites/GUIMacerator.png", machine, 3);
		mod_HowManyItems.addTab(maceratorTab);
		mod_HowManyItems.addGuiToBlock(ic2.GuiMacerator.class, new ItemStack(machine, 1, 3));
		
		TabSmelting compressorTab = new TabSmelting(basemod, TileEntityCompressor.recipes, fuels, "/IC2sprites/GUICompressor.png", machine, 5);
		mod_HowManyItems.addTab(compressorTab);
		mod_HowManyItems.addGuiToBlock(ic2.GuiCompressor.class, new ItemStack(machine, 1, 5));

		mod_HowManyItems.addTab(new TabSmelting(basemod, TileEntityExtractor.recipes, fuels, "/IC2sprites/GUIExtractor.png", machine, 4));
		mod_HowManyItems.addGuiToBlock(ic2.GuiExtractor.class, new ItemStack(machine, 1, 4));
		
		mod_HowManyItems.addTab(new TabIC2CanningMachine(basemod, fuels, "/IC2sprites/GUICanner.png", machine, 6));
		mod_HowManyItems.addGuiToBlock(ic2.GuiCanner.class, new ItemStack(machine, 1, 6));
		
		mod_HowManyItems.addTab(new TabIC2Recycler(basemod, fuels, "/IC2sprites/GUIRecycler.png", machine, 11));
		mod_HowManyItems.addGuiToBlock(ic2.GuiRecycler.class, new ItemStack(machine, 1, 11));
		
		HashMap matterFabricatorRecipes = new HashMap();
		matterFabricatorRecipes.put(null, new ItemStack(mod_IC2.itemMatter));
		matterFabricatorRecipes.put(mod_IC2.itemScrap.shiftedIndex, new ItemStack(mod_IC2.itemMatter));
		Tab matterFabricatorTab = new TabSmelting(basemod, 2, matterFabricatorRecipes, null, "/IC2sprites/GUIMatter.png", 30, 62, 107, 11, machine, 14);
		matterFabricatorTab.slots[0] = new Integer[] {7, 10};
		matterFabricatorTab.slots[1] = new Integer[] {7, 46};
		mod_HowManyItems.addTab(matterFabricatorTab);
		mod_HowManyItems.addGuiToBlock(GuiMatter.class, new ItemStack(machine, 1, 14));
		
		if(ModLoader.isModLoaded("mod_IC2_AdvMachine")) {
			((AdvMachinesHandler) Utils.getHandler("ic2.advMachines")).init(maceratorTab, compressorTab);
		}
	}

}
