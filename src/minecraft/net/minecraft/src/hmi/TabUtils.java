package net.minecraft.src.hmi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import buildcraft.factory.*;
import ic2.*;
import net.minecraft.src.*;
import net.minecraft.src.hmi.tabs.*;
import net.minecraft.src.hmi.tabs.mods.*;

public class TabUtils {

	public static void loadTabs(ArrayList<Tab> tabList, BaseMod mod) {
		TabCrafting workbenchTab = new TabCrafting(mod);
		tabList.add(workbenchTab);
		guiToBlock.put(GuiCrafting.class, new ItemStack(Block.workbench));
		
		Tab smeltingTab = new TabSmelting(mod);
		tabList.add(smeltingTab);
		smeltingTab.equivalentCraftingStations.add(new ItemStack(Block.stoneOvenActive));
		guiToBlock.put(GuiFurnace.class, new ItemStack(Block.stoneOvenIdle));
		
		if(ModLoader.isModLoaded("mod_Planes")) {
			ArrayList planeRecipes = new ArrayList(CraftingManager.getInstance().getRecipeList());
			for (int i = 0; i < planeRecipes.size(); i++) {
				//Removes recipes for vanilla crafting table
				if(((IRecipe)planeRecipes.get(i)).getRecipeSize() <= 9)
	            {
					planeRecipes.remove(i);
					i-=1;
	            }
	    	}
			TabCrafting planeTab = new TabCrafting(mod, 16, planeRecipes, mod_Planes.planeWorkbench, "/gui/planeCrafting.png", 128, 56, 24, 16, 92, 46, 5);
			planeTab.slots[0] = new Integer[]{110, 23};
			planeTab.guiCraftingStations.add(GuiPlaneCrafting.class);
			tabList.add(planeTab);
			guiToBlock.put(GuiPlaneCrafting.class, new ItemStack(mod_Planes.planeWorkbench));
		}

		if(ModLoader.isModLoaded("mod_Uranium")) {
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
			Tab reactorTab = new TabSmelting(mod, ReactorRecipes.smelting().getSmeltingList(), fuels, "/uraniumTextures/reactorgui.png", mod_Uranium.reactorIdle);
			reactorTab.equivalentCraftingStations.add(new ItemStack(mod_Uranium.reactorActive));
			tabList.add(reactorTab);
			guiToBlock.put(GuiUraniumMod.class, new ItemStack(mod_Uranium.reactorIdle));
		}
		if(ModLoader.isModLoaded("mod_IndustrialCraft")) {
			ArrayList<ItemStack> fuels = new ArrayList<ItemStack>();
			fuels.add(new ItemStack(Item.redstone));
			fuels.add(new ItemStack(mod_IndustrialCraft.akkuFull));
			fuels.add(new ItemStack(mod_IndustrialCraft.itemOneBattery));
			
			try {
				Tab maceratorTab = new TabSmelting(mod, (Map)ModLoader.getPrivateValue(MaceratorRecipes.class, MaceratorRecipes.smelting(), "smeltingList"), fuels, "/IndustrialSprites/MaceratorGUI.png", mod_IndustrialCraft.blockMaceratorOff);
				maceratorTab.equivalentCraftingStations.add(new ItemStack(mod_IndustrialCraft.blockMaceratorOn));
				tabList.add(maceratorTab);
				guiToBlock.put(net.minecraft.src.GuiMacerator.class, new ItemStack(mod_IndustrialCraft.blockMaceratorOff));
				
				Tab extractorTab = new TabSmelting(mod, (Map)ModLoader.getPrivateValue(ExtractorRecipes.class, ExtractorRecipes.smelting(), "smeltingList"), fuels, "/IndustrialSprites/ExtractorGUI.png", mod_IndustrialCraft.blockExtractorOff);
				extractorTab.equivalentCraftingStations.add(new ItemStack(mod_IndustrialCraft.blockExtractorOn));
				tabList.add(extractorTab);
				guiToBlock.put(net.minecraft.src.GuiExtractor.class, new ItemStack(mod_IndustrialCraft.blockExtractorOff));
			
				Tab compressorTab = new TabSmelting(mod, (Map)ModLoader.getPrivateValue(CompressorRecipes.class, CompressorRecipes.smelting(), "smeltingList"), fuels, "/IndustrialSprites/CompressorGUI.png", mod_IndustrialCraft.blockCompressorOff);
				compressorTab.equivalentCraftingStations.add(new ItemStack(mod_IndustrialCraft.blockCompressorOff));
				tabList.add(compressorTab);
				guiToBlock.put(net.minecraft.src.GuiCompressor.class, new ItemStack(mod_IndustrialCraft.blockCompressorOff));
			} 
			catch (IllegalArgumentException e) { e.printStackTrace(); } 
			catch (NoSuchFieldException e) { e.printStackTrace(); }
		}
		if(ModLoader.isModLoaded("mod_IC2")) {
			Block machine = mod_IC2.blockMachine;
			
			smeltingTab.equivalentCraftingStations.add(new ItemStack(machine, 1, 1));
			guiToBlock.put(ic2.GuiIronFurnace.class, new ItemStack(Block.stoneOvenIdle));
			
			smeltingTab.equivalentCraftingStations.add(new ItemStack(machine, 1, 2));
			guiToBlock.put(ic2.GuiElecFurnace.class, new ItemStack(Block.stoneOvenIdle));
			
			smeltingTab.equivalentCraftingStations.add(new ItemStack(machine, 1, 13));
			guiToBlock.put(ic2.GuiInduction.class, new ItemStack(Block.stoneOvenIdle));
			
			ArrayList<ItemStack> fuels = new ArrayList<ItemStack>();
			fuels.add(new ItemStack(Item.redstone));
			fuels.add(new ItemStack(mod_IC2.itemBatSU));
			for(Item item: Item.itemsList) {
				if(item != null && item instanceof ItemBattery) fuels.add(new ItemStack(item));
			}
			
			Tab maceratorTab = new TabSmelting(mod, TileEntityMacerator.recipes, fuels, "/IC2sprites/GUIMacerator.png", machine, 3);
			tabList.add(maceratorTab);
			guiToBlock.put(ic2.GuiMacerator.class, new ItemStack(machine, 1, 3));
			
			Tab compressorTab = new TabSmelting(mod, TileEntityCompressor.recipes, fuels, "/IC2sprites/GUICompressor.png", machine, 5);
			tabList.add(compressorTab);
			guiToBlock.put(ic2.GuiCompressor.class, new ItemStack(machine, 1, 5));

			tabList.add(new TabSmelting(mod, TileEntityExtractor.recipes, fuels, "/IC2sprites/GUIExtractor.png", machine, 4));
			guiToBlock.put(ic2.GuiExtractor.class, new ItemStack(machine, 1, 4));
			
			tabList.add(new TabIC2CanningMachine(mod, fuels, "/IC2sprites/GUICanner.png", machine, 6));
			guiToBlock.put(ic2.GuiCanner.class, new ItemStack(machine, 1, 6));
			
			tabList.add(new TabIC2Recycler(mod, fuels, "/IC2sprites/GUIRecycler.png", machine, 11));
			guiToBlock.put(ic2.GuiRecycler.class, new ItemStack(machine, 1, 11));
			
			HashMap matterFabricatorRecipes = new HashMap();
			matterFabricatorRecipes.put(null, new ItemStack(mod_IC2.itemMatter));
			matterFabricatorRecipes.put(mod_IC2.itemScrap.shiftedIndex, new ItemStack(mod_IC2.itemMatter));
			Tab matterFabricatorTab = new TabSmelting(mod, 2, matterFabricatorRecipes, null, "/IC2sprites/GUIMatter.png", 30, 62, 107, 11, machine, 14);
			matterFabricatorTab.slots[0] = new Integer[] {7, 10};
			matterFabricatorTab.slots[1] = new Integer[] {7, 46};
			tabList.add(matterFabricatorTab);
			guiToBlock.put(ic2.GuiMatter.class, new ItemStack(machine, 1, 14));
			
			if(ModLoader.isModLoaded("mod_IC2_AdvMachine")) {
				maceratorTab.equivalentCraftingStations.add(new ItemStack(mod_IC2_AdvMachine.blockAdvMachine, 1, 0));
				guiToBlock.put(GuiRotary.class, new ItemStack(mod_IC2_AdvMachine.blockAdvMachine, 1, 0));
				
				compressorTab.equivalentCraftingStations.add(new ItemStack(mod_IC2_AdvMachine.blockAdvMachine, 1, 1));
				guiToBlock.put(GuiSingularity.class, new ItemStack(mod_IC2_AdvMachine.blockAdvMachine, 1, 1));
			}
		}
		if(ModLoader.isModLoaded("mod_Aether")) {
			tabList.add(new TabAether(mod, TileEntityEnchanter.class, new ArrayList<ItemStack>(Arrays.asList(new ItemStack(AetherItems.AmbrosiumShard))), "/aether/gui/enchanter.png", AetherBlocks.Enchanter));
			guiToBlock.put(GuiEnchanter.class, new ItemStack(AetherBlocks.Enchanter));
			try
	        {
				Class.forName("TileEntityFreezer");
				tabList.add(new TabAether(mod, TileEntityFreezer.class, new ArrayList<ItemStack>(Arrays.asList(new ItemStack(AetherBlocks.Icestone))), "/aether/gui/enchanter.png", AetherBlocks.Freezer));
				guiToBlock.put(GuiFreezer.class, new ItemStack(AetherBlocks.Freezer));
	        } catch (ClassNotFoundException e) { }
		}
		if(ModLoader.isModLoaded("mod_FCBetterThanWolves")) {
			HashMap millstoneRecipes = new HashMap();
			millstoneRecipes.put(new ItemStack(Item.wheat), new ItemStack(mod_FCBetterThanWolves.fcFlour));
			millstoneRecipes.put(new ItemStack(Item.leather), new ItemStack(mod_FCBetterThanWolves.fcScouredLeather));
			millstoneRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcHemp), new ItemStack(mod_FCBetterThanWolves.fcHempFibers, 4));
			millstoneRecipes.put(new ItemStack(Item.reed), new ItemStack(Item.sugar));
			millstoneRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcCompanionCube), new ItemStack(mod_FCBetterThanWolves.fcWolfRaw));
			millstoneRecipes.put(new ItemStack(Block.netherrack), new ItemStack(mod_FCBetterThanWolves.fcGroundNetherrack));
			millstoneRecipes.put(new ItemStack(Item.bone), new ItemStack(Item.dyePowder, 3, 15));
			millstoneRecipes.put(new ItemStack(Item.coal), new ItemStack(mod_FCBetterThanWolves.fcCoalDust));
			millstoneRecipes.put(new ItemStack(Block.plantRed), new ItemStack(Item.dyePowder, 2, 1));
			millstoneRecipes.put(new ItemStack(Block.plantYellow), new ItemStack(Item.dyePowder, 2, 11));
			tabList.add(new TabGenericBlock(mod, millstoneRecipes, mod_FCBetterThanWolves.fcMillStone));
			guiToBlock.put(FCGuiMillStone.class, new ItemStack(mod_FCBetterThanWolves.fcMillStone));
			
			HashMap sawRecipes = new HashMap();
			sawRecipes.put(new ItemStack(Block.wood), new ItemStack(Block.planks, 4));
			sawRecipes.put(new ItemStack(Block.planks), new ItemStack(mod_FCBetterThanWolves.fcOmniSlab, 2, 1));
			sawRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcOmniSlab, 1, 1), new ItemStack(mod_FCBetterThanWolves.fcMoulding, 2));
			sawRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcMoulding), new ItemStack(mod_FCBetterThanWolves.fcCorner, 2));
			sawRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcCorner), new ItemStack(mod_FCBetterThanWolves.fcGear, 2));
			sawRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcCompanionCube), new ItemStack(mod_FCBetterThanWolves.fcCompanionCube, 2, 1));
			tabList.add(new TabGenericBlock(mod, sawRecipes, mod_FCBetterThanWolves.fcSaw));
			
			HashMap hopperRecipes = new HashMap();
			hopperRecipes.put(new ItemStack[] {new ItemStack(Block.gravel), new ItemStack(mod_FCBetterThanWolves.fcWicker)}, new ItemStack[] {new ItemStack(Block.sand), new ItemStack(Item.flint)});
			hopperRecipes.put(new ItemStack[] {new ItemStack(mod_FCBetterThanWolves.fcGroundNetherrack), new ItemStack(Block.slowSand)}, new ItemStack(mod_FCBetterThanWolves.fcHellfireDust));
			tabList.add(new TabBTWHopper(mod, hopperRecipes));
			guiToBlock.put(FCGuiHopper.class, new ItemStack(mod_FCBetterThanWolves.fcHopper));
			
			HashMap turntableRecipes = new HashMap();
			turntableRecipes.put(new ItemStack(Block.blockClay), new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery));
			turntableRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery), new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 1));
			turntableRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 1), new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 2));
			tabList.add(new TabGenericBlock(mod, turntableRecipes, mod_FCBetterThanWolves.fcTurntable));
			
			HashMap kilnRecipes = new HashMap();
			kilnRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery), new ItemStack(mod_FCBetterThanWolves.fcCrucible));
			kilnRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 1), new ItemStack(mod_FCBetterThanWolves.fcPlanter));
			kilnRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 2), new ItemStack(mod_FCBetterThanWolves.fcVase));
			Tab kilnTab = new TabBTWCauldron(mod, null, kilnRecipes, Block.brick, 1, "Kiln");
			kilnTab.equivalentCraftingStations.clear();
			tabList.add(kilnTab);
			
			recipeListField = Utils.getField(FCCraftingManagerBulk.class, new String[] {"m_recipes"});
			outputItemStackField = Utils.getField(FCCraftingManagerBulkRecipe.class, new String[] {"m_recipeOutputStack"});
			inputItemStacksListField = Utils.getField(FCCraftingManagerBulkRecipe.class, new String[] {"m_recipeInputStacks"});
			
			tabList.add(new TabBTWCauldron(mod, BTWMap(FCCraftingManagerCauldron.getInstance()), BTWMap(FCCraftingManagerCauldronStoked.getInstance()), mod_FCBetterThanWolves.fcCauldron, 2, null));
			guiToBlock.put(FCGuiCauldron.class, new ItemStack(mod_FCBetterThanWolves.fcCauldron));
			
			FCTileEntityCrucible crucible = new FCTileEntityCrucible();
			Method getMeltingResultMethod = Utils.getMethod(FCTileEntityCrucible.class, new String[] {"GetMeltingResult"}, new Class<?>[] {Item.class});
			HashMap crucibleRecipes = new HashMap();
			crucibleRecipes.put(new ItemStack[] {new ItemStack(mod_FCBetterThanWolves.fcCoalDust), new ItemStack(mod_FCBetterThanWolves.fcConcentratedHellfire), new ItemStack(Item.ingotIron, 3)}, new ItemStack(mod_FCBetterThanWolves.fcSteel, 4));
			crucibleRecipes.put(new ItemStack(Block.rail, 16), new ItemStack(Item.ingotIron, 6));
			crucibleRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcBroadheadArrowhead, 4), new ItemStack(mod_FCBetterThanWolves.fcSteel, 1));
			crucibleRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcBroadheadArrow, 16), new ItemStack(mod_FCBetterThanWolves.fcSteel, 1));
			for(Item input : Item.itemsList) {
				if(input != null) {
					try {
						ItemStack output = (ItemStack) getMeltingResultMethod.invoke(crucible, new Object[] {input});
						if(output != null) {
							crucibleRecipes.put(new ItemStack(input), output);
						}
					} 
					catch (Exception e) { e.printStackTrace(); break; }
				}
			}
			tabList.add(new TabBTWCauldron(mod, null, crucibleRecipes, mod_FCBetterThanWolves.fcCrucible, 3, null));
			guiToBlock.put(FCGuiCrucible.class, new ItemStack(mod_FCBetterThanWolves.fcCrucible));
			
			TabCrafting anvilTab = new TabCrafting(mod, new ArrayList(FCCraftingManagerAnvil.getInstance().getRecipeList()), mod_FCBetterThanWolves.fcAnvil);
			anvilTab.guiCraftingStations.add(FCGuiCraftingAnvil.class);
			tabList.add(anvilTab);
			workbenchTab.equivalentCraftingStations.add(new ItemStack(mod_FCBetterThanWolves.fcAnvil));
			workbenchTab.guiCraftingStations.add(FCGuiCraftingAnvil.class);
			guiToBlock.put(FCGuiCraftingAnvil.class, new ItemStack(mod_FCBetterThanWolves.fcAnvil));
		}
		
		if(ModLoader.isModLoaded("mod_BuildCraftFactory")) {
			buildcraftBlockField = Utils.getField(BuildCraftFactory.class, new String[] {"autoWorkbenchBlock"});
			try {
				Block block = (Block) buildcraftBlockField.get(null);
				workbenchTab.guiCraftingStations.add(GuiAutoCrafting.class);
				workbenchTab.equivalentCraftingStations.add(new ItemStack(block));
				guiToBlock.put(GuiAutoCrafting.class, new ItemStack(block));
			} 
			catch (Exception e) { e.printStackTrace(); } 
		}
		
		if(ModLoader.isModLoaded("mod_EE")) {
			smeltingTab.equivalentCraftingStations.add(new ItemStack(mod_EE.darkMatterFurnaceOff));
			smeltingTab.equivalentCraftingStations.add(new ItemStack(mod_EE.darkMatterFurnaceOn));
			guiToBlock.put(GuiDarkMatterFurnace.class, new ItemStack(Block.stoneOvenIdle));
			
			HashMap glowAggRecipes = new HashMap();
			glowAggRecipes.put(new ItemStack(Item.redstone), new ItemStack(Item.lightStoneDust));
			glowAggRecipes.put(new ItemStack(Item.lightStoneDust), new ItemStack(Block.glowStone));
			glowAggRecipes.put(new ItemStack(Block.dirt), new ItemStack(Block.glowStone));
			glowAggRecipes.put(new ItemStack(Block.cobblestone), new ItemStack(Block.glowStone));
			glowAggRecipes.put(new ItemStack(Block.stone), new ItemStack(Block.glowStone));
			glowAggRecipes.put(new ItemStack(Block.netherrack), new ItemStack(Block.glowStone));
			glowAggRecipes.put(new ItemStack(Block.slowSand), new ItemStack(Block.glowStone));
			tabList.add(new TabGenericBlock(mod, glowAggRecipes, mod_EE.glowStoneAggregator));
			guiToBlock.put(GuiAggregator.class, new ItemStack(mod_EE.glowStoneAggregator));
			
			HashMap obsAggRecipes = new HashMap();
			obsAggRecipes.put(new ItemStack[] {new ItemStack(Item.bucketLava), new ItemStack(Item.bucketWater)},  new ItemStack(Block.obsidian));
			obsAggRecipes.put(new ItemStack[] {new ItemStack(Item.redstone), new ItemStack(Item.coal), new ItemStack(Item.bucketEmpty), new ItemStack(Block.ice)},  new ItemStack(Block.obsidian));
			obsAggRecipes.put(new ItemStack[] {new ItemStack(Item.redstone), new ItemStack(mod_EE.volcanite), new ItemStack(mod_EE.evertide)},  new ItemStack(Block.obsidian));
			Tab obsAggTab = new TabGenericBlock(mod, obsAggRecipes, 4, 1, mod_EE.obsAggregatorOff, 0);
			obsAggTab.equivalentCraftingStations.add(new ItemStack(mod_EE.obsAggregatorOn));
			tabList.add(obsAggTab);
			guiToBlock.put(GuiObsAgg.class, new ItemStack(mod_EE.obsAggregatorOff));
			
			HashMap locusRecipes = new HashMap();
			locusRecipes.put(new ItemStack[] {new ItemStack(Item.diamond), new ItemStack(mod_EE.mobiusFuel)}, new ItemStack(Item.diamond, 2));
			locusRecipes.put(new ItemStack[] {new ItemStack(Block.blockDiamond), new ItemStack(mod_EE.mobiusFuel)}, new ItemStack(Block.blockDiamond, 2));
			locusRecipes.put(new ItemStack[] {new ItemStack(mod_EE.darkMatter), new ItemStack(mod_EE.mobiusFuel)}, new ItemStack(mod_EE.darkMatter, 2));
			locusRecipes.put(new ItemStack[] {new ItemStack(mod_EE.darkMatterBlock), new ItemStack(mod_EE.mobiusFuel)}, new ItemStack(mod_EE.darkMatterBlock, 2));
			tabList.add(new TabGenericBlock(mod, locusRecipes, 2, 1, mod_EE.dmLocus, 0));
			guiToBlock.put(GuiLocus.class, new ItemStack(mod_EE.dmLocus));
		}
	}
	
	public static ItemStack getItemFromGui(GuiContainer screen) {
		return guiToBlock.get(screen.getClass());
	}
	
	private static Map<Class<? extends GuiContainer>, ItemStack> guiToBlock = new HashMap();
	
	private static Field recipeListField;
	private static Field outputItemStackField;
	private static Field inputItemStacksListField;
	private static Field buildcraftBlockField;
	
	private static Map BTWMap(Object recipeListInstance) {
		HashMap recipes = new HashMap();
		try {
			List recipeListBTW = (List)recipeListField.get(recipeListInstance);
			for(Object obj : recipeListBTW) {
				ItemStack outputItem = (ItemStack)outputItemStackField.get(obj);
				List inputItemStacksList = (List)inputItemStacksListField.get(obj);
				ItemStack[] inputItemStacks = new ItemStack[inputItemStacksList.size()];
				for(int i = 0; i < inputItemStacksList.size(); i++) {
					inputItemStacks[i] = (ItemStack)inputItemStacksList.get(i);
				}
				recipes.put(inputItemStacks, outputItem);
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		return recipes;
	}
	
	public static void addHiddenModItems(ArrayList<ItemStack> itemList) {
		if(ModLoader.isModLoaded("mod_FCBetterThanWolves")) {
			Utils.addItemInOrder(itemList, new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 1));
			Utils.addItemInOrder(itemList, new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 2));
		}
	}
}
