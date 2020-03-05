// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import ic2.ItemBattery;
import ic2.TileEntityCompressor;
import ic2.TileEntityExtractor;
import ic2.TileEntityMacerator;
import net.minecraft.client.Minecraft;

// Referenced classes of package net.minecraft.src:
//            BaseMod, ItemRecipeBook, Item, ModLoader, 
//            ItemStack

public class mod_HowManyItems extends BaseMod
{

	public String Version()
    {
        return "v4.1.0";
    }
	
	public String Description() {
		//For mine_diver's mod menu
		return "How do I craft this again?";
	}
	
	public String Name() {
		//For mine_diver's mod menu
		return "HowManyItems";
	}
	
	
	
	public mod_HowManyItems()
    {
		thisMod = this;
		if (!configFile.exists())
			writeConfig();
		readConfig();
		ModLoader.RegisterKey(this, toggleOverlay, false);
		
		ModLoader.SetInGUIHook(this, true, false);
		ModLoader.SetInGameHook(this, true, false);
    }
	
	public static Gui_HMI hmi;
	public static boolean dontRender = false;
	private static BaseMod thisMod;
	private int lastMouseButtonPressed = -1;
	
	public boolean OnTickInGUI(Minecraft mc, GuiScreen guiscreen) {
		if(guiscreen instanceof GuiContainer || guiscreen instanceof Gui_HMI) {
			
			if(optionsEnabled) {
				if(Gui_HMI.allItems == null) {
					Gui_HMI.loadItems();
				}
				GuiContainer screen;
				if(guiscreen instanceof Gui_HMI) screen = Gui_HMI.screen;
				else screen = (GuiContainer)guiscreen;
				
				//System.out.println(screen.controlList.size() +" : " +  buttonIndex);
				if(Gui_HMI.screen != screen) {
		        	hmi = new Gui_HMI(screen);
			        hmi.setWorldAndResolution(mc, screen.width, screen.height);
		        }
				if(guiscreen != hmi && guiscreen == screen) {
					//int posX = (Mouse.getEventX() * screen.width) / mc.displayWidth;
		            //int posY = screen.height - (Mouse.getEventY() * screen.height) / mc.displayHeight - 1;
		            //if(mc.theWorld != null && mc.currentScreen != null) System.out.println("guitick");
		            ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		            int i = scaledresolution.getScaledWidth();
		            int j = scaledresolution.getScaledHeight();
		            int posX = (Mouse.getX() * i) / mc.displayWidth;
		            int posY = j - (Mouse.getY() * j) / mc.displayHeight - 1;
		            if(!dontRender) hmi.drawScreen(posX, posY);
		            if(hmi.mouseOverUI(mc, posX, posY)) {
		            	for(; Mouse.next(); hmi.handleMouseInput()) { }
		            }
		            else {

		            	if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
							hmi.searchBoxMouseClicked(posX, posY, Mouse.getEventButton());
						}
		            }
			        
					hmi.keyTyped();
				}
				else {
					hmi.modTickKeyPress = false;
				}
			}
			dontRender = false;
			
			if(Keyboard.isKeyDown(pushRecipe.keyCode) || Keyboard.isKeyDown(pushUses.keyCode)) {
				if(!keyHeldLastTick) {
					boolean getUses = Keyboard.isKeyDown(pushUses.keyCode);
					if (guiscreen instanceof GuiContainer || guiscreen instanceof Gui_HMI) {
						if(guiscreen instanceof Gui_HMI) guiscreen = Gui_HMI.screen;
						ItemStack newFilter = null;
						
						ScaledResolution scaledresolution = new ScaledResolution(ModLoader.getMinecraftInstance().gameSettings, ModLoader.getMinecraftInstance().displayWidth, ModLoader.getMinecraftInstance().displayHeight);
						int i = scaledresolution.getScaledWidth();
						int j = scaledresolution.getScaledHeight();
						int posX = (Mouse.getEventX() * i) / ModLoader.getMinecraftInstance().displayWidth;
						int posY = j - (Mouse.getEventY() * j) / ModLoader.getMinecraftInstance().displayHeight - 1;
						newFilter = itemAtPosition((GuiContainer)guiscreen, posX, posY);
						if (newFilter == null) {
							newFilter = Gui_HMI.hoverItem;
						}
						if(newFilter == null) {
							if(guiscreen instanceof GuiRecipeViewer)
								newFilter = ((GuiRecipeViewer)guiscreen).getHoverItem();
						}
						if(newFilter != null) {
							pushRecipe(guiscreen, newFilter, getUses);
						}
						else {
							if(optionsEnabled && guiscreen == Gui_HMI.screen && !Gui_HMI.searchBoxFocused() && optionsFastSearch) {
								Gui_HMI.focusSearchBox();
							}
						}
					}
				}
			}
			else if(Keyboard.isKeyDown(prevRecipe.keyCode)) {
				if(!keyHeldLastTick) {
					if ((guiscreen instanceof GuiRecipeViewer || guiscreen instanceof Gui_HMI) && !Gui_HMI.searchBoxFocused()) {
						if(guiscreen instanceof Gui_HMI && Gui_HMI.screen instanceof GuiRecipeViewer) guiscreen = Gui_HMI.screen;
						if(guiscreen instanceof GuiRecipeViewer) ((GuiRecipeViewer) guiscreen).pop();
					}
					else {
						if(optionsEnabled && guiscreen == Gui_HMI.screen && !Gui_HMI.searchBoxFocused() && optionsFastSearch)
							if(!Gui_HMI.emptySearchBox()) Gui_HMI.focusSearchBox();
					}
				}
			}
			else if(clearSearchBox.keyCode == focusSearchBox.keyCode
					&& Keyboard.isKeyDown(clearSearchBox.keyCode)) {
				
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof Gui_HMI) {
					if(System.currentTimeMillis() > focusCooldown) {
						focusCooldown = System.currentTimeMillis() + 800L;
						if(!Gui_HMI.searchBoxFocused())
						Gui_HMI.clearSearchBox();
						Gui_HMI.focusSearchBox();
					}
				}
			}
			else if(Keyboard.isKeyDown(clearSearchBox.keyCode)) {
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof Gui_HMI) {
					Gui_HMI.clearSearchBox();
				}
			}
			else if(Keyboard.isKeyDown(focusSearchBox.keyCode)) {
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof Gui_HMI) {
					if(System.currentTimeMillis() > focusCooldown) {
						focusCooldown = System.currentTimeMillis() + 800L;
						Gui_HMI.focusSearchBox();
					}
				}
			}
			else if(Keyboard.isKeyDown(allRecipes.keyCode)) {
				if (guiscreen instanceof Gui_HMI) {
					guiscreen = Gui_HMI.screen;
				}
				pushRecipe(guiscreen, null, false);
			}
			else {
				keyHeldLastTick = false;
			}
			if(Keyboard.isKeyDown(pushRecipe.keyCode) || Keyboard.isKeyDown(pushUses.keyCode) || Keyboard.isKeyDown(prevRecipe.keyCode)) {
				keyHeldLastTick = true;
			}
			
		}
		return true;
	}
	
	public boolean OnTickInGame(Minecraft minecraft)
    {
		if(minecraft.currentScreen == null && Keyboard.isKeyDown(allRecipes.keyCode) && !keyHeldLastTick) {
			keyHeldLastTick = true;
			pushRecipe(null, null, false);
		}
        return true;
    }
	
	public static boolean keyHeldLastTick = false;
	private static long focusCooldown = 0L;
	
	public static ItemStack itemAtPosition(GuiContainer gui, int x, int y) {
		Method getSlotAtPositionMethod = null;
		try {
			getSlotAtPositionMethod = GuiContainer.class.getDeclaredMethod("getSlotAtPosition", new Class[] {int.class, int.class});
		} catch (NoSuchMethodException e) {
			try {
				getSlotAtPositionMethod = GuiContainer.class.getDeclaredMethod("a", new Class[] {int.class, int.class});
			} 
			catch (NoSuchMethodException e1) { e1.printStackTrace(); }
		}
		if(getSlotAtPositionMethod != null) {
			getSlotAtPositionMethod.setAccessible(true);
			ItemStack itemstack = null;
			try {
				Slot slotAtPosition = (Slot)getSlotAtPositionMethod.invoke(gui, new Object[]{x, y});
				if(slotAtPosition != null) itemstack = slotAtPosition.getStack();
			} 
			catch (IllegalAccessException e) { e.printStackTrace(); } 
			catch (InvocationTargetException e) { e.printStackTrace(); }
			return itemstack;
		}
		return null;
	}
	
	public void KeyboardEvent(KeyBinding event)
    {
		if (event == allRecipes) {
			
		}
		else if (event == toggleOverlay) {
			if ((ModLoader.getMinecraftInstance().currentScreen instanceof GuiContainer
					|| ModLoader.getMinecraftInstance().currentScreen instanceof Gui_HMI)
					&& !Gui_HMI.searchBoxFocused()) {
				optionsEnabled = !optionsEnabled;
				if(hmi != null) hmi.toggle();
			}
		}
		else if (event == clearSearchBox) {
			
		}
    }
	
	public static void pushRecipe(GuiScreen gui, ItemStack item, boolean getUses) {
		if(ModLoader.getMinecraftInstance().thePlayer.inventory.getItemStack() == null) {
			if (gui instanceof GuiRecipeViewer) {
				((GuiRecipeViewer) gui).push(item, getUses);
			}
			else if (!Gui_HMI.searchBoxFocused() && getTabs().size() > 0){
				Minecraft mc = ModLoader.getMinecraftInstance();
				mc.setIngameNotInFocus();
				GuiRecipeViewer newgui = new GuiRecipeViewer(item, getUses, gui);
				mc.currentScreen = newgui;
				ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
				int i = scaledresolution.getScaledWidth();
				int j = scaledresolution.getScaledHeight();
	            newgui.setWorldAndResolution(mc, i, j);
	            mc.skipRenderWorld = false;
			}
		}
	}
    
    public static ArrayList<TabRecipeViewer> getTabs() {
		if (allTabs == null) {
			allTabs = new ArrayList<TabRecipeViewer>();
			allTabs.add(new TabRecipeViewerCrafting(thisMod));
			allTabs.add(new TabRecipeViewerFurnace(thisMod));
			
			List modList = ModLoader.getLoadedMods();
			for (Object obj : modList) {
				BaseMod mod = (BaseMod)obj;
				ArrayList<ItemStack> fuels = new ArrayList<ItemStack>();
				if (mod.getClass().getName() == "mod_Planes") {
					allTabs.add(new TabRecipeViewerPlanes(thisMod));
					continue;
				}
				if (mod.getClass().getName() == "mod_Uranium") {
					fuels.add(new ItemStack (mod_Uranium.uraniumDust));
					fuels.add(new ItemStack (mod_Uranium.uraniumCoal));
					fuels.add(new ItemStack (mod_Uranium.skullUranium));
					for(Block block: Block.blocksList) {
						if(block != null && ModLoader.AddAllFuel(block.blockID) > 0) 
							fuels.add(new ItemStack(block));
							
					}
					for(Item item: Item.itemsList) {
						if(item != null && ModLoader.AddAllFuel(item.shiftedIndex) > 0) fuels.add(new ItemStack(item));
					}
					allTabs.add(new TabRecipeViewerFurnace(thisMod, ReactorRecipes.smelting().getSmeltingList(), fuels, "/uraniumTextures/reactorgui.png", mod_Uranium.reactorIdle));
					continue;
				}
				if (mod.getClass().getName() == "mod_IC2") {
					fuels.add(new ItemStack(Item.redstone));
					fuels.add(new ItemStack(mod_IC2.itemBatSU));
					for(Item item: Item.itemsList) {
						if(item != null && item instanceof ItemBattery) fuels.add(new ItemStack(item));
					}
					Block machine = mod_IC2.blockMachine;
					allTabs.add(new TabRecipeViewerFurnace(thisMod, TileEntityMacerator.recipes, fuels, "/IC2sprites/GUIMacerator.png", machine, 3));
					allTabs.add(new TabRecipeViewerFurnace(thisMod, TileEntityExtractor.recipes, fuels, "/IC2sprites/GUIExtractor.png", machine, 4));
					allTabs.add(new TabRecipeViewerFurnace(thisMod, TileEntityCompressor.recipes, fuels, "/IC2sprites/GUICompressor.png", machine, 5));
					allTabs.add(new TabRecipeViewerIC2CanningMachine(thisMod, fuels, "/IC2sprites/GUICanner.png", machine, 6));
					continue;
				}
				if (mod.getClass().getName() == "mod_Aether") {
					allTabs.add(new TabRecipeViewerAether(thisMod, TileEntityEnchanter.class, new ArrayList<ItemStack>(Arrays.asList(new ItemStack(AetherItems.AmbrosiumShard))), "/aether/gui/enchanter.png", AetherBlocks.Enchanter));
					try
			        {
						Class.forName("TileEntityFreezer");
						allTabs.add(new TabRecipeViewerAether(thisMod, TileEntityFreezer.class, new ArrayList<ItemStack>(Arrays.asList(new ItemStack(AetherBlocks.Icestone))), "/aether/gui/enchanter.png", AetherBlocks.Freezer));
					} catch (ClassNotFoundException e) { }
					continue;
				}
				if (mod.getClass().getName() == "mod_BuildCraftFactory") {
					buildcraftFactoryInstalled = true;
					continue;
				}
			}
			for(TabRecipeViewer tab : modTabs) {
				allTabs.add(tab);
			}
		}
		tabs = orderTabs();
        return tabs;
	}
    
    private static ArrayList<TabRecipeViewer> orderTabs() {
    	ArrayList<TabRecipeViewer> orderedTabs = new ArrayList<TabRecipeViewer>();
    	for(int i = 0; i < indicies.length && i < allTabs.size(); i++) {
    		if(indicies[i] != -1) {
    			while(orderedTabs.size() < indicies[i] + 1)
    				orderedTabs.add(null);
				orderedTabs.set(indicies[i], allTabs.get(i));
			}
		}
    	if(allTabs.size() != indicies.length) {
			int[] newIndicies = Arrays.copyOf(indicies, allTabs.size());
			for(int i = indicies.length; i < allTabs.size(); i++) {
				newIndicies[i] = i;
				orderedTabs.add(allTabs.get(i));
			}
			indicies = newIndicies;
			writeConfig();
		}
    	while(orderedTabs.remove(null)) {}
    	return orderedTabs;
	}
    
    public static void tabOrderChanged(boolean[] tabEnabled, TabRecipeViewer[] tabOrder) {
    	for(int i = 0; i < allTabs.size(); i++) {
    		for(int j = 0; j < tabOrder.length; j++) {
    			if(allTabs.get(i).equals(tabOrder[j])) {
    				indicies[i] = j;
    				if(!tabEnabled[j]) indicies[i] = -1;
    			}
    		}
    	}
    	writeConfig();
    }

	public static void addModTab(TabRecipeViewer tab) {
    	if(tab != null) {
    		modTabs.add(tab);
    	}
    }
    
    public static void writeConfig() {
		try {
			BufferedWriter configWriter = new BufferedWriter(new FileWriter(configFile));
			configWriter.write("// Config file for HowManyItems");

			for (Field field : mod_HowManyItems.class.getFields()) {
				if (field.getName().contains("options"))
					try {
						configWriter.write(System.getProperty("line.separator") + field.getName().replaceFirst("options", "")
								+ "=" + field.get(null).toString());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
			}
			KeyBinding[] keybindList = {pushRecipe, pushUses, prevRecipe, allRecipes, clearSearchBox, focusSearchBox};
			for (KeyBinding keybind : keybindList) {
				configWriter.write(System.getProperty("line.separator") + "key_" + keybind.keyDescription + ":" + keybind.keyCode);
			}
			configWriter.write(System.getProperty("line.separator") + "hiddenItems=");
			for(int i = 0; i < Gui_HMI.hiddenItems.size(); i++) {
				if(i > 0) configWriter.write(",");
				ItemStack item = Gui_HMI.hiddenItems.get(i);
				configWriter.write(String.valueOf(item.itemID));
				if(item.getHasSubtypes()) configWriter.write(":" + String.valueOf(item.getItemDamage()));
			}
			if(indicies != null) {
				configWriter.write(System.getProperty("line.separator") + "// Below are the index values for each tab");
				configWriter.write(System.getProperty("line.separator") + "// The first value is the original index, the 2nd value is the new index");
				configWriter.write(System.getProperty("line.separator") + "// Use -1 to disable the tab");
				for (int i=0; i < indicies.length; i++){
					configWriter.write(System.getProperty("line.separator") + i + ":" + indicies[i]);
	    		}
			}
			configWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
    
    private void readConfig() {
    	try {
			BufferedReader configReader = new BufferedReader(new FileReader(configFile));
			String s;
			while ((s = configReader.readLine()) != null) {
				if (s.charAt(0) == '/' && s.charAt(1) == '/') {
					continue; // Ignore comments
				}
				if(s.startsWith("key_")) {
					String as[] = s.split(":");
					String desc = as[0].replaceFirst("key_", "");
					for(Field currentField : mod_HowManyItems.class.getFields()) {
						if(currentField.getType() == KeyBinding.class) {
							KeyBinding bind = ((KeyBinding)currentField.get(this));
							if(bind.keyDescription.equals(desc)) {
								currentField.set(this, new KeyBinding(desc, Integer.parseInt(as[1])));
								break;
							}
						}
					}
				}
				else if(s.startsWith("hiddenItems=")) {
					String as[] = s.replaceFirst("hiddenItems=", "").split(",");
					Gui_HMI.hiddenItems.clear();
					for(int i = 0; i < as.length; i++) {
						if(as[i].contains(":")) {
							String as2[] = as[i].split(":");
							Gui_HMI.hiddenItems.add(new ItemStack(Integer.parseInt(as2[0]), 1, Integer.parseInt(as2[1])));
						}
						else if(as[i].length() > 0){
							Gui_HMI.hiddenItems.add(new ItemStack(Integer.parseInt(as[i]), 1, 0));
						}
					}
				}
				else if (s.contains("=")) {
					String as[] = s.split("=");
					Field field = mod_HowManyItems.class.getField("options" + (as[0]));
					if (field.getType() == int.class) {
						field.set(this, Integer.parseInt(as[1]));
					} else if (field.getType() == boolean.class) {
						field.set(this, Boolean.parseBoolean(as[1]));
					} else if (field.getType() == String.class) {
						field.set(this, String.valueOf(as[1]));
					}
				}
				else if (s.contains(":")) {
					String as[] = s.split(":");
					
					if(Integer.parseInt(as[0]) > indicies.length - 1) {
						indicies = Arrays.copyOf(indicies, Integer.parseInt(as[0]) + 1);
					}
					indicies[Integer.parseInt(as[0])] = Integer.parseInt(as[1]);
				}
			}
			configReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
    private static ArrayList<TabRecipeViewer> tabs;
    public static ArrayList<TabRecipeViewer> allTabs;
    private static ArrayList<TabRecipeViewer> modTabs = new ArrayList<TabRecipeViewer>();
    private static int[] indicies = new int[0];
    
    private static File configFile = new File((Minecraft.getMinecraftDir()) + "/config/HowManyItems.cfg");
    
    public static boolean optionsEnabled = true;
    public static boolean optionsCheatsEnabled = false;
    public static boolean optionsShowItemIDs = false;
    public static boolean optionsCentredSearchBar = false;
	public static boolean optionsFastSearch = false;
	public static boolean optionsScrollInverted = false;
    
	public static String optionsMpGiveCommand = "/give {0} {1} {2}";
	
    public static boolean optionsRecipeViewerDraggableGui = false;
    
    public static int optionsRecipeViewerGuiWidth = 251;
    public static int optionsRecipeViewerGuiHeight = 134;
    
    public static boolean buildcraftFactoryInstalled;
    
    public static KeyBinding pushRecipe = new KeyBinding("Get Recipes", Keyboard.KEY_R);
    public static KeyBinding pushUses = new KeyBinding("Get Uses", Keyboard.KEY_U);
    public static KeyBinding prevRecipe = new KeyBinding("Previous Recipe", Keyboard.KEY_BACK);
    public static KeyBinding allRecipes = new KeyBinding("Show All Recipes", Keyboard.KEY_NONE);

    public static KeyBinding toggleOverlay = new KeyBinding("Toggle HMI", Keyboard.KEY_O);
    public static KeyBinding clearSearchBox = new KeyBinding("Clear Search", Keyboard.KEY_DELETE);
    public static KeyBinding focusSearchBox = new KeyBinding("Focus Search", Keyboard.KEY_RETURN);
    
}
