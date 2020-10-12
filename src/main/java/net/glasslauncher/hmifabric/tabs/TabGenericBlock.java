package net.glasslauncher.hmifabric.tabs;

import net.glasslauncher.hmifabric.Utils;
import net.minecraft.block.BlockBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationloader.api.common.mod.StationMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TabGenericBlock extends Tab{

	protected Map recipesComplete;
	protected ArrayList<ItemInstance[]> recipes = new ArrayList<>();
	protected BlockBase tabBlock;
	protected int metadata;
	protected int inputSlots;
	protected int outputSlots;
	protected int slotOffsetX = -1;
	protected int slotOffsetY = -4;
	private String name;
	
	public TabGenericBlock(StationMod tabCreator, Map recipes, BlockBase tabBlock) {
		this(tabCreator, recipes, 1, 1, tabBlock, 0);
	}
	
	public TabGenericBlock(StationMod tabCreator, Map recipes, BlockBase tabBlock, String name) {
		this(tabCreator, recipes, 1, 1, tabBlock, 0);
		this.name = name;
	}
	
	public TabGenericBlock(StationMod tabCreator, Map recipes, BlockBase tabBlock, int metadata) {
		this(tabCreator, recipes, 1, 1, tabBlock, metadata);
	}
	
	public TabGenericBlock(StationMod tabCreator, Map recipes, int inputSlots, int outputSlots, BlockBase tabBlock, String name) {
		this(tabCreator, recipes, inputSlots, outputSlots, tabBlock, 0);
		this.name = name;
	}
	
	public TabGenericBlock(StationMod tabCreator, Map recipes, int inputSlots, int outputSlots, BlockBase tabBlock, int metadata) {
		this(tabCreator, inputSlots, outputSlots, recipes, 140, Math.max(42, Math.max(inputSlots * 18 + 4, outputSlots * 18 + 4)), 3, 3, tabBlock, metadata);
	}
	
	public TabGenericBlock(StationMod tabCreator, int inputSlots, int outputSlots, Map recipes, int width, int height, int minPaddingX, int minPaddingY, BlockBase tabBlock, int metadata) {
		super(tabCreator, inputSlots + outputSlots, width, height, minPaddingX, minPaddingY);
		this.tabBlock = tabBlock;
		this.metadata = metadata;
		this.inputSlots = inputSlots;
		this.outputSlots = outputSlots;
		this.recipesComplete = recipes;
		
		int inputSlotsHeightOffset = 9 * inputSlots;
		int outputSlotsHeightOffset = 9 * outputSlots;
		for (int i = 0; i < inputSlots; i++) {
			slots[i] = new Integer[]{2, HEIGHT / 2 - inputSlotsHeightOffset + i * 18 - slotOffsetY};
		}
		for (int i = 0; i < outputSlots; i++) {
			slots[i + inputSlots] = new Integer[]{WIDTH - slotOffsetX - 19, HEIGHT / 2 - outputSlotsHeightOffset + i * 18 - slotOffsetY};
		}
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
		lastIndex = 0;
		recipes.clear();
		for (Object obj : recipesComplete.keySet()) {
			boolean addRecipe = false;
			ItemInstance[] inputs; 
			if(obj instanceof ItemInstance[]) {
				inputs = (ItemInstance[])obj;
			}
			else {
				inputs = new ItemInstance[] {(ItemInstance)obj};
			}
			ItemInstance[] outputs;
			if(recipesComplete.get(obj) instanceof ItemInstance[]) {
				outputs = (ItemInstance[])recipesComplete.get(obj);
			}
			else {
				outputs = new ItemInstance[] {(ItemInstance)recipesComplete.get(obj)};
			}
			if(filter == null) {
				addRecipe = true;
			}
			else if (getUses) {
				for(int i = 0; i < inputs.length; i++) {
					if(inputs[i].itemId == filter.itemId && (inputs[i].getDamage() == filter.getDamage() || inputs[i].getDamage() < 0 || !inputs[i].method_719())) {
						addRecipe = true;
						break;
					}
				}
			}
			else {
				for(int i = 0; i < outputs.length; i++) {
					if(outputs[i].itemId == filter.itemId && (outputs[i].getDamage() == filter.getDamage() || outputs[i].getDamage() < 0 || !outputs[i].method_719())) {
						addRecipe = true;
						break;
					}
				}
			}
			if(addRecipe) {
				ItemInstance[] recipe = Arrays.copyOf(inputs, inputSlots + outputSlots);
				System.arraycopy(outputs, 0, recipe, inputSlots, outputs.length);
				recipes.add(recipe);
			}
			
		}
    	size = recipes.size();
		super.updateRecipes(filter, getUses);
		size = recipes.size();
	}

	public ItemInstance getTabItem() {
		return new ItemInstance(tabBlock, 1, metadata);
	}
	
	public ItemInstance getBlockToDraw() {
		return getTabItem();
	}

	public void draw(int x, int y, int recipeOnThisPageIndex, int cursorX, int cursorY) {
		drawSlotsAndArrows(x, y);
		Utils.drawScaledItem(getBlockToDraw(), x + WIDTH / 2 - 20, y + HEIGHT / 2 - 19, 40);
	}
	
	protected void drawSlotsAndArrows(int x, int y) {
		Utils.bindTexture();
		for (Integer[] slotCoords: slots) {
			Utils.drawSlot(x + slotCoords[0] + slotOffsetX, y + slotCoords[1] + slotOffsetY);
		}
		Utils.drawArrow(x + 23, y + HEIGHT / 2 - 12);
		Utils.drawArrow(x + 92, y + HEIGHT / 2 - 12);
	}
	
	public String name() {
		if(name != null) {
			return name;
		}
		return super.name();
	}


}
