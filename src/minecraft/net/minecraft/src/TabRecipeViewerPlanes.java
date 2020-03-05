package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;

public class TabRecipeViewerPlanes extends TabRecipeViewerCrafting {

	public TabRecipeViewerPlanes(BaseMod tabCreator) {
		super(tabCreator, 16, "/gui/planeCrafting.png", 128, 56, 24, 16, 92, 46, 5);
		recipesComplete = new ArrayList(CraftingManager.getInstance().getRecipeList());
		for (int i = 0; i < recipesComplete.size(); i++) {
			//Removes recipes for vanilla crafting table
			if(((IRecipe)recipesComplete.get(i)).getRecipeSize() <= 9)
            {
				recipesComplete.remove(i);
				i-=1;
            }
    	}
		slots[0] = new Integer[]{110, 23};
	}

	public Boolean drawSetupRecipeButton(GuiScreen parent, ItemStack[] recipeItems) {
		if (parent instanceof GuiPlaneCrafting) return true;
		return false;
	}
	
	public ItemStack getTabItem() {
		return new ItemStack(mod_Planes.planeWorkbench);
	}
}
