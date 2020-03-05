package hmi.tabs;

import java.util.ArrayList;

import net.minecraft.src.*;

public abstract class Tab {
	
	public Tab(BaseMod tabCreator, int slotsPerRecipe, int width, int height, int minPaddingX, int minPaddingY) {
		slots = new Integer[slotsPerRecipe][];
		WIDTH = width;
		HEIGHT = height;
		MIN_PADDING_X = minPaddingX;
		MIN_PADDING_Y = minPaddingY;
		TAB_CREATOR = tabCreator;
	}
	
	public abstract ItemStack getTabItem();
	public abstract ItemStack[][] getItems(int index, ItemStack filter);
	public int size;
	
	public void updateRecipes(ItemStack filter, Boolean getUses) {
		if (size == 0 && getUses){
			for(ItemStack craftingStation : equivalentCraftingStations) {
				if(filter.itemID == craftingStation.itemID && filter.getItemDamage() == craftingStation.getItemDamage()) {
		    		updateRecipes(null, getUses);
		    		break;
				}
			}
    	}
	};
	
	public abstract void draw(int x, int y, int recipeOnThisPageIndex, int cursorX, int cursorY);
	
	public String name() {
		return StringTranslate.getInstance().translateNamedKey(getTabItem().getItemName()).toString().trim();
	}
	
	public ArrayList<ItemStack> equivalentCraftingStations = new ArrayList<ItemStack>();

	public int index = -2;
	public int recipesPerPage = 1;
	public Boolean redrawSlots = false;
	public int recipesOnThisPage = 1;
	public int lastIndex = 0;
	
	public int autoX = 1;
	public int autoY = 2;
	
	public final BaseMod TAB_CREATOR;
	
	public Integer[][] slots;
	
	public final int WIDTH;
	public final int HEIGHT;
	public final int MIN_PADDING_X;
	public final int MIN_PADDING_Y;

}
