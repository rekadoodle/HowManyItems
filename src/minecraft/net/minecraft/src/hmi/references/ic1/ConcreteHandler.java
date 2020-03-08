package net.minecraft.src.hmi.references.ic1;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.tabs.Tab;
import net.minecraft.src.hmi.tabs.TabSmelting;

public class ConcreteHandler extends TabHandler {
	
	@SuppressWarnings("rawtypes")
	@Override
	public void loadTabs(BaseMod basemod) {
		ArrayList<ItemStack> fuels = new ArrayList<ItemStack>();
		fuels.add(new ItemStack(Item.redstone));
		fuels.add(new ItemStack(mod_IndustrialCraft.akkuFull));
		fuels.add(new ItemStack(mod_IndustrialCraft.itemOneBattery));
		
		try {
			Tab maceratorTab = new TabSmelting(basemod, (Map)ModLoader.getPrivateValue(MaceratorRecipes.class, MaceratorRecipes.smelting(), "smeltingList"), fuels, "/IndustrialSprites/MaceratorGUI.png", mod_IndustrialCraft.blockMaceratorOff);
			maceratorTab.equivalentCraftingStations.add(new ItemStack(mod_IndustrialCraft.blockMaceratorOn));
			mod_HowManyItems.addTab(maceratorTab);
			mod_HowManyItems.addGuiToBlock(GuiMacerator.class, new ItemStack(mod_IndustrialCraft.blockMaceratorOff));
			
			Tab extractorTab = new TabSmelting(basemod, (Map)ModLoader.getPrivateValue(ExtractorRecipes.class, ExtractorRecipes.smelting(), "smeltingList"), fuels, "/IndustrialSprites/ExtractorGUI.png", mod_IndustrialCraft.blockExtractorOff);
			extractorTab.equivalentCraftingStations.add(new ItemStack(mod_IndustrialCraft.blockExtractorOn));
			mod_HowManyItems.addTab(extractorTab);
			mod_HowManyItems.addGuiToBlock(GuiExtractor.class, new ItemStack(mod_IndustrialCraft.blockExtractorOff));
		
			Tab compressorTab = new TabSmelting(basemod, (Map)ModLoader.getPrivateValue(CompressorRecipes.class, CompressorRecipes.smelting(), "smeltingList"), fuels, "/IndustrialSprites/CompressorGUI.png", mod_IndustrialCraft.blockCompressorOff);
			compressorTab.equivalentCraftingStations.add(new ItemStack(mod_IndustrialCraft.blockCompressorOff));
			mod_HowManyItems.addTab(compressorTab);
			mod_HowManyItems.addGuiToBlock(GuiCompressor.class, new ItemStack(mod_IndustrialCraft.blockCompressorOff));
		} 
		catch (IllegalArgumentException e) { e.printStackTrace(); } 
		catch (NoSuchFieldException e) { e.printStackTrace(); }
	}
}
