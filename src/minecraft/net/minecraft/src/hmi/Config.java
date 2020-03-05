package net.minecraft.src.hmi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.mod_HowManyItems;
import net.minecraft.src.hmi.tabs.Tab;

public class Config {

	public static void init() {
		if (!configFile.exists())
			writeConfig();
		readConfig();
	}
	
	public static void writeConfig() {
		try {
			BufferedWriter configWriter = new BufferedWriter(new FileWriter(configFile));
			configWriter.write("// Config file for HowManyItems");
			
			for (Field field : Config.class.getFields()) {
				if (field.getType() == boolean.class || field.getType() == String.class || field.getType() == int.class)
					try {
						configWriter.write(System.getProperty("line.separator") + field.getName()
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
			for(int i = 0; i < GuiOverlay.hiddenItems.size(); i++) {
				if(i > 0) configWriter.write(",");
				ItemStack item = GuiOverlay.hiddenItems.get(i);
				configWriter.write(String.valueOf(item.itemID));
				if(item.getHasSubtypes()) {
					configWriter.write(":" + String.valueOf(item.getItemDamage()));
					int meta = item.getItemDamage();
					for(int q = i + 1; q < GuiOverlay.hiddenItems.size() && GuiOverlay.hiddenItems.get(q).itemID == item.itemID; q++) {
						if(++meta == GuiOverlay.hiddenItems.get(q).getItemDamage()) {
							i = q;
						}
						else {
							meta--;
							break;
						}
					}
					if(meta > item.getItemDamage()) {
						configWriter.write("-" + String.valueOf(meta));
					}
				}
			}
			if(mod_HowManyItems.allTabs != null) {
				configWriter.write(System.getProperty("line.separator") + "// Below are the index values for each tab");
				configWriter.write(System.getProperty("line.separator") + "// Use -1 to disable the tab");
				for(int i = 0; i < Utils.visibleTabSize(); i++) {
					for(Tab tab : mod_HowManyItems.allTabs) {
						if(tab.index == i) {
							configWriter.write(System.getProperty("line.separator") + tab.TAB_CREATOR.getClass().getSimpleName() + ":" + tab.name() + ":" + tab.index);
						}
					}
				}
				for(Tab tab : mod_HowManyItems.allTabs) {
					if(tab.index == -1) {
						configWriter.write(System.getProperty("line.separator") + tab.TAB_CREATOR.getClass().getSimpleName() + ":" + tab.name() + ":" + tab.index);
					}
				}
			}
			configWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
    
    public static void readConfig() {
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
					for(Field currentField : Config.class.getFields()) {
						if(currentField.getType() == KeyBinding.class) {
							KeyBinding bind = ((KeyBinding)currentField.get(null));
							if(bind.keyDescription.equals(desc)) {
								currentField.set(null, new KeyBinding(desc, Integer.parseInt(as[1])));
								break;
							}
						}
					}
				}
				else if(s.startsWith("hiddenItems=")) {
					if(GuiOverlay.hiddenItems == null) {
						GuiOverlay.hiddenItems = new ArrayList<ItemStack>();
						String as[] = s.replaceFirst("hiddenItems=", "").split(",");
						for(int i = 0; i < as.length; i++) {
							if(as[i].contains(":")) {
								String as2[] = as[i].split(":");
								if(as2[1].contains("-")) {
									String meta[] = as2[1].split("-");
									int minmeta = Integer.parseInt(meta[0]);
									int maxmeta = Integer.parseInt(meta[1]);
									for(int q = minmeta; q <= maxmeta; q++) {
										GuiOverlay.hiddenItems.add(new ItemStack(Integer.parseInt(as2[0]), 1, q));
										if(minmeta > maxmeta) break;
									}
									
								}
								else GuiOverlay.hiddenItems.add(new ItemStack(Integer.parseInt(as2[0]), 1, Integer.parseInt(as2[1])));
							}
							else if(as[i].length() > 0){
								GuiOverlay.hiddenItems.add(new ItemStack(Integer.parseInt(as[i]), 1, 0));
							}
						}
					}
				}
				else if (s.contains("=")) {
					String as[] = s.split("=");
					for(Field field : Config.class.getDeclaredFields()) {
						if(field.getName().equalsIgnoreCase(as[0])) {
							if (field.getType() == int.class) {
								field.set(null, Integer.parseInt(as[1]));
							} else if (field.getType() == boolean.class) {
								field.set(null, Boolean.parseBoolean(as[1]));
							} else if (field.getType() == String.class) {
								if(as.length == 1) {
									field.set(null, "");
								}
								else {
									field.set(null, String.valueOf(as[1]));
								}
							}
						}
					}
					
				}
				else if (s.contains(":") && mod_HowManyItems.allTabs != null) {
					String as[] = s.split(":");
					for(Tab tab : mod_HowManyItems.allTabs) {
						if(tab.TAB_CREATOR.getClass().getSimpleName().equalsIgnoreCase(as[0]) 
								&& tab.name().equalsIgnoreCase(as[1])) {
							//TODO tab name
							tab.index = Integer.parseInt(as[2]);
						}
					}
				}
			}
			configReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
    
    public static ArrayList<Tab> orderTabs() {
    	ArrayList<Tab> orderedTabs = new ArrayList<Tab>();
    	for(int i = 0; i < mod_HowManyItems.allTabs.size(); i++) {
    		Tab tab = mod_HowManyItems.allTabs.get(i);
    		while(orderedTabs.size() < tab.index + 1)
				orderedTabs.add(null);
    		if(tab.index >= 0) {
    			orderedTabs.set(tab.index, tab);
    		}
		}
    	while(orderedTabs.remove(null)) {}
    	for(int i = 0; i < orderedTabs.size(); i++) {
    		orderedTabs.get(i).index = i;
		}
    	for(int i = 0; i < mod_HowManyItems.allTabs.size(); i++) {
    		Tab tab = mod_HowManyItems.allTabs.get(i);
    		if(tab.index == -2) {
    			tab.index = orderedTabs.size();
    			orderedTabs.add(tab);
    		}
    		else if(tab.index < 0) {
    			tab.index = -1;
    		}
		}
    	writeConfig();
    	return orderedTabs;
	}
    
    public static void tabOrderChanged(boolean[] tabEnabled, Tab[] tabOrder) {
    	for(int i = 0; i < mod_HowManyItems.allTabs.size(); i++) {
    		Tab tab = mod_HowManyItems.allTabs.get(i);
    		for(int j = 0; j < tabOrder.length; j++) {
    			if(tab.equals(tabOrder[j])) {
    				tab.index = j;
    				if(!tabEnabled[j]) tab.index = -1;
    			}
    		}
    	}
    	writeConfig();
    }
	
	private static File configFile = new File((Minecraft.getMinecraftDir()) + "/config/HowManyItems.cfg");
	


    public static boolean overlayEnabled = true;
    public static boolean cheatsEnabled = false;
    public static boolean showItemIDs = false;
    public static boolean centredSearchBar = false;
	public static boolean fastSearch = false;
	public static boolean scrollInverted = false;
    
	public static String mpGiveCommand = "/give {0} {1} {2}";
	public static String mpHealCommand = "";
	public static String mpTimeDayCommand = "/time set 0";
	public static String mpTimeNightCommand = "/time set 13000";
	public static String mpRainONCommand = "";
	public static String mpRainOFFCommand = "";
	
    public static boolean recipeViewerDraggableGui = false;
    
    public static int recipeViewerGuiWidth = 251;
    public static int recipeViewerGuiHeight = 134;
    
    public static KeyBinding pushRecipe = new KeyBinding("Get Recipes", Keyboard.KEY_R);
    public static KeyBinding pushUses = new KeyBinding("Get Uses", Keyboard.KEY_U);
    public static KeyBinding prevRecipe = new KeyBinding("Previous Recipe", Keyboard.KEY_BACK);
    public static KeyBinding allRecipes = new KeyBinding("Show All Recipes", Keyboard.KEY_NONE);

    public static KeyBinding toggleOverlay = new KeyBinding("Toggle HMI", Keyboard.KEY_O);
    public static KeyBinding clearSearchBox = new KeyBinding("Clear Search", Keyboard.KEY_DELETE);
    public static KeyBinding focusSearchBox = new KeyBinding("Focus Search", Keyboard.KEY_RETURN);
}
