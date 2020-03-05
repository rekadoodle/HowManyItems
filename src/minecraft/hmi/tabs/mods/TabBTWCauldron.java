package hmi.tabs.mods;

import java.util.Map;

import hmi.Utils;
import net.minecraft.src.*;

public class TabBTWCauldron extends TabGenericBlock {
	
	private Map stoked;

	public TabBTWCauldron(BaseMod tabCreator, Map unstokedRecipes, Map stokedRecipes, Block block, int inputSlots, String name) {
		super(tabCreator, null, inputSlots, 1, block, name);
		recipesComplete = unstokedRecipes;
		stoked = stokedRecipes; 
		if(recipesComplete != null) {
			recipesComplete.put(new ItemStack[] {new ItemStack(mod_FCBetterThanWolves.fcDung), new ItemStack(mod_FCBetterThanWolves.fcWolfRaw)}, new ItemStack(mod_FCBetterThanWolves.fcFoulFood));
			recipesComplete.putAll(stoked);
		}
		else recipesComplete = stoked;
	}
	
	private boolean[] isRecipeStoked;
	
	public ItemStack[][] getItems(int index, ItemStack filter) {
		ItemStack[][] items = super.getItems(index, filter);
		updateStokedRecipesOnPage(items);
		return items;
	}
	
	private void updateStokedRecipesOnPage(ItemStack[][] items) {
		isRecipeStoked = new boolean[items.length];
		for(int i = 0; i < items.length; i++) {
			isRecipeStoked[i] = isRecipeStoked(items[i]);
		}
	}
	
	
	private boolean isRecipeStoked(ItemStack[] recipe) {
		recipeLoop: 
		for (Object obj : stoked.keySet()) {
			ItemStack[] inputs; 
			if(obj instanceof ItemStack[]) {
				inputs = (ItemStack[])obj;
			}
			else {
				inputs = new ItemStack[] {(ItemStack)obj};
			}
			ItemStack[] outputs;
			if(stoked.get(obj) instanceof ItemStack[]) {
				outputs = (ItemStack[])stoked.get(obj);
			}
			else {
				outputs = new ItemStack[] {(ItemStack)stoked.get(obj)};
			}
			for(int i = 0; i < inputs.length; i++) {
				if(inputs[i] == null) continue;
				if(recipe[i] == null || inputs[i].itemID != recipe[i].itemID) {
					continue recipeLoop;
				}
			}
			for(int i = 0; i < outputs.length; i++) {
				if(outputs[i] == null) continue;
				if(recipe[i + inputSlots] == null || outputs[i].itemID != recipe[i + inputSlots].itemID) {
					continue recipeLoop;
				}
			}
			return true;
		}
		return false;
	}

	public void draw(int x, int y, int recipeOnThisPageIndex, int cursorX, int cursorY) {
		drawSlotsAndArrows(x, y);
		Utils.drawScaledItem(getBlockToDraw(), x + WIDTH / 2 - 14, y + HEIGHT / 2 - 17, 31);
		if(recipeOnThisPageIndex < isRecipeStoked.length && isRecipeStoked[recipeOnThisPageIndex]) {
			Utils.drawScaledItem(new ItemStack(mod_FCBetterThanWolves.fcBellows), x + WIDTH / 2 - 8, y + HEIGHT / 2 + 8, 16);
			if(cursorX > x + WIDTH / 2 - 9 && cursorX < x + WIDTH / 2 + 10
					&& cursorY > y + HEIGHT / 2 - 15 && cursorY < y + HEIGHT / 2 + 22) {
				Utils.drawTooltip("Must be stoked with bellows", x - 10, y);
			}
		}
	}
}
