// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.glasslauncher.hmifabric;

import java.util.Stack;

import net.glasslauncher.hmifabric.tabs.Tab;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.item.ItemInstance;

// Referenced classes of package net.minecraft.src:
//            InventoryBase, CraftingManager, ModLoader, ItemInstance, 
//            IRecipe, ShapedRecipes, ShapelessRecipes, EntityPlayer

public class InventoryRecipeViewer implements InventoryBase
{
	public InventoryRecipeViewer(ItemInstance itemstack)
    {
		newList = true;
		//setFilter(null);
		
    }
    
    public void initTab(Tab tab) {
    	currentTab = tab;
    	newList = true;
    	index = setIndex(tab.lastIndex);
    }
    
    
    public void decIndex()
    {
        index = setIndex(index - currentTab.recipesPerPage);
    }

    public void incIndex()
    {
        index = setIndex(index + currentTab.recipesPerPage);
    }
    
    public int getPage() {
    	return index/currentTab.recipesPerPage;
    }

    public int setIndex(int i)
    {
        if(index == i && !newList)
        {
            return i;
        }
        if (!((double)currentTab.size/currentTab.recipesPerPage > 1)) {
        	i = 0;
    	}
        newList = false;
        if(i < 0)
        {
        	if (currentTab.size % currentTab.recipesPerPage != 0)
        		i = currentTab.size - (currentTab.size % currentTab.recipesPerPage);
        	else
        		i = currentTab.size - currentTab.recipesPerPage;
        	if (i == -1)
        		i = 0;
        	if (i == currentTab.size)
        		i = currentTab.size - 1;
        } else
        if(i >= currentTab.size)
        {
            i = 0;
        }
        if(!filter.isEmpty())
        items = currentTab.getItems(i, filter.peek());
        currentTab.lastIndex = i;
        return i;
    }

    @Override
    public int getInventorySize()
    {
        return currentTab.recipesPerPage * currentTab.slots.length;
    }

    @Override
    public ItemInstance takeInventoryItem(int i, int j) { return null; }

    @Override
    public void setInventoryItem(int i, ItemInstance itemstack) {  }

    @Override
    public boolean canPlayerUse(PlayerBase entityplayer)
    {
        return true;
    }

    @Override
    public String getContainerName()
    {
        return String.format("%d / %d", new Object[] {
            getPage() + 1, Integer.valueOf((currentTab.size - 1) / currentTab.recipesPerPage) + 1
        });
    }

    @Override
    public ItemInstance getInventoryItem(int i)
    {
    	if (!filter.isEmpty() && items[i / currentTab.slots.length] != null) {
    		return items[i / currentTab.slots.length][i % currentTab.slots.length];
    	}
    	return null;
    }

    @Override
    public int getMaxItemCount()
    {
        return 64;
    }

    @Override
    public void markDirty() { }
    
    public Stack<ItemInstance> filter = new Stack<>();
    public Stack<Tab> prevTabs = new Stack<>();
    public Stack<Integer> prevPages = new Stack<>();
    public Stack<Boolean> prevGetUses = new Stack<>();
    public Tab currentTab = HowManyItems.getTabs().get(0);
    
    public Boolean newList = false;
    //protected ItemInstance filter;
    public int index;
    public ItemInstance items[][];
	

}
