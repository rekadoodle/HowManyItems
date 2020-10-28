package net.glasslauncher.hmifabric.tabs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.glasslauncher.hmifabric.Utils;
import net.minecraft.block.BlockBase;
import net.minecraft.client.ClientInteractionManager;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.client.gui.screen.container.Crafting;
import net.minecraft.client.gui.screen.container.PlayerInventory;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.container.slot.Slot;
import net.minecraft.entity.player.AbstractClientPlayer;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeRegistry;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import org.lwjgl.input.Keyboard;

public class TabCrafting extends TabWithTexture {

	protected List recipesComplete;
	protected List recipes;
	private int slotsWidth;
	protected ClientModInitializer mod;
	private BlockBase tabBlock;
	private boolean isVanillaWorkbench = false; //THIS IS LAZY
	public ArrayList<Class<? extends ContainerBase>> guiCraftingStations = new ArrayList<>();
	
	public TabCrafting(ClientModInitializer tabCreator) {
		this(tabCreator, new ArrayList(RecipeRegistry.getInstance().getRecipes()), BlockBase.WORKBENCH);
		for (int i = 0; i < recipesComplete.size(); i++) {
			//Removes recipes that are too big and ruin everything @flans mod
			if(((Recipe)recipesComplete.get(i)).getIngredientCount() > 9)
            {
				recipesComplete.remove(i);
				i-=1;
            }
    	}
		isVanillaWorkbench = true;
		guiCraftingStations.add(Crafting.class);
	}
	
	public TabCrafting(ClientModInitializer tabCreator, List recipesComplete, BlockBase tabBlock) {
		this(tabCreator, 10, recipesComplete, tabBlock, "/gui/crafting.png", 118, 56, 28, 15, 56, 46, 3);
		slots[0] = new Integer[]{96, 23};
	}

	public TabCrafting(ClientModInitializer tabCreator, int slotsPerRecipe, List recipesComplete, BlockBase tabBlock, String texturePath, int width, int height, int textureX, int textureY, int buttonX, int buttonY, int slotsWidth) {
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
	
	public ItemInstance[][] getItems(int index, ItemInstance filter) {
		ItemInstance[][] items = new ItemInstance[recipesPerPage][];
		for(int j = 0; j < recipesPerPage; j++)
        {
            items[j] = new ItemInstance[slots.length];
            int k = index + j;
            if(k < recipes.size())
            {
                Recipe irecipe = (Recipe)recipes.get(k);
                try
                {
                    if(irecipe instanceof ShapedRecipe)
                    {
                        int l = ((Integer) Utils.getPrivateValue(ShapedRecipe.class, (ShapedRecipe) irecipe, 0)).intValue();
                        ItemInstance aitemstack[] = (ItemInstance[])Utils.getPrivateValue(ShapedRecipe.class, (ShapedRecipe)irecipe, 2);
                        items[j][0] = irecipe.getOutput();
                        for(int k1 = 0; k1 < aitemstack.length; k1++)
                        {
                        	if (aitemstack[k1] != null && aitemstack[k1].count > 1) aitemstack[k1].count = 1;
                            int l1 = k1 % l;
                            int i2 = k1 / l;
                            items[j][l1 + i2 * slotsWidth + 1] = aitemstack[k1];
                            if (aitemstack[k1] != null && aitemstack[k1].getDamage() == -1) {
                            	if (aitemstack[k1].method_719()) {
                            		if (filter != null && aitemstack[k1].itemId == filter.itemId) {
                            			items[j][l1 + i2 * slotsWidth + 1] = new ItemInstance(aitemstack[k1].getType(), 0, filter.getDamage());
                            		}
                            		else {
                            			items[j][l1 + i2 * slotsWidth + 1] = new ItemInstance(aitemstack[k1].getType());
                            		}
                            	}
                            	else if (filter != null && aitemstack[k1].itemId == filter.itemId){
                            		items[j][l1 + i2 * slotsWidth + 1] = new ItemInstance(aitemstack[k1].getType(), 0, filter.getDamage());
                            	}
                            }
                        }

                    } else
                    if(irecipe instanceof ShapelessRecipe)
                    {
                        List list = (List)Utils.getPrivateValue(ShapelessRecipe.class, irecipe, 1);
                        items[j][0] = irecipe.getOutput();
                        for(int j1 = 0; j1 < list.size(); j1++)
                        {
                        	ItemInstance item = (ItemInstance)list.get(j1);
                            items[j][j1 + 1] = item;
                            if (item != null && item.getDamage() == -1) {
                            	if (item.method_719()) {
                            		if (filter != null && item.itemId == filter.itemId) {
                            			items[j][j1 + 1] = new ItemInstance(item.getType(), 0, filter.getDamage());
                            		}
                            		else {
                            			items[j][j1 + 1] = new ItemInstance(item.getType());
                            		}
                            	}
                            	else if (filter != null && item.itemId == filter.itemId){
                            		items[j][j1 + 1] = new ItemInstance(item.getType(), 0, filter.getDamage());
                            	}
                            }
                        }

                    }
                }
                catch(Throwable throwable)
                {
                    throwable.printStackTrace();
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

	
	public void updateRecipes(ItemInstance filter, Boolean getUses) {
		List arraylist = new ArrayList();
    	if (filter == null) {
    		recipes = recipesComplete;
    	}
    	else {
    	for(Iterator iterator = recipesComplete.iterator(); iterator.hasNext();)
        {
            Recipe irecipe = (Recipe)iterator.next();
           if(!getUses && filter.itemId == irecipe.getOutput().itemId && (irecipe.getOutput().getDamage() == filter.getDamage() || irecipe.getOutput().getDamage() < 0 || !irecipe.getOutput().method_719() ))
            //if(itemstack.itemId == irecipe.getOutput().itemId && ( (irecipe.getOutput().getDamage() == itemstack.getDamage() || !irecipe.getOutput().method_719()) )|| irecipe.getOutput().getDamage() < 0)
            {
                arraylist.add(irecipe);
                continue;
            } 
           else if(irecipe instanceof ShapedRecipe && getUses)
            {
                ShapedRecipe shapedrecipes = (ShapedRecipe)irecipe;
                try
                {
                    ItemInstance aitemstack[] = (ItemInstance[])Utils.getPrivateValue(ShapedRecipe.class, (ShapedRecipe)irecipe, 2);
                    ItemInstance aitemstack1[];
                    int j = (aitemstack1 = aitemstack).length;
                    for(int i = 0; i < j; i++)
                    {
                        ItemInstance itemstack1 = aitemstack1[i];
                        if(itemstack1 == null || filter.itemId != itemstack1.itemId || (itemstack1.method_719() && itemstack1.getDamage() != filter.getDamage()) && itemstack1.getDamage() >= 0)
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
            else if(irecipe instanceof ShapelessRecipe && getUses)
            {
                ShapelessRecipe shapelessrecipes = (ShapelessRecipe)irecipe;
                try
                {
                    List list = (List)Utils.getPrivateValue(ShapelessRecipe.class, (ShapelessRecipe)irecipe, 1);
                    for(Iterator iterator1 = list.iterator(); iterator1.hasNext();)
                    {
                        Object obj = iterator1.next();
                        ItemInstance itemstack2 = (ItemInstance)obj;
                        if(filter.itemId == itemstack2.itemId && (itemstack2.getDamage() == filter.getDamage() || itemstack2.getDamage() < 0 || !itemstack2.method_719()))
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

	public ItemInstance getTabItem() {
		return new ItemInstance(tabBlock);
	}
	
	public Boolean drawSetupRecipeButton(ScreenBase parent, ItemInstance[] recipeItems) {
		for(Class<? extends ContainerBase> gui : guiCraftingStations) {
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
	
	public Boolean[] itemsInInventory(ScreenBase parent, ItemInstance[] recipeItems) {
		Boolean[] itemsInInv = new Boolean[slots.length - 1];
		List list;
		if (parent instanceof ContainerBase)
			list = ((ContainerBase)parent).container.slots;
		else
			list = Utils.getMC().player.container.slots;
        ItemInstance aslot[] = new ItemInstance[list.size()];
        for(int i = 0; i < list.size(); i++)
        {
        	if(((Slot)list.get(i)).hasItem())
            aslot[i] = ((Slot)list.get(i)).getItem().copy();
        }
        
        aslot[0] = null;
        recipe:
        for (int i = 1; i < recipeItems.length; i++) {
        	ItemInstance item = recipeItems[i];
        	if (item == null) {
        		itemsInInv[i - 1] = true;
        		continue;
        	}
        	
        	for (ItemInstance slot : aslot) {
        		if (slot != null && slot.count > 0 && slot.itemId == item.itemId && (slot.getDamage() == item.getDamage() || item.getDamage() < 0 || !item.method_719())) {
        			slot.count -= 1;
        			itemsInInv[i - 1] = true;
        			continue recipe;
        		}
        	}
        	itemsInInv[i - 1] = false;
    	}
		return itemsInInv;
	}
	
	private int recipeStackSize(List list, ItemInstance[] recipeItems) {
        
        int[] itemStackSize = new int[recipeItems.length - 1];
        
        for (int i = 1; i < recipeItems.length; i++) {
        	ItemInstance aslot[] = new ItemInstance[list.size()];
            for(int k = 0; k < list.size(); k++)
            {
            	if(((Slot)list.get(k)).hasItem())
                aslot[k] = ((Slot)list.get(k)).getItem().copy();
            }
            aslot[0] = null;
            
        	ItemInstance item = recipeItems[i];
        	itemStackSize[i - 1] = 0;
        	if (item == null) {
        		itemStackSize[i - 1] = -1;
        		continue;
        	}
        	int count = 0;
        	for (ItemInstance slot : aslot) {
        		if (slot != null && slot.count > 0 && slot.itemId == item.itemId && (slot.getDamage() == item.getDamage() || item.getDamage() < 0 || !item.method_719())) {
        			count += slot.count;
        			slot.count = 0;
        		}
        	}
        	int prevEqualItemCount = 1;
        	for (int j = 1; j < i; j++) {
                if(recipeItems[j] != null && recipeItems[j].isEqualIgnoreFlags(item)) {
                   	prevEqualItemCount++;
                }
            }
        	for (int j = 1; j < recipeItems.length; j++) {
                if(recipeItems[j] != null && recipeItems[j].isEqualIgnoreFlags(item)) {
                	itemStackSize[j - 1] = count / prevEqualItemCount;
                }
            }
    	}
        int finalItemStackSize = -1;
        for (int i = 0; i < itemStackSize.length; i++) {
        	ItemInstance item = recipeItems[i + 1];
        	if(itemStackSize[i] == -1 || item.method_709() == 1) continue;
        	if(finalItemStackSize == -1 || itemStackSize[i] < finalItemStackSize || finalItemStackSize > item.method_709()) {
        		finalItemStackSize = Math.min(itemStackSize[i], item.method_709());
        	}
    	}
        if(finalItemStackSize > 0) return finalItemStackSize;
		return 1;
	}

	public void setupRecipe(ScreenBase parent, ItemInstance[] recipeItems) {
		if (parent == null) {
			Utils.getMC().method_2134();
			ScreenScaler scaledresolution = new ScreenScaler(Utils.getMC().options, Utils.getMC().actualWidth, Utils.getMC().actualHeight);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
			parent = new PlayerInventory(Utils.getMC().player);
			Utils.getMC().currentScreen = parent;
			parent.init(Utils.getMC(), i, j);
			Utils.getMC().skipGameRender = false;
		}
		ContainerBase container = ((ContainerBase)parent);
		List<?> inventorySlots = container.container.slots;
		
		int recipeStackSize = 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			recipeStackSize = recipeStackSize(inventorySlots, recipeItems);
		}
		
		this.player = Utils.getMC().player;
		this.inv = Utils.getMC().interactionManager;
		this.windowId = container.container.currentContainerId;
        for(int recipeSlotIndex = 1; recipeSlotIndex < recipeItems.length; recipeSlotIndex++) {
        	if (isInv(parent) && recipeSlotIndex > 5)
        		break;
        	int slotid = recipeSlotIndex;
        	if (isInv(parent) && recipeSlotIndex > 3) {
        		slotid--;
        	}
        	Slot recipeSlot = (Slot)inventorySlots.get(slotid);
        	//clear recipe slot
        	if(recipeSlot.hasItem()) {
        		this.clickSlot(slotid, true, true);
        		
        		if (recipeSlot.hasItem()) {
        			this.clickSlot(slotid, true, false);
        			if (player.inventory.getCursorItem() != null) {
        				for (int j = slotid + 1; j < inventorySlots.size(); j++) {
        					Slot slot = (Slot)inventorySlots.get(j);
        					if (!slot.hasItem()) {
        						this.clickSlot(j, true, false);
        						break;
        					}
        				}
        				if (player.inventory.getCursorItem() != null) {
        					this.clickSlot(-999, true, false);
        				}
        			}
        		}
        	}
        	
        	//if recipe slot should be empty, continue
        	ItemInstance item = recipeItems[recipeSlotIndex];
        	if (item == null) {
        		continue;
        	}
        	
        	//locate correct item and put in recipe slot
        	while(!recipeSlot.hasItem() || (recipeSlot.getItem().count < recipeStackSize && recipeSlot.getItem().method_709() > 1))
        	for (int inventorySlotIndex = recipeSlotIndex + 1; inventorySlotIndex < inventorySlots.size(); inventorySlotIndex++) {
        		Slot inventorySlot = (Slot)inventorySlots.get(inventorySlotIndex);
        		if (inventorySlot.hasItem() && inventorySlot.getItem().itemId == item.itemId && (inventorySlot.getItem().getDamage() == item.getDamage() || item.getDamage() < 0 || !item.method_719())) {
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
	
	ClientInteractionManager inv;
	AbstractClientPlayer player;
	int windowId;
	
	void clickSlot(int slotIndex, boolean leftClick, boolean shiftClick) {
		inv.method_1708(windowId, slotIndex, leftClick ? 0 : 1, shiftClick, player);
	}
	
	boolean isInv(ScreenBase screen) {
		return screen instanceof InventoryBase;
	}

}
