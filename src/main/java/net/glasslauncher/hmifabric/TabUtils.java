package net.glasslauncher.hmifabric;

import net.fabricmc.api.ClientModInitializer;
import net.glasslauncher.hmifabric.tabs.Tab;
import net.glasslauncher.hmifabric.tabs.TabCrafting;
import net.glasslauncher.hmifabric.tabs.TabSmelting;
import net.minecraft.block.BlockBase;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.client.gui.screen.container.Crafting;
import net.minecraft.client.gui.screen.container.Furnace;
import net.minecraft.item.ItemInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabUtils {
	
	private static TabCrafting workbenchTab;
	private static TabSmelting furnaceTab;

	private static Map<Class<? extends ContainerBase>, ItemInstance> guiToBlock = new HashMap<>();

	public static void loadTabs(ArrayList<Tab> tabList, ClientModInitializer mod) {
		workbenchTab = new TabCrafting(mod);
		tabList.add(workbenchTab);
		guiToBlock.put(Crafting.class, new ItemInstance(BlockBase.WORKBENCH));
		
		furnaceTab = new TabSmelting(mod);
		tabList.add(furnaceTab);
		furnaceTab.equivalentCraftingStations.add(new ItemInstance(BlockBase.FURNACE_LIT));
		guiToBlock.put(Furnace.class, new ItemInstance(BlockBase.FURNACE));

	}
	
	public static ItemInstance getItemFromGui(ContainerBase screen) {
		return guiToBlock.get(screen.getClass());
	}
	
	public static void putItemGui(Class<? extends ContainerBase> gui, ItemInstance item) {
		guiToBlock.put(gui, item);
	}
	
	public static void addWorkBenchGui(Class<? extends ContainerBase> gui) {
		workbenchTab.guiCraftingStations.add(gui);
	}
	
	public static void addEquivalentWorkbench(ItemInstance item) {
		workbenchTab.equivalentCraftingStations.add(item);
	}
	
	public static void addEquivalentFurnace(ItemInstance item) {
		furnaceTab.equivalentCraftingStations.add(item);
	}
	
	public static void addHiddenModItems(ArrayList<ItemInstance> itemList) {
	}
}
