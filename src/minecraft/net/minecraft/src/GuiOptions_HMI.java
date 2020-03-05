package net.minecraft.src;

import net.java.games.input.Mouse;

public class GuiOptions_HMI extends GuiScreen {

	public GuiOptions_HMI(GuiScreen guiscreen)
    {
        parentScreen = guiscreen;
    }
	
	private GuiButton buttonCheats;
	private GuiButton buttonIDs;
	private GuiButton buttonCentredSearchBar;
	private GuiButton buttonFastSearch;
	private GuiButton buttonHiding;
	private GuiButton buttonInvertedScroll;
	
	private GuiButton buttonKeybinds;
	private GuiButton buttonTabOrder;
	
	private GuiButton buttonDone;
	
	public void initGui()
    {
        int i = -1;
        controlList.add(buttonCheats = new GuiSmallButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Mode: " + (mod_HowManyItems.optionsCheatsEnabled ? "Cheat Mode" : "Recipe Mode")));
        controlList.add(buttonIDs = new GuiSmallButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Item IDs: " + (mod_HowManyItems.optionsShowItemIDs ? "ON" : "OFF")));
        controlList.add(buttonCentredSearchBar = new GuiSmallButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Centred Search Bar: " + (mod_HowManyItems.optionsCentredSearchBar ? "ON" : "OFF")));
        controlList.add(buttonFastSearch = new GuiSmallButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Fast Search: " + (mod_HowManyItems.optionsFastSearch ? "ON" : "OFF")));
        controlList.add(buttonHiding = new GuiSmallButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Hide Items Mode: " + (Gui_HMI.showHiddenItems ? "ON" : "OFF")));
        controlList.add(buttonInvertedScroll = new GuiSmallButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Flip Scroll Direction: " + (mod_HowManyItems.optionsScrollInverted ? "ON" : "OFF")));

        //controlList.add(new GuiButton(++i, width / 2 - 100, height / 6 + 72 + 12, "Commands & Loadout Names..."));
        controlList.add(buttonKeybinds = new GuiButton(++i, width / 2 - 100, height / 6 + 96 + 12, "Keybinds..."));
        controlList.add(buttonTabOrder = new GuiButton(++i, width / 2 - 100, height / 6 + 120 + 12, "Recipe Viewer Settings..."));
        
        controlList.add(buttonDone = new GuiButton(++i, width / 2 - 100, height / 6 + 168, "Done"));
    }
	
	protected void actionPerformed(GuiButton guibutton)
    {
        if(guibutton == buttonCheats)
        {
        	mod_HowManyItems.optionsCheatsEnabled = !mod_HowManyItems.optionsCheatsEnabled;
        	buttonCheats.displayString = "Mode: " + (mod_HowManyItems.optionsCheatsEnabled ? "Cheat Mode" : "Recipe Mode");
        }
        else if(guibutton == buttonIDs)
        {
        	mod_HowManyItems.optionsShowItemIDs = !mod_HowManyItems.optionsShowItemIDs;
        	buttonIDs.displayString = "Item IDs: " + (mod_HowManyItems.optionsShowItemIDs ? "ON" : "OFF");
        }
        else if(guibutton == buttonCentredSearchBar)
        {
        	mod_HowManyItems.optionsCentredSearchBar = !mod_HowManyItems.optionsCentredSearchBar;
        	buttonCentredSearchBar.displayString = "Centred Search Bar: " + (mod_HowManyItems.optionsCentredSearchBar ? "ON" : "OFF");
        }
        else if(guibutton == buttonFastSearch)
        {
        	mod_HowManyItems.optionsFastSearch = !mod_HowManyItems.optionsFastSearch;
        	buttonFastSearch.displayString = "Fast Search: " + (mod_HowManyItems.optionsFastSearch ? "ON" : "OFF");
        }
        else if(guibutton == buttonHiding)
        {
        	Gui_HMI.showHiddenItems = !Gui_HMI.showHiddenItems;
        	Gui_HMI.resetItems();
        	buttonHiding.displayString = "Hide Items Mode: " + (Gui_HMI.showHiddenItems ? "ON" : "OFF");
        }
        else if(guibutton == buttonInvertedScroll)
        {
        	mod_HowManyItems.optionsScrollInverted = !mod_HowManyItems.optionsScrollInverted;
        	buttonInvertedScroll.displayString = "Flip Scroll Direction: " + (mod_HowManyItems.optionsScrollInverted ? "ON" : "OFF");
        }
        else if(guibutton == buttonDone)
        {
        	Gui_HMI.guiClosedCooldown = System.currentTimeMillis() + 100L;
            mc.displayGuiScreen(parentScreen);
            return;
        }
        else if(guibutton == buttonKeybinds)
        {
            mc.displayGuiScreen(new GuiControls_HMI(this));
            return;
        }
        else if(guibutton == buttonTabOrder)
        {
            mc.displayGuiScreen(new GuiTabOrder(this));
            return;
        }
        Gui_HMI.onSettingChanged();
    }
	
	public void drawScreen(int posX, int posY, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "HMI Options", width / 2, 20, 0xffffff);
        super.drawScreen(posX, posY, f);
        
        if(Math.abs(posX - lastMouseX) > 5 || Math.abs(posY - lastMouseY) > 5)
        {
            lastMouseX = posX;
            lastMouseY = posY;
            mouseStillTime = System.currentTimeMillis();
            return;
        }
        int k = 700;
        if(System.currentTimeMillis() < mouseStillTime + (long)k)
        {
            return;
        }
        GuiButton hoveredButton = null;
        for(Object obj : controlList) {
        	if(((GuiButton)obj).mousePressed(mc, posX, posY))
        		hoveredButton = (GuiButton)obj;
        }
        
        if(hoveredButton != null && getTooltipContent(hoveredButton) != null)
        {
        	String[] tooltip = getTooltipContent(hoveredButton);
        	int i = width / 2 - 150;
            int j = height / 6 - 5;
            if(posY <= j + 98)
            {
            	//j += 105;
                j += 182 - 11 * tooltip.length;
            }
            int j1 = i + 150 + 150;
            //int k1 = j + 84 + 10;
            int k1 = j + 11 * tooltip.length + 6;
            
            drawGradientRect(i, j, j1, k1, 0xe0000000, 0xe0000000);
            for(int l1 = 0; l1 < tooltip.length; l1++)
            {
                String line = tooltip[l1];
                fontRenderer.drawStringWithShadow(line, i + 5, j + 5 + l1 * 11, 0xdddddd);
            }
        }
    }
	
	private String[] getTooltipContent(GuiButton guibutton) {
		if(guibutton == buttonCheats)
        {
        	return new String[] {
        		"Recipe Mode",
        		"  LMB on items to see recipes and RMB to see uses",
        		"Cheat Mode",
        		"  LMB on items to spawn a stack and RMB to spawn 1",
        		"  Also enables utility buttons"
        	};
        }
		else if(guibutton == buttonIDs)
        {
			return new String[] {
	        	"Show item IDs in HowManyItems overlay"
	        };
        }
        else if(guibutton == buttonFastSearch)
        {
        	return new String[] {
        		"Automatically focus the searchbar when you press a key"
        	};
        }
        else if(guibutton == buttonHiding)
        {
        	return new String[] {
        		"View and configure hidden items",
        		"  Click to toggle an item being hidden",
        		"  Shift click to toggle items with the same ID and higher dmg",
        		"  Click and drag to toggle all selected items",
        		"",
        		"Turn off to save config"
        	};
        }
        else if(guibutton == buttonInvertedScroll)
        {
        	return new String[] {
        		"Invert page scroll direction when using mouse wheel"
        	};
        }
        else if(guibutton == buttonTabOrder)
        {
        	return new String[] {
        		"Enable/disable recipe viewer tabs",
        		"Change recipe viewer tab order",
        		"Change recipe viewer gui size option"
        	};
        }
		return null;
	}

	private int lastMouseX;
    private int lastMouseY;
    private long mouseStillTime;
	
	
	
	private GuiScreen parentScreen;
}
