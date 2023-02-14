// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src.hmi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.hmi.tabs.Tab;
import net.minecraft.src.hmi.tabs.TabWithTexture;

// Referenced classes of package net.minecraft.src:
//            GuiContainer, CraftingInventoryRecipeBookCB, InventoryRecipeBook, FontRenderer, 
//            RenderEngine

public class GuiRecipeViewer extends GuiContainer
{
	private static Field xSizeField = Utils.getField(GuiContainer.class, new String[] {"xSize", "a"});
    
	public GuiRecipeViewer(ItemStack itemstack, Boolean getUses, GuiScreen parent)
    {
		super(container = new ContainerRecipeViewer(inv = new InventoryRecipeViewer(itemstack)));
		this.parent = parent;
		init();
        push(itemstack, getUses);
    }
	
	public GuiRecipeViewer(ItemStack itemstack, GuiScreen parent)
    {
		super(container = new ContainerRecipeViewer(inv = new InventoryRecipeViewer(itemstack)));
		this.parent = parent;
		init();
        pushTabBlock(itemstack);
    }
	
	private void init() {
		if(Config.recipeViewerDraggableGui) {
	        xSize = Config.recipeViewerGuiWidth;
	        ySize = Config.recipeViewerGuiHeight;
		}
		else {
			if(parent instanceof GuiContainer) {
				try {
					xSize = xSizeField.getInt(parent);
				} catch (Exception e) { e.printStackTrace(); }
				ySize -= 80;
			}
			else {
				xSize = 254;
				ySize = 136;
			}
			
		}
        tabs = mod_HowManyItems.getTabs();
        newTab(tabs.get(0));
	}
	
	public void pushTabBlock(ItemStack itemstack) {
		if(itemstack == null) {
			return;
		}
		inv.filter.push(null);
		inv.newList = true;
		inv.prevTabs.push(inv.currentTab);
		inv.prevPages.push(inv.getPage()*inv.currentTab.recipesPerPage);
		inv.prevGetUses.push(true);
		
		for (Tab tab : mod_HowManyItems.getTabs()) {
			boolean tabMatchesBlock = false;
			for(ItemStack tabBlock : tab.equivalentCraftingStations) {
				if(tabBlock.isItemEqual(itemstack)) {
					tabMatchesBlock = true;
					tab.updateRecipes(null, false);
					break;
				}
			}
			if(!tabMatchesBlock) {
				tab.size = 0;
			}
		}
		postPush();
	}
	
	public void push(ItemStack itemstack, Boolean getUses)
    {
		if(!inv.filter.isEmpty() && itemstack == null && inv.filter.peek() == null) {
			return;
		}
		if(inv.filter.isEmpty() || getUses != inv.prevGetUses.peek() || 
				(itemstack == null && inv.filter.peek() != null) || (itemstack != null && inv.filter.peek() == null) || 
				(itemstack.itemID != inv.filter.peek().itemID || (itemstack.getItemDamage() != inv.filter.peek().getItemDamage() && itemstack.getHasSubtypes())))
    	{
			
    		inv.newList = true;
    		if(itemstack == null) {
    			inv.filter.push(null);
    		}
    		else
    		inv.filter.push(new ItemStack(itemstack.getItem(), 1, itemstack.getItemDamage()));
    		inv.prevTabs.push(inv.currentTab);
    		inv.prevPages.push(inv.getPage()*inv.currentTab.recipesPerPage);
    		inv.prevGetUses.push(getUses);

    		inv.newList = true;
    		for (Tab tab : mod_HowManyItems.getTabs()) {
    			tab.updateRecipes(inv.filter.peek(), getUses);
    		}
    		postPush();
    	}

    }
	
	private void postPush() {
		if (inv.currentTab.size == 0) {
			for (Tab tab : mod_HowManyItems.getTabs()) {
    			if(tab.size > 0) {
    				newTab(tab);
    				break;
    			}
    			if(mod_HowManyItems.getTabs().indexOf(tab) == mod_HowManyItems.getTabs().size() - 1) {
    				inv.filter.pop();
    				inv.prevTabs.pop();
    				inv.prevPages.pop();
    				inv.prevGetUses.pop();
    				if (inv.filter.isEmpty()) {
    					inv.newList = false;
    					return;
    				}
    				else
    				for (Tab tab2 : mod_HowManyItems.getTabs()) {
    					
    	    			tab2.updateRecipes(inv.filter.peek(), inv.prevGetUses.peek());
    	    		}
    				inv.index = inv.setIndex(inv.index);
    				initButtons();
    				return;
    			}
    		}
			
		}
		
		inv.index = inv.setIndex(0);
		initButtons();
	}
	
	public void pop()
    {
		inv.filter.pop();
		inv.prevGetUses.pop();
		if (inv.filter.isEmpty()) {
			inv.newList = false;
			displayParent();
			return;
		}
		else {
			for (Tab tab : mod_HowManyItems.getTabs()) {
    			tab.updateRecipes(inv.filter.peek(), inv.prevGetUses.peek());
    		}
    		newTab(inv.prevTabs.pop());
    		inv.newList = true;
    		inv.index = inv.setIndex(inv.prevPages.pop());
    		initButtons();
		}
    }
	
	//Change page with scroll wheel
    public void handleMouseInput()
    {
    	int i = Mouse.getEventDWheel();
    	//TODO if(mouse in gui bounds) {
    	if(!Config.scrollInverted) {
			if(i > 0) { inv.incIndex(); initButtons(); }
            if(i < 0) { inv.decIndex(); initButtons(); }
		}
		else {
			if(i > 0) { inv.decIndex(); initButtons(); }
            if(i < 0) { inv.incIndex(); initButtons(); }
		}
        //}
        super.handleMouseInput();
    }
    
    protected void mouseMovedOrUp(int i, int j, int k) {
    	if(dragging && k != -1) {
        	dragging = false;
        }
    	if(dragging) {
    		int x = (width - xSize) / 2;
        	int y = (height - ySize) / 2;
    		if (xSize != i - x || ySize != j - y) {
    			if (i - x > tabs.get(tabIndex).WIDTH + 2*EDGE_SIZE) xSize = i - x;
    			if (j - y > tabs.get(tabIndex).HEIGHT + 2*EDGE_SIZE) ySize = j - y;
        		tabs.get(tabIndex).redrawSlots = true;
    		}
    	}
    }

    private Boolean dragging = false;
    
    
    protected void mouseClicked(int posX, int posY, int k)
    {
    	super.mouseClicked(posX, posY, k);
    	int x = (width - xSize) / 2;
    	int y = (height - ySize) / 2;
    	ItemStack item = Utils.hoveredItem(this, posX, posY);
    	if(item != null && mc.thePlayer.inventory.getItemStack() == null) {
    		push(item, k == 1);
    	}
    	else {
    		//Start dragging to change gui size
        	if (Config.recipeViewerDraggableGui && (posX - xSize + 10 > x) && (posX - xSize - 4 < x)
        			&& (posY - ySize + 10 > y) && (posY - ySize - 4 < y) && k == 0 && !dragging) {
        		dragging = true;
            }
        	//Change page with LMB or RMB
        	else if ((posX > x) && (posX < x + xSize)
            		&& (posY > y + 4) && (posY < y + ySize + 4)) {
            	if(k == 0) {inv.incIndex(); initButtons();
            	}
            	if(k == 1) {inv.decIndex(); initButtons();
            	}
            }
            else {
            	//Change tab
            	int tabCount = 0;
            	for (int z = tabPage; z < tabs.size() && (tabCount+1)*TAB_WIDTH < xSize; z++) {
            		if (tabs.get(z).size > 0) {
            			if ((posX - tabCount * TAB_WIDTH + 1> x) && (posX - (tabCount+1) * TAB_WIDTH < x)
                        		&& (posY + 21 > y) && (posY - 3 < y) 
                        		&& k == 0 && tabIndex != z) {
            				newTab(tabs.get(z));
                			break;
                		}
            			tabCount++;
            		}
            	}
            }
    	}
    }
    
    public void newTab(Tab tab) {
    	tabIndex = tabs.indexOf(tab);
    	tab.redrawSlots = true;
		inv.initTab(tab);
		initButtons();
    }
    
    
    //Change page with arrow keys
    protected void keyTyped(char c, int i)
    {
    	if (i == Keyboard.KEY_RIGHT) {inv.incIndex(); initButtons();
    	}
        if (i == Keyboard.KEY_LEFT) {inv.decIndex(); initButtons();
        }
        if (i == Keyboard.KEY_ESCAPE || i == mc.gameSettings.keyBindInventory.keyCode) {
        	displayParent();
        	if(i == mc.gameSettings.keyBindInventory.keyCode) mc.thePlayer.closeScreen();
        }
        else
        super.keyTyped(c, i);
    }
    
    public void initGui()
    {
    	super.initGui();
        if(inv.filter.isEmpty()) displayParent();
        else initButtons();
    }
    
    public void initButtons()
    {
    	controlList.clear();
    	int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        
        controlList.add(new GuiSmallButton(-1, x + xSize - 20, y - 43, 20, 20, ">"));
        controlList.add(new GuiSmallButton(-2, x, y - 43, 20, 20, "<"));
        ((GuiButton)controlList.get(0)).enabled2 = tabPageButtons;
        ((GuiButton)controlList.get(0)).enabled = tabPageButton1;
        
        ((GuiButton)controlList.get(1)).enabled2 = tabPageButtons;
        ((GuiButton)controlList.get(1)).enabled = tabPageButton2;
        
        for(int k = 0; k < controlList.size(); k++)
        {
            GuiButton guibutton = (GuiButton)controlList.get(k);
            guibutton.drawButton(mc, cursorPosX, cursorPosY);
        }
        
        if(tabs.get(tabIndex) instanceof TabWithTexture) {
        	TabWithTexture tab = (TabWithTexture)tabs.get(tabIndex);
        	int gapX = (xSize - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
        	int noX = (xSize - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
        	if (noX == 0) noX++;
        	
        	int gapY = (ySize - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
        	int noY = (ySize - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
        	if (noY == 0) noY++;
        	
        	if(tab.size == 1) {
        		noX = 1;
        		noY = 1;
        	}
        	
        	int i = 0;
        	for(int l1 = 0; l1 < noX; l1++)
            {
        		for(int i2 = 0; i2 < noY; i2 ++)
                {
        			if(tab.size > 0 && inv.items != null && i++ < tab.recipesOnThisPage && inv.items.length > i - 1 && tab.drawSetupRecipeButton(parent, inv.items[i - 1])) {
        				int posX = EDGE_SIZE + gapX/4 + l1*(xSize - gapX/2)/noX;
        				int posY = EDGE_SIZE + gapY/4 + i2*(ySize - gapY/2)/noY;
        				if(noX == 1) posX = (xSize - tab.WIDTH)/2;
        				if(noY == 1) posY = (ySize - tab.HEIGHT)/2;
        				GuiButtonHMI button = new GuiButtonHMI(i, x + posX + tab.BUTTON_POS_X, y + posY + tab.BUTTON_POS_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "+");
        				Boolean[] itemsInInv = tab.itemsInInventory(parent, inv.items[i - 1]);
        				for (int qq = 0; qq < itemsInInv.length; qq++) {
        					if (!itemsInInv[qq]) {
        						button.enabled = false;
        						break;
        					}
        				}
        				controlList.add(button);
        			}
                }
        	}
        }
    }
    
    private final int BUTTON_WIDTH = 12;
    private final int BUTTON_HEIGHT = 12;
    
    protected void actionPerformed(GuiButton guibutton)
    {
    	super.actionPerformed(guibutton);
    	if (guibutton.id == -1) {
    		tabPage += xSize / TAB_WIDTH;
    		if (tabPage >= tabs.size()) tabPage -= xSize / TAB_WIDTH;
    	}
    	else if (guibutton.id == -2) {
    		tabPage -= xSize / TAB_WIDTH;
    		if (tabPage < 0) tabPage = 0;
    	}
    	else {
    		if(guibutton.id - 1 < inv.items.length && tabs.get(tabIndex) instanceof TabWithTexture) {
    			displayParent();
    			((TabWithTexture)tabs.get(tabIndex)).setupRecipe(parent, inv.items[guibutton.id - 1]);
    		}
    	}
    	
    }
    
    public void displayParent() {
    	//if (parent instanceof GuiInventory) {
    		mc = Utils.mc;
			mc.thePlayer.craftingInventory = mc.thePlayer.inventorySlots;
		//}
			this.onGuiClosed();
			if(parent != null) {
				mc.currentScreen = parent;
				ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	            int i = scaledresolution.getScaledWidth();
	            int j = scaledresolution.getScaledHeight();
	            mc.setIngameNotInFocus();
				parent.setWorldAndResolution(mc, i, j);
			}
			else {
				mc.displayGuiScreen(parent);
			}
    }
    
    private int cursorPosX;
    private int cursorPosY;
    private int tabPage = 0;
    
    public void drawScreen(int i, int j, float f) {
    	cursorPosX = i;
    	cursorPosY = j;
    	super.drawScreen(i, j, f);
    }
    
    protected void drawGuiContainerForegroundLayer()
    {
    	if ((double)inv.currentTab.size/inv.currentTab.recipesPerPage > 1) {
    		String s = inv.getInvName();
        	fontRenderer.drawString(s, xSize - 4 - fontRenderer.getStringWidth(s), 10, 0x404040);
    	}
        int tabCount = 0;
    	int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        for (int z = tabPage; z < tabs.size(); z++) {
    		if (tabs.get(z).size > 0 && (tabCount+1)*TAB_WIDTH < xSize) {
    			if ((cursorPosX > x + tabCount * TAB_WIDTH + 6) && (cursorPosX < x + (tabCount+1) * TAB_WIDTH - 6)
                		&& (cursorPosY > y - 16) && (cursorPosY < y)) {
    				
    				String s2 = (tabs.get(z).name());
    	            if(s2.length() > 0)
    	            {
    	            	Utils.drawTooltip(s2, cursorPosX, cursorPosY);
    	            }
        			break;
        		}
    			tabCount++;
    		}
    	}
        if(tabs.get(tabIndex) instanceof TabWithTexture) {
        	TabWithTexture tab = (TabWithTexture)tabs.get(tabIndex);
        	y += 3;
            int gapX = (xSize - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
            int noX = (xSize - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
            if (noX == 0) noX++;
       	
            int gapY = (ySize - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
            int noY = (ySize - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
            if (noY == 0) noY++;
       	
            if(tab.size == 1) {
        		noX = 1;
        		noY = 1;
        	}
            
            int i = 0;
            for(int l1 = 0; l1 < noX; l1++)
            {
            	for(int i2 = 0; i2 < noY; i2 ++)
            	{
            		if(tab.size > 0 && i++ < tab.recipesOnThisPage) {
            			int posX = EDGE_SIZE + gapX/4 + l1*(xSize - gapX/2)/noX;
            			int posY = EDGE_SIZE + gapY/4 + i2*(ySize - gapY/2)/noY;
            			if(noX == 1) posX = (xSize - tab.WIDTH)/2;
            			if(noY == 1) posY = (ySize - tab.HEIGHT)/2;
            			if (tab.drawSetupRecipeButton(parent, inv.items[i-1]))
            			if ((cursorPosX > x + posX + tab.BUTTON_POS_X - 1) && (cursorPosX < x + posX + tab.BUTTON_POS_X + BUTTON_WIDTH)
                       		&& (cursorPosY > y + posY + tab.BUTTON_POS_Y - 3 - 1) && (cursorPosY < y + posY + tab.BUTTON_POS_Y - 3 + BUTTON_HEIGHT)) {
            				//System.out.println(inv.items[i - 1][0].getItemName());
            				
            				Boolean[] itemsInInv = tab.itemsInInventory(parent, inv.items[i-1]);
       					
            				for (int qq = 0; qq < itemsInInv.length; qq++) {
            					if (!itemsInInv[qq]) {
            						drawGradientRect(
            							 posX + tab.slots[qq + 1][0], posY + tab.slots[qq + 1][1],
            							 posX + tab.slots[qq + 1][0] + 16, posY + tab.slots[qq + 1][1] + 16,
            							0x80EC1C12, 0x80EC1C12);
            					}
            				}
       					
            			}	
            		}
               }
            }
        }
        Utils.postRender();
        Utils.disableLighting();
    }
    
    public ItemStack getHoverItem() {
    	int tabCount = 0;
    	int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
    	for (int z = tabPage; z < tabs.size(); z++) {
    		if (tabs.get(z).size > 0) {
    			if ((cursorPosX > x + tabCount * TAB_WIDTH + 6) && (cursorPosX < x + (tabCount+1) * TAB_WIDTH - 6)
                		&& (cursorPosY > y - 16) && (cursorPosY < y)) {
    				return tabs.get(z).getTabItem();
        		}
    			tabCount++;
    		}
    	}
		return null;
	}
    
    private final int EDGE_SIZE = 4;
    private final int TAB_WIDTH = 27;
    private boolean tabPageButtons;
    private boolean tabPageButton1;
    private boolean tabPageButton2;
    
    protected void drawGuiContainerBackgroundLayer(float f)
    {
    	Utils.preRender();
    	int x = (width - xSize) / 2;
        int y = (height - ySize) / 2 + 3;
        
       
        
        //DRAW TABS
        int tabCount = 0;
        for (int z = tabPage; z < tabs.size(); z++) {
        	if(tabs.get(z).size > 0) {
        		if (z != tabIndex && (tabCount+1)*TAB_WIDTH < xSize) {
        			Utils.bindTexture();
        	        Utils.disableLighting();
        			drawTexturedModalRect(x + tabCount*TAB_WIDTH, y -25, 28, 113, 28, 28);
        			Utils.drawItemStack(x + 6 + tabCount*TAB_WIDTH, y -18, tabs.get(z).getTabItem(), true);
        		}
        		tabCount++;
        	}
        }
        //DRAW BACKGROUND
        Utils.bindTexture();
        Utils.disableLighting();
        for(int l1 = 0; l1 < xSize - EDGE_SIZE; l1+= 48)
        {
            for(int i2 = 0; i2 < ySize - EDGE_SIZE; i2+= 48)
            {
            	drawTexturedModalRect(x + EDGE_SIZE + l1, y + EDGE_SIZE + i2, 4, 145, xSize - l1 - EDGE_SIZE*2, ySize - i2 - EDGE_SIZE*2); //grey background
            }
        }
        //SETUP TAB PAGE BUTTONS
        if (controlList.size() >= 2) {
        	 if ((tabCount + tabPage)* TAB_WIDTH < xSize) {
        		 tabPage = 0;
              	tabPageButtons = false;
             }
             else {
              	tabPageButtons = true;
             }
          	((GuiButton)controlList.get(0)).enabled2 = tabPageButtons;
          	((GuiButton)controlList.get(1)).enabled2 = tabPageButtons;
             
             if (xSize / TAB_WIDTH >= tabCount) {
             	tabPageButton1 = false;
             }
             else {
            	 tabPageButton1 = true;
             }
             if (tabPage == 0) {
            	tabPageButton2 = false;
             }
             else {
            	 tabPageButton2 = true;
             }
             ((GuiButton)controlList.get(0)).enabled = tabPageButton1;
             ((GuiButton)controlList.get(1)).enabled = tabPageButton2;
        }
        
        //DRAW EDGES
        for(int l1 = 0; l1 < xSize - EDGE_SIZE; l1+= 48)
        {
        	drawTexturedModalRect(x + EDGE_SIZE + l1, y, 4, 141, xSize - l1 - EDGE_SIZE*2, EDGE_SIZE); //top border
            drawTexturedModalRect(x + EDGE_SIZE + l1, (y + ySize) - EDGE_SIZE, 4, 193, xSize - l1 - EDGE_SIZE*2, EDGE_SIZE); //bottom border
        }
        
        for(int i2 = 0; i2 < ySize - EDGE_SIZE; i2+= 48)
        {
            drawTexturedModalRect(x, y + EDGE_SIZE + i2, 0, 145, EDGE_SIZE, ySize - i2 - EDGE_SIZE*2); //left border
            drawTexturedModalRect((x + xSize) - EDGE_SIZE, y + EDGE_SIZE + i2, 52, 145, EDGE_SIZE, ySize - i2 - EDGE_SIZE*2); //right border
        }
        
        drawTexturedModalRect(x, y, 0, 141, EDGE_SIZE, EDGE_SIZE); //top left corner
        drawTexturedModalRect(x + xSize - EDGE_SIZE, y, 52, 141, EDGE_SIZE, EDGE_SIZE); //top right corner
        drawTexturedModalRect(x, y + ySize - EDGE_SIZE, 0, 193, EDGE_SIZE, EDGE_SIZE); //bottom left corner
        drawTexturedModalRect(x + xSize - EDGE_SIZE, y + ySize - EDGE_SIZE, 52, 193, EDGE_SIZE, EDGE_SIZE); //bottom right corner
        
      //DRAW CURRENT TAB
        tabCount = 0;
        for (int z = tabPage; z < tabs.size(); z++) {
        	if (tabs.get(z).size > 0) {
        		if (z == tabIndex && (tabCount+1)*TAB_WIDTH < xSize) {
        			Utils.bindTexture();
        	        Utils.disableLighting();
        			drawTexturedModalRect(x + tabCount*TAB_WIDTH, y -25, 0, 113, 28, 28);
        			if(tabCount == 0) {
        				drawTexturedModalRect(x, y, 0, 145, EDGE_SIZE, 5);
        			}
        			Utils.drawItemStack(x + 6 + tabCount*TAB_WIDTH, y -18, tabs.get(z).getTabItem(), true);
        		}
        		tabCount++;
        	}
        }
        
        Utils.bindTexture();
        Utils.disableLighting();
        //DRAW dragging indicator thing
        if (Config.recipeViewerDraggableGui) {
            drawTexturedModalRect(x + xSize - 29, y + ySize - 29, 56, 169, 28, 28);
        }
        
        //DRAW RECIPE CONTAINER
       
        	Tab tab = tabs.get(tabIndex);
        	
        	 
        	int gapX = (xSize - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
        	int noX = (xSize - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
        	if (noX == 0) noX++;
        	
        	int gapY = (ySize - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
        	int noY = (ySize - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
        	if (noY == 0) noY++;
        	
        	if (xSize < tab.WIDTH + 2*EDGE_SIZE) xSize = tab.WIDTH + 2*EDGE_SIZE;
    		if (ySize < tab.HEIGHT + 2*EDGE_SIZE) ySize = tab.HEIGHT + 2*EDGE_SIZE;
        	
    		if(!tab.redrawSlots && tab.slots.length > 0 && container.slots.size() / tab.slots.length > tab.recipesOnThisPage) {
    			//tab.recipesOnThisPage = container.slots.size() / tab.slots.length;
    			tab.redrawSlots = true;
    		}
    		Boolean redrawItems = false;
        	if (tab.recipesPerPage != noX * noY) {
        		tab.recipesPerPage = noX * noY;
        		tab.recipesOnThisPage = tab.recipesPerPage;
        		tab.redrawSlots = true;
        		redrawItems = true;
        	}
        	if(tab.size == 1) {
        		noX = 1;
        		noY = 1;
        	}
        	
        	if (tab.redrawSlots) {
        		container.resetSlots();
        	}
        	int i = 0;
        	for(int l1 = 0; l1 < noX; l1++)
            {
        		for(int i2 = 0; i2 < noY; i2 ++)
                {
        			if(tab.size > 0 && i++ < tab.recipesOnThisPage) {
        				int posX = EDGE_SIZE + gapX/4 + l1*(xSize - gapX/2)/noX;
        				int posY = EDGE_SIZE + gapY/4 + i2*(ySize - gapY/2)/noY;
        				if(noX == 1) posX = (xSize - tab.WIDTH)/2;
        				if(noY == 1) posY = (ySize - tab.HEIGHT)/2;
        				tab.draw(x + posX, y + posY, i - 1, cursorPosX, cursorPosY);
        				if(tab.redrawSlots) {
        					for(int q = 0; q < tab.slots.length; q++) {
        						container.addSlot(posX + tab.slots[q][0], posY + tab.slots[q][1]);
        					}
        				}
        				
        			}
                }
        	}
        	if (tab.redrawSlots) {
        		inv.newList = true;
        		if (redrawItems)
            	inv.setIndex(inv.index);
        		initButtons();
            	tab.redrawSlots = false;
        	}
        	
        	
        	
        	
        Utils.postRender()
    }
    
    public void onGuiClosed()
    {
    	if (Config.recipeViewerGuiWidth != xSize || Config.recipeViewerGuiHeight != ySize) {
    		Config.recipeViewerGuiWidth = xSize;
    		Config.recipeViewerGuiHeight = ySize;
    		Config.writeConfig();
    	}
        //super.onGuiClosed();
    }


    private GuiScreen parent;
    private int tabIndex;
    private RenderItem itemRenderer = new RenderItem();
    public static ArrayList<Tab> tabs;
    private static InventoryRecipeViewer inv;
    private static ContainerRecipeViewer container;


	
}
