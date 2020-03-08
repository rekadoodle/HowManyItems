package net.minecraft.src.hmi.references.aether;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.src.*;
import net.minecraft.src.hmi.tabs.TabSmelting;

public class TabAether extends TabSmelting {

	private List recipesComplete;
	
	public TabAether(BaseMod tabCreator, Class class1, ArrayList<ItemStack> fuels, String texturePath, Block tabBlock) {
		super(tabCreator, null, fuels, texturePath, tabBlock);
		try {
			recipesComplete = (List)ModLoader.getPrivateValue(class1, null, 0);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public ItemStack[][] getItems(int index, ItemStack filter) {
		ItemStack[][] items = super.getItems(index, filter);
		for(ItemStack[] recipe: items) {
			if(recipe == null || recipe[0] == null) {
				continue;
			}
			for (Object obj : recipesComplete) {
				if (obj instanceof Enchantment) {
					Enchantment enchant = (Enchantment)obj;
					if(recipe[0].isItemEqual(enchant.enchantTo) && recipe[1].isItemEqual(enchant.enchantFrom)) {
						recipe[2] = new ItemStack(recipe[2].getItem(), (int)Math.ceil((double)enchant.enchantPowerNeeded / 500));
						break;
					}
				}
				else if (obj instanceof Frozen) {
					Frozen froze = (Frozen)obj;
					if(recipe[0].isItemEqual(froze.frozenTo) && recipe[1].isItemEqual(froze.frozenFrom)) {
						recipe[2] = new ItemStack(recipe[2].getItem(), (int)Math.ceil((double)froze.frozenPowerNeeded / 500));
						break;
					}
				}
			}
		}
		return items;
	}
	
	public void updateRecipes(ItemStack filter, Boolean getUses) {
		recipes.clear();
		lastIndex = 0;
		for (Object obj : recipesComplete) {
			ItemStack input = null;
			ItemStack output = null;
			
			if (obj instanceof Enchantment) {
				input = ((Enchantment)obj).enchantFrom;
				output = ((Enchantment)obj).enchantTo;
			}
			else if (obj instanceof Frozen) {
				input = ((Frozen)obj).frozenFrom;
				output = ((Frozen)obj).frozenTo;
			}
			if (filter != null) input.setItemDamage(filter.getItemDamage());
			
			if(filter == null ||
					(getUses && input.itemID == filter.itemID && (output.getItemDamage() == filter.getItemDamage() || output.getItemDamage() < 0 || !output.getHasSubtypes())) ||
					(!getUses && output.itemID == filter.itemID && (output.getItemDamage() == filter.getItemDamage() || output.getItemDamage() < 0 || !output.getHasSubtypes())))
			{
				recipes.add(new ItemStack[]{output, input});
            }
			
		}
		if (recipes.size() == 0 && ( getUses
				&& filter.itemID == getTabItem().itemID && filter.getItemDamage() == getTabItem().getItemDamage())){
    		updateRecipes(null, getUses);
		}
		size = recipes.size();
	}
	
}
