// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

// Referenced classes of package net.minecraft.src:
//            GuiContainer, CraftingInventoryRecipeBookCB, InventoryRecipeBook, FontRenderer, 
//            RenderEngine

public class GuiRecipeViewer extends GuiContainer
{
    
	public GuiRecipeViewer(ItemStack itemstack, Boolean getUses, GuiScreen parent)
    {
		super(container = new ContainerRecipeViewer(inv = new InventoryRecipeViewer(itemstack)));
		this.parent = parent;
		if(mod_HowManyItems.optionsRecipeViewerDraggableGui) {
	        xSize = mod_HowManyItems.optionsRecipeViewerGuiWidth;
	        ySize = mod_HowManyItems.optionsRecipeViewerGuiHeight;
		}
		else {
			if(parent instanceof GuiContainer) {
				xSize = ((GuiContainer)parent).xSize;
				ySize -= 80;
			}
			else {
				xSize = 254;
				ySize = 136;
			}
			
		}
        tabs = mod_HowManyItems.getTabs();
        newTab(tabs.get(0));
        push(itemstack, getUses);
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
    		for (TabRecipeViewer tab : mod_HowManyItems.getTabs()) {
    			tab.updateRecipes(inv.filter.peek(), getUses);
    		}
    		if (inv.currentTab.size() == 0) {
    			for (TabRecipeViewer tab : mod_HowManyItems.getTabs()) {
        			if(tab.size() > 0) {
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
        				for (TabRecipeViewer tab2 : mod_HowManyItems.getTabs()) {
        					
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
			for (TabRecipeViewer tab : mod_HowManyItems.getTabs()) {
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
    	if(!mod_HowManyItems.optionsScrollInverted) {
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
    	Boolean tabClicked = false;
    	int x = (width - xSize) / 2;
    	int y = (height - ySize) / 2;
    	ItemStack item = mod_HowManyItems.itemAtPosition(this, posX, posY);
    	if(item != null && mc.thePlayer.inventory.getItemStack() == null) {
    		push(item, k == 1);
    	}
    	else {
    		//Start dragging to change gui size
        	if (mod_HowManyItems.optionsRecipeViewerDraggableGui && (posX - xSize + 10 > x) && (posX - xSize - 4 < x)
        			&& (posY - ySize + 10 > y) && (posY - ySize - 4 < y) && k == 0 && !dragging) {
        		dragging = true;
        		tabClicked = true;
            }
        	//Change page with LMB or RMB
        	else if ((posX > x) && (posX < x + xSize)
            		&& (posY > y) && (posY < y + ySize)) {
            	if(k == 0) {inv.incIndex(); initButtons();
            	}
            	if(k == 1) {inv.decIndex(); initButtons();
            	}
            	tabClicked = true;
            }
            else {
            	//Change tab
            	int tabCount = 0;
            	for (int z = tabPage; z < tabs.size(); z++) {
            		if (tabs.get(z).size() > 0) {
            			if ((posX - tabCount * TAB_WIDTH + 1> x) && (posX - (tabCount+1) * TAB_WIDTH < x)
                        		&& (posY + 21 > y) && (posY - 3 < y) 
                        		&& k == 0 && tabIndex != z) {
            				newTab(tabs.get(z));
                			tabClicked = true;
                			break;
                		}
            			tabCount++;
            		}
            	}
            }
    	}
    	
    	
    	//if (!tabClicked) 
    	
    }
    
    public void newTab(TabRecipeViewer newTab) {
    	tabIndex = tabs.indexOf(newTab);
    	newTab.redrawSlots = true;
		inv.initTab(newTab);
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
        	//System.out.println("parent = " + parent.getClass().getSimpleName());
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
        
        controlList.add(new GuiSmallButton(-1, x + xSize, y - 22, 20, 20, ">"));
        controlList.add(new GuiSmallButton(-2, x - 20, y - 22, 20, 20, "<"));
        ((GuiButton)controlList.get(0)).enabled2 = false;
        ((GuiButton)controlList.get(1)).enabled2 = false;
        
        TabRecipeViewer tab = tabs.get(tabIndex);
    	int gapX = (xSize - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
    	int noX = (xSize - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
    	if (noX == 0) noX++;
    	
    	int gapY = (ySize - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
    	int noY = (ySize - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
    	if (noY == 0) noY++;
    	
    	if(tab.size() == 1) {
    		noX = 1;
    		noY = 1;
    	}
    	
    	int i = 0;
    	for(int l1 = 0; l1 < noX; l1++)
        {
    		for(int i2 = 0; i2 < noY; i2 ++)
            {
    			if(tab.size() > 0 && inv.items != null && i++ < tab.recipesOnThisPage && inv.items.length > i - 1 && tab.drawSetupRecipeButton(parent, inv.items[i - 1])) {
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
    		if(guibutton.id - 1 < inv.items.length) {
    			displayParent();
    			tabs.get(tabIndex).setupRecipe(parent, inv.items[guibutton.id - 1]);
    		}
    	}
    	
    }
    
    public void displayParent() {
    	//if (parent instanceof GuiInventory) {
    		Minecraft mc2 = ModLoader.getMinecraftInstance();
			mc2.thePlayer.craftingInventory = mc2.thePlayer.inventorySlots;
		//}
			this.onGuiClosed();
			if(parent != null) {
				mc2.currentScreen = parent;
				ScaledResolution scaledresolution = new ScaledResolution(mc2.gameSettings, mc2.displayWidth, mc2.displayHeight);
	            int i = scaledresolution.getScaledWidth();
	            int j = scaledresolution.getScaledHeight();
	            mc2.setIngameNotInFocus();
				parent.setWorldAndResolution(mc2, i, j);
			}
			else {
				mc2.displayGuiScreen(parent);
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
    	if ((double)inv.currentTab.size()/inv.currentTab.recipesPerPage > 1) {
    		String s = inv.getInvName();
        	fontRenderer.drawString(s, xSize - 4 - fontRenderer.getStringWidth(s), 10, 0x404040);
    	}
        int tabCount = 0;
    	int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        for (int z = tabPage; z < tabs.size(); z++) {
    		if (tabs.get(z).size() > 0) {
    			if ((cursorPosX > x + tabCount * TAB_WIDTH + 6) && (cursorPosX < x + (tabCount+1) * TAB_WIDTH - 6)
                		&& (cursorPosY > y - 16) && (cursorPosY < y)) {
    				
    				String s2 = (new StringBuilder()).append("").append(StringTranslate.getInstance().translateNamedKey(tabs.get(z).getTabItem().getItemName())).toString().trim();
    	            if(s2.length() > 0)
    	            {
    	                int j1 = cursorPosX - x + 12;
    	                int l1 = cursorPosY - y - 12;
    	                int j2 = fontRenderer.getStringWidth(s2);
    	                drawGradientRect(j1 - 3, l1 - 3, j1 + j2 + 3, l1 + 8 + 3, 0xc0000000, 0xc0000000);
    	                fontRenderer.drawStringWithShadow(s2, j1, l1, -1);
    	            }
        			break;
        		}
    			tabCount++;
    		}
    	}
        
        TabRecipeViewer tab = tabs.get(tabIndex);
        y += 3;
        int gapX = (xSize - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
        int noX = (xSize - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
        if (noX == 0) noX++;
   	
        int gapY = (ySize - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
        int noY = (ySize - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
        if (noY == 0) noY++;
   	
        if(tab.size() == 1) {
    		noX = 1;
    		noY = 1;
    	}
        
        int i = 0;
        for(int l1 = 0; l1 < noX; l1++)
        {
        	for(int i2 = 0; i2 < noY; i2 ++)
        	{
        		if(tab.size() > 0 && i++ < tab.recipesOnThisPage) {
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
    
    public ItemStack getHoverItem() {
    	int tabCount = 0;
    	int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
    	for (int z = tabPage; z < tabs.size(); z++) {
    		if (tabs.get(z).size() > 0) {
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
    
    protected void drawGuiContainerBackgroundLayer(float f)
    {
    	int x = (width - xSize) / 2;
        int y = (height - ySize) / 2 + 3;
        
       
        mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/crafting.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        //DRAW TABS
        int tabCount = 0;
        for (int z = tabPage; z < tabs.size(); z++) {
        	if(tabs.get(z).size() > 0) {
        		if (z != tabIndex && (tabCount+1)*TAB_WIDTH < xSize) {
        			
        			GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
            		GL11.glPushMatrix();
            		GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
            		RenderHelper.enableStandardItemLighting();
            		GL11.glPopMatrix();
            		itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, tabs.get(z).getTabItem(), x + 6 + tabCount*TAB_WIDTH, y -18);
            		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, tabs.get(z).getTabItem(), x + 6 + tabCount*TAB_WIDTH, y -18);
            		GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
            		RenderHelper.disableStandardItemLighting();
            		mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/crafting.png"));
            		drawTexturedModalRect(x + tabCount*TAB_WIDTH + 1, y -21, 119, 32, TAB_WIDTH - EDGE_SIZE + 1, 22); //background
        			drawTexturedModalRect(x + tabCount*TAB_WIDTH, y -20, 0, 4, 3, 21); //left border
        			drawTexturedModalRect(x - 2 + (tabCount + 1)*TAB_WIDTH, y -20, 173, 4, EDGE_SIZE, 21);//right border
        			drawTexturedModalRect(x + 4 + tabCount*TAB_WIDTH, y-23, 4, 0, 20, 3); //top border
        			drawTexturedModalRect(x + - 3 + (tabCount + 1)*TAB_WIDTH, y-23, 172, 0, EDGE_SIZE, EDGE_SIZE -1); //top right corner
        			drawTexturedModalRect(x + tabCount*TAB_WIDTH, y-23, 0, 0, EDGE_SIZE, EDGE_SIZE); //top left corner
        		}
        		tabCount++;
        	}
        }
        
        //DRAW BACKGROUND
        mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/furnace.png"));
        for(int l1 = 0; l1 < xSize - EDGE_SIZE; l1+= 47)
        {
            for(int i2 = 0; i2 < ySize - EDGE_SIZE; i2+= 55)
            {
            	drawTexturedModalRect(x + EDGE_SIZE + l1, y + EDGE_SIZE + i2, 4, 4, xSize - l1 - EDGE_SIZE*2, ySize - i2 - EDGE_SIZE*2); //grey background
            }
        }
        
        //SETUP TAB PAGE BUTTONS
        if (controlList.size() >= 2) {
        	 if ((tabCount + tabPage)* TAB_WIDTH < xSize) {
        		 tabPage = 0;
             	((GuiButton)controlList.get(0)).enabled2 = false;
             	((GuiButton)controlList.get(1)).enabled2 = false;
             }
             else {
             	((GuiButton)controlList.get(0)).enabled2 = true;
             	((GuiButton)controlList.get(1)).enabled2 = true;
             }
             
             if (xSize / TAB_WIDTH >= tabCount) {
             	((GuiButton)controlList.get(0)).enabled = false;
             }
             else {
             	((GuiButton)controlList.get(0)).enabled = true;
             }
             if (tabPage == 0) {
             	((GuiButton)controlList.get(1)).enabled = false;
             }
             else {
             	((GuiButton)controlList.get(1)).enabled = true;
             }
        }
        
        //DRAW EDGES
        for(int l1 = 0; l1 < xSize - EDGE_SIZE; l1+= 47)
        {
        	drawTexturedModalRect(x + EDGE_SIZE + l1, y, 4, 0, xSize - l1 - EDGE_SIZE*2, EDGE_SIZE); //top border
            drawTexturedModalRect(x + EDGE_SIZE + l1, (y + ySize) - EDGE_SIZE, 4, 162, xSize - l1 - EDGE_SIZE*2, EDGE_SIZE); //bottom border
        }
        
        for(int i2 = 0; i2 < ySize - EDGE_SIZE; i2+= 55)
        {
            drawTexturedModalRect(x, y + EDGE_SIZE + i2, 0, 4, EDGE_SIZE, ySize - i2 - EDGE_SIZE*2); //left border
            drawTexturedModalRect((x + xSize) - EDGE_SIZE, y + EDGE_SIZE + i2, 172, 4, EDGE_SIZE, ySize - i2 - EDGE_SIZE*2); //right border
        }
        
        drawTexturedModalRect(x, y, 0, 0, EDGE_SIZE, EDGE_SIZE); //top left corner
        drawTexturedModalRect(x + xSize - EDGE_SIZE, y, 172, 0, EDGE_SIZE, EDGE_SIZE); //top right corner
        drawTexturedModalRect(x, y + ySize - EDGE_SIZE, 0, 162, EDGE_SIZE, EDGE_SIZE); //bottom left corner
        drawTexturedModalRect(x + xSize - EDGE_SIZE, y + ySize - EDGE_SIZE, 172, 162, EDGE_SIZE, EDGE_SIZE); //bottom right corner
        
      //DRAW CURRENT TAB
        tabCount = 0;
        for (int z = tabPage; z < tabs.size(); z++) {
        	if (tabs.get(z).size() > 0) {
        		if (z == tabIndex && (tabCount+1)*TAB_WIDTH < xSize) {
        			GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        			GL11.glPushMatrix();
        			GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
        			RenderHelper.enableStandardItemLighting();
        			GL11.glPopMatrix();
        			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, tabs.get(z).getTabItem(), x + 6 + tabCount*TAB_WIDTH, y -18);
        			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, tabs.get(z).getTabItem(), x + 6 + tabCount*TAB_WIDTH, y -18);
        			GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        			RenderHelper.disableStandardItemLighting();
        			mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/crafting.png"));
        			drawTexturedModalRect(x + tabCount*TAB_WIDTH + 3, y -22, 3, 16, TAB_WIDTH - 2*EDGE_SIZE + 3, 26); //background
        			drawTexturedModalRect(x + tabCount*TAB_WIDTH, y -21, 0, 4, 3, 20); //left border
        			drawTexturedModalRect(x + EDGE_SIZE + tabCount*TAB_WIDTH, y-25, 4, 0, TAB_WIDTH - 2*EDGE_SIZE + 1, EDGE_SIZE); //top border
        			drawTexturedModalRect(x + tabCount*TAB_WIDTH + 1, y -17, 1, 4, 2, 19); //white blob at bottom left
        			drawTexturedModalRect(x - 2 + (tabCount+1)*TAB_WIDTH, y -22, 173, 4, EDGE_SIZE, 23); //right border
            		drawTexturedModalRect(x - 3 + (tabCount+1)*TAB_WIDTH, y-25, 172, 0, 4, 3); //right corner
            		drawTexturedModalRect(x + tabCount*TAB_WIDTH, y-25, 0, 0, EDGE_SIZE, EDGE_SIZE); //left corner
        		}
        		tabCount++;
        	}
        }
        
        //DRAW dragging indicator thing
        if (mod_HowManyItems.optionsRecipeViewerDraggableGui) {
        	mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/gui.png"));
            drawTexturedModalRect(x + xSize - 9, y + ySize - 9, 15, 37, 8, 8);
        }
        
        //DRAW RECIPE CONTAINER
       
        	TabRecipeViewer tab = tabs.get(tabIndex);
        	 mc.renderEngine.bindTexture(mc.renderEngine.getTexture(tab.TEXTURE_PATH));
        	 
        	int gapX = (xSize - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
        	int noX = (xSize - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
        	if (noX == 0) noX++;
        	
        	int gapY = (ySize - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
        	int noY = (ySize - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
        	if (noY == 0) noY++;
        	
        	if (xSize < tab.WIDTH + 2*EDGE_SIZE) xSize = tab.WIDTH + 2*EDGE_SIZE;
    		if (ySize < tab.HEIGHT + 2*EDGE_SIZE) ySize = tab.HEIGHT + 2*EDGE_SIZE;
        	
    		if(!tab.redrawSlots && container.slots.size() / tab.slots.length > tab.recipesOnThisPage) {
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
        	if(tab.size() == 1) {
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
        			if(tab.size() > 0 && i++ < tab.recipesOnThisPage) {
        				int posX = EDGE_SIZE + gapX/4 + l1*(xSize - gapX/2)/noX;
        				int posY = EDGE_SIZE + gapY/4 + i2*(ySize - gapY/2)/noY;
        				if(noX == 1) posX = (xSize - tab.WIDTH)/2;
        				if(noY == 1) posY = (ySize - tab.HEIGHT)/2;
        				
        				drawTexturedModalRect(x + posX, y + posY, tab.TEXTURE_X, tab.TEXTURE_Y, tab.WIDTH, tab.HEIGHT);
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
        	
        	
        	
        	
        
    }
    
    public void onGuiClosed()
    {
    	if (mod_HowManyItems.optionsRecipeViewerGuiWidth != xSize || mod_HowManyItems.optionsRecipeViewerGuiHeight != ySize) {
    		mod_HowManyItems.optionsRecipeViewerGuiWidth = xSize;
    		mod_HowManyItems.optionsRecipeViewerGuiHeight = ySize;
    		mod_HowManyItems.writeConfig();
    	}
        //super.onGuiClosed();
    }


    private GuiScreen parent;
    private int tabIndex;
    private RenderItem itemRenderer = new RenderItem();
    public static ArrayList<TabRecipeViewer> tabs;
    private static InventoryRecipeViewer inv;
    private static ContainerRecipeViewer container;


	
}
