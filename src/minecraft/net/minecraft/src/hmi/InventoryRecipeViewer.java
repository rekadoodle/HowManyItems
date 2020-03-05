// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src.hmi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import net.minecraft.src.*;
import net.minecraft.src.hmi.tabs.Tab;
import net.minecraft.src.hmi.tabs.TabWithTexture;

// Referenced classes of package net.minecraft.src:
//            IInventory, CraftingManager, ModLoader, ItemStack, 
//            IRecipe, ShapedRecipes, ShapelessRecipes, EntityPlayer

public class InventoryRecipeViewer
    implements IInventory
{
	public InventoryRecipeViewer(ItemStack itemstack)
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
    
    

    public int getSizeInventory()
    {
        return currentTab.recipesPerPage * currentTab.slots.length;
    }

    public ItemStack decrStackSize(int i, int j) { return null; }

    public void setInventorySlotContents(int i, ItemStack itemstack) {  }

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    public String getInvName()
    {
        return String.format("%d / %d", new Object[] {
            getPage() + 1, Integer.valueOf((currentTab.size - 1) / currentTab.recipesPerPage) + 1
        });
    }

    public ItemStack getStackInSlot(int i)
    {
    	if (!filter.isEmpty() && items[i / currentTab.slots.length] != null) {
    		return items[i / currentTab.slots.length][i % currentTab.slots.length];
    	}
    	return null;
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public void onInventoryChanged() { }
    
    public Stack<ItemStack> filter = new Stack<ItemStack>(); 
    public Stack<Tab> prevTabs = new Stack<Tab>(); 
    public Stack<Integer> prevPages = new Stack<Integer>(); 
    public Stack<Boolean> prevGetUses = new Stack<Boolean>();
    public Tab currentTab = mod_HowManyItems.getTabs().get(0);
    
    public Boolean newList = false;
    //protected ItemStack filter;
    public int index;
    public ItemStack items[][];
	

}
