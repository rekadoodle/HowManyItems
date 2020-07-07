package net.minecraft.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.src.hmi.*;
import net.minecraft.src.hmi.tabs.*;

public class mod_HowManyItems extends BaseMod
{

	@Override
	public void ModsLoaded() {
		if(ModLoader.isModLoaded("mod_FCBetterThanWolves")) {
			TabUtils.btwHandler = ((TabHandler) Utils.getHandler("btw"));
		}
	}
	
	//TODO
	//UPDATE VERSION
	//code cleanup
	//inventory saves
	
	//CHANGELOG
	//fixed crash issue with minecolony
	
	/**
	 * @deprecated Use addTab()
	 */
	public static void addModTab(Tab tab) {
    	addTab(tab);
    }
	
	//Use this if you are a making a mod that adds a tab
	public static void addTab(Tab tab) {
    	if(tab != null) {
    		modTabs.add(tab);
    	}
    }
	
	public static void addGuiToBlock(Class<? extends GuiContainer> gui, ItemStack item) {
    	TabUtils.putItemGui(gui, item);
    }
	
	public static void addWorkBenchGui(Class<? extends GuiContainer> gui) {
		TabUtils.addWorkBenchGui(gui);
	}
	
	public static void addEquivalentWorkbench(ItemStack item) {
		TabUtils.addEquivalentWorkbench(item);
	}
	
	public static void addEquivalentFurnace(ItemStack item) {
		TabUtils.addEquivalentFurnace(item);
	}
	
	public String Version()
    {
        return "PRE c19";
    }

	//For mine_diver's mod menu
	public String Name() {
		return "How Many Items";
	}
	
	public String Description() {
		return "TMI but cooler.";
	}

	public String Icon() {
		return Utils.getResource("modmenu.png");
	}
	
	public mod_HowManyItems()
    {
		thisMod = this;
		Config.init();
		ModLoader.RegisterKey(this, Config.toggleOverlay, false);
		
		ModLoader.SetInGUIHook(this, true, false);
		ModLoader.SetInGameHook(this, true, false);
    }
	
	private GuiOverlay overlay;
	public static mod_HowManyItems thisMod;
	
	public static void onSettingChanged() {
		if(thisMod.overlay != null) thisMod.overlay.initGui();
		Config.writeConfig();
	}
	
	public boolean OnTickInGUI(Minecraft mc, GuiScreen guiscreen) {
		if(guiscreen instanceof GuiContainer) {
			GuiContainer screen = (GuiContainer)guiscreen;
			if(Config.overlayEnabled) {
				if(GuiOverlay.screen != screen || overlay == null || screen.width != overlay.width || screen.height != overlay.height
		    			|| screen.xSize != overlay.xSize || screen.ySize != overlay.ySize) {
					overlay = new GuiOverlay(screen);
		        }
				overlay.onTick();
			}
			Utils.drawStoredToolTip();
			if(Keyboard.isKeyDown(Config.pushRecipe.keyCode) || Keyboard.isKeyDown(Config.pushUses.keyCode)) {
				if(!keyHeldLastTick) {
					boolean getUses = Keyboard.isKeyDown(Config.pushUses.keyCode);
					if (guiscreen instanceof GuiContainer) {
						ItemStack newFilter = null;
						
						ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
						int i = scaledresolution.getScaledWidth();
						int j = scaledresolution.getScaledHeight();
						int posX = (Mouse.getEventX() * i) / mc.displayWidth;
						int posY = j - (Mouse.getEventY() * j) / mc.displayHeight - 1;
						newFilter = Utils.hoveredItem((GuiContainer)guiscreen, posX, posY);
						if (newFilter == null) {
							newFilter = GuiOverlay.hoverItem;
						}
						if(newFilter == null) {
							if(guiscreen instanceof GuiRecipeViewer)
								newFilter = ((GuiRecipeViewer)guiscreen).getHoverItem();
						}
						if(newFilter != null) {
							pushRecipe(guiscreen, newFilter, getUses);
						}
						else {
							if(Config.overlayEnabled && guiscreen == GuiOverlay.screen && !GuiOverlay.searchBoxFocused() && Config.fastSearch) {
								GuiOverlay.focusSearchBox();
							}
						}
					}
				}
			}
			else if(Keyboard.isKeyDown(Config.prevRecipe.keyCode)) {
				if(!keyHeldLastTick) {
					if ((guiscreen instanceof GuiRecipeViewer || guiscreen instanceof GuiOverlay) && !GuiOverlay.searchBoxFocused()) {
						if(guiscreen instanceof GuiOverlay && GuiOverlay.screen instanceof GuiRecipeViewer) guiscreen = GuiOverlay.screen;
						if(guiscreen instanceof GuiRecipeViewer) ((GuiRecipeViewer) guiscreen).pop();
					}
					else {
						if(Config.overlayEnabled && guiscreen == GuiOverlay.screen && !GuiOverlay.searchBoxFocused() && Config.fastSearch)
							if(!GuiOverlay.emptySearchBox()) GuiOverlay.focusSearchBox();
					}
				}
			}
			else if(Config.clearSearchBox.keyCode == Config.focusSearchBox.keyCode
					&& Keyboard.isKeyDown(Config.clearSearchBox.keyCode)) {
				
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof GuiOverlay) {
					if(System.currentTimeMillis() > focusCooldown) {
						focusCooldown = System.currentTimeMillis() + 800L;
						if(!GuiOverlay.searchBoxFocused())
						GuiOverlay.clearSearchBox();
						GuiOverlay.focusSearchBox();
					}
				}
			}
			else if(Keyboard.isKeyDown(Config.clearSearchBox.keyCode)) {
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof GuiOverlay) {
					GuiOverlay.clearSearchBox();
				}
			}
			else if(Keyboard.isKeyDown(Config.focusSearchBox.keyCode)) {
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof GuiOverlay) {
					if(System.currentTimeMillis() > focusCooldown) {
						focusCooldown = System.currentTimeMillis() + 800L;
						GuiOverlay.focusSearchBox();
					}
				}
			}
			else if(Keyboard.isKeyDown(Config.allRecipes.keyCode)) {
				if (guiscreen instanceof GuiOverlay) {
					guiscreen = GuiOverlay.screen;
				}
				pushRecipe(guiscreen, null, false);
			}
			else {
				keyHeldLastTick = false;
			}
			if(Keyboard.isKeyDown(Config.pushRecipe.keyCode) || Keyboard.isKeyDown(Config.pushUses.keyCode) || Keyboard.isKeyDown(Config.prevRecipe.keyCode)) {
				keyHeldLastTick = true;
			}
			
		}
		return true;
	}
	
	public boolean OnTickInGame(Minecraft minecraft)
    {
		if(minecraft.currentScreen == null && Keyboard.isKeyDown(Config.allRecipes.keyCode) && !keyHeldLastTick) {
			keyHeldLastTick = true;
			pushRecipe(null, null, false);
		}
        return true;
    }
	
	public static boolean keyHeldLastTick = false;
	private static long focusCooldown = 0L;
	
	public void KeyboardEvent(KeyBinding event)
    {
		if (event == Config.toggleOverlay) {
			if (ModLoader.isGUIOpen(GuiContainer.class) && !GuiOverlay.searchBoxFocused()) {
				Config.overlayEnabled = !Config.overlayEnabled;
				Config.writeConfig();
				if(overlay != null) overlay.toggle();
			}
		}
    }
	
	public static void pushRecipe(GuiScreen gui, ItemStack item, boolean getUses) {
		if(Utils.mc.thePlayer.inventory.getItemStack() == null) {
			if (gui instanceof GuiRecipeViewer) {
				((GuiRecipeViewer) gui).push(item, getUses);
			}
			else if (!GuiOverlay.searchBoxFocused() && getTabs().size() > 0){
				Utils.mc.setIngameNotInFocus();
				GuiRecipeViewer newgui = new GuiRecipeViewer(item, getUses, gui);
				Utils.mc.currentScreen = newgui;
				ScaledResolution scaledresolution = new ScaledResolution(Utils.mc.gameSettings, Utils.mc.displayWidth, Utils.mc.displayHeight);
				int i = scaledresolution.getScaledWidth();
				int j = scaledresolution.getScaledHeight();
	            newgui.setWorldAndResolution(Utils.mc, i, j);
	            Utils.mc.skipRenderWorld = false;
			}
		}
	}
	
	public static void pushTabBlock(GuiScreen gui, ItemStack item) {
		if (gui instanceof GuiRecipeViewer) {
			((GuiRecipeViewer) gui).pushTabBlock(item);
		}
		else if (!GuiOverlay.searchBoxFocused() && getTabs().size() > 0){
			Utils.mc.setIngameNotInFocus();
			GuiRecipeViewer newgui = new GuiRecipeViewer(item, gui);
			Utils.mc.currentScreen = newgui;
			ScaledResolution scaledresolution = new ScaledResolution(Utils.mc.gameSettings, Utils.mc.displayWidth, Utils.mc.displayHeight);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
	        newgui.setWorldAndResolution(Utils.mc, i, j);
	        Utils.mc.skipRenderWorld = false;
		}
	}
	
	//Used to avoid reflection
	public static void drawRect(int i, int j, int k, int l, int i1) {
		Utils.gui.drawRect(i, j, k, l, i1);
	}
	
    public static ArrayList<Tab> getTabs() {
    	if(tabs == null) {
    		allTabs = new ArrayList<Tab>();
    		
    		TabUtils.loadTabs(allTabs, thisMod);
    		
    		for(Tab tab : modTabs) {
    			allTabs.add(tab);
    		}
    		Config.readConfig();
    		tabs = Config.orderTabs();
    	}
        return tabs;
	}
    
    public static void tabOrderChanged(boolean[] tabEnabled, Tab[] tabOrder) {
    	Config.tabOrderChanged(tabEnabled, tabOrder);
		tabs = Config.orderTabs();
    }
    
    private static ArrayList<Tab> tabs;
    public static ArrayList<Tab> allTabs;
    private static ArrayList<Tab> modTabs = new ArrayList<Tab>();

	
}
