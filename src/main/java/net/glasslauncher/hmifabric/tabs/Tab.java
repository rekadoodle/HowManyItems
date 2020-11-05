package net.glasslauncher.hmifabric.tabs;

import java.util.ArrayList;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemInstance;

public abstract class Tab {
	
	public Tab(ClientModInitializer tabCreator, int slotsPerRecipe, int width, int height, int minPaddingX, int minPaddingY) {
		slots = new Integer[slotsPerRecipe][];
		WIDTH = width;
		HEIGHT = height;
		MIN_PADDING_X = minPaddingX;
		MIN_PADDING_Y = minPaddingY;
		TAB_CREATOR = tabCreator;
	}
	
	public abstract ItemInstance getTabItem();
	public abstract ItemInstance[][] getItems(int index, ItemInstance filter);
	public int size;
	
	public void updateRecipes(ItemInstance filter, Boolean getUses) {
		if (size == 0 && getUses){
			for(ItemInstance craftingStation : equivalentCraftingStations) {
				if(filter.itemId == craftingStation.itemId && filter.getDamage() == craftingStation.getDamage()) {
		    		updateRecipes(null, getUses);
		    		break;
				}
			}
    	}
	};
	
	public abstract void draw(int x, int y, int recipeOnThisPageIndex, int cursorX, int cursorY);
	
	public String name() {
		return TranslationStorage.getInstance().method_995(getTabItem().getTranslationKey());
	}
	
	public ArrayList<ItemInstance> equivalentCraftingStations = new ArrayList<>();

	public int index = -2;
	public int recipesPerPage = 1;
	public Boolean redrawSlots = false;
	public int recipesOnThisPage = 1;
	public int lastIndex = 0;
	
	public int autoX = 1;
	public int autoY = 2;
	
	public final ClientModInitializer TAB_CREATOR;
	
	public Integer[][] slots;
	
	public final int WIDTH;
	public final int HEIGHT;
	public final int MIN_PADDING_X;
	public final int MIN_PADDING_Y;

}
