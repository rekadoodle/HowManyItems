package net.minecraft.src.hmi.references.btw;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.Utils;
import net.minecraft.src.hmi.tabs.Tab;
import net.minecraft.src.hmi.tabs.TabCrafting;
import net.minecraft.src.hmi.tabs.TabGenericBlock;

public class ConcreteHandler extends TabHandler {

	@Override
	public void loadTabs(BaseMod basemod) {
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
		
		mod_HowManyItems.addTab(new TabGenericBlock(basemod, millstoneRecipes, mod_FCBetterThanWolves.fcMillStone));
		mod_HowManyItems.addGuiToBlock(FCGuiMillStone.class, new ItemStack(mod_FCBetterThanWolves.fcMillStone));
		
		HashMap sawRecipes = new HashMap();
		sawRecipes.put(new ItemStack(Block.wood), new ItemStack(Block.planks, 4));
		sawRecipes.put(new ItemStack(Block.planks), new ItemStack(mod_FCBetterThanWolves.fcOmniSlab, 2, 1));
		sawRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcOmniSlab, 1, 1), new ItemStack(mod_FCBetterThanWolves.fcMoulding, 2));
		sawRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcMoulding), new ItemStack(mod_FCBetterThanWolves.fcCorner, 2));
		sawRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcCorner), new ItemStack(mod_FCBetterThanWolves.fcGear, 2));
		sawRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcCompanionCube), new ItemStack(mod_FCBetterThanWolves.fcCompanionCube, 2, 1));
		mod_HowManyItems.addTab(new TabGenericBlock(basemod, sawRecipes, mod_FCBetterThanWolves.fcSaw));
		
		HashMap hopperRecipes = new HashMap();
		hopperRecipes.put(new ItemStack[] {new ItemStack(Block.gravel), new ItemStack(mod_FCBetterThanWolves.fcWicker)}, new ItemStack[] {new ItemStack(Block.sand), new ItemStack(Item.flint)});
		hopperRecipes.put(new ItemStack[] {new ItemStack(mod_FCBetterThanWolves.fcGroundNetherrack), new ItemStack(Block.slowSand)}, new ItemStack(mod_FCBetterThanWolves.fcHellfireDust));
		mod_HowManyItems.addTab(new TabBTWHopper(basemod, hopperRecipes));
		mod_HowManyItems.addGuiToBlock(FCGuiHopper.class, new ItemStack(mod_FCBetterThanWolves.fcHopper));
		
		HashMap turntableRecipes = new HashMap();
		turntableRecipes.put(new ItemStack(Block.blockClay), new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery));
		turntableRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery), new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 1));
		turntableRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 1), new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 2));
		mod_HowManyItems.addTab(new TabGenericBlock(basemod, turntableRecipes, mod_FCBetterThanWolves.fcTurntable));
		
		HashMap kilnRecipes = new HashMap();
		kilnRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery), new ItemStack(mod_FCBetterThanWolves.fcCrucible));
		kilnRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 1), new ItemStack(mod_FCBetterThanWolves.fcPlanter));
		kilnRecipes.put(new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 2), new ItemStack(mod_FCBetterThanWolves.fcVase));
		Tab kilnTab = new TabBTWCauldron(basemod, null, kilnRecipes, Block.brick, 1, "Kiln");
		kilnTab.equivalentCraftingStations.clear();
		mod_HowManyItems.addTab(kilnTab);
		
		recipeListField = Utils.getField(FCCraftingManagerBulk.class, new String[] {"m_recipes"});
		outputItemStackField = Utils.getField(FCCraftingManagerBulkRecipe.class, new String[] {"m_recipeOutputStack"});
		inputItemStacksListField = Utils.getField(FCCraftingManagerBulkRecipe.class, new String[] {"m_recipeInputStacks"});
		
		mod_HowManyItems.addTab(new TabBTWCauldron(basemod, BTWMap(FCCraftingManagerCauldron.getInstance()), BTWMap(FCCraftingManagerCauldronStoked.getInstance()), mod_FCBetterThanWolves.fcCauldron, 2, null));
		mod_HowManyItems.addGuiToBlock(FCGuiCauldron.class, new ItemStack(mod_FCBetterThanWolves.fcCauldron));
		
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
		mod_HowManyItems.addTab(new TabBTWCauldron(basemod, null, crucibleRecipes, mod_FCBetterThanWolves.fcCrucible, 3, null));
		mod_HowManyItems.addGuiToBlock(FCGuiCrucible.class, new ItemStack(mod_FCBetterThanWolves.fcCrucible));
		
		TabCrafting anvilTab = new TabCrafting(basemod, new ArrayList(FCCraftingManagerAnvil.getInstance().getRecipeList()), mod_FCBetterThanWolves.fcAnvil);
		anvilTab.guiCraftingStations.add(FCGuiCraftingAnvil.class);
		mod_HowManyItems.addTab(anvilTab);
		mod_HowManyItems.addEquivalentWorkbench(new ItemStack(mod_FCBetterThanWolves.fcAnvil));
		mod_HowManyItems.addWorkBenchGui(FCGuiCraftingAnvil.class);
		mod_HowManyItems.addGuiToBlock(FCGuiCraftingAnvil.class, new ItemStack(mod_FCBetterThanWolves.fcAnvil));
	}
	
	@Override
	public void registerItems(ArrayList<ItemStack> itemList) {
		Utils.addItemInOrder(itemList, new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 1));
		Utils.addItemInOrder(itemList, new ItemStack(mod_FCBetterThanWolves.fcUnfiredPottery, 1, 2));
	}

	private static Field recipeListField;
	private static Field outputItemStackField;
	private static Field inputItemStacksListField;
	
	private Map BTWMap(Object recipeListInstance) {
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
}