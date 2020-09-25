package net.minecraft.src.hmi.references.ic2mp;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.src.ic2.platform.*;
import net.minecraft.src.ic2.common.ItemBattery;
import net.minecraft.src.ic2.common.RecipeInput;
import net.minecraft.src.ic2.common.TileEntityCompressor;
import net.minecraft.src.ic2.common.TileEntityExtractor;
import net.minecraft.src.ic2.common.TileEntityMacerator;
import net.minecraft.src.*;
import net.minecraft.src.hmi.IC2MPHandler;
import net.minecraft.src.hmi.Utils;
import net.minecraft.src.hmi.tabs.Tab;
import net.minecraft.src.hmi.tabs.TabSmelting;

public class ConcreteHandler extends IC2MPHandler {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void loadTabs(BaseMod basemod) {
		Block machine = mod_IC2Mp.blockMachine;
		
		mod_HowManyItems.addEquivalentFurnace(new ItemStack(machine, 1, 1));
		mod_HowManyItems.addGuiToBlock(net.minecraft.src.ic2.platform.GuiIronFurnace.class, new ItemStack(Block.stoneOvenIdle));
		
		mod_HowManyItems.addEquivalentFurnace(new ItemStack(machine, 1, 2));
		mod_HowManyItems.addGuiToBlock(net.minecraft.src.ic2.platform.GuiElecFurnace.class, new ItemStack(Block.stoneOvenIdle));
		
		mod_HowManyItems.addEquivalentFurnace(new ItemStack(machine, 1, 13));
		mod_HowManyItems.addGuiToBlock(net.minecraft.src.ic2.platform.GuiInduction.class, new ItemStack(Block.stoneOvenIdle));
		
		ArrayList<ItemStack> fuels = new ArrayList<ItemStack>();
		fuels.add(new ItemStack(Item.redstone));
		fuels.add(new ItemStack(mod_IC2Mp.itemBatSU));
		for(Item item: Item.itemsList) {
			if(item != null && item instanceof ItemBattery) fuels.add(new ItemStack(item));
		}
		
		TabSmelting maceratorTab = new TabSmelting(basemod, TileEntityMacerator.recipes, fuels, "/ic2/sprites/GUIMacerator.png", machine, 3);
		mod_HowManyItems.addTab(maceratorTab);
		mod_HowManyItems.addGuiToBlock(net.minecraft.src.ic2.platform.GuiMacerator.class, new ItemStack(machine, 1, 3));
		
		TabSmelting compressorTab = new TabSmelting(basemod, TileEntityCompressor.recipes, fuels, "/ic2/sprites/GUICompressor.png", machine, 5);
		mod_HowManyItems.addTab(compressorTab);
		mod_HowManyItems.addGuiToBlock(net.minecraft.src.ic2.platform.GuiCompressor.class, new ItemStack(machine, 1, 5));

		mod_HowManyItems.addTab(new TabSmelting(basemod, TileEntityExtractor.recipes, fuels, "/ic2/sprites/GUIExtractor.png", machine, 4));
		mod_HowManyItems.addGuiToBlock(net.minecraft.src.ic2.platform.GuiExtractor.class, new ItemStack(machine, 1, 4));
		
		mod_HowManyItems.addTab(new TabIC2CanningMachine(basemod, fuels, "/ic2/sprites/GUICanner.png", machine, 6));
		mod_HowManyItems.addGuiToBlock(net.minecraft.src.ic2.platform.GuiCanner.class, new ItemStack(machine, 1, 6));
		
		mod_HowManyItems.addTab(new TabIC2Recycler(basemod, fuels, "/ic2/sprites/GUIRecycler.png", machine, 11));
		mod_HowManyItems.addGuiToBlock(net.minecraft.src.ic2.platform.GuiRecycler.class, new ItemStack(machine, 1, 11));
		
		HashMap matterFabricatorRecipes = new HashMap();
		matterFabricatorRecipes.put(null, new ItemStack(mod_IC2Mp.itemMatter));
		matterFabricatorRecipes.put(mod_IC2Mp.itemScrap.shiftedIndex, new ItemStack(mod_IC2Mp.itemMatter));
		Tab matterFabricatorTab = new TabSmelting(basemod, 2, matterFabricatorRecipes, null, "/ic2/sprites/GUIMatter.png", 30, 62, 107, 11, machine, 14);
		matterFabricatorTab.slots[0] = new Integer[] {7, 10};
		matterFabricatorTab.slots[1] = new Integer[] {7, 46};
		mod_HowManyItems.addTab(matterFabricatorTab);
		mod_HowManyItems.addGuiToBlock(GuiMatter.class, new ItemStack(machine, 1, 14));
		
		if(ModLoader.isModLoaded("mod_IC2_AdvMachine")) {
			((AdvMachinesHandler) Utils.getHandler("ic2mp.advMachines")).init(maceratorTab, compressorTab);
		}
	}
	
	final Utils.EasyField<Integer> idField = new Utils.EasyField<Integer>(RecipeInput.class, "itemId");
	final Utils.EasyField<Integer> dmgField = new Utils.EasyField<Integer>(RecipeInput.class, "itemDamage");

	@Override
	public boolean isRecipeInput(Object obj) {
		return obj instanceof RecipeInput;
	}

	@Override
	public int getId(Object recipeInput) {
		return idField.get(recipeInput);
	}

	@Override
	public int getDamage(Object recipeInput) {
		return dmgField.get(recipeInput);
	}

}
