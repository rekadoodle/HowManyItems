package net.minecraft.src.hmi.references.planes;

import java.util.ArrayList;

import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.tabs.TabCrafting;

public class ConcreteHandler extends TabHandler {

	@Override
	public void loadTabs(BaseMod basemod) {
		ArrayList planeRecipes = new ArrayList(CraftingManager.getInstance().getRecipeList());
		for (int i = 0; i < planeRecipes.size(); i++) {
			//Removes recipes for vanilla crafting table
			if(((IRecipe)planeRecipes.get(i)).getRecipeSize() <= 9)
            {
				planeRecipes.remove(i);
				i-=1;
            }
    	}
		TabCrafting planeTab = new TabCrafting(basemod, 16, planeRecipes, mod_Planes.planeWorkbench, "/gui/planeCrafting.png", 128, 56, 24, 16, 92, 46, 5);
		planeTab.slots[0] = new Integer[]{110, 23};
		planeTab.guiCraftingStations.add(GuiPlaneCrafting.class);
		mod_HowManyItems.addTab(planeTab);
		mod_HowManyItems.addGuiToBlock(GuiPlaneCrafting.class, new ItemStack(mod_Planes.planeWorkbench));
	}
}