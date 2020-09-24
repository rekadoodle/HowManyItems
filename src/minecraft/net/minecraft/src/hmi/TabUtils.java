package net.minecraft.src.hmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.*;
import net.minecraft.src.hmi.tabs.*;

public class TabUtils {
	
	private static TabCrafting workbenchTab;
	private static TabSmelting furnaceTab;
	
	public static TabHandler btwHandler;
	public static AetherHandler aetherHandler;

	public static void loadTabs(ArrayList<Tab> tabList, BaseMod mod) {
		workbenchTab = new TabCrafting(mod);
		tabList.add(workbenchTab);
		guiToBlock.put(GuiCrafting.class, new ItemStack(Block.workbench));
		
		furnaceTab = new TabSmelting(mod);
		tabList.add(furnaceTab);
		furnaceTab.equivalentCraftingStations.add(new ItemStack(Block.stoneOvenActive));
		guiToBlock.put(GuiFurnace.class, new ItemStack(Block.stoneOvenIdle));
		
		if(ModLoader.isModLoaded("mod_Planes")) {
			((TabHandler) Utils.getHandler("planes")).loadTabs(mod);
		}
		if(ModLoader.isModLoaded("mod_Uranium")) {
			((TabHandler) Utils.getHandler("uranium")).loadTabs(mod);
		}
		if(ModLoader.isModLoaded("mod_IndustrialCraft")) {
			((TabHandler) Utils.getHandler("ic1")).loadTabs(mod);
		}
		if(ModLoader.isModLoaded("mod_IC2")) {
			((TabHandler) Utils.getHandler("ic2")).loadTabs(mod);
		}
		if(ModLoader.isModLoaded("mod_Aether")) {
			aetherHandler = ((AetherHandler) Utils.getHandler("aether"));
			aetherHandler.loadTabs(mod);
		}
		
		if(btwHandler != null) {
			btwHandler.loadTabs(mod);
		}
		
		if(ModLoader.isModLoaded("mod_BuildCraftFactory")) {
			Utils.getHandler("buildcraft");
		}
		
		if(ModLoader.isModLoaded("mod_EE")) {
			((TabHandler) Utils.getHandler("ee1")).loadTabs(mod);
		}
		
		if(Utils.nmsClassExists("NFC")) {
			((TabHandler) Utils.getHandler("nfc")).loadTabs(mod);
		}
	}
	
	public static ItemStack getItemFromGui(GuiContainer screen) {
		return guiToBlock.get(screen.getClass());
	}
	
	public static void putItemGui(Class<? extends GuiContainer> gui, ItemStack item) {
		guiToBlock.put(gui, item);
	}
	
	public static void addWorkBenchGui(Class<? extends GuiContainer> gui) {
		workbenchTab.guiCraftingStations.add(gui);
	}
	
	public static void addEquivalentWorkbench(ItemStack item) {
		workbenchTab.equivalentCraftingStations.add(item);
	}
	
	public static void addEquivalentFurnace(ItemStack item) {
		furnaceTab.equivalentCraftingStations.add(item);
	}
	
	private static Map<Class<? extends GuiContainer>, ItemStack> guiToBlock = new HashMap();
	
	public static void addHiddenModItems(ArrayList<ItemStack> itemList) {
		if(btwHandler != null) {
			btwHandler.registerItems(itemList);
		}
	}
}
