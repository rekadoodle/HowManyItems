package hmi.tabs.mods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import hmi.Utils;
import hmi.tabs.Tab;
import net.minecraft.src.*;

public class TabGenericBlock extends Tab{

	protected Map recipesComplete;
	protected ArrayList<ItemStack[]> recipes = new ArrayList<ItemStack[]>();
	protected Block tabBlock;
	protected int metadata;
	protected int inputSlots;
	protected int outputSlots;
	protected int slotOffsetX = -1;
	protected int slotOffsetY = -4;
	private String name;
	
	public TabGenericBlock(BaseMod tabCreator, Map recipes, Block tabBlock) {
		this(tabCreator, recipes, 1, 1, tabBlock, 0);
	}
	
	public TabGenericBlock(BaseMod tabCreator, Map recipes, Block tabBlock, String name) {
		this(tabCreator, recipes, 1, 1, tabBlock, 0);
		this.name = name;
	}
	
	public TabGenericBlock(BaseMod tabCreator, Map recipes, Block tabBlock, int metadata) {
		this(tabCreator, recipes, 1, 1, tabBlock, metadata);
	}
	
	public TabGenericBlock(BaseMod tabCreator, Map recipes, int inputSlots, int outputSlots, Block tabBlock, String name) {
		this(tabCreator, recipes, inputSlots, outputSlots, tabBlock, 0);
		this.name = name;
	}
	
	public TabGenericBlock(BaseMod tabCreator, Map recipes, int inputSlots, int outputSlots, Block tabBlock, int metadata) {
		this(tabCreator, inputSlots, outputSlots, recipes, 140, Math.max(42, Math.max(inputSlots * 18 + 4, outputSlots * 18 + 4)), 3, 3, tabBlock, metadata);
	}
	
	public TabGenericBlock(BaseMod tabCreator, int inputSlots, int outputSlots, Map recipes, int width, int height, int minPaddingX, int minPaddingY, Block tabBlock, int metadata) {
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
            		if (recipe[i] != null && recipe[i].getItemDamage() == -1) {
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
		lastIndex = 0;
		recipes.clear();
		for (Object obj : recipesComplete.keySet()) {
			boolean addRecipe = false;
			ItemStack[] inputs; 
			if(obj instanceof ItemStack[]) {
				inputs = (ItemStack[])obj;
			}
			else {
				inputs = new ItemStack[] {(ItemStack)obj};
			}
			ItemStack[] outputs;
			if(recipesComplete.get(obj) instanceof ItemStack[]) {
				outputs = (ItemStack[])recipesComplete.get(obj);
			}
			else {
				outputs = new ItemStack[] {(ItemStack)recipesComplete.get(obj)};
			}
			if(filter == null) {
				addRecipe = true;
			}
			else if (getUses) {
				for(int i = 0; i < inputs.length; i++) {
					if(inputs[i].itemID == filter.itemID && (inputs[i].getItemDamage() == filter.getItemDamage() || inputs[i].getItemDamage() < 0 || !inputs[i].getHasSubtypes())) {
						addRecipe = true;
						break;
					}
				}
			}
			else {
				for(int i = 0; i < outputs.length; i++) {
					if(outputs[i].itemID == filter.itemID && (outputs[i].getItemDamage() == filter.getItemDamage() || outputs[i].getItemDamage() < 0 || !outputs[i].getHasSubtypes())) {
						addRecipe = true;
						break;
					}
				}
			}
			if(addRecipe) {
				ItemStack[] recipe = Arrays.copyOf(inputs, inputSlots + outputSlots);
				System.arraycopy(outputs, 0, recipe, inputSlots, outputs.length);
				recipes.add(recipe);
			}
			
		}
    	size = recipes.size();
		super.updateRecipes(filter, getUses);
		size = recipes.size();
	}

	public ItemStack getTabItem() {
		return new ItemStack(tabBlock, 1, metadata);
	}
	
	public ItemStack getBlockToDraw() {
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
