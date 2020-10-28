package net.glasslauncher.hmifabric.tabs;

import net.fabricmc.api.ClientModInitializer;
import net.glasslauncher.hmifabric.TabUtils;
import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.recipe.SmeltingRecipeRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TabSmelting extends TabWithTexture {

	protected Map recipesComplete;
	protected ArrayList<ItemInstance[]> recipes = new ArrayList<>();
	private ArrayList<ItemInstance> fuels;
	private BlockBase tabBlock;
	private int metadata;
	private boolean damagedFurnaceInput = false;
	
	public TabSmelting(ClientModInitializer tabCreator) {
		this(tabCreator, new HashMap(), new ArrayList<>(), "/gui/furnace.png", BlockBase.FURNACE);
		
		recipesComplete = SmeltingRecipeRegistry.getInstance().getRecipes();
		
		fuels.add(new ItemInstance(ItemBase.stick));
		fuels.add(new ItemInstance(ItemBase.coal));
		fuels.add(new ItemInstance(ItemBase.bucketLava));
		fuels.add(new ItemInstance(BlockBase.SAPLING));
		for(BlockBase block: BlockBase.BY_ID) {
			if(block != null && (block.material == Material.WOOD /*|| ModLoader.AddAllFuel(block.id) > 0 Not sure how to reimplement in SL*/)
					//ignore signs, doors and locked chest
					&& block.id != 63 && block.id != 64 && block.id != 68 && block.id != 95) 
				fuels.add(new ItemInstance(block));
				
		}

		/*
		for(ItemBase item: ItemBase.byId) {
			if(item != null && ModLoader.AddAllFuel(item.id) > 0)
				fuels.add(new ItemInstance(item));
		}*/
		
		try
        {
            //ModLoader.getPrivateValue(net.minecraft.src.TileEntityFurnace.class, new TileEntityFurnace(), "furnaceHacks");
            damagedFurnaceInput = true;
        }
        catch(Exception exception)
        {
        	damagedFurnaceInput = false;
        }
	}
	
	public TabSmelting(ClientModInitializer tabCreator, Map recipes, ArrayList<ItemInstance> fuels, String texturePath, BlockBase tabBlock) {
		this(tabCreator, recipes, fuels, texturePath, tabBlock, 0);
	}
	
	public TabSmelting(ClientModInitializer tabCreator, Map recipes, ArrayList<ItemInstance> fuels, String texturePath, BlockBase tabBlock, int metadata) {
		this(tabCreator, 3, recipes, fuels, texturePath, 84, 56, 54, 15, tabBlock, metadata);
	}
	
	public TabSmelting(ClientModInitializer tabCreator, int slotsPerRecipe, Map recipes, ArrayList<ItemInstance> fuels, String texturePath, int width, int height, int textureX, int textureY, BlockBase tabBlock, int metadata) {
		this(tabCreator, slotsPerRecipe, texturePath, width, height, textureX, textureY, tabBlock, metadata);
		
		this.recipesComplete = recipes;
		this.fuels = fuels;
	}
	
	public TabSmelting(ClientModInitializer tabCreator, int slotsPerRecipe, String texturePath, int width, int height, int textureX, int textureY, BlockBase tabBlock, int metadata) {
		super(tabCreator, slotsPerRecipe, texturePath, width, height, 3, 3, textureX, textureY);
		
		this.tabBlock = tabBlock;
		this.metadata = metadata;
		
		slots[0] = new Integer[]{62, 23};
		slots[1] = new Integer[]{2, 5};
		if(slotsPerRecipe > 2)
		slots[2] = new Integer[]{2, 41};
		equivalentCraftingStations.add(getTabItem());
	}

	public ItemInstance[][] getItems(int index, ItemInstance filter) {
		ItemInstance[][] items = new ItemInstance[recipesPerPage][];
		for(int j = 0; j < recipesPerPage; j++)
        {
            items[j] = new ItemInstance[slots.length];
            int k = index + j;
            if(k < recipes.size())
            {
            	ItemInstance[] recipe = (ItemInstance[])recipes.get(k);
            	for (int i = 0; i < recipe.length; i++) {
            		items[j][i] = recipe[i];
            		if (recipe[i] != null && recipe[i].getDamage() == -1) {
                    	if (recipe[i].method_719()) {
                    		if (filter != null && recipe[i].itemId == filter.itemId) {
                    			items[j][i] = new ItemInstance(recipe[i].getType(), 0, filter.getDamage());
                    		}
                    		else {
                    			items[j][i] = new ItemInstance(recipe[i].getType());
                    		}
                    	}
                    	else if (filter != null && recipe[i].itemId == filter.itemId){
                    		items[j][i] = new ItemInstance(recipe[i].getType(), 0, filter.getDamage());
                    	}
                    }
            	}
                if(fuels != null) {
                    items[j][2] = fuels.get(rand.nextInt(fuels.size()));
                }
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

	public void updateRecipes(ItemInstance filter, Boolean getUses) {
		recipes.clear();
		updateRecipesWithoutClear(filter, getUses);
	}
	
	public void updateRecipesWithoutClear(ItemInstance filter, Boolean getUses) {
		lastIndex = 0;
		for (Object obj : recipesComplete.keySet()) {
			int dmg = 0;
			if (filter != null) dmg = filter.getDamage();
			
			ItemInstance output = (ItemInstance)(recipesComplete.get(obj));
			ItemInstance input = null;
			if(obj != null) {
				//fix for nfc 1.8.7
				if(obj instanceof String) {
					String[] string = ((String)obj).split(":");
					obj = Integer.parseInt(string[0]);
					dmg = Integer.parseInt(string[1]);
				}
				else if (obj instanceof ItemInstance) {
					ItemInstance itemInstance = (ItemInstance) obj;
					obj = itemInstance.itemId;
					dmg = itemInstance.getDamage();
				}
				if ((Integer)obj < BlockBase.BY_ID.length) {
					if(BlockBase.BY_ID[(Integer)obj] == null) continue;
					input = new ItemInstance(BlockBase.BY_ID[(Integer)obj], 1, dmg);
				}
				else {
					if((Integer)obj < ItemBase.byId.length)
					input = new ItemInstance(ItemBase.byId[(Integer)obj], 1, dmg);
					//fix for tmim's mods
					else if(damagedFurnaceInput && (Integer)obj - (output.getDamage() << 16) < BlockBase.BY_ID.length){
						if(BlockBase.BY_ID[(Integer)obj - (output.getDamage() << 16)] == null) continue;
						input = new ItemInstance(BlockBase.BY_ID[(Integer)obj - (output.getDamage() << 16)], 1, output.getDamage());
					}
					else continue;
				}
			}
			if(filter == null ||
					(getUses && input != null && input.itemId == filter.itemId ) ||
					(!getUses && output.itemId == filter.itemId && (output.getDamage() == filter.getDamage() || output.getDamage() < 0 || !output.method_719())))
			{
				recipes.add(new ItemInstance[]{output, input});
            }
			
		}
		size = recipes.size();
		super.updateRecipes(filter, getUses);
    	size = recipes.size();
	}

	public ItemInstance getTabItem() {
		return new ItemInstance(tabBlock, 1, metadata);
	}
	
	protected Random rand = new Random();
}
