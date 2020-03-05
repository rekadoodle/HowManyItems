package net.minecraft.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TabRecipeViewer {

	//public List recipesComplete;
	//public List recipes;
	public TabRecipeViewer(BaseMod tabCreator, int slotsPerRecipe, String texturePath, int width, int height, int minPaddingX, int minPaddingY, int textureX, int textureY) {
		this(tabCreator, slotsPerRecipe, texturePath, width, height, minPaddingX, minPaddingY, textureX, textureY, 0, 0);
	}
	
	public TabRecipeViewer(BaseMod tabCreator, int slotsPerRecipe, String texturePath, int width, int height, int minPaddingX, int minPaddingY, int textureX, int textureY, int buttonX, int buttonY) {
		slots = new Integer[slotsPerRecipe][];
		TEXTURE_PATH = texturePath;
		WIDTH = width;
		HEIGHT = height;
		MIN_PADDING_X = minPaddingX;
		MIN_PADDING_Y = minPaddingY;
		TEXTURE_X = textureX;
		TEXTURE_Y = textureY;
		BUTTON_POS_X = buttonX;
		BUTTON_POS_Y = buttonY;
		TAB_CREATOR = tabCreator;
	}

	public abstract ItemStack getTabItem();
	public abstract ItemStack[][] getItems(int index, ItemStack filter);
	public abstract int size();
	
	public void updateRecipes(ItemStack filter, Boolean getUses) {
		if (size() == 0 && (getUses
				&& filter.itemID == getTabItem().itemID && filter.getItemDamage() == getTabItem().getItemDamage())){
    		updateRecipes(null, getUses);
    	}
	};
	
	public Boolean drawSetupRecipeButton(GuiScreen parent, ItemStack[] recipeItems) {
		return false;
	}
	
	public Boolean[] itemsInInventory(GuiScreen parent, ItemStack[] recipeItems) {
		return new Boolean[] {true};
	}
	
	public void setupRecipe(GuiScreen parent, ItemStack[] recipeItems) {
		
	}
	
	public final BaseMod TAB_CREATOR;
	
	public int recipesPerPage = 1;
	public Integer[][] slots;
	public Boolean redrawSlots = false;
	public int recipesOnThisPage = 1;
	public int lastIndex = 0;
	
	public int autoX = 1;
	public int autoY = 2;
	
	public final int WIDTH;
	public final int HEIGHT;
	public final int MIN_PADDING_X;
	public final int MIN_PADDING_Y;
	public final String TEXTURE_PATH;
	public final int TEXTURE_X;
	public final int TEXTURE_Y;
	public final int BUTTON_POS_X;
	public final int BUTTON_POS_Y;
}
