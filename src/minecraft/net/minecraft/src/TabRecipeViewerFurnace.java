package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TabRecipeViewerFurnace extends TabRecipeViewer {

	protected Map recipesComplete;
	protected ArrayList<ItemStack[]> recipes = new ArrayList<ItemStack[]>();
	private ArrayList<ItemStack> fuels;
	private Block tabBlock;
	private int metadata;
	
	public TabRecipeViewerFurnace(BaseMod tabCreator) {
		this(tabCreator, new HashMap(), new ArrayList<ItemStack>(), "/gui/furnace.png", Block.stoneOvenIdle);
		
		recipesComplete = FurnaceRecipes.smelting().getSmeltingList();
		
		fuels.add(new ItemStack(Item.stick));
		fuels.add(new ItemStack(Item.coal));
		fuels.add(new ItemStack(Item.bucketLava));
		fuels.add(new ItemStack(Block.sapling));
		for(Block block: Block.blocksList) {
			if(block != null && (block.blockMaterial == Material.wood || ModLoader.AddAllFuel(block.blockID) > 0) 
					//ignore signs, doors and locked chest
					&& block.blockID != 63 && block.blockID != 64 && block.blockID != 68 && block.blockID != 95) 
				fuels.add(new ItemStack(block));
				
		}
		for(Item item: Item.itemsList) {
			if(item != null && ModLoader.AddAllFuel(item.shiftedIndex) > 0) fuels.add(new ItemStack(item));
		}
	}
	
	public TabRecipeViewerFurnace(BaseMod tabCreator, Map recipes, ArrayList<ItemStack> fuels, String texturePath, Block tabBlock) {
		this(tabCreator, recipes, fuels, texturePath, tabBlock, 0);
	}
	
	public TabRecipeViewerFurnace(BaseMod tabCreator, Map recipes, ArrayList<ItemStack> fuels, String texturePath, Block tabBlock, int metadata) {
		this(tabCreator, 3, recipes, fuels, texturePath, 84, 56, 54, 15, tabBlock, metadata);
	}
	
	public TabRecipeViewerFurnace(BaseMod tabCreator, int slotsPerRecipe, Map recipes, ArrayList<ItemStack> fuels, String texturePath, int width, int height, int textureX, int textureY, Block tabBlock, int metadata) {
		this(tabCreator, slotsPerRecipe, texturePath, width, height, textureX, textureY, tabBlock, metadata);
		
		this.recipesComplete = recipes;
		this.fuels = fuels;
	}
	
	public TabRecipeViewerFurnace(BaseMod tabCreator, int slotsPerRecipe, String texturePath, int width, int height, int textureX, int textureY, Block tabBlock, int metadata) {
		super(tabCreator, slotsPerRecipe, texturePath, width, height, 3, 3, textureX, textureY);
		
		this.tabBlock = tabBlock;
		this.metadata = metadata;
		
		slots[0] = new Integer[]{62, 23};
		slots[1] = new Integer[]{2, 5};
		slots[2] = new Integer[]{2, 41};
	}

	public ItemStack[][] getItems(int index, ItemStack filter) {
		ItemStack[][] items = new ItemStack[recipesPerPage][];
		for(int j = 0; j < recipesPerPage; j++)
        {
            items[j] = new ItemStack[slots.length];
            int k = index + j;
            if(k < recipes.size())
            {
            	ItemStack[] recipe = (ItemStack[])recipes.get(k);
            	for (int i = 0; i < recipe.length; i++) {
            		items[j][i] = recipe[i];
            		if (recipe[i].getItemDamage() == -1) {
                    	if (recipe[i].getHasSubtypes()) {
                    		if (filter != null && recipe[i].itemID == filter.itemID) {
                    			items[j][i] = new ItemStack(recipe[i].getItem(), 0, filter.getItemDamage());
                    		}
                    		else {
                    			items[j][i] = new ItemStack(recipe[i].getItem());
                    		}
                    	}
                    	else if (filter != null && recipe[i].itemID == filter.itemID){
                    		items[j][i] = new ItemStack(recipe[i].getItem(), 0, filter.getItemDamage());
                    	}
                    }
            	}
                
                items[j][2] = fuels.get(rand.nextInt(fuels.size()));
             }

            if(items[j][0] == null && recipesOnThisPage > j) {
            	recipesOnThisPage = j;
                redrawSlots = true;
                break;
            }
            else if(items[j][0] != null && recipesOnThisPage == j) {
            	recipesOnThisPage = j+1;
                redrawSlots = true;
            }
        }
		
		return items;
	}

	public void updateRecipes(ItemStack filter, Boolean getUses) {
		recipes.clear();
		updateRecipesWithoutClear(filter, getUses);
	}
	
	public void updateRecipesWithoutClear(ItemStack filter, Boolean getUses) {
		lastIndex = 0;
		for (Object obj : recipesComplete.keySet()) {
			int dmg = 0;
			if (filter != null) dmg = filter.getItemDamage();
			ItemStack input;
			if ((Integer)obj < Block.blocksList.length) {
				input = new ItemStack(Block.blocksList[(Integer)obj], 1, dmg);
			}
			else {
				input = new ItemStack(Item.itemsList[(Integer)obj], 1, dmg);
			}
			ItemStack output = (ItemStack)(recipesComplete.get(obj));
			
			if(filter == null ||
					(getUses && input.itemID == filter.itemID ) ||
					(!getUses && output.itemID == filter.itemID && (output.getItemDamage() == filter.getItemDamage() || output.getItemDamage() < 0 || !output.getHasSubtypes())))
			{
				recipes.add(new ItemStack[]{output, input});
            }
			
		}
		super.updateRecipes(filter, getUses);
	}

	public ItemStack getTabItem() {
		return new ItemStack(tabBlock, 1, metadata);
	}

	public int size() {
		return recipes.size();
	}
	
	protected Random rand = new Random();
}
