package net.minecraft.src.hmi.tabs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.hmi.TabUtils;
import net.minecraft.src.hmi.Utils;

public class TabCrafting extends TabWithTexture {

	protected List recipesComplete;
	protected List recipes;
	private int slotsWidth;
	protected BaseMod mod;
	private Block tabBlock;
	private boolean isVanillaWorkbench = false; //THIS IS LAZY
	public ArrayList<Class<? extends GuiContainer>> guiCraftingStations = new ArrayList<Class<? extends GuiContainer>>();
	
	public TabCrafting(BaseMod tabCreator) {
		this(tabCreator, new ArrayList(CraftingManager.getInstance().getRecipeList()), Block.workbench);
		for (int i = 0; i < recipesComplete.size(); i++) {
			//Removes recipes that are too big and ruin everything @flans mod
			if(((IRecipe)recipesComplete.get(i)).getRecipeSize() > 9)
            {
				recipesComplete.remove(i);
				i-=1;
            }
    	}
		isVanillaWorkbench = true;
		guiCraftingStations.add(GuiCrafting.class);
	}
	
	public TabCrafting(BaseMod tabCreator, List recipesComplete, Block tabBlock) {
		this(tabCreator, 10, recipesComplete, tabBlock, "/gui/crafting.png", 118, 56, 28, 15, 56, 46, 3);
		slots[0] = new Integer[]{96, 23};
	}

	public TabCrafting(BaseMod tabCreator, int slotsPerRecipe, List recipesComplete, Block tabBlock, String texturePath, int width, int height, int textureX, int textureY, int buttonX, int buttonY, int slotsWidth) {
		super(tabCreator, slotsPerRecipe, texturePath, width, height, 3, 4, textureX, textureY, buttonX, buttonY);
		this.slotsWidth = slotsWidth;
		this.recipesComplete = recipesComplete;
		this.tabBlock = tabBlock;
		recipes = recipesComplete;
		int i = 1;
		for(int l = 0; l < 3; l++) {
			for(int i1 = 0; i1 < slotsWidth; i1++) {
				slots[i++] = new Integer[]{2 + i1 * 18, 5 + l * 18};
			}
		}
		equivalentCraftingStations.add(getTabItem());
	}
	
	public ItemStack[][] getItems(int index, ItemStack filter) {
		ItemStack[][] items = new ItemStack[recipesPerPage][];
		for(int j = 0; j < recipesPerPage; j++)
        {
            items[j] = new ItemStack[slots.length];
            int k = index + j;
            if(k < recipes.size())
            {
                IRecipe irecipe = (IRecipe)recipes.get(k);
                try
                {
                    if(irecipe instanceof ShapedRecipes)
                    {
                        int l = ((Integer)ModLoader.getPrivateValue(net.minecraft.src.ShapedRecipes.class, (ShapedRecipes)irecipe, 0)).intValue();
                        ItemStack aitemstack[] = (ItemStack[])ModLoader.getPrivateValue(net.minecraft.src.ShapedRecipes.class, (ShapedRecipes)irecipe, 2);
                        items[j][0] = irecipe.getRecipeOutput();
                        for(int k1 = 0; k1 < aitemstack.length; k1++)
                        {
                        	if (aitemstack[k1] != null && aitemstack[k1].stackSize > 1) aitemstack[k1].stackSize = 1;
                            int l1 = k1 % l;
                            int i2 = k1 / l;
                            items[j][l1 + i2 * slotsWidth + 1] = aitemstack[k1];
                            if (aitemstack[k1] != null && aitemstack[k1].getItemDamage() == -1) {
                            	if (aitemstack[k1].getHasSubtypes()) {
                            		if (filter != null && aitemstack[k1].itemID == filter.itemID) {
                            			items[j][l1 + i2 * slotsWidth + 1] = new ItemStack(aitemstack[k1].getItem(), 0, filter.getItemDamage());
                            		}
                            		else {
                            			items[j][l1 + i2 * slotsWidth + 1] = new ItemStack(aitemstack[k1].getItem());
                            		}
                            	}
                            	else if (filter != null && aitemstack[k1].itemID == filter.itemID){
                            		items[j][l1 + i2 * slotsWidth + 1] = new ItemStack(aitemstack[k1].getItem(), 0, filter.getItemDamage());
                            	}
                            }
                        }

                    } else
                    if(irecipe instanceof ShapelessRecipes)
                    {
                        List list = (List)ModLoader.getPrivateValue(net.minecraft.src.ShapelessRecipes.class, (ShapelessRecipes)irecipe, 1);
                        items[j][0] = irecipe.getRecipeOutput();
                        for(int j1 = 0; j1 < list.size(); j1++)
                        {
                        	ItemStack item = (ItemStack)list.get(j1);
                            items[j][j1 + 1] = item;
                            if (item != null && item.getItemDamage() == -1) {
                            	if (item.getHasSubtypes()) {
                            		if (filter != null && item.itemID == filter.itemID) {
                            			items[j][j1 + 1] = new ItemStack(item.getItem(), 0, filter.getItemDamage());
                            		}
                            		else {
                            			items[j][j1 + 1] = new ItemStack(item.getItem());
                            		}
                            	}
                            	else if (filter != null && item.itemID == filter.itemID){
                            		items[j][j1 + 1] = new ItemStack(item.getItem(), 0, filter.getItemDamage());
                            	}
                            }
                        }

                    }
                }
                catch(Throwable throwable)
                {
                    ModLoader.getLogger().throwing("RecipeInventory", "setIndex", throwable);
                    ModLoader.ThrowException("Exception in RecipeInventory", throwable);
                }
            }

            if(items[j][0] == null && recipesOnThisPage > j) {
            	recipesOnThisPage = j;
                redrawSlots = true;
                break;
            }
            if(items[j][0] != null && recipesOnThisPage == j) {
            	recipesOnThisPage = j+1;
                redrawSlots = true;
            } 
        }
		return items;
	}

	
	public void updateRecipes(ItemStack filter, Boolean getUses) {
		List arraylist = new ArrayList();
    	if (filter == null) {
    		recipes = recipesComplete;
    	}
    	else {
    	for(Iterator iterator = recipesComplete.iterator(); iterator.hasNext();)
        {
            IRecipe irecipe = (IRecipe)iterator.next();
           if(!getUses && filter.itemID == irecipe.getRecipeOutput().itemID && (irecipe.getRecipeOutput().getItemDamage() == filter.getItemDamage() || irecipe.getRecipeOutput().getItemDamage() < 0 || !irecipe.getRecipeOutput().getHasSubtypes() ))
            //if(itemstack.itemID == irecipe.getRecipeOutput().itemID && ( (irecipe.getRecipeOutput().getItemDamage() == itemstack.getItemDamage() || !irecipe.getRecipeOutput().getHasSubtypes()) )|| irecipe.getRecipeOutput().getItemDamage() < 0)
            {
                arraylist.add(irecipe);
                continue;
            } 
           else if(irecipe instanceof ShapedRecipes && getUses)
            {
                ShapedRecipes shapedrecipes = (ShapedRecipes)irecipe;
                try
                {
                    ItemStack aitemstack[] = (ItemStack[])ModLoader.getPrivateValue(net.minecraft.src.ShapedRecipes.class, (ShapedRecipes)irecipe, 2);
                    ItemStack aitemstack1[];
                    int j = (aitemstack1 = aitemstack).length;
                    for(int i = 0; i < j; i++)
                    {
                        ItemStack itemstack1 = aitemstack1[i];
                        if(itemstack1 == null || filter.itemID != itemstack1.itemID || (itemstack1.getHasSubtypes() && itemstack1.getItemDamage() != filter.getItemDamage()) && itemstack1.getItemDamage() >= 0)
                        {
                            continue;
                        }
                        arraylist.add(irecipe);
                        break;
                    }

                }
                catch(Exception exception)
                {
                   exception.printStackTrace();
                }
            } 
            else if(irecipe instanceof ShapelessRecipes && getUses)
            {
                ShapelessRecipes shapelessrecipes = (ShapelessRecipes)irecipe;
                try
                {
                    List list = (List)ModLoader.getPrivateValue(net.minecraft.src.ShapelessRecipes.class, (ShapelessRecipes)irecipe, 1);
                    for(Iterator iterator1 = list.iterator(); iterator1.hasNext();)
                    {
                        Object obj = iterator1.next();
                        ItemStack itemstack2 = (ItemStack)obj;
                        if(filter.itemID == itemstack2.itemID && (itemstack2.getItemDamage() == filter.getItemDamage() || itemstack2.getItemDamage() < 0 || !itemstack2.getHasSubtypes()))
                        {
                            arraylist.add(irecipe);
                            break;
                        }
                    }

                }
                catch(Exception exception)
                    {
                        exception.printStackTrace();
                    }
                
            }
        }
        recipes = arraylist;
    	}
    	size = recipes.size();
    	super.updateRecipes(filter, getUses);
    	size = recipes.size();
	}

	public ItemStack getTabItem() {
		return new ItemStack(tabBlock);
	}
	
	public Boolean drawSetupRecipeButton(GuiScreen parent, ItemStack[] recipeItems) {
		for(Class<? extends GuiContainer> gui : guiCraftingStations) {
			if(gui.isInstance(parent)) return true;
		}
		if (isVanillaWorkbench && (parent == null || isInv(parent))) {
			for (int i = 3; i < 10; i++) {
				if (i != 4 && i != 5 && recipeItems[i] != null)
					return false;
			}
			return true;
		}
		return false;
	}
	
	public Boolean[] itemsInInventory(GuiScreen parent, ItemStack[] recipeItems) {
		Boolean[] itemsInInv = new Boolean[slots.length - 1];
		List list;
		if (parent instanceof GuiContainer)
			list = ((GuiContainer)parent).inventorySlots.slots;
		else
			list = Utils.mc.thePlayer.inventorySlots.slots;
        ItemStack aslot[] = new ItemStack[list.size()];
        for(int i = 0; i < list.size(); i++)
        {
        	if(((Slot)list.get(i)).getHasStack())
            aslot[i] = ((Slot)list.get(i)).getStack().copy();
        }
        
        aslot[0] = null;
        recipe:
        for (int i = 1; i < recipeItems.length; i++) {
        	ItemStack item = recipeItems[i];
        	if (item == null) {
        		itemsInInv[i - 1] = true;
        		continue;
        	}
        	
        	for (ItemStack slot : aslot) {
        		if (slot != null && slot.stackSize > 0 && slot.itemID == item.itemID && (slot.getItemDamage() == item.getItemDamage() || item.getItemDamage() < 0 || !item.getHasSubtypes())) {
        			slot.stackSize -= 1;
        			itemsInInv[i - 1] = true;
        			continue recipe;
        		}
        	}
        	itemsInInv[i - 1] = false;
    	}
		return itemsInInv;
	}
	
	private int recipeStackSize(List list, ItemStack[] recipeItems) {
        
        int[] itemStackSize = new int[recipeItems.length - 1];
        
        for (int i = 1; i < recipeItems.length; i++) {
        	ItemStack aslot[] = new ItemStack[list.size()];
            for(int k = 0; k < list.size(); k++)
            {
            	if(((Slot)list.get(k)).getHasStack())
                aslot[k] = ((Slot)list.get(k)).getStack().copy();
            }
            aslot[0] = null;
            
        	ItemStack item = recipeItems[i];
        	itemStackSize[i - 1] = 0;
        	if (item == null) {
        		itemStackSize[i - 1] = -1;
        		continue;
        	}
        	int stackSize = 0;
        	for (ItemStack slot : aslot) {
        		if (slot != null && slot.stackSize > 0 && slot.itemID == item.itemID && (slot.getItemDamage() == item.getItemDamage() || item.getItemDamage() < 0 || !item.getHasSubtypes())) {
        			stackSize += slot.stackSize;
        			slot.stackSize = 0;
        		}
        	}
        	int prevEqualItemCount = 1;
        	for (int j = 1; j < i; j++) {
                if(recipeItems[j] != null && recipeItems[j].isItemEqual(item)) {
                   	prevEqualItemCount++;
                }
            }
        	for (int j = 1; j < recipeItems.length; j++) {
                if(recipeItems[j] != null && recipeItems[j].isItemEqual(item)) {
                	itemStackSize[j - 1] = stackSize / prevEqualItemCount;
                }
            }
    	}
        int finalItemStackSize = -1;
        for (int i = 0; i < itemStackSize.length; i++) {
        	ItemStack item = recipeItems[i + 1];
        	if(itemStackSize[i] == -1 || item.getMaxStackSize() == 1) continue;
        	if(finalItemStackSize == -1 ||itemStackSize[i] < finalItemStackSize) {
        		finalItemStackSize = itemStackSize[i];
        	}
    	}
        if(finalItemStackSize > 0) return finalItemStackSize;
		return 1;
	}

	public void setupRecipe(GuiScreen parent, ItemStack[] recipeItems) {
		if (parent == null) {
			Utils.mc.setIngameNotInFocus();
			ScaledResolution scaledresolution = new ScaledResolution(Utils.mc.gameSettings, Utils.mc.displayWidth, Utils.mc.displayHeight);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
			if(TabUtils.aetherHandler != null) {
				parent = TabUtils.aetherHandler.newInv(Utils.mc.thePlayer);
			}
			else {
				parent = new GuiInventory(Utils.mc.thePlayer);
			}
			Utils.mc.currentScreen = parent;
			parent.setWorldAndResolution(Utils.mc, i, j);
			Utils.mc.skipRenderWorld = false;
		}
		GuiContainer container = ((GuiContainer)parent);
		List<?> inventorySlots = container.inventorySlots.slots;
		
		int recipeStackSize = 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			recipeStackSize = recipeStackSize(inventorySlots, recipeItems);
		}
		
		this.player = Utils.mc.thePlayer;
		this.inv = Utils.mc.playerController;
		this.windowId = container.inventorySlots.windowId;
        for(int recipeSlotIndex = 1; recipeSlotIndex < recipeItems.length; recipeSlotIndex++) {
        	if (isInv(parent) && recipeSlotIndex > 5)
        		break;
        	int slotid = recipeSlotIndex;
        	if (isInv(parent) && recipeSlotIndex > 3) {
        		slotid--;
        	}
        	Slot recipeSlot = (Slot)inventorySlots.get(slotid);
        	//clear recipe slot
        	if(recipeSlot.getHasStack()) {
        		this.clickSlot(slotid, true, true);
        		
        		if (recipeSlot.getHasStack()) {
        			this.clickSlot(slotid, true, false);
        			if (player.inventory.getItemStack() != null) {
        				for (int j = slotid + 1; j < inventorySlots.size(); j++) {
        					Slot slot = (Slot)inventorySlots.get(j);
        					if (!slot.getHasStack()) {
        						this.clickSlot(j, true, false);
        						break;
        					}
        				}
        				if (player.inventory.getItemStack() != null) {
        					this.clickSlot(-999, true, false);
        				}
        			}
        		}
        	}
        	
        	//if recipe slot should be empty, continue
        	ItemStack item = recipeItems[recipeSlotIndex];
        	if (item == null) {
        		continue;
        	}
        	
        	//locate correct item and put in recipe slot
        	while(!recipeSlot.getHasStack() || (recipeSlot.getStack().stackSize < recipeStackSize && recipeSlot.getStack().getMaxStackSize() > 1)) 
        	for (int inventorySlotIndex = recipeSlotIndex + 1; inventorySlotIndex < inventorySlots.size(); inventorySlotIndex++) {
        		Slot inventorySlot = (Slot)inventorySlots.get(inventorySlotIndex);
        		if (inventorySlot.getHasStack() && inventorySlot.getStack().itemID == item.itemID && (inventorySlot.getStack().getItemDamage() == item.getItemDamage() || item.getItemDamage() < 0 || !item.getHasSubtypes())) {
        			this.clickSlot(inventorySlotIndex, true, false);
        			if (isInv(parent) && recipeSlotIndex > 3) {
        				this.clickSlot(recipeSlotIndex - 1, false, false);
        			}
        			else 
        				this.clickSlot(recipeSlotIndex, false, false);
        			this.clickSlot(inventorySlotIndex, true, false);
        			break;
        		}
        	}
        	
    	}
		
	}
	
	PlayerController inv;
	EntityPlayerSP player;
	int windowId;
	
	void clickSlot(int slotIndex, boolean leftClick, boolean shiftClick) {
		inv.handleMouseClick(windowId, slotIndex, leftClick ? 0 : 1, shiftClick, player);
	}
	
	boolean isInv(GuiScreen screen) {
		return screen instanceof GuiInventory || (TabUtils.aetherHandler != null && TabUtils.aetherHandler.isInventory(screen));
	}

}
