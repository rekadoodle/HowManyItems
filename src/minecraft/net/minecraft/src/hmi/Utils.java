package net.minecraft.src.hmi;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.hmi.tabs.Tab;

public class Utils {
	private static ArrayList<ItemStack> allItems;
	private static Method getSlotAtPositionMethod = getMethod(GuiContainer.class, new String[] {"getSlotAtPosition", "a"}, new Class<?>[] {int.class, int.class});
	public static RenderItem itemRenderer = new RenderItem();
	public static Random rand = new Random();
	public static Gui gui = new Gui();
	public static Minecraft mc = ModLoader.getMinecraftInstance();
	
	private static final List<String> loadedResources = new ArrayList<String>();
	private static final List<String> missingResources = new ArrayList<String>();
	private static final String resourcesFolder = "/hmi/resources/";
	
	//clean mine_diver code
	//Used for easy reflection with obfuscated or regular fields
	public static final Field getField(Class<?> target, String names[]) {
		for (Field field : target.getDeclaredFields()) {
			for (String name : names) {
				if (field.getName() == name) {
					field.setAccessible(true);
					return field;
				}
			}
		}
		return null;
	}
	
	//clean mine_diver code
	//Used for easy reflection with obfuscated or regular methods
	public static final Method getMethod(Class<?> target, String names[], Class<?> types[]) {
		for (String name : names) {
			try {
				Method method = target.getDeclaredMethod(name, types);
				method.setAccessible(true);
				return method;
			}  
			catch (NoSuchMethodException e) { /* Do nothing */}
        }
        return null;
    }
	
	//Returns the translated name of an itemstack with its ID if config allows and ID is wanted
	public static String getNiceItemName(ItemStack item, boolean withID) {
		String s = StringTranslate.getInstance().translateNamedKey(item.getItemName());
		if(s == null || s.length() == 0) {
			s = item.getItemName();
			if(s == null) s = "null";
		}
		if(Config.showItemIDs && withID) {
			s += " " + item.itemID;
			if(item.getHasSubtypes()) s+= ":" + item.getItemDamage();
		}
		return s;
	}
	
	//Returns the translated name of an itemstack with its ID if config allows
	public static String getNiceItemName(ItemStack item) {
		return getNiceItemName(item, true);
	}
	
	//Returns the item that the user is hovering in their inventory
	public static ItemStack hoveredItem(GuiContainer gui, int posX, int posY) {
		try {
			Slot slotAtPosition = (Slot)getSlotAtPositionMethod.invoke(gui, new Object[]{posX, posY});
			if(slotAtPosition != null) return slotAtPosition.getStack();
		} 
		catch (Exception e) { e.printStackTrace(); } 
		return null;
	}
	
	//Returns the list of all blocks and items (that I can find)
	public static ArrayList<ItemStack> itemList() {
		if(allItems == null) {
;			allItems = new ArrayList<ItemStack>();
			
			Item[] mcItemsList = Item.itemsList;
	        for(int j = 0; j < mcItemsList.length; j++)
	        {
	            Item item = mcItemsList[j];
	            if(item == null)
	            {
	                continue;
	            }
	            HashSet<String> currentItemNames = new HashSet<String>();
	            for(int dmg = 0;; dmg++)
	            {
	                ItemStack itemstack = new ItemStack(item, 1, dmg);
	                for(ItemStack hiddenItem : GuiOverlay.hiddenItems) {
	                	if(itemstack.isItemEqual(hiddenItem)) {
	                		itemstack = hiddenItem;
	                		break;
	                	}
	                }
	                try
	                {
	                    int l = item.getIconIndex(itemstack);
	                    String s = (new StringBuilder()).append(StringTranslate.getInstance().translateNamedKey(itemstack.getItemName())).toString();
	                    if(s.length() == 0) s = (new StringBuilder()).append(itemstack.getItemName()).append("@").append(l).toString();
	                    if(dmg >= 4 && (s.contains(String.valueOf(dmg)) || s.contains(String.valueOf(dmg + 1)) || s.contains(String.valueOf(dmg - 1)))){
	                    	break;
	                    }
	                    s = (new StringBuilder(s)).append("@").append(l).toString();
	                    //System.out.println(s);
	                    if(!currentItemNames.contains(s))
	                    {
	                        allItems.add(itemstack);
	                        currentItemNames.add(s);
	                        continue;
	                    }
	                    else {
	                    	break;
	                    }
	                }
	                catch(NullPointerException nullpointerexception) { }
	                catch(IndexOutOfBoundsException indexoutofboundsexception) { }
	                break;
	            }
	        }
	        
	        List recipes = CraftingManager.getInstance().getRecipeList();
	        recipeLoop : for(Iterator iterator = recipes.iterator(); iterator.hasNext();)
	        {
	            IRecipe irecipe = (IRecipe)iterator.next();
	            if(irecipe != null && irecipe.getRecipeOutput() != null && irecipe.getRecipeOutput().getItem() != null) {
	            	ItemStack itemstack = new ItemStack(irecipe.getRecipeOutput().getItem(), 1, irecipe.getRecipeOutput().getItemDamage());
	                for(ItemStack hiddenItem : GuiOverlay.hiddenItems) {
	                	if(itemstack.isItemEqual(hiddenItem)) {
	                		itemstack = hiddenItem;
	                		break;
	                	}
	                }
		            if(!itemstack.getHasSubtypes()) {
		            	continue recipeLoop;
		            }
		            addItemInOrder(allItems, itemstack);
	            }
	        }
	        TabUtils.addHiddenModItems(allItems);
			
		}
		return allItems;
	}
	
	public static void addItemInOrder(ArrayList<ItemStack> itemList, ItemStack itemstack) {
		for(ItemStack item : itemList) {
			if(item.isItemEqual(itemstack)) {
        		return;
        	}
        	if(item.itemID > itemstack.itemID || (item.itemID == itemstack.itemID && item.getItemDamage() > itemstack.getItemDamage())) {
        		itemList.add(itemList.indexOf(item), itemstack);
        		return;
        	}
        }
	}
	
	//Returns the default list of items that are hidden in the overlay
	public static ArrayList<ItemStack> hiddenItems = new ArrayList<ItemStack>();
	static {
		hiddenItems.add(new ItemStack(Block.waterStill));
		hiddenItems.add(new ItemStack(Block.lavaStill));
		hiddenItems.add(new ItemStack(Block.blockBed));
		hiddenItems.add(new ItemStack(Block.tallGrass));
		hiddenItems.add(new ItemStack(Block.deadBush));
		hiddenItems.add(new ItemStack(Block.pistonExtension));
		hiddenItems.add(new ItemStack(Block.pistonMoving));
		hiddenItems.add(new ItemStack(Block.stairDouble));
		hiddenItems.add(new ItemStack(Block.redstoneWire));
		hiddenItems.add(new ItemStack(Block.crops));
		hiddenItems.add(new ItemStack(Block.tilledField));
		hiddenItems.add(new ItemStack(Block.stoneOvenActive));
		hiddenItems.add(new ItemStack(Block.signPost));
		hiddenItems.add(new ItemStack(Block.doorWood));
		hiddenItems.add(new ItemStack(Block.signWall));
		hiddenItems.add(new ItemStack(Block.doorSteel));
		hiddenItems.add(new ItemStack(Block.oreRedstoneGlowing));
		hiddenItems.add(new ItemStack(Block.torchRedstoneIdle));
		hiddenItems.add(new ItemStack(Block.reed));
		hiddenItems.add(new ItemStack(Block.cake));
		hiddenItems.add(new ItemStack(Block.redstoneRepeaterIdle));
		hiddenItems.add(new ItemStack(Block.redstoneRepeaterActive));
		hiddenItems.add(new ItemStack(Block.lockedChest));
	}
	
	//Returns the number of enabled tabs
	public static int visibleTabSize() {
		int largestIndex = -1;
		for(Tab tab : mod_HowManyItems.allTabs) {
			if(tab.index > largestIndex)
				largestIndex = tab.index;
		}
		return largestIndex + 1;
	}
	
	public static void drawScaledItem(ItemStack item, int x, int y, int length) {
		GL11.glPushMatrix();
		float scaleFactor = (float)length / 16;
		GL11.glScalef(scaleFactor, scaleFactor, 1);
		drawItemStack((int)(x / scaleFactor), (int)(y / scaleFactor), item, true);
		GL11.glPopMatrix();
	}
	
	public static void drawRect(int i, int j, int k, int l, int colour) {
		disableLighting();
		mod_HowManyItems.drawRect(i, j, k, l, colour);
	}
	
	public static void drawSlot(int x, int y, int colour) {
		drawRect(x, y, x + 18, y + 18, colour);
	}

	public static void drawArrow(int x, int y) {
		disableLighting();
		bindTexture();
		gui.drawTexturedModalRect(x, y, 0, 63, 25, 25);
	}
	
	public static void drawSlot(int x, int y) {
		disableLighting();
		bindTexture();
		gui.drawTexturedModalRect(x, y, 25, 63, 25, 25);
	}
	
	public static void drawTooltip(String s, int x, int y) {
		//stores tooltip info, to be drawn after overlay
		tooltipText = s;
		tooltipX = x;
		tooltipY = y;
	}
	
	public static void drawStoredToolTip() {
		if(tooltipText != null) {
			disableLighting();
			int k1 = tooltipX + 12;
			int i2 = tooltipY - 12;
			int j2 = mc.fontRenderer.getStringWidth(tooltipText);
			drawRect(k1 - 3, i2 - 3, k1 + j2 + 3, i2 + 8 + 3, 0xc0000000);
			mc.fontRenderer.drawStringWithShadow(tooltipText, k1, i2, -1);
			tooltipText = null;
			postRender();
		}
	}
	
	public static void disableLighting() {
		if(lighting != Boolean.FALSE) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(2896 /*GL_LIGHTING*/);
			GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
			lighting = false;
		}
	}
	
	private static void enableLighting() {
		if(lighting != Boolean.TRUE) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(2896 /*GL_LIGHTING*/);
	        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
			lighting = true;
		}
	}
	
	public static void bindTexture(String texturePath) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(texturePath));
		localTextureBound = false;
	}
	
	public static void bindTexture() {
		if(!localTextureBound) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(mc.renderEngine.getTexture(getResource("icons.png")));
			localTextureBound = true;
		}
	}
	
	public static void drawItemStack(int x, int y, ItemStack item, boolean drawOverlay) {
		localTextureBound = false;
		enableItemLighting();
		itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, item, x, y);
		if(drawOverlay) {
			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, item, x, y);
		}
	}
	
	private static void enableItemLighting() {
		enableLighting();
		if(!itemLighting) {
			GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
			GL11.glPushMatrix();
			GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
			RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();
			itemLighting = true;
		}
	}
	
	private static boolean itemLighting;
	private static boolean localTextureBound;
	public static Boolean lighting;
	private static String tooltipText;
	private static int tooltipX;
	private static int tooltipY;
	
	public static void drawString(String s, int x, int y) {
		disableLighting();
		int k1 = (x) + 12;
		int i2 = y - 12;
		mc.fontRenderer.drawStringWithShadow(s, k1, i2, -1);
	}
	
	public static void preRender() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		lighting = null;
		itemLighting = false;
		localTextureBound = false;
	}

	public static void postRender() {
		lighting = null;
		RenderHelper.disableStandardItemLighting();
		enableLighting();
	}
	
	public static boolean classExists(String className) {
		try {
			Class.forName(className);
		} 
		catch (ClassNotFoundException e) {
			return false; 
		}
		return true;
	}
	
	public static boolean nmsClassExists(String className) {
		return classExists(className) || classExists("net.minecraft.src." + className);
	}
	
	public static String getResource(String resource) {
		resourceExists(resource);
		return new StringBuilder().append(resourcesFolder).append(resource).toString();
	}
	
	public static boolean resourceExists(String resource) {
		if(loadedResources.contains(resource)) {
			return true;
		}
		if(missingResources.contains(resource)) {
			return false;
		}
		String location = new StringBuilder().append(resourcesFolder).append(resource).toString();
		if(Utils.class.getResource(location) != null) {
			loadedResources.add(resource);
			return true;
		}
		else  {
			missingResources.add(resource);
			String error = "Missing file " + new File("bin\\minecraft.jar").getAbsolutePath().replace("\\", "/") + location;
			if(System.getProperty("java.class.path").toLowerCase().contains("eclipse"))
	        {
				logError(new String[] {error, "Alternate Location " + mod_HowManyItems.class.getClassLoader().getResource("").getPath().replaceFirst("/*$", "") + location});
	        }
			else {
				logError(error);
			}
			return false;
		}
	}
	
	public static void logError(String... lines) {
		System.out.println(new StringBuilder().append("HMI ERROR: ").append(lines[0]).toString());
		for (String message : lines) {
			if(message == lines[0]) continue;
			System.out.println(new StringBuilder().append('\t').append(message).toString());
		}
	}
	
	public static Object getHandler(String path) {
		try { 
			return Utils.class.getClassLoader().loadClass(Utils.class.getPackage().getName() + ".references." + path + ".ConcreteHandler").newInstance(); 
		}
		catch (Throwable e) { e.printStackTrace(); return null; } 
	}
	
	public static boolean isKeyDown(KeyBinding keybind) {
		return isKeyDown(keybind.keyCode);
	}
	
	public static boolean isKeyDown(int keyCode) {
		return Keyboard.isKeyDown(keyCode) && keyCodeValid(keyCode);
	}
	
	public static boolean keyEquals(int keyCode, KeyBinding keybind) {
		return keyCode == keybind.keyCode && keybindValid(keybind);
	}
	
	public static boolean keybindValid(KeyBinding keybind) {
		return keyCodeValid(keybind.keyCode);
	}
	
	public static boolean keyCodeValid(int keyCode) {
		return keyCode != Keyboard.KEY_NONE;
	}
	
	public static class EasyField<T> {

		private static final EasyField<Integer> modifiersField = new EasyField<Integer>(Field.class, "modifiers");
		public final Field field;
		
		public EasyField(Object value, Class<?> target) {
			this(value, target, null);
		}
		
		public EasyField(Object value, Class<?> target, Object instance) {
			Field correctField = null;
			for (Field field : target.getDeclaredFields()) {
				try {
					if(field.get(instance) == value) {
						field.setAccessible(true);
						correctField = field;
						break;
					}
				} 
				catch (Exception e) { } 
			}
			this.field = correctField;
			if(this.field == null)
			logError("Failed to locate field " + value.getClass().getSimpleName() + " in class " + target.getSimpleName());
		}
		
		public EasyField(Class<?> target, String... names) {
			for (Field field : target.getDeclaredFields()) {
				for (String name : names) {
					if (field.getName() == name) {
						field.setAccessible(true);
						this.field = field;
						return;
					}
				}
			}
			this.field = null;
			logError("Failed to locate field " + names[0] + " in class " + target.getSimpleName());
		}
		
		public boolean exists() {
			return field != null;
		}
		
		@SuppressWarnings("unchecked")
		public T get(Object instance) {
			try {
				return (T) field.get(instance);
			}
			catch (Exception e) { e.printStackTrace(); }
			return null;
		}
		
		public T get() {
			return this.get(null);
		}
		
		public void set(Object instance, T value) {
			try {
				field.set(instance, value);
			} 
			catch (Exception e) { e.printStackTrace(); }
		}
		
		public void set(T value) {
			this.set(null, value);
		}
		
		public void removeFinalModifier() {
			modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
		}
		
	}
}
