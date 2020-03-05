package net.minecraft.src;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

public class Gui_HMI extends GuiScreen {

	public static GuiContainer screen;
	private static RenderItem itemRenderer = new RenderItem();
	private final int BUTTON_HEIGHT = 20;
	public static ArrayList<ItemStack> allItems;
	private static ArrayList<ItemStack> currentItems;
	
	public static ItemStack hoverItem;
	private static GuiTextFieldHMI searchBox;
	private static int index = 0;
	private int itemsPerPage;
	
	private GuiButtonHMI buttonNextPage;
	private GuiButtonHMI buttonPrevPage;
	private GuiButtonHMI buttonOptions;
	
	private GuiButtonHMI buttonTimeDay;
	private GuiButtonHMI buttonTimeNight;
	private GuiButtonHMI buttonToggleRain;
	private GuiButtonHMI buttonHeal;
	private GuiButtonHMI buttonTrash;
	
	//TODO
	//button icons
	
	//mp functions
	//inventory saves
	//btw recipes

	
	public static ArrayList<ItemStack> hiddenItems = new ArrayList<ItemStack>() {{
		add(new ItemStack(Block.waterStill));
		add(new ItemStack(Block.lavaStill));
		add(new ItemStack(Block.blockBed));
		add(new ItemStack(Block.tallGrass));
		add(new ItemStack(Block.deadBush));
		add(new ItemStack(Block.pistonExtension));
		add(new ItemStack(Block.pistonMoving));
		add(new ItemStack(Block.stairDouble));
		add(new ItemStack(Block.redstoneWire));
		add(new ItemStack(Block.crops));
		add(new ItemStack(Block.tilledField));
		add(new ItemStack(Block.stoneOvenActive));
		add(new ItemStack(Block.signPost));
		add(new ItemStack(Block.doorWood));
		add(new ItemStack(Block.signWall));
		add(new ItemStack(Block.doorSteel));
		add(new ItemStack(Block.oreRedstoneGlowing));
		add(new ItemStack(Block.torchRedstoneIdle));
		add(new ItemStack(Block.reed));
		add(new ItemStack(Block.cake));
		add(new ItemStack(Block.redstoneRepeaterIdle));
		add(new ItemStack(Block.redstoneRepeaterActive));
		add(new ItemStack(Block.lockedChest));
	}};
	
	
	public static String[] mpSpawnCommand;
	public static boolean showHiddenItems = false;
	
	private int xSize = 0;
	private int ySize = 0;
 	
	public Gui_HMI(GuiContainer gui) {
		super();
		screen = gui;
		lastKeyTimeout = System.currentTimeMillis() + 200L;
		lastKey = Keyboard.getEventKey();
		//this.setWorldAndResolution(screen.mc, screen.width, screen.height);
	}
	
	public void initGui() {
		xSize = screen.xSize;
		ySize = screen.ySize;
		controlList.clear();
		int k = (screen.width - screen.xSize) / 2 + 1;
        int l = (screen.height - screen.ySize) / 2;
        String search = "";
		if (searchBox != null) search = searchBox.getText();
		int searchBoxX = k + screen.xSize + 1;
		int searchBoxWidth = screen.width - k - screen.xSize - BUTTON_HEIGHT - 2;
		if(mod_HowManyItems.optionsCentredSearchBar) {
			searchBoxX -= screen.xSize;
			searchBoxWidth = screen.xSize - BUTTON_HEIGHT - 3;
		}
		int id = 0;
		searchBox = new GuiTextFieldHMI(screen, screen.fontRenderer, searchBoxX, screen.height - BUTTON_HEIGHT + 1, searchBoxWidth, BUTTON_HEIGHT - 4, search);
		searchBox.setMaxStringLength((searchBoxWidth - 10) / 6);
		controlList.add(buttonOptions = new GuiButtonHMI(id++, searchBoxX + searchBoxWidth + 1, screen.height - BUTTON_HEIGHT - 1, BUTTON_HEIGHT, mod_HowManyItems.optionsCheatsEnabled ? 1 : 0));
		controlList.add(buttonNextPage = new GuiButtonHMI(id++, screen.width - (screen.width - k - screen.xSize) / 3, 0, (screen.width - k - screen.xSize) / 3, BUTTON_HEIGHT, "Next"));
		controlList.add(buttonPrevPage = new GuiButtonHMI(id++, k + screen.xSize, 0, (screen.width - k - screen.xSize) / 3, BUTTON_HEIGHT, "Prev"));
		if(mod_HowManyItems.optionsCheatsEnabled && !mc.theWorld.multiplayerWorld) {
			controlList.add(buttonTimeDay = new GuiButtonHMI(id++, 0, 0, BUTTON_HEIGHT, 12));
			controlList.add(buttonTimeNight = new GuiButtonHMI(id++, BUTTON_HEIGHT, 0, BUTTON_HEIGHT, 13));
			controlList.add(buttonToggleRain = new GuiButtonHMI(id++, BUTTON_HEIGHT * 2, 0, BUTTON_HEIGHT, 14));
			controlList.add(buttonHeal = new GuiButtonHMI(id++, BUTTON_HEIGHT * 3, 0, BUTTON_HEIGHT, 15));
			controlList.add(buttonTrash = new GuiButtonHMI(id++, 0, screen.height - BUTTON_HEIGHT - 1, 60, BUTTON_HEIGHT, "Trash"));
		}
	}
	
	public void drawScreen(int posX, int posY, float f) {
		screen.drawScreen(posX, posY, f);
		drawScreen(posX, posY);
		mod_HowManyItems.dontRender = true;
	}
	
	public boolean doesGuiPauseGame()
    {
        return false;
    }
	
	public void drawScreen(int posX, int posY) {
		if(screen.width != width || screen.height != height
				|| screen.xSize != xSize || screen.ySize != ySize) 
			setWorldAndResolution(mc, screen.width, screen.height);
		if((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && mod_HowManyItems.getTabs().size() > 0) {
			buttonOptions.iconIndex = 2;
		}
		else buttonOptions.iconIndex = mod_HowManyItems.optionsCheatsEnabled ? 1 : 0;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		int k = (screen.width - screen.xSize) / 2 + screen.xSize + 1;
		int w = screen.width - (screen.width - screen.xSize) / 2 - screen.xSize - 1;
		
		boolean buttonHovered = false;
		for(int kx = 0; kx < controlList.size(); kx++)
		{
			if(((GuiButton)controlList.get(kx)).mousePressed(mc, posX, posY)) {
				buttonHovered = true;
				break;
			}
		}
		if(posX > k || buttonHovered || searchBox.hovered(posX, posY)) {
			mc.currentScreen = this;
		}
		else if(mc.currentScreen == this) mc.currentScreen = screen;
		GL11.glDisable(2896 /*GL_LIGHTING*/);
		for(int kx = 0; kx < controlList.size(); kx++)
		{
			((GuiButton)controlList.get(kx)).drawButton(mc, posX, posY);
		}
		
		
		//DRAW ITEMS + TOOLTIPS
		
		GL11.glDisable(2896 /*GL_LIGHTING*/);
		//guiscreen.drawRect(k + screen.xSize, 0, screen.width, screen.height - BUTTON_HEIGHT, 0xee401008);
		
		
		//screen.drawString(screen.fontRenderer, "hfhfhtf", k, 40, 0xee1FBED6);
		
		searchBox.drawTextBox();
		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		GL11.glPushMatrix();
		GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		int x = 0;
		int y = 0;
		Boolean itemHovered = false;
		InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
		int x1, x2, y1, y2;
		x1 = x2 = y1 = y2 = 0;
		int canvasHeight = screen.height - BUTTON_HEIGHT * 2;
		if(mod_HowManyItems.optionsCentredSearchBar) canvasHeight += BUTTON_HEIGHT;
		if(mc.currentScreen == screen && inventoryplayer.getItemStack() == null)
        {
			for(int i1 = 0; i1 < screen.inventorySlots.slots.size(); i1++)
	        {
	            ItemStack slot1 = mod_HowManyItems.itemAtPosition(screen, posX, posY);
	            if(slot1 != null)
	            {
	            	String s = (new StringBuilder()).append("").append(StringTranslate.getInstance().translateNamedKey(slot1.getItemName())).toString().trim();
		            if(s.length() > 0)
		            {
		                int k1 = (posX) + 12;
		                int i2 = posY - 12;
		                int j2 = fontRenderer.getStringWidth(s);
		                x1 = k1 - 3 - 9;
		                x2 = k1 + j2 + 3 - 9;
		                y1 = i2 - 3 - 9;
		                y2 = i2 + 8 + 3 - 9;
		            }
	            }
            }
        }
		for(int i = index; i < currentItems.size(); i++) {
			if((x + 1) * 18 > w) {
				y++;
				x = 0;
			}
			if((y + 1) * 18 > canvasHeight) {
				if(index + itemsPerPage <= currentItems.size()) {
					//itemsPerPage = i - index;
				}
				
				break;
			}
			GL11.glDisable(2896 /*GL_LIGHTING*/);
            GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
			if(!itemHovered && posX + 2> k + (w % 18)/2 &&
					(posX - (w % 18)/2 - k + 1) / 18 >= x && (posX - (w % 18)/2 - k + 1) / 18 < x + 1
					&& (posY - (canvasHeight % 18) /2  - BUTTON_HEIGHT) / 18 >= y && (posY - (canvasHeight % 18) /2  - BUTTON_HEIGHT) / 18 < y + 1 && posY > BUTTON_HEIGHT + (canvasHeight % 18) /2) {
				itemHovered = true;
				hoverItem = currentItems.get(i);
                if(!hiddenItems.contains(currentItems.get(i))) {
                	if(!showHiddenItems) {
                		screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0x40ffffff);
                	}
                	else if(draggingFrom == null || !hiddenItems.contains(draggingFrom)){
                		screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0xAAE50000);
                	}
                	else screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0xAA66CD00);
                }
                else {
                	if(draggingFrom == null || hiddenItems.contains(draggingFrom))
                		screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0xAA66CD00);
                	else screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0xAAE50000);
                }
			}
			else if(showHiddenItems && hoverItem != null && currentItems.indexOf(hoverItem) < i && hoverItem.itemID == currentItems.get(i).itemID && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && !Mouse.isButtonDown(0)) {
				if(!hiddenItems.contains(hoverItem)) 
						screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0xAAE50000);
					else
						screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0xAA66CD00);
			}
			else if(hiddenItems.contains(currentItems.get(i)) && draggingFrom == null) {
				screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0x80E50000);
			}
			else if (showHiddenItems && draggingFrom != null && hoverItem != null){
				if((currentItems.indexOf(draggingFrom) <= i && i < currentItems.indexOf(hoverItem) || (currentItems.indexOf(draggingFrom) >= i && i > currentItems.indexOf(hoverItem)))){
					if(!hiddenItems.contains(draggingFrom))
						screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0xAAE50000);
					else
						screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0xAA66CD00);
					
				}
				else {
					if(hiddenItems.contains(currentItems.get(i)))
						screen.drawRect((w % 18)/2 + k + x * 18 - 1, y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT, (w % 18)/2 + k + x * 18 + 17, y * 18 + 17 + (canvasHeight % 18) /2 + BUTTON_HEIGHT, 0x80E50000);
				}
			}
			GL11.glEnable(2896 /*GL_LIGHTING*/);
            GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
			
			if(!(x1 < (w % 18)/2 + k + x * 18 + 9 && x2 > (w % 18)/2 + k + x * 18 - 9
					&& y1 < BUTTON_HEIGHT + (canvasHeight % 18) /2 + y  * 18 + 9 && y2 > BUTTON_HEIGHT + (canvasHeight % 18) /2 + y * 18 - 9)) {
				itemRenderer.renderItemIntoGUI(screen.fontRenderer, screen.mc.renderEngine, currentItems.get(i), (w % 18)/2 + k + x * 18, BUTTON_HEIGHT + (canvasHeight % 18) /2 + y * 18);
			}
			x++;
			if(i == currentItems.size() - 1) {
				if((canvasHeight / 18) * (w / 18) > currentItems.size()) {
					index = 0;
				}
			}
		}
		
		if(draggingFrom != null && !Mouse.isButtonDown(0)) {
			int lowerIndex;
			int higherIndex;
        	if(!((lowerIndex = currentItems.indexOf(draggingFrom)) < (higherIndex = currentItems.indexOf(hoverItem)))) {
        		int temp = lowerIndex;
        		lowerIndex = higherIndex;
        		higherIndex = temp;
        	}
        	boolean hideItems = !hiddenItems.contains(draggingFrom);
        	for(int i = lowerIndex; i <= higherIndex; i++) {
        		ItemStack currentItem = currentItems.get(i);
        		if(hideItems) {
            		if(!hiddenItems.contains(currentItem))
            			hiddenItems.add(currentItem);
            	}
        		else {
        			if(hiddenItems.contains(currentItem))
            			hiddenItems.remove(hiddenItems.indexOf(currentItem));
        		}
        	}
        	draggingFrom = null;
        }
		
		itemsPerPage = (canvasHeight / 18) * ((w - (w % 18)) / 18);
		if(itemsPerPage == 0) itemsPerPage = currentItems.size();
		int pageIndex = index / itemsPerPage;
		if(index + itemsPerPage > currentItems.size()) {
			pageIndex = 0;
		}
		if(itemsPerPage < currentItems.size()) {
			pageIndex = index / itemsPerPage;
		}
		String page = (pageIndex + 1) + "/" + (currentItems.size() / itemsPerPage + 1);
		fontRenderer.drawStringWithShadow(page, screen.width - w/2 - fontRenderer.getStringWidth(page)/2, 6, 0xe0e0e0);
		buttonNextPage.enabled = buttonPrevPage.enabled = itemsPerPage < currentItems.size();
        if(mc.currentScreen == this && inventoryplayer.getItemStack() != null)
        {
            GL11.glTranslatef(0.0F, 0.0F, 32F);
            itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), posX - 8, posY - 8);
            itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), posX - 8, posY - 8);
        }
		if(!itemHovered) {
			hoverItem = null;
		}
			if(mc.currentScreen == this) {
				String s = "";
				if (inventoryplayer.getItemStack() == null && hoverItem != null) {
					if(!showHiddenItems) {
						s = getNiceItemName(hoverItem); 
					}
					else {
						if(draggingFrom != null && draggingFrom != hoverItem) {
							if(hiddenItems.contains(hoverItem)) {
								s = "Unhide selected items";
							}
							else {
								s = "Hide selected items";
							}
						}
						else if(hiddenItems.contains(hoverItem)) {
							if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && hoverItem.getHasSubtypes()) {
								s = "Unhide all items with same ID and higher dmg";
							}
							else {
								s = "Unhide " + getNiceItemName(hoverItem); 
							}
						}
						else {
							if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && hoverItem.getHasSubtypes()) {
								s = "Hide all items with same ID and higher dmg";
							}
							else {
								s = "Hide " + getNiceItemName(hoverItem); 
							}
						}
					}
				}
				else if(mod_HowManyItems.optionsCheatsEnabled && inventoryplayer.getItemStack() != null && (hoverItem != null || ((posX > k + (w % 18)/2 && posY > screen.height - BUTTON_HEIGHT + (canvasHeight % 18) /2 - canvasHeight
						&& posX < screen.width - (w % 18)/2) && posY < screen.height - BUTTON_HEIGHT - (canvasHeight % 18) /2))) 
				{
					s = "Delete " + getNiceItemName(inventoryplayer.getItemStack());
				}
				else if(buttonOptions.mousePressed(mc, posX, posY)) 
				{
					s = ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && mod_HowManyItems.getTabs().size() > 0) ? "View All Recipes" : "Settings";
				}
				else if(mod_HowManyItems.optionsCheatsEnabled && !mc.theWorld.multiplayerWorld && buttonTimeDay.mousePressed(mc, posX, posY)) 
				{
					s = "Set time to day";
				}
				else if(mod_HowManyItems.optionsCheatsEnabled && !mc.theWorld.multiplayerWorld && buttonTimeNight.mousePressed(mc, posX, posY)) 
				{
					s = "Set time to night";
				}
				else if(mod_HowManyItems.optionsCheatsEnabled && !mc.theWorld.multiplayerWorld && buttonToggleRain.mousePressed(mc, posX, posY)) 
				{
					s = "Toggle rain";
				}
				else if(mod_HowManyItems.optionsCheatsEnabled && !mc.theWorld.multiplayerWorld && buttonHeal.mousePressed(mc, posX, posY)) 
				{
					s = "Heal";
				}
				else if(mod_HowManyItems.optionsCheatsEnabled && !mc.theWorld.multiplayerWorld && buttonTrash.mousePressed(mc, posX, posY)) 
				{
					if(inventoryplayer.getItemStack() == null) {
						if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
							s = "Delete ALL Items";
						}
						else s = "Drag item here to delete";
					}
					else {
						if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
							s = "Delete ALL " + getNiceItemName(inventoryplayer.getItemStack());
						}
						else s = "Delete " + getNiceItemName(inventoryplayer.getItemStack());
					}
				}
				if(s.length() > 0)
				{
					int k1 = (posX) + 12;
					int i2 = posY - 12;
					int j2 = fontRenderer.getStringWidth(s);
					if(k1 + j2 > screen.width - 3)
					{
						k1 -= (k1 + j2) - screen.width + 2;
					}
					if(i2 - 3 < 0)
					{
						i2 -= (i2 - 3);
					}
					GL11.glDisable(2896 /*GL_LIGHTING*/);
					GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
					drawRect(k1 - 3, i2 - 3, k1 + j2 + 3, i2 + 8 + 3, 0xc0000000);
					fontRenderer.drawStringWithShadow(s, k1, i2, -1);
					GL11.glEnable(2896 /*GL_LIGHTING*/);
					GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
				}
			}
		
		GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		RenderHelper.disableStandardItemLighting();
		
		
	}
	
	public String getNiceItemName(ItemStack item) {
		String s = StringTranslate.getInstance().translateNamedKey(item.getItemName());
		if(s == null || s.length() == 0) {
			s = item.getItemName();
			if(s == null) s = "null";
		}
		if(mod_HowManyItems.optionsShowItemIDs) {
			s += " " + item.itemID;
			if(item.getHasSubtypes()) s+= ":" + item.getItemDamage();
		}
		return s;
	}
	
	public static long guiClosedCooldown = 0L;
	private ItemStack draggingFrom = null;

	public void mouseClicked(int posX, int posY, int eventButton) {
		if(System.currentTimeMillis() > guiClosedCooldown) {
			
		int k = (screen.width - screen.xSize) / 2 + screen.xSize + 1;
		int w = screen.width - (screen.width - screen.xSize) / 2 - screen.xSize - 1;
		if(posX > k) {
			int x = (posX - k - (w % 18)/2)/18;
			int y = (posY - BUTTON_HEIGHT)/18 + 1;
		}
		int canvasHeight = screen.height - BUTTON_HEIGHT * 2;
		if(mod_HowManyItems.optionsCentredSearchBar) canvasHeight += BUTTON_HEIGHT;
		searchBox.mouseClicked(posX, posY, eventButton);
		if(!showHiddenItems) {
		if(hoverItem != null && mc.thePlayer.inventory.getItemStack() == null) {
			if(mc.thePlayer.inventory.getItemStack() == null && mod_HowManyItems.optionsCheatsEnabled) {
				
				if(eventButton == 0 || eventButton == 1) {
					if(!mc.theWorld.multiplayerWorld) {
						ItemStack spawnedItem = hoverItem.copy();
						if(eventButton == 0) spawnedItem.stackSize = hoverItem.getMaxStackSize();
						else spawnedItem.stackSize = 1;
						mc.thePlayer.inventory.addItemStackToInventory(spawnedItem);
					}
					else if(mod_HowManyItems.optionsMpGiveCommand.length() > 0) {
						NumberFormat numberformat = NumberFormat.getIntegerInstance();
			            numberformat.setGroupingUsed(false);
			            MessageFormat messageformat = new MessageFormat(mod_HowManyItems.optionsMpGiveCommand);
			            messageformat.setFormatByArgumentIndex(1, numberformat);
			            messageformat.setFormatByArgumentIndex(2, numberformat);
			            messageformat.setFormatByArgumentIndex(3, numberformat);
			            Object aobj[] = {
			                mc.thePlayer.username, Integer.valueOf(hoverItem.itemID), (eventButton == 0) ? hoverItem.getMaxStackSize() : 1, Integer.valueOf(hoverItem.getItemDamage())
			            };
			            mc.thePlayer.sendChatMessage(messageformat.format(((Object) (aobj))));
					}
				}
			}
			else if(mc.thePlayer.inventory.getItemStack() == null) {
				if(!modTickKeyPress) {
					mod_HowManyItems.pushRecipe(screen, hoverItem, eventButton == 1);
				}
				else modTickKeyPress = false;
			}
			
		}
		
		}
		else {
			if(mc.thePlayer.inventory.getItemStack() == null) {
				if(hiddenItems.contains(hoverItem)) {
					if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
						for(int i = currentItems.indexOf(hoverItem); currentItems.get(i).itemID == hoverItem.itemID && i < currentItems.size(); i++) {
							if(hiddenItems.contains(currentItems.get(i)))
							hiddenItems.remove(hiddenItems.indexOf(currentItems.get(i)));
						}
					}
					else {
						draggingFrom = hoverItem;
						//hiddenItems.remove(hiddenItems.indexOf(hoverItem));
					}
				}
				else {
					if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
						for(int i = currentItems.indexOf(hoverItem); currentItems.get(i).itemID == hoverItem.itemID; i++) {
							if(!hiddenItems.contains(currentItems.get(i)))
							hiddenItems.add(currentItems.get(i));
						}
					}
					else {
						draggingFrom = hoverItem;
						//hiddenItems.add(hoverItem);
					}
				}
			}
		}
		if((mc.thePlayer.inventory.getItemStack() != null && !mc.theWorld.multiplayerWorld && (hoverItem != null || ((posX > k + (w % 18)/2 && posY > BUTTON_HEIGHT + (canvasHeight % 18) /2
				&& posX < screen.width - (w % 18)/2) && posY < screen.height - BUTTON_HEIGHT - (canvasHeight % 18) /2))) && mod_HowManyItems.optionsCheatsEnabled) {
			if(eventButton == 0) {
				mc.thePlayer.inventory.setItemStack(null);
			}
			else if(eventButton == 1) {
				mc.thePlayer.inventory.setItemStack(mc.thePlayer.inventory.getItemStack().splitStack(mc.thePlayer.inventory.getItemStack().stackSize - 1));
			}
		}
		else if(mod_HowManyItems.optionsCheatsEnabled && !mc.theWorld.multiplayerWorld && buttonTrash.mousePressed(mc, posX, posY) && mc.thePlayer.inventory.getItemStack() != null && eventButton == 1) {
			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			if(mc.thePlayer.inventory.getItemStack().stackSize > 1) {
				mc.thePlayer.inventory.setItemStack(mc.thePlayer.inventory.getItemStack().splitStack(mc.thePlayer.inventory.getItemStack().stackSize - 1));
			}
			else {
				mc.thePlayer.inventory.setItemStack(null);
			}
		}
		else 
		
		
		if(mc.currentScreen == this) {
			super.mouseClicked(posX, posY, eventButton);
			for(int kx = 0; kx < controlList.size(); kx++)
			{
				if(((GuiButton)controlList.get(kx)).mousePressed(mc, posX, posY)) {
					return;
				}
			}
			if (!searchBox.hovered(posX, posY)) screen.mouseClicked(posX, posY, eventButton);
		}

		}
	}
	
	protected void actionPerformed(GuiButton guibutton)
    {
		if(guibutton == buttonNextPage) {
			incIndex();
		}
		else if(guibutton == buttonPrevPage) {
			decIndex();
		}
		else if(guibutton == buttonOptions) {
			if((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && mod_HowManyItems.getTabs().size() > 0) {
				mod_HowManyItems.pushRecipe(screen, null, false);
			}
			else {
				mc.displayGuiScreen(new GuiOptions_HMI(screen));
			}
		}
		else if(!mc.theWorld.multiplayerWorld && (guibutton == buttonTimeDay || guibutton == buttonTimeNight || guibutton == buttonToggleRain)) {
			if(!mc.theWorld.multiplayerWorld) {
				Field worldInfoField = World.class.getDeclaredFields()[30];
				try {
					WorldInfo worldInfo = (WorldInfo)worldInfoField.get(mc.theWorld);
					if(guibutton == buttonTimeDay) {
						long l = worldInfo.getWorldTime() + 24000L;
		                worldInfo.setWorldTime(l - l % 24000L);
					}
					else if(guibutton == buttonTimeNight) {
						long l = worldInfo.getWorldTime() + 24000L;
		                worldInfo.setWorldTime(l - (l % 24000L) + 13000L);
					}
					else {
						worldInfo.setThundering(!worldInfo.getThundering());
						worldInfo.setRaining(!worldInfo.getRaining());
					}
				} 
				catch (IllegalArgumentException e) { e.printStackTrace(); } 
				catch (IllegalAccessException e) { e.printStackTrace(); }
			}
			else {
				//mp command
			}
		}
		else if(!mc.theWorld.multiplayerWorld && guibutton == buttonHeal) {
			if(!mc.theWorld.multiplayerWorld) {
				mc.thePlayer.heal(100);
			}
			else {
				//mp command
			}
		}
		else if(!mc.theWorld.multiplayerWorld && guibutton == buttonTrash) {
			if(mc.thePlayer.inventory.getItemStack() == null) {
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					if(!(screen instanceof GuiRecipeViewer) && System.currentTimeMillis() > deleteAllWaitUntil)
                    {
                        for(int i = 0; i < screen.inventorySlots.slots.size(); i++)
                        {
                            Slot slot = (Slot)screen.inventorySlots.slots.get(i);
                            slot.putStack((ItemStack)null);
                        }

                    }
				}
			}
			else {
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					for(int i = 0; i < screen.inventorySlots.slots.size(); i++)
                    {
                        Slot slot = (Slot)screen.inventorySlots.slots.get(i);
                        if(slot.getHasStack() && slot.getStack().isItemEqual(mc.thePlayer.inventory.getItemStack()))
                        slot.putStack((ItemStack)null);
                    }
					deleteAllWaitUntil = System.currentTimeMillis() + 1000L;
				}
				mc.thePlayer.inventory.setItemStack(null);
			}
		}
    }
	
	private static long deleteAllWaitUntil = 0L;
	
	protected void keyTyped(char c, int i)
    {
		if(!searchBoxFocused() && mod_HowManyItems.optionsFastSearch && !mod_HowManyItems.keyHeldLastTick) {
			if(i != mc.gameSettings.keyBindInventory.keyCode && i != mod_HowManyItems.allRecipes.keyCode && i != mod_HowManyItems.toggleOverlay.keyCode
					&& (ChatAllowedCharacters.allowedCharacters.indexOf(c) >= 0 || (i == Keyboard.KEY_BACK && searchBox.getText().length() > 0))) {
				ScaledResolution scaledresolution = new ScaledResolution(ModLoader.getMinecraftInstance().gameSettings, ModLoader.getMinecraftInstance().displayWidth, ModLoader.getMinecraftInstance().displayHeight);
				int i2 = scaledresolution.getScaledWidth();
				int j2 = scaledresolution.getScaledHeight();
				int posX = (Mouse.getEventX() * i2) / ModLoader.getMinecraftInstance().displayWidth;
				int posY = j2 - (Mouse.getEventY() * j2) / ModLoader.getMinecraftInstance().displayHeight - 1;
				if((mod_HowManyItems.itemAtPosition(screen, posX, posY) == null && hoverItem == null) || (i != mod_HowManyItems.pushRecipe.keyCode && i != mod_HowManyItems.pushUses.keyCode)){
					if(!(screen instanceof GuiRecipeViewer) || i != mod_HowManyItems.prevRecipe.keyCode)
						if(System.currentTimeMillis() > lastKeyTimeout)
					searchBox.isFocused = true;
				}
        	}
        }
		if(searchBoxFocused()) {
        	Keyboard.enableRepeatEvents(true);
        	if(i == Keyboard.KEY_ESCAPE) {
        		Keyboard.enableRepeatEvents(false);
        		searchBox.setFocused(false);
        	}
			else searchBox.textboxKeyTyped(c, i);
        	if(searchBox.getText().length() > lastSearch.length()) {
				prevSearches.push(currentItems);
				currentItems = getCurrentList(currentItems);
			}else if(searchBox.getText().length() == 0) {
				resetItems();
			}
			else if(searchBox.getText().length() < lastSearch.length()) {
				if(prevSearches.isEmpty()) currentItems = getCurrentList(allItems);
				else currentItems = prevSearches.pop();
			}
			lastSearch = searchBox.getText();
        }
        else {
        	Keyboard.enableRepeatEvents(false);
        	if(modTickKeyPress) {
        		if(modTickKeyPress && (i != lastKey || System.currentTimeMillis() > lastKeyTimeout)) {
            		//System.out.println(screen.getClass().getSimpleName() + " "+ c + " " + lastKey + " " + lastKeyTimeout);
        			lastKey = i;
        			lastKeyTimeout = System.currentTimeMillis() + 200L;
                	if(mc.currentScreen == this) {
                		if(i == mod_HowManyItems.allRecipes.keyCode) {
                			if (screen instanceof GuiRecipeViewer) {
                				((GuiRecipeViewer) screen).push(null, false);
                			}
                			else if (mod_HowManyItems.getTabs().size() > 0){
                				Minecraft mc = ModLoader.getMinecraftInstance();
                				GuiRecipeViewer newgui = new GuiRecipeViewer(null, false, screen);
                				mc.currentScreen = newgui;
                				ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
                				int i2 = scaledresolution.getScaledWidth();
                				int j2 = scaledresolution.getScaledHeight();
                	            newgui.setWorldAndResolution(mc, i2, j2);
                			}
                		}
                		else if(i == Keyboard.KEY_ESCAPE && screen instanceof GuiRecipeViewer) {

                        	//System.out.println("KEY TYPED");
                		}

                		//screen.keyTyped(c, i);
                	}
                    //else super.keyTyped(c, i);
            	}
        	}
        	else screen.keyTyped(c, i);
        }
    }
	
	public static void resetItems() {
		currentItems = getCurrentList(allItems);
		prevSearches.clear();
	}
	
	public void setWorldAndResolution(Minecraft minecraft, int i, int j)
    {
		if(minecraft.currentScreen == this) screen.setWorldAndResolution(minecraft, i, j);
		super.setWorldAndResolution(minecraft, i, j);
    }

    protected void mouseMovedOrUp(int i, int j, int k)
    {
        super.mouseMovedOrUp(i, j, k);
        if(mc.currentScreen == this) screen.mouseMovedOrUp(i, j, k);
    }
	
	public static boolean searchBoxFocused() {
		if(searchBox != null) return searchBox.isFocused;
		return false;
	}

	public void keyTyped() {
		if(searchBoxFocused()) {
			while( Keyboard.next()) {
				modTickKeyPress = false;
				if(Keyboard.getEventKeyState())
				{
					keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
				}
			}
		}
		else {
			if(Keyboard.getEventKeyState())
			{
				modTickKeyPress = true;
				keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			}
		}
	}

	private static int lastKey = -1;
	private static long lastKeyTimeout = 0L;
	
	public void handleMouseInput()
    {
    	
    	int posX = (Mouse.getEventX() * screen.width) / mc.displayWidth;
    	int k = (screen.width - screen.xSize) / 2 + screen.xSize + 1;
    	if(posX > k) {
    		int i = Mouse.getEventDWheel();
    		if(!mod_HowManyItems.optionsScrollInverted) {
    			if(i > 0) { incIndex(); }
                if(i < 0) { decIndex(); }
    		}
    		else {
    			if(i > 0) { decIndex(); }
                if(i < 0) { incIndex(); }
    		}
    	}
        super.handleMouseInput();
    }
	
	public void incIndex() {
		index += itemsPerPage;
		if(index > currentItems.size()) index = 0;
	}
	
	public void decIndex() {
		if(index > 0) {
			index -= itemsPerPage;
			if(index < 0) index = 0;
		}
		else {
			index = currentItems.size() - (currentItems.size() % itemsPerPage);
		}
	}

	public static void loadItems() {
		if(allItems == null) {
			allItems = new ArrayList<ItemStack>();
			
			Item mcItemsList[] = Item.itemsList;
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
	                for(ItemStack hiddenItem : hiddenItems) {
	                	if(itemstack.isItemEqual(hiddenItem)) {
	                		itemstack = hiddenItem;
	                	}
	                }
	                try
	                {
	                    int l = item.getIconIndex(itemstack);
	                    String s = (new StringBuilder()).append(itemstack.getItemName()).append("@").append(l).toString();
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
			currentItems = getCurrentList(allItems);
		}
	}
	
	public static void clearSearchBox() {
		if(searchBox != null) {
			boolean wasFocused = searchBox.isFocused;
			searchBox.isFocused = true;
			searchBox.setText("");
			searchBox.isFocused = wasFocused;
			currentItems = getCurrentList(allItems);
			prevSearches.clear();
		}
	}
	
	private static ArrayList<ItemStack> getCurrentList(ArrayList<ItemStack> listToSearch){
		index = 0;
		ArrayList<ItemStack> newList = new ArrayList<ItemStack>();
		if(searchBox != null && searchBox.getText().length() > 0) {
			for(ItemStack currentItem : listToSearch) {
				String s = (new StringBuilder()).append("").append(StringTranslate.getInstance().translateNamedKey(currentItem.getItemName())).toString().trim();
				if(s.toLowerCase().contains(searchBox.getText().toLowerCase()) && (showHiddenItems || !hiddenItems.contains(currentItem))) {
					newList.add(currentItem);
				}
			}
		}
		else if(showHiddenItems) {
			return new ArrayList<ItemStack>(allItems);
		}
		else {
			for(ItemStack currentItem : allItems) {
				if(!hiddenItems.contains(currentItem)) {
					newList.add(currentItem);
				}
			}
			return newList;
		}
		return newList;
	}
	private static Stack<ArrayList<ItemStack>> prevSearches = new Stack<ArrayList<ItemStack>>(); 
	private static String lastSearch = "";
	public boolean modTickKeyPress = false;

	public void toggle() {
		if(buttonNextPage != null) {
			for(Object obj : controlList) {
				GuiButton button = (GuiButton)obj;
				if(mod_HowManyItems.optionsEnabled) {
					if(mod_HowManyItems.optionsCheatsEnabled) button.enabled2 = true;
					else if(button == buttonNextPage || button == buttonPrevPage || button == buttonOptions) button.enabled2 = true;
				}
				else {
					button.enabled2 = false;
				}
			}
			searchBox.isEnabled = mod_HowManyItems.optionsEnabled;
		}
		if(!mod_HowManyItems.optionsEnabled) {
			ModLoader.getMinecraftInstance().currentScreen = screen;
			hoverItem = null;
		}
	}

	public static void focusSearchBox() {
		if(searchBox != null) {
			if(searchBox.isFocused = !searchBox.isFocused) {
				Keyboard.enableRepeatEvents(false);
			}
		}
	}
	
	public static boolean emptySearchBox() {
		if(searchBox != null) {
			return searchBox.getText().length() == 0;
		}
		return false;
	}
	
	public static void onSettingChanged() {
		if(mod_HowManyItems.hmi != null) mod_HowManyItems.hmi.initGui();
		mod_HowManyItems.writeConfig();
		
	}
}
