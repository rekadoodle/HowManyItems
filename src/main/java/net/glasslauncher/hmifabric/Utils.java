package net.glasslauncher.hmifabric;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.hmifabric.tabs.Tab;
import net.minecraft.block.BlockBase;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.RenderHelper;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.container.slot.Slot;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeRegistry;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

public class Utils {
	private static ArrayList<ItemInstance> allItems;
	private static Method getSlotAtPositionMethod = getMethod(ContainerBase.class, new String[] {"getSlot", "method_986"}, new Class<?>[] {int.class, int.class});
	public static ItemRenderer itemRenderer = new ItemRenderer();
	public static Random rand = new Random();
	public static DrawableHelper gui = new DrawableHelper();
	public static Minecraft getMC() { return (Minecraft) FabricLoader.getInstance().getGameInstance();}
	
	private static final List<String> loadedResources = new ArrayList<>();
	private static final List<String> missingResources = new ArrayList<>();
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
	public static String getNiceItemName(ItemInstance item, boolean withID) {
		String s = TranslationStorage.getInstance().translate(item.getTranslationKey() + ".name");
		if(s == null || s.length() == 0) {
			s = item.getTranslationKey();
			if(s == null) s = "null";
		}
		if(Config.showItemIDs && withID) {
			s += " " + item.itemId;
			if(item.method_719()) s+= ":" + item.getDamage();
		}
		return s;
	}
	
	//Returns the translated name of an itemstack with its ID if config allows
	public static String getNiceItemName(ItemInstance item) {
		return getNiceItemName(item, true);
	}
	
	//Returns the item that the user is hovering in their inventory
	public static ItemInstance hoveredItem(ContainerBase gui, int posX, int posY) {
		try {
			Slot slotAtPosition = (Slot)getSlotAtPositionMethod.invoke(gui, new Object[]{posX, posY});
			if(slotAtPosition != null) return slotAtPosition.getItem();
		} 
		catch (Exception e) { e.printStackTrace(); } 
		return null;
	}
	
	//Returns the list of all blocks and items (that I can find)
	public static ArrayList<ItemInstance> itemList() {
		if(allItems == null) {
;			allItems = new ArrayList<>();
			
			ItemBase[] mcItemsList = ItemBase.byId;
	        for(int j = 0; j < mcItemsList.length; j++)
	        {
	            ItemBase item = mcItemsList[j];
	            if(item == null)
	            {
	                continue;
	            }
	            HashSet<String> currentItemNames = new HashSet<>();
	            for(int dmg = 0;; dmg++)
	            {
	                ItemInstance itemstack = new ItemInstance(item, 1, dmg);
	                for(ItemInstance hiddenItem : GuiOverlay.hiddenItems) {
	                	if(itemstack.isEqualIgnoreFlags(hiddenItem)) {
	                		itemstack = hiddenItem;
	                		break;
	                	}
	                }
	                try
	                {
	                    int l = item.getTexturePosition(itemstack);
	                    String s = TranslationStorage.getInstance().translate(itemstack.getTranslationKey());
	                    if(s.length() == 0) s = itemstack.getTranslationKey() + "@" + l;
	                    if(dmg >= 4 && (s.contains(String.valueOf(dmg)) || s.contains(String.valueOf(dmg + 1)) || s.contains(String.valueOf(dmg - 1)))){
	                    	break;
	                    }
	                    s = s + "@" + l;
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
	        
	        List recipes = RecipeRegistry.getInstance().getRecipes();
	        recipeLoop : for(Iterator iterator = recipes.iterator(); iterator.hasNext();)
	        {
	            Recipe irecipe = (Recipe)iterator.next();
	            if(irecipe != null && irecipe.getOutput() != null && irecipe.getOutput().getType() != null) {
	            	ItemInstance itemstack = new ItemInstance(irecipe.getOutput().getType(), 1, irecipe.getOutput().getDamage());
	                for(ItemInstance hiddenItem : GuiOverlay.hiddenItems) {
	                	if(itemstack.isEqualIgnoreFlags(hiddenItem)) {
	                		itemstack = hiddenItem;
	                		break;
	                	}
	                }
		            if(!itemstack.method_719()) {
		            	continue recipeLoop;
		            }
		            addItemInOrder(allItems, itemstack);
	            }
	        }
	        TabUtils.addHiddenModItems(allItems);
			
		}
		return allItems;
	}
	
	public static void addItemInOrder(ArrayList<ItemInstance> itemList, ItemInstance itemstack) {
		for(ItemInstance item : itemList) {
			if(item.isEqualIgnoreFlags(itemstack)) {
        		return;
        	}
        	if(item.itemId > itemstack.itemId || (item.itemId == itemstack.itemId && item.getDamage() > itemstack.getDamage())) {
        		itemList.add(itemList.indexOf(item), itemstack);
        		return;
        	}
        }
	}
	
	//Returns the default list of items that are hidden in the overlay
	public static ArrayList<ItemInstance> hiddenItems = new ArrayList<>();
	static {
		hiddenItems.add(new ItemInstance(BlockBase.STILL_WATER));
		hiddenItems.add(new ItemInstance(BlockBase.STILL_LAVA));
		hiddenItems.add(new ItemInstance(BlockBase.BED));
		hiddenItems.add(new ItemInstance(BlockBase.TALLGRASS));
		hiddenItems.add(new ItemInstance(BlockBase.DEADBUSH));
		hiddenItems.add(new ItemInstance(BlockBase.PISTON_HEAD));
		hiddenItems.add(new ItemInstance(BlockBase.MOVING_PISTON));
		hiddenItems.add(new ItemInstance(BlockBase.DOUBLE_STONE_SLAB));
		hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_DUST));
		hiddenItems.add(new ItemInstance(BlockBase.CROPS));
		hiddenItems.add(new ItemInstance(BlockBase.FARMLAND));
		hiddenItems.add(new ItemInstance(BlockBase.FURNACE_LIT));
		hiddenItems.add(new ItemInstance(BlockBase.STANDING_SIGN));
		hiddenItems.add(new ItemInstance(BlockBase.DOOR_WOOD));
		hiddenItems.add(new ItemInstance(BlockBase.WALL_SIGN));
		hiddenItems.add(new ItemInstance(BlockBase.DOOR_IRON));
		hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_ORE_LIT));
		hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_TORCH));
		hiddenItems.add(new ItemInstance(BlockBase.REEDS));
		hiddenItems.add(new ItemInstance(BlockBase.CAKE));
		hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_REPEATER));
		hiddenItems.add(new ItemInstance(BlockBase.REDSTONE_REPEATER_LIT));
		hiddenItems.add(new ItemInstance(BlockBase.LOCKED_CHEST));
	}
	
	//Returns the number of enabled tabs
	public static int visibleTabSize() {
		int largestIndex = -1;
		for(Tab tab : HowManyItems.allTabs) {
			if(tab.index > largestIndex)
				largestIndex = tab.index;
		}
		return largestIndex + 1;
	}
	
	public static void drawScaledItem(ItemInstance item, int x, int y, int length) {
		GL11.glPushMatrix();
		float scaleFactor = (float)length / 16;
		GL11.glScalef(scaleFactor, scaleFactor, 1);
		drawItemStack((int)(x / scaleFactor), (int)(y / scaleFactor), item, true);
		GL11.glPopMatrix();
	}
	
	public static void drawRect(int i, int j, int k, int l, int colour) {
		disableLighting();
		HowManyItems.drawRect(i, j, k, l, colour);
	}
	
	public static void drawSlot(int x, int y, int colour) {
		drawRect(x, y, x + 18, y + 18, colour);
	}

	public static void drawArrow(int x, int y) {
		disableLighting();
		bindTexture();
		gui.blit(x, y, 0, 63, 25, 25);
	}
	
	public static void drawSlot(int x, int y) {
		disableLighting();
		bindTexture();
		gui.blit(x, y, 25, 63, 25, 25);
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
			int j2 = getMC().textRenderer.getTextWidth(tooltipText);
			drawRect(k1 - 3, i2 - 3, k1 + j2 + 3, i2 + 8 + 3, 0xc0000000);
			getMC().textRenderer.drawTextWithShadow(tooltipText, k1, i2, -1);
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
		getMC().textureManager.bindTexture(getMC().textureManager.getTextureId(texturePath));
		localTextureBound = false;
	}
	
	public static void bindTexture() {
		if(!localTextureBound) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			getMC().textureManager.bindTexture(getMC().textureManager.getTextureId(getResource("/assets/hmifabric/textures/icons.png")));
			localTextureBound = true;
		}
	}
	
	public static void drawItemStack(int x, int y, ItemInstance item, boolean drawOverlay) {
		localTextureBound = false;
		enableItemLighting();
		itemRenderer.method_1487(getMC().textRenderer, getMC().textureManager, item, x, y);
		if(drawOverlay) {
			itemRenderer.method_1488(getMC().textRenderer, getMC().textureManager, item, x, y);
		}
	}
	
	private static void enableItemLighting() {
		enableLighting();
		if(!itemLighting) {
			GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
			GL11.glPushMatrix();
			GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
			RenderHelper.enableLighting();
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
		getMC().textRenderer.drawTextWithShadow(s, k1, i2, -1);
	}
	
	public static void preRender() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		lighting = null;
		itemLighting = false;
		localTextureBound = false;
	}

	public static void postRender() {
		lighting = null;
		RenderHelper.disableLighting();
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
		return resource;
	}
	
	public static boolean resourceExists(String resource) {
		if(loadedResources.contains(resource)) {
			return true;
		}
		if(missingResources.contains(resource)) {
			return false;
		}
		if(Utils.class.getResource(resource) != null) {
			loadedResources.add(resource);
			return true;
		}
		else  {
			missingResources.add(resource);
			String error = "Missing file " + new File("bin\\minecraft.jar").getAbsolutePath().replace("\\", "/") + resource;
			if(System.getProperty("java.class.path").toLowerCase().contains("eclipse"))
	        {
				logError(new String[] {error, "Alternate Location " + HowManyItems.class.getClassLoader().getResource("").getPath().replaceFirst("/*$", "") + resource});
	        }
			else {
				logError(error);
			}
			return false;
		}
	}
	
	public static void logError(String... lines) {
		System.out.println("HMI ERROR: " + lines[0]);
		for (String message : lines) {
			if(message == lines[0]) continue;
			System.out.println('\t' + message);
		}
	}
	
	public static Object getHandler(String path) {
		try { 
			return Utils.class.getClassLoader().loadClass(Utils.class.getPackage().getName() + ".references." + path + ".ConcreteHandler").newInstance(); 
		}
		catch (Throwable e) { e.printStackTrace(); return null; } 
	}
	
	public static boolean isKeyDown(KeyBinding keybind) {
		return isKeyDown(keybind.key);
	}
	
	public static boolean isKeyDown(int keyCode) {
		return Keyboard.isKeyDown(keyCode) && keyCodeValid(keyCode);
	}
	
	public static boolean keyEquals(int keyCode, KeyBinding keybind) {
		return keyCode == keybind.key && keybindValid(keybind);
	}
	
	public static boolean keybindValid(KeyBinding keybind) {
		return keyCodeValid(keybind.key);
	}
	
	public static boolean keyCodeValid(int keyCode) {
		return keyCode != Keyboard.KEY_NONE;
	}
	
	public static class EasyField<T> {

		private static final EasyField<Integer> modifiersField = new EasyField<>(Field.class, "modifiers");
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

	public static Object getPrivateValue(Class instanceclass, Object instance, int fieldindex)
			throws IllegalArgumentException, SecurityException, NoSuchFieldException
	{
		try
		{
			Field f = instanceclass.getDeclaredFields()[fieldindex];
			f.setAccessible(true);
			return f.get(instance);
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isGuiOpen(Class gui) {
		if(gui == null)
		{
			return getMC().currentScreen == null;
		}
		if(getMC().currentScreen == null)
		{
			return false;
		} else
		{
			return gui.isInstance(getMC().currentScreen);
		}
	}
}
