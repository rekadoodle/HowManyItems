// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.glasslauncher.hmifabric;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.glasslauncher.hmifabric.tabs.Tab;
import net.glasslauncher.hmifabric.tabs.TabWithTexture;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.OptionButton;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.item.ItemInstance;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

// Referenced classes of package net.minecraft.src:
//            ContainerBase, CraftingInventoryRecipeBookCB, InventoryRecipeBook, FontRenderer,
//            RenderEngine

public class GuiRecipeViewer extends ContainerBase
{
	private static Field xSizeField = Utils.getField(ContainerBase.class, new String[] {"containerWidth", "a"});

	public GuiRecipeViewer(ItemInstance itemstack, Boolean getUses, ScreenBase parent)
    {
		super(container = new ContainerRecipeViewer(inv = new InventoryRecipeViewer(itemstack)));
		this.parent = parent;
		init2();
        push(itemstack, getUses);
    }

	public GuiRecipeViewer(ItemInstance itemstack, ScreenBase parent)
    {
		super(container = new ContainerRecipeViewer(inv = new InventoryRecipeViewer(itemstack)));
		this.parent = parent;
		init2();
        pushTabBlock(itemstack);
    }

    public void init2() {
		if(Config.recipeViewerDraggableGui) {
	        containerWidth = Config.recipeViewerGuiWidth;
	        containerHeight = Config.recipeViewerGuiHeight;
		}
		else {
			if(parent instanceof ContainerBase) {
				try {
					containerWidth = xSizeField.getInt(parent);
				} catch (Exception e) { e.printStackTrace(); }
				containerHeight -= 80;
			}
			else {
				containerWidth = 254;
				containerHeight = 136;
			}

		}
        tabs = HowManyItems.getTabs();
        newTab(tabs.get(0));
	}

	public void pushTabBlock(ItemInstance itemstack) {
		if(itemstack == null) {
			return;
		}
		inv.filter.push(null);
		inv.newList = true;
		inv.prevTabs.push(inv.currentTab);
		inv.prevPages.push(inv.getPage()*inv.currentTab.recipesPerPage);
		inv.prevGetUses.push(true);

		for (Tab tab : HowManyItems.getTabs()) {
			boolean tabMatchesBlock = false;
			for(ItemInstance tabBlock : tab.equivalentCraftingStations) {
				if(tabBlock.isEqualIgnoreFlags(itemstack)) {
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

	public void push(ItemInstance itemstack, Boolean getUses)
    {
		if(!inv.filter.isEmpty() && itemstack == null && inv.filter.peek() == null) {
			return;
		}
		if(inv.filter.isEmpty() || getUses != inv.prevGetUses.peek() ||
				(itemstack == null && inv.filter.peek() != null) || (itemstack != null && inv.filter.peek() == null) ||
				(itemstack.itemId != inv.filter.peek().itemId || (itemstack.getDamage() != inv.filter.peek().getDamage() && itemstack.method_719())))
    	{

    		inv.newList = true;
    		if(itemstack == null) {
    			inv.filter.push(null);
    		}
    		else
    		inv.filter.push(new ItemInstance(itemstack.getType(), 1, itemstack.getDamage()));
    		inv.prevTabs.push(inv.currentTab);
    		inv.prevPages.push(inv.getPage()*inv.currentTab.recipesPerPage);
    		inv.prevGetUses.push(getUses);

    		inv.newList = true;
    		for (Tab tab : HowManyItems.getTabs()) {
    			tab.updateRecipes(inv.filter.peek(), getUses);
    		}
    		postPush();
    	}

    }

	private void postPush() {
		if (inv.currentTab.size == 0) {
			for (Tab tab : HowManyItems.getTabs()) {
    			if(tab.size > 0) {
    				newTab(tab);
    				break;
    			}
    			if(HowManyItems.getTabs().indexOf(tab) == HowManyItems.getTabs().size() - 1) {
    				inv.filter.pop();
    				inv.prevTabs.pop();
    				inv.prevPages.pop();
    				inv.prevGetUses.pop();
    				if (inv.filter.isEmpty()) {
    					inv.newList = false;
    					return;
    				}
    				else
    				for (Tab tab2 : HowManyItems.getTabs()) {

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
			for (Tab tab : HowManyItems.getTabs()) {
    			tab.updateRecipes(inv.filter.peek(), inv.prevGetUses.peek());
    		}
    		newTab(inv.prevTabs.pop());
    		inv.newList = true;
    		inv.index = inv.setIndex(inv.prevPages.pop());
    		initButtons();
		}
    }

	//Change page with scroll wheel
	@Override
    public void onMouseEvent()
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
        super.onMouseEvent();
    }

    @Override
    protected void mouseReleased(int i, int j, int k) {
    	if(dragging && k != -1) {
        	dragging = false;
        }
    	if(dragging) {
    		int x = (width - containerWidth) / 2;
        	int y = (height - containerHeight) / 2;
    		if (containerWidth != i - x || containerHeight != j - y) {
    			if (i - x > tabs.get(tabIndex).WIDTH + 2*EDGE_SIZE) containerWidth = i - x;
    			if (j - y > tabs.get(tabIndex).HEIGHT + 2*EDGE_SIZE) containerHeight = j - y;
        		tabs.get(tabIndex).redrawSlots = true;
    		}
    	}
    }

    private Boolean dragging = false;

	@Override
    protected void mouseClicked(int posX, int posY, int k)
    {
    	super.mouseClicked(posX, posY, k);
    	int x = (width - containerWidth) / 2;
    	int y = (height - containerHeight) / 2;
    	ItemInstance item = Utils.hoveredItem(this, posX, posY);
    	if(item != null && minecraft.player.inventory.getCursorItem() == null) {
    		push(item, k == 1);
    	}
    	else {
    		//Start dragging to change gui size
        	if (Config.recipeViewerDraggableGui && (posX - containerWidth + 10 > x) && (posX - containerWidth - 4 < x)
        			&& (posY - containerHeight + 10 > y) && (posY - containerHeight - 4 < y) && k == 0 && !dragging) {
        		dragging = true;
            }
        	//Change page with LMB or RMB
        	else if ((posX > x) && (posX < x + containerWidth)
            		&& (posY > y + 4) && (posY < y + containerHeight + 4)) {
            	if(k == 0) {inv.incIndex(); initButtons();
            	}
            	if(k == 1) {inv.decIndex(); initButtons();
            	}
            }
            else {
            	//Change tab
            	int tabCount = 0;
            	for (int z = tabPage; z < tabs.size() && (tabCount+1)*TAB_WIDTH < containerWidth; z++) {
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
	@Override
    protected void keyPressed(char c, int i)
    {
    	if (i == Keyboard.KEY_RIGHT) {inv.incIndex(); initButtons();
    	}
        if (i == Keyboard.KEY_LEFT) {inv.decIndex(); initButtons();
        }
        if (i == Keyboard.KEY_ESCAPE || i == minecraft.options.inventoryKey.key) {
        	displayParent();
        	if(i == minecraft.options.inventoryKey.key) minecraft.player.closeContainer();
        }
        else
        super.keyPressed(c, i);
    }

    @Override
    public void init()
    {
    	super.init();
        if(inv.filter.isEmpty()) displayParent();
        else initButtons();
    }

    public void initButtons()
    {
    	buttons.clear();
    	int x = (width - containerWidth) / 2;
        int y = (height - containerHeight) / 2;

        buttons.add(new OptionButton(-1, x + containerWidth - 20, y - 43, 20, 20, ">"));
        buttons.add(new OptionButton(-2, x, y - 43, 20, 20, "<"));
        ((Button)buttons.get(0)).visible = tabPageButtons;
        ((Button)buttons.get(0)).active = tabPageButton1;

        ((Button)buttons.get(1)).visible = tabPageButtons;
        ((Button)buttons.get(1)).active = tabPageButton2;

        for(int k = 0; k < buttons.size(); k++)
        {
            Button guibutton = (Button)buttons.get(k);
            guibutton.render(minecraft, cursorPosX, cursorPosY);
        }

        if(tabs.get(tabIndex) instanceof TabWithTexture) {
        	TabWithTexture tab = (TabWithTexture)tabs.get(tabIndex);
        	int gapX = (containerWidth - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
        	int noX = (containerWidth - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
        	if (noX == 0) noX++;

        	int gapY = (containerHeight - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
        	int noY = (containerHeight - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
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
        				int posX = EDGE_SIZE + gapX/4 + l1*(containerWidth - gapX/2)/noX;
        				int posY = EDGE_SIZE + gapY/4 + i2*(containerHeight - gapY/2)/noY;
        				if(noX == 1) posX = (containerWidth - tab.WIDTH)/2;
        				if(noY == 1) posY = (containerHeight - tab.HEIGHT)/2;
        				GuiButtonHMI button = new GuiButtonHMI(i, x + posX + tab.BUTTON_POS_X, y + posY + tab.BUTTON_POS_Y, BUTTON_WIDTH, BUTTON_HEIGHT, "+");
        				Boolean[] itemsInInv = tab.itemsInInventory(parent, inv.items[i - 1]);
        				for (int qq = 0; qq < itemsInInv.length; qq++) {
        					if (!itemsInInv[qq]) {
        						button.active = false;
        						break;
        					}
        				}
        				buttons.add(button);
        			}
                }
        	}
        }
    }

    private final int BUTTON_WIDTH = 12;
    private final int BUTTON_HEIGHT = 12;

    @Override
    protected void buttonClicked(Button guibutton)
    {
    	super.buttonClicked(guibutton);
    	if (guibutton.id == -1) {
    		tabPage += containerWidth / TAB_WIDTH;
    		if (tabPage >= tabs.size()) tabPage -= containerWidth / TAB_WIDTH;
    	}
    	else if (guibutton.id == -2) {
    		tabPage -= containerWidth / TAB_WIDTH;
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
    		minecraft = Utils.getMC();
			minecraft.player.container = minecraft.player.playerContainer;
		//}
			this.onClose();
			if(parent != null) {
				minecraft.currentScreen = parent;
				ScreenScaler scaledresolution = new ScreenScaler(minecraft.options, minecraft.actualWidth, minecraft.actualHeight);
	            int i = scaledresolution.getScaledWidth();
	            int j = scaledresolution.getScaledHeight();
	            minecraft.method_2134();
				parent.init(minecraft, i, j);
			}
			else {
				minecraft.openScreen(parent);
			}
    }

    private int cursorPosX;
    private int cursorPosY;
    private int tabPage = 0;

    @Override
    public void render(int i, int j, float f) {
    	cursorPosX = i;
    	cursorPosY = j;
    	super.render(i, j, f);
    }

    @Override
    protected void renderForeground()
    {
    	if ((double)inv.currentTab.size/inv.currentTab.recipesPerPage > 1) {
    		String s = inv.getContainerName();
        	textManager.drawTextWithShadow(s, containerWidth - 4 - textManager.getTextWidth(s), 10, 0x404040);
    	}
        int tabCount = 0;
    	int x = (width - containerWidth) / 2;
        int y = (height - containerHeight) / 2;
        for (int z = tabPage; z < tabs.size(); z++) {
    		if (tabs.get(z).size > 0 && (tabCount+1)*TAB_WIDTH < containerWidth) {
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
            int gapX = (containerWidth - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
            int noX = (containerWidth - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
            if (noX == 0) noX++;

            int gapY = (containerHeight - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
            int noY = (containerHeight - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
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
            			int posX = EDGE_SIZE + gapX/4 + l1*(containerWidth - gapX/2)/noX;
            			int posY = EDGE_SIZE + gapY/4 + i2*(containerHeight - gapY/2)/noY;
            			if(noX == 1) posX = (containerWidth - tab.WIDTH)/2;
            			if(noY == 1) posY = (containerHeight - tab.HEIGHT)/2;
            			if (tab.drawSetupRecipeButton(parent, inv.items[i-1]))
            			if ((cursorPosX > x + posX + tab.BUTTON_POS_X - 1) && (cursorPosX < x + posX + tab.BUTTON_POS_X + BUTTON_WIDTH)
                       		&& (cursorPosY > y + posY + tab.BUTTON_POS_Y - 3 - 1) && (cursorPosY < y + posY + tab.BUTTON_POS_Y - 3 + BUTTON_HEIGHT)) {
            				//System.out.println(inv.items[i - 1][0].getItemName());

            				Boolean[] itemsInInv = tab.itemsInInventory(parent, inv.items[i-1]);

            				for (int qq = 0; qq < itemsInInv.length; qq++) {
            					if (!itemsInInv[qq]) {
            						blit(
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

    public ItemInstance getHoverItem() {
    	int tabCount = 0;
    	int x = (width - containerWidth) / 2;
        int y = (height - containerHeight) / 2;
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

    @Override
    protected void renderContainerBackground(float f)
    {
    	Utils.preRender();
    	int x = (width - containerWidth) / 2;
        int y = (height - containerHeight) / 2 + 3;



        //DRAW TABS
        int tabCount = 0;
        for (int z = tabPage; z < tabs.size(); z++) {
        	if(tabs.get(z).size > 0) {
        		if (z != tabIndex && (tabCount+1)*TAB_WIDTH < containerWidth) {
        			Utils.bindTexture();
        	        Utils.disableLighting();
        			blit(x + tabCount*TAB_WIDTH, y -25, 28, 113, 28, 28);
        			Utils.drawItemStack(x + 6 + tabCount*TAB_WIDTH, y -18, tabs.get(z).getTabItem(), true);
        		}
        		tabCount++;
        	}
        }
        //DRAW BACKGROUND
        Utils.bindTexture();
        Utils.disableLighting();
        for(int l1 = 0; l1 < containerWidth - EDGE_SIZE; l1+= 48)
        {
            for(int i2 = 0; i2 < containerHeight - EDGE_SIZE; i2+= 48)
            {
            	blit(x + EDGE_SIZE + l1, y + EDGE_SIZE + i2, 4, 145, containerWidth - l1 - EDGE_SIZE*2, containerHeight - i2 - EDGE_SIZE*2); //grey background
            }
        }
        //SETUP TAB PAGE BUTTONS
        if (buttons.size() >= 2) {
        	 if ((tabCount + tabPage)* TAB_WIDTH < containerWidth) {
        		 tabPage = 0;
              	tabPageButtons = false;
             }
             else {
              	tabPageButtons = true;
             }
          	((Button)buttons.get(0)).visible = tabPageButtons;
          	((Button)buttons.get(1)).visible = tabPageButtons;

             if (containerWidth / TAB_WIDTH >= tabCount) {
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
             ((Button)buttons.get(0)).active = tabPageButton1;
             ((Button)buttons.get(1)).active = tabPageButton2;
        }

        //DRAW EDGES
        for(int l1 = 0; l1 < containerWidth - EDGE_SIZE; l1+= 48)
        {
        	blit(x + EDGE_SIZE + l1, y, 4, 141, containerWidth - l1 - EDGE_SIZE*2, EDGE_SIZE); //top border
            blit(x + EDGE_SIZE + l1, (y + containerHeight) - EDGE_SIZE, 4, 193, containerWidth - l1 - EDGE_SIZE*2, EDGE_SIZE); //bottom border
        }

        for(int i2 = 0; i2 < containerHeight - EDGE_SIZE; i2+= 48)
        {
            blit(x, y + EDGE_SIZE + i2, 0, 145, EDGE_SIZE, containerHeight - i2 - EDGE_SIZE*2); //left border
            blit((x + containerWidth) - EDGE_SIZE, y + EDGE_SIZE + i2, 52, 145, EDGE_SIZE, containerHeight - i2 - EDGE_SIZE*2); //right border
        }

        blit(x, y, 0, 141, EDGE_SIZE, EDGE_SIZE); //top left corner
        blit(x + containerWidth - EDGE_SIZE, y, 52, 141, EDGE_SIZE, EDGE_SIZE); //top right corner
        blit(x, y + containerHeight - EDGE_SIZE, 0, 193, EDGE_SIZE, EDGE_SIZE); //bottom left corner
        blit(x + containerWidth - EDGE_SIZE, y + containerHeight - EDGE_SIZE, 52, 193, EDGE_SIZE, EDGE_SIZE); //bottom right corner

      //DRAW CURRENT TAB
        tabCount = 0;
        for (int z = tabPage; z < tabs.size(); z++) {
        	if (tabs.get(z).size > 0) {
        		if (z == tabIndex && (tabCount+1)*TAB_WIDTH < containerWidth) {
        			Utils.bindTexture();
        	        Utils.disableLighting();
        			blit(x + tabCount*TAB_WIDTH, y -25, 0, 113, 28, 28);
        			if(tabCount == 0) {
        				blit(x, y, 0, 145, EDGE_SIZE, 5);
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
            blit(x + containerWidth - 29, y + containerHeight - 29, 56, 169, 28, 28);
        }

        //DRAW RECIPE CONTAINER

        	Tab tab = tabs.get(tabIndex);


        	int gapX = (containerWidth - 2*EDGE_SIZE) % (tab.WIDTH + tab.MIN_PADDING_X);
        	int noX = (containerWidth - 2*EDGE_SIZE) / (tab.WIDTH + tab.MIN_PADDING_X);
        	if (noX == 0) noX++;

        	int gapY = (containerHeight - 2*EDGE_SIZE) % (tab.HEIGHT + tab.MIN_PADDING_Y);
        	int noY = (containerHeight - 2*EDGE_SIZE) / (tab.HEIGHT + tab.MIN_PADDING_Y);
        	if (noY == 0) noY++;

        	if (containerWidth < tab.WIDTH + 2*EDGE_SIZE) containerWidth = tab.WIDTH + 2*EDGE_SIZE;
    		if (containerHeight < tab.HEIGHT + 2*EDGE_SIZE) containerHeight = tab.HEIGHT + 2*EDGE_SIZE;

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
        				int posX = EDGE_SIZE + gapX/4 + l1*(containerWidth - gapX/2)/noX;
        				int posY = EDGE_SIZE + gapY/4 + i2*(containerHeight - gapY/2)/noY;
        				if(noX == 1) posX = (containerWidth - tab.WIDTH)/2;
        				if(noY == 1) posY = (containerHeight - tab.HEIGHT)/2;
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





    }

    @Override
    public void onClose()
    {
    	if (Config.recipeViewerGuiWidth != containerWidth || Config.recipeViewerGuiHeight != containerHeight) {
    		Config.recipeViewerGuiWidth = containerWidth;
    		Config.recipeViewerGuiHeight = containerHeight;
    		Config.writeConfig();
    	}
        //super.onGuiClosed();
    }


    private ScreenBase parent;
    private int tabIndex;
    public static ArrayList<Tab> tabs;
    private static InventoryRecipeViewer inv;
    private static ContainerRecipeViewer container;


	
}
