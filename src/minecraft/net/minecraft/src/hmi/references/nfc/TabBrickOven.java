package net.minecraft.src.hmi.references.nfc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.hmi.tabs.TabSmelting;
import net.minecraft.src.*;

public class TabBrickOven extends TabSmelting {
	
	private final List<Recipe> recipeList = new ArrayList<Recipe>();
	private final List<Recipe> filteredRecipes = new ArrayList<Recipe>();
	private final List<Fuel> fuelList = new ArrayList<Fuel>();
	
	class Recipe {
		public Recipe(ItemStack recipeOutput, int time, ItemStack... recipeItems) {
			this.output = recipeOutput;
			this.inputs = recipeItems;
			this.cookTime = time;
		}

		public final ItemStack output;
		public final ItemStack[] inputs;
		public final int cookTime;
	}
	
	class Fuel {
		public Fuel(ItemStack itemstack, int burnTime) {
			this.itemstack = itemstack;
			this.burnTime = burnTime;
		}

		public final ItemStack itemstack;
		public final int burnTime;
	}
	
	public TabBrickOven(BaseMod tabCreator) {
		super(tabCreator, 11, "/gui/oven.png", 102, 92, 36, 15, NFC.BrickOvenIdle, 0);
		
		int count = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				slots[count++] = (new Integer[] {2 + x*18, 5 + y*18});
			}
		}
		
		slots[count++] = new Integer[] {20, 77};
		slots[count++] = new Integer[] {80, 59};
		
		fuelList.add(new Fuel(new ItemStack(Block.planks), 100));
		fuelList.add(new Fuel(new ItemStack(Item.coal), 1600));
		fuelList.add(new Fuel(new ItemStack(Item.coal, 1, 1), 800));
		fuelList.add(new Fuel(new ItemStack(NFC.anthracite), 6400));
		
		recipeList.add(new Recipe(new ItemStack(NFC.brass, 6), 1600, new ItemStack(NFC.copper, 3), new ItemStack(NFC.zinc, 3)));
		recipeList.add(new Recipe(new ItemStack(NFC.bronze, 6), 1600, new ItemStack(NFC.copper, 5), new ItemStack(NFC.tin)));
		recipeList.add(new Recipe(new ItemStack(NFC.steel, 8), 6400, new ItemStack(NFC.chrome), new ItemStack(Item.ingotIron, 7)));
		recipeList.add(new Recipe(new ItemStack(NFC.tungsten), 200, new ItemStack(NFC.tungstenore)));
		recipeList.add(new Recipe(new ItemStack(NFC.titanium), 200, new ItemStack(NFC.titaniumore)));
		
		for (int i = 0; i < 16; i++) {
			recipeList.add(new Recipe(new ItemStack(Block.lockedChest, 8, 15 - i), 100, new ItemStack(Item.dyePowder, 1, i), new ItemStack(Block.glass, 8, 0)));
		}
	}
	
	public ItemStack[][] getItems(int index, ItemStack filter) {
		ItemStack[][] items = new ItemStack[recipesPerPage][];
		for(int j = 0; j < recipesPerPage; j++)
        {
            items[j] = new ItemStack[slots.length];
            int k = index + j;
            if(k < filteredRecipes.size())
            {
            	Recipe recipe = filteredRecipes.get(k);
            	
            	int slotIndex = 0;
            	for (int i = 0; i < recipe.inputs.length; i++) {
            		int currentSlot = slotIndex;
            		items[j][slotIndex++] = recipe.inputs[i].copy();
            		while(items[j][currentSlot].stackSize > 1) {
            			items[j][currentSlot].stackSize--;
            			items[j][slotIndex++] = new ItemStack(items[j][currentSlot].getItem(), 1, items[j][currentSlot].getItemDamage());
            		}
            		
            	}
            	items[j][10] = recipe.output.copy();
            	Fuel fuel = fuelList.get(rand.nextInt(fuelList.size()));
            	items[j][9] = fuel.itemstack.copy();
            	items[j][9].stackSize = recipe.cookTime / fuel.burnTime;
             }

            if(items[j][10] == null && recipesOnThisPage > j) {
            	recipesOnThisPage = j;
                redrawSlots = true;
                break;
            }
            else if(items[j][10] != null && recipesOnThisPage == j) {
            	recipesOnThisPage = j+1;
                redrawSlots = true;
            }
        }
		return items;
	}
	
	public void updateRecipes(ItemStack filter, Boolean getUses) {
		filteredRecipes.clear();
		updateRecipesWithoutClear(filter, getUses);
	}
	
	public void updateRecipesWithoutClear(ItemStack filter, Boolean getUses) {
		for(Recipe recipe : recipeList) {
			if(filter == null) {
				filteredRecipes.add(recipe);
			}
			else if(!getUses && recipe.output.isItemEqual(filter)) {
				filteredRecipes.add(recipe);
			}
			else if(getUses){
				for(ItemStack item : recipe.inputs) {
					if(item.isItemEqual(filter)) {
						filteredRecipes.add(recipe);
						break;
					}
				}
			}
		}
		size = filteredRecipes.size();
		if (size == 0 && getUses){
			for(ItemStack craftingStation : equivalentCraftingStations) {
				if(filter.itemID == craftingStation.itemID && filter.getItemDamage() == craftingStation.getItemDamage()) {
		    		updateRecipes(null, getUses);
		    		break;
				}
			}
    	}
    	size = filteredRecipes.size();
	}

}
