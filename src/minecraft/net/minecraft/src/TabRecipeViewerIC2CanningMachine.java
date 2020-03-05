package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TabRecipeViewerIC2CanningMachine extends TabRecipeViewerFurnace {

	public TabRecipeViewerIC2CanningMachine(BaseMod tabCreator, ArrayList<ItemStack> fuels, String texturePath, Block tabBlock, int metadata) {
		super(tabCreator, 4, new HashMap(), fuels, texturePath, 113, 56, 28, 15, tabBlock, metadata);
		recipesComplete.put(mod_IC2.itemFuelCanEmpty.shiftedIndex, new ItemStack(mod_IC2.itemFuelCan));
		recipesComplete.put(mod_IC2.itemArmorJetpack.shiftedIndex, new ItemStack(mod_IC2.itemArmorJetpack));
		for(Item item: Item.itemsList) {
			int i;
			if(item != null && item instanceof ItemFood 
					&& (i = (int)Math.ceil((double)((ItemFood)item).getHealAmount() / 2D)) > 0) 
				recipesComplete.put(item.shiftedIndex,  new ItemStack(mod_IC2.itemTinCanFilled, 1, i));
		}
		slots[0] = new Integer[]{91, 23};
		slots[1] = new Integer[]{41, 5};
		slots[2] = new Integer[]{2, 33};
		slots[3] = new Integer[]{41, 41};
	}

	public ItemStack[][] getItems(int index, ItemStack filter) {
		ItemStack[][] items = super.getItems(index, filter);
		for(ItemStack[] recipe: items) {
			if(recipe == null || recipe[0] == null) {
				continue;
			}
			if (recipe[0].getItem().shiftedIndex == mod_IC2.itemFuelCan.shiftedIndex
					|| recipe[0].getItem().shiftedIndex == mod_IC2.itemArmorJetpack.shiftedIndex) {
				if (filterCellCoal)
					recipe[3] = new ItemStack(mod_IC2.itemCellCoalRef, 6);
				else if (filterCellBio)
					recipe[3] = new ItemStack(mod_IC2.itemCellBioRef, 6);
				else if (rand.nextBoolean())
					recipe[3] = new ItemStack(mod_IC2.itemCellCoalRef, 6);
				else
					recipe[3] = new ItemStack(mod_IC2.itemCellBioRef, 6);
			}
			else if (recipe[0].getItem().shiftedIndex == mod_IC2.itemTinCanFilled.shiftedIndex){
				recipe[3] = new ItemStack(mod_IC2.itemTinCan, recipe[0].stackSize);
			}
		}
		return items;
	}
	
	private Boolean filterCellCoal;
	private Boolean filterCellBio;

	public void updateRecipes(ItemStack filter, Boolean getUses) {
		recipes.clear();
		filterCellCoal = false;
		filterCellBio = false;
		if (filter != null && filter.getItem().shiftedIndex == mod_IC2.itemCellCoalRef.shiftedIndex) {
			filterCellCoal = true;
			recipes.add(new ItemStack[]{new ItemStack(mod_IC2.itemFuelCan), new ItemStack(mod_IC2.itemFuelCanEmpty)});
			recipes.add(new ItemStack[]{new ItemStack(mod_IC2.itemArmorJetpack), new ItemStack(mod_IC2.itemArmorJetpack)});
		}
		else if (filter != null && filter.getItem().shiftedIndex == mod_IC2.itemCellBioRef.shiftedIndex) {
			filterCellBio = true;
			recipes.add(new ItemStack[]{new ItemStack(mod_IC2.itemFuelCan), new ItemStack(mod_IC2.itemFuelCanEmpty)});
			recipes.add(new ItemStack[]{new ItemStack(mod_IC2.itemArmorJetpack), new ItemStack(mod_IC2.itemArmorJetpack)});
		}
		super.updateRecipesWithoutClear(filter, getUses);
	}
}
