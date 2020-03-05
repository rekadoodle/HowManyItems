package hmi.tabs.mods;

import java.util.ArrayList;
import java.util.HashMap;

import hmi.Utils;
import hmi.tabs.TabSmelting;
import ic2.TileEntityRecycler;
import net.minecraft.src.*;

public class TabIC2Recycler extends TabSmelting {

	public TabIC2Recycler(BaseMod tabCreator, ArrayList<ItemStack> fuels, String texturePath, Block tabBlock, int metadata) {
		super(tabCreator, 3, new HashMap(), fuels, texturePath, 84, 56, 54, 15, tabBlock, metadata);
		recipesComplete.put(null, new ItemStack(mod_IC2.itemScrap));
	}

	public ItemStack[][] getItems(int index, ItemStack filter) {
		ItemStack[][] items = super.getItems(index, filter);
		for(ItemStack[] recipe: items) {
			if(recipe == null || recipe[0] == null) {
				continue;
			}
			while(true) {
				int i = Utils.rand.nextInt(Utils.itemList().size());
				if(tileentityrecycler.canRecycle(Utils.itemList().get(i))) {
					recipe[1] = Utils.itemList().get(i);
					break;
				}
			}
		}
		return items;
	}
	
	public void draw(int x, int y, int recipeOnThisPageIndex, int cursorX, int cursorY) {
		super.draw(x, y, recipeOnThisPageIndex, cursorX, cursorY);
		Utils.drawString((int)(100.0/TileEntityRecycler.recycleChance()) + "% chance", x + WIDTH / 2 - 19, y + HEIGHT - 2);
	}
	
	private TileEntityRecycler tileentityrecycler = new TileEntityRecycler();
}
