package net.glasslauncher.hmifabric;

import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.OptionButton;

public class GuiOptionsHMI extends ScreenBase {

	public GuiOptionsHMI(ScreenBase guiscreen)
    {
        parentScreen = guiscreen;
    }
	
	private Button buttonCheats;
	private Button buttonIDs;
	private Button buttonCentredSearchBar;
	private Button buttonFastSearch;
	private Button buttonHiding;
	private Button buttonInvertedScroll;
	
	private Button buttonKeybinds;
	private Button buttonTabOrder;
	
	private Button buttonDone;

	@Override
	public void init()
    {
        int i = -1;
        buttons.add(buttonCheats = new OptionButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Mode: " + (Config.cheatsEnabled ? "Cheat Mode" : "Recipe Mode")));
        buttons.add(buttonIDs = new OptionButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Item IDs: " + (Config.showItemIDs ? "ON" : "OFF")));
        buttons.add(buttonCentredSearchBar = new OptionButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Centred Search Bar: " + (Config.centredSearchBar ? "ON" : "OFF")));
        buttons.add(buttonFastSearch = new OptionButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Fast Search: " + (Config.fastSearch ? "ON" : "OFF")));
        buttons.add(buttonHiding = new OptionButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Hide Items Mode: " + (GuiOverlay.showHiddenItems ? "ON" : "OFF")));
        buttons.add(buttonInvertedScroll = new OptionButton(++i, (width / 2 - 155) + (i % 2) * 160, height / 6 + 24 * (i >> 1), "Flip Scroll Direction: " + (Config.scrollInverted ? "ON" : "OFF")));

        //buttons.add(new Button(++i, width / 2 - 100, height / 6 + 72 + 12, "Commands & Loadout Names..."));
        buttons.add(buttonKeybinds = new Button(++i, width / 2 - 100, height / 6 + 96 + 12, "Keybinds..."));
        buttons.add(buttonTabOrder = new Button(++i, width / 2 - 100, height / 6 + 120 + 12, "Recipe Viewer Settings..."));
        
        buttons.add(buttonDone = new Button(++i, width / 2 - 100, height / 6 + 168, "Done"));
    }

    @Override
	protected void buttonClicked(Button guibutton)
    {
        if(guibutton == buttonCheats)
        {
        	Config.cheatsEnabled = !Config.cheatsEnabled;
        	buttonCheats.text = "Mode: " + (Config.cheatsEnabled ? "Cheat Mode" : "Recipe Mode");
        }
        else if(guibutton == buttonIDs)
        {
        	Config.showItemIDs = !Config.showItemIDs;
        	buttonIDs.text = "Item IDs: " + (Config.showItemIDs ? "ON" : "OFF");
        }
        else if(guibutton == buttonCentredSearchBar)
        {
        	Config.centredSearchBar = !Config.centredSearchBar;
        	buttonCentredSearchBar.text = "Centred Search Bar: " + (Config.centredSearchBar ? "ON" : "OFF");
        }
        else if(guibutton == buttonFastSearch)
        {
        	Config.fastSearch = !Config.fastSearch;
        	buttonFastSearch.text = "Fast Search: " + (Config.fastSearch ? "ON" : "OFF");
        }
        else if(guibutton == buttonHiding)
        {
        	GuiOverlay.showHiddenItems = !GuiOverlay.showHiddenItems;
        	GuiOverlay.resetItems();
        	buttonHiding.text = "Hide Items Mode: " + (GuiOverlay.showHiddenItems ? "ON" : "OFF");
        }
        else if(guibutton == buttonInvertedScroll)
        {
        	Config.scrollInverted = !Config.scrollInverted;
        	buttonInvertedScroll.text = "Flip Scroll Direction: " + (Config.scrollInverted ? "ON" : "OFF");
        }
        else if(guibutton == buttonDone)
        {
        	GuiOverlay.guiClosedCooldown = System.currentTimeMillis() + 100L;
            minecraft.openScreen(parentScreen);
            return;
        }
        else if(guibutton == buttonKeybinds)
        {
            minecraft.openScreen(new GuiControlsHMI(this));
            return;
        }
        else if(guibutton == buttonTabOrder)
        {
            minecraft.openScreen(new GuiTabOrder(this));
            return;
        }
        HowManyItems.onSettingChanged();
    }

    @Override
	public void render(int posX, int posY, float f)
    {
        renderBackground();
        drawTextWithShadowCentred(textManager, "HMI Options", width / 2, 20, 0xffffff);
        super.render(posX, posY, f);
        
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
        Button hoveredButton = null;
        for(Object obj : buttons) {
        	if(((Button)obj).isMouseOver(minecraft, posX, posY))
        		hoveredButton = (Button)obj;
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
            
            fillGradient(i, j, j1, k1, 0xe0000000, 0xe0000000);
            for(int l1 = 0; l1 < tooltip.length; l1++)
            {
                String line = tooltip[l1];
                textManager.drawTextWithShadow(line, i + 5, j + 5 + l1 * 11, 0xdddddd);
            }
        }
    }
	
	private String[] getTooltipContent(Button guibutton) {
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
	
	
	
	private ScreenBase parentScreen;
}
