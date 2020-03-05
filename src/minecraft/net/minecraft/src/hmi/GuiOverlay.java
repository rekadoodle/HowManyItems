package net.minecraft.src.hmi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class GuiOverlay extends GuiScreen {

	public static GuiContainer screen;
	private final int BUTTON_HEIGHT = 20;
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
	
	private ItemStack guiBlock;
	
	public static ArrayList<ItemStack> hiddenItems;
	public static String[] mpSpawnCommand;
	public static boolean showHiddenItems = false;
	
	public int xSize = 0;
	public int ySize = 0;
 	
	public GuiOverlay(GuiContainer gui) {
		super();
		if(hiddenItems == null) hiddenItems = Utils.hiddenItems;
		if(currentItems == null) currentItems = getCurrentList(Utils.itemList());
		screen = gui;
		lastKeyTimeout = System.currentTimeMillis() + 200L;
		lastKey = Keyboard.getEventKey();
		
		if(mod_HowManyItems.getTabs().size() > 0) guiBlock = TabUtils.getItemFromGui(screen);
		
		setWorldAndResolution(Utils.mc, screen.width, screen.height);
		
	}
	
	private static Field xSizeField = Utils.getField(GuiContainer.class, new String[] {"xSize", "a"});
	private static Field ySizeField = Utils.getField(GuiContainer.class, new String[] {"ySize", "i"});
	private static Method mouseClickedMethod = Utils.getMethod(GuiScreen.class, new String[] {"mouseClicked", "a"}, new Class<?>[] {int.class, int.class, int.class});
	private static Method keyTypedMethod = Utils.getMethod(GuiScreen.class, new String[] {"keyTyped", "a"}, new Class<?>[] {char.class, int.class});
	private static Method mouseMovedOrUpMethod = Utils.getMethod(GuiScreen.class, new String[] {"mouseMovedOrUp", "b"}, new Class<?>[] {int.class, int.class, int.class});
	
	private static Field worldInfoField = Utils.getField(World.class, new String[] {"worldInfo", "x"});
	
	public void initGui() {
		try {
			xSize = xSizeField.getInt(screen);
			ySize = ySizeField.getInt(screen);
		} 
		catch (Exception e) { e.printStackTrace(); }
		controlList.clear();
		int k = (screen.width - xSize) / 2 + 1;
        int l = (screen.height - ySize) / 2;
        String search = "";
		if (searchBox != null) search = searchBox.getText();
		int searchBoxX = k + xSize + 1;
		int searchBoxWidth = screen.width - k - xSize - BUTTON_HEIGHT - 2;
		if(Config.centredSearchBar) {
			searchBoxX -= xSize;
			searchBoxWidth = xSize - BUTTON_HEIGHT - 3;
		}
		int id = 0;
		searchBox = new GuiTextFieldHMI(screen, fontRenderer, searchBoxX, screen.height - BUTTON_HEIGHT + 1, searchBoxWidth, BUTTON_HEIGHT - 4, search);
		searchBox.setMaxStringLength((searchBoxWidth - 10) / 6);
		controlList.add(buttonOptions = new GuiButtonHMI(id++, searchBoxX + searchBoxWidth + 1, screen.height - BUTTON_HEIGHT - 1, BUTTON_HEIGHT, Config.cheatsEnabled ? 1 : 0, guiBlock));
		controlList.add(buttonNextPage = new GuiButtonHMI(id++, screen.width - (screen.width - k - xSize) / 3, 0, (screen.width - k - xSize) / 3, BUTTON_HEIGHT, "Next"));
		controlList.add(buttonPrevPage = new GuiButtonHMI(id++, k + xSize, 0, (screen.width - k - xSize) / 3, BUTTON_HEIGHT, "Prev"));
		if(Config.cheatsEnabled) {
			boolean mp = mc.theWorld.multiplayerWorld;
			if(!mp || !Config.mpTimeDayCommand.isEmpty()) 
				controlList.add(buttonTimeDay = new GuiButtonHMI(id++, 0, 0, BUTTON_HEIGHT, 12));
			if(!mp || !Config.mpTimeNightCommand.isEmpty()) 
				controlList.add(buttonTimeNight = new GuiButtonHMI(id++, BUTTON_HEIGHT, 0, BUTTON_HEIGHT, 13));
			if(!mp || !Config.mpRainOFFCommand.isEmpty() || !Config.mpRainONCommand.isEmpty()) 
				controlList.add(buttonToggleRain = new GuiButtonHMI(id++, BUTTON_HEIGHT * 2, 0, BUTTON_HEIGHT, 14));
			if(!mp || !Config.mpHealCommand.isEmpty()) 
				controlList.add(buttonHeal = new GuiButtonHMI(id++, BUTTON_HEIGHT * 3, 0, BUTTON_HEIGHT, 15));
			if(!mp) 
				controlList.add(buttonTrash = new GuiButtonHMI(id++, 0, screen.height - BUTTON_HEIGHT - 1, 60, BUTTON_HEIGHT, "Trash"));
		}
	}
	
	
	public void drawScreen(int posX, int posY) {
		
		
		
		
		boolean shiftHeld = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if(shiftHeld && mod_HowManyItems.getTabs().size() > 0) {
			buttonOptions.iconIndex = 2;
			if(buttonTrash != null) {
				buttonTrash.displayString = "Delete ALL";
			}
		}
		else {
			buttonOptions.iconIndex = Config.cheatsEnabled ? 1 : 0;
			if(buttonTrash != null) {
				buttonTrash.displayString = "Trash";
			}
		}
		
		int k = (screen.width - xSize) / 2 + xSize + 1;
		int w = screen.width - (screen.width - xSize) / 2 - xSize - 1;

		Utils.disableLighting();
		for(int kx = 0; kx < controlList.size(); kx++)
		{
			((GuiButton)controlList.get(kx)).drawButton(mc, posX, posY);
		}
		searchBox.drawTextBox();
		
		//DRAW ITEMS + TOOLTIPS
		
		int x = 0;
		int y = 0;
		Boolean itemHovered = false;
		InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
		int canvasHeight = screen.height - BUTTON_HEIGHT * 2;
		if(Config.centredSearchBar) canvasHeight += BUTTON_HEIGHT;
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
            int white = 0x40ffffff;
            int green = 0xAA66CD00;
            int lightRed = 0xAAE50000;
            int darkRed = 0x80E50000;
            int slotX = (w % 18)/2 + k + x * 18 - 1;
            int slotY = y * 18 - 1 + (canvasHeight % 18) /2  + BUTTON_HEIGHT;
			if(!itemHovered && posX + 2> k + (w % 18)/2 &&
					(posX - (w % 18)/2 - k + 1) / 18 >= x && (posX - (w % 18)/2 - k + 1) / 18 < x + 1
					&& (posY - (canvasHeight % 18) /2  - BUTTON_HEIGHT) / 18 >= y && (posY - (canvasHeight % 18) /2  - BUTTON_HEIGHT) / 18 < y + 1 && posY >= BUTTON_HEIGHT + (canvasHeight % 18) /2 - 1) {
				itemHovered = true;
				hoverItem = currentItems.get(i);
                if(!hiddenItems.contains(currentItems.get(i))) {
                	if(!showHiddenItems) {
                		Utils.drawSlot(slotX, slotY, white);
                	}
                	else if(draggingFrom == null || !hiddenItems.contains(draggingFrom)){
                		Utils.drawSlot(slotX, slotY, lightRed);
                	}
                	else Utils.drawSlot(slotX, slotY, green);
                }
                else {
                	if(draggingFrom == null || hiddenItems.contains(draggingFrom))
                		Utils.drawSlot(slotX, slotY, green);
                	else Utils.drawSlot(slotX, slotY, lightRed);
                }
			}
			else if(showHiddenItems && hoverItem != null && currentItems.indexOf(hoverItem) < i && hoverItem.itemID == currentItems.get(i).itemID && shiftHeld && !Mouse.isButtonDown(0)) {
				if(!hiddenItems.contains(hoverItem)) 
					Utils.drawSlot(slotX, slotY, lightRed);
					else
						Utils.drawSlot(slotX, slotY, green);
			}
			else if(hiddenItems.contains(currentItems.get(i)) && draggingFrom == null) {
				Utils.drawSlot(slotX, slotY, darkRed);
			}
			else if (showHiddenItems && draggingFrom != null && hoverItem != null){
				if((currentItems.indexOf(draggingFrom) <= i && i < currentItems.indexOf(hoverItem) || (currentItems.indexOf(draggingFrom) >= i && i > currentItems.indexOf(hoverItem)))){
					if(!hiddenItems.contains(draggingFrom))
						Utils.drawSlot(slotX, slotY, lightRed);
					else
						Utils.drawSlot(slotX, slotY, green);
					
				}
				else {
					if(hiddenItems.contains(currentItems.get(i)))
						Utils.drawSlot(slotX, slotY, darkRed);
				}
			}
			Utils.drawItemStack(slotX + 1, slotY + 1, currentItems.get(i), true);
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
		Utils.disableLighting();
		String page = (pageIndex + 1) + "/" + (currentItems.size() / itemsPerPage + 1);
		fontRenderer.drawStringWithShadow(page, screen.width - w/2 - fontRenderer.getStringWidth(page)/2, 6, 0xffffff);
		buttonNextPage.enabled = buttonPrevPage.enabled = itemsPerPage < currentItems.size();
        if(inventoryplayer.getItemStack() != null)
        {
        	Utils.drawItemStack(posX - 8, posY - 8, inventoryplayer.getItemStack(), true);
        }
		if(!itemHovered) {
			hoverItem = null;
		}
			String s = "";
				if (inventoryplayer.getItemStack() == null && hoverItem != null) {
					if(!showHiddenItems) {
						s = Utils.getNiceItemName(hoverItem); 
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
							if(shiftHeld && hoverItem.getHasSubtypes()) {
								s = "Unhide all items with same ID and higher dmg";
							}
							else {
								s = "Unhide " + Utils.getNiceItemName(hoverItem); 
							}
						}
						else {
							if(shiftHeld && hoverItem.getHasSubtypes()) {
								s = "Hide all items with same ID and higher dmg";
							}
							else {
								s = "Hide " + Utils.getNiceItemName(hoverItem); 
							}
						}
					}
				}
				else if(Config.cheatsEnabled && inventoryplayer.getItemStack() != null && (hoverItem != null || (posX > k + (w % 18)/2 && posY > screen.height - BUTTON_HEIGHT + (canvasHeight % 18) /2 - canvasHeight
						&& posX < screen.width - (w % 18)/2 && posY > BUTTON_HEIGHT + (canvasHeight % 18) /2  && posY < BUTTON_HEIGHT + canvasHeight))) 
				{
					s = "Delete " + Utils.getNiceItemName(inventoryplayer.getItemStack());
				}
				else if(buttonOptions.mousePressed(mc, posX, posY)) 
				{
					if(!shiftHeld || mod_HowManyItems.getTabs().size() == 0) {
						s = "Settings";
					}
					else if(guiBlock != null) {
						s = "View " + Utils.getNiceItemName(guiBlock, false) + " Recipes";
					}
					else {
						s = "View All Recipes";
					}
				}
				else if(Config.cheatsEnabled && !mc.theWorld.multiplayerWorld && buttonTimeDay.mousePressed(mc, posX, posY)) 
				{
					s = "Set time to day";
				}
				else if(Config.cheatsEnabled && !mc.theWorld.multiplayerWorld && buttonTimeNight.mousePressed(mc, posX, posY)) 
				{
					s = "Set time to night";
				}
				else if(Config.cheatsEnabled && !mc.theWorld.multiplayerWorld && buttonToggleRain.mousePressed(mc, posX, posY)) 
				{
					s = "Toggle rain";
				}
				else if(Config.cheatsEnabled && !mc.theWorld.multiplayerWorld && buttonHeal.mousePressed(mc, posX, posY)) 
				{
					s = "Heal";
				}
				else if(Config.cheatsEnabled && !mc.theWorld.multiplayerWorld && buttonTrash.mousePressed(mc, posX, posY)) 
				{
					if(inventoryplayer.getItemStack() == null) {
						if(shiftHeld) {
							s = "Delete ALL Items";
						}
						else s = "Drag item here to delete";
					}
					else {
						if(shiftHeld) {
							s = "Delete ALL " + Utils.getNiceItemName(inventoryplayer.getItemStack());
						}
						else s = "Delete " + Utils.getNiceItemName(inventoryplayer.getItemStack());
					}
				}
				if(s.length() > 0)
				{
					int k1 = posX;
					int i2 = posY;
					int j2 = fontRenderer.getStringWidth(s);
					if(k1 + j2 + 12 > screen.width - 3)
					{
						k1 -= (k1 + j2 + 12) - screen.width + 2;
					}
					if(i2 - 15 < 0)
					{
						i2 -= (i2 - 15);
					}
					Utils.drawTooltip(s, k1, i2);
				}
				else if(inventoryplayer.getItemStack() == null && Utils.hoveredItem(screen, posX, posY) != null) {
					ItemStack item = Utils.hoveredItem(screen, posX, posY);
					s = StringTranslate.getInstance().translateNamedKey(item.getItemName());
					int k1 = posX;
					int i2 = posY;
					int j2 = fontRenderer.getStringWidth(s);
					if(k1 + 9 <= k && k1 + j2 + 15 > k) {
						Utils.drawRect(k, i2 - 15, k1 + j2 + 15, i2 - 1, 0xc0000000);
						fontRenderer.drawStringWithShadow(s, k1 + 12, i2 - 12, -1);
					}
					if(s.length() == 0) {
						Utils.drawTooltip(Utils.getNiceItemName(item), k1, i2);
					}
					else if(Config.showItemIDs) {
						s = " " + item.itemID;
						if(item.getHasSubtypes()) s+= ":" + item.getItemDamage();
						int j3 = fontRenderer.getStringWidth(s);
						Utils.drawRect(k1 + j2 + 15, i2 - 15, k1 + j2 + j3 + 15, i2 + 8 - 9, 0xc0000000);
						fontRenderer.drawStringWithShadow(s, k1 + j2 + 12, i2 - 12, -1);
					}
				}
	}
	
	
	
	public static long guiClosedCooldown = 0L;
	private ItemStack draggingFrom = null;

	public void mouseClicked(int posX, int posY, int eventButton) {
		boolean shiftHeld = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if(System.currentTimeMillis() > guiClosedCooldown) {
		int k = (screen.width - xSize) / 2 + xSize + 1;
		int w = screen.width - (screen.width - xSize) / 2 - xSize - 1;

		int canvasHeight = screen.height - BUTTON_HEIGHT * 2;
		if(Config.centredSearchBar) canvasHeight += BUTTON_HEIGHT;
		searchBox.mouseClicked(posX, posY, eventButton);
		if(!showHiddenItems) {
		if(hoverItem != null && mc.thePlayer.inventory.getItemStack() == null) {
			if(mc.thePlayer.inventory.getItemStack() == null && Config.cheatsEnabled) {
				
				if(eventButton == 0 || eventButton == 1) {
					if(!mc.theWorld.multiplayerWorld) {
						ItemStack spawnedItem = hoverItem.copy();
						if(eventButton == 0) spawnedItem.stackSize = hoverItem.getMaxStackSize();
						else spawnedItem.stackSize = 1;
						mc.thePlayer.inventory.addItemStackToInventory(spawnedItem);
					}
					else if(Config.mpGiveCommand.length() > 0) {
						NumberFormat numberformat = NumberFormat.getIntegerInstance();
			            numberformat.setGroupingUsed(false);
			            MessageFormat messageformat = new MessageFormat(Config.mpGiveCommand);
			            messageformat.setFormatByArgumentIndex(1, numberformat);
			            messageformat.setFormatByArgumentIndex(2, numberformat);
			            messageformat.setFormatByArgumentIndex(3, numberformat);
			            Object aobj[] = {
			                mc.thePlayer.username, hoverItem.itemID, (eventButton == 0) ? hoverItem.getMaxStackSize() : 1, Integer.valueOf(hoverItem.getItemDamage())
			            };
			            mc.thePlayer.sendChatMessage(messageformat.format((aobj)));
					}
				}
			}
			else if(mc.thePlayer.inventory.getItemStack() == null) {
				mod_HowManyItems.pushRecipe(screen, hoverItem, eventButton == 1);
			}
			
		}
		
		}
		else {
			if(hoverItem != null && mc.thePlayer.inventory.getItemStack() == null) {
				if(hiddenItems.contains(hoverItem)) {
					if(shiftHeld) {
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
					if(shiftHeld) {
						for(int i = currentItems.indexOf(hoverItem); currentItems.get(i).itemID == hoverItem.itemID && i < currentItems.size(); i++) {
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
		if((mc.thePlayer.inventory.getItemStack() != null && !mc.theWorld.multiplayerWorld && (hoverItem != null || (posX > k + (w % 18)/2 && posY > screen.height - BUTTON_HEIGHT + (canvasHeight % 18) /2 - canvasHeight
				&& posX < screen.width - (w % 18)/2 && posY > BUTTON_HEIGHT + (canvasHeight % 18) /2  && posY < BUTTON_HEIGHT + canvasHeight))) && Config.cheatsEnabled) {
			if(eventButton == 0) {
				mc.thePlayer.inventory.setItemStack(null);
			}
			else if(eventButton == 1) {
				mc.thePlayer.inventory.setItemStack(mc.thePlayer.inventory.getItemStack().splitStack(mc.thePlayer.inventory.getItemStack().stackSize - 1));
			}
		}
		else if(Config.cheatsEnabled && !mc.theWorld.multiplayerWorld && buttonTrash.mousePressed(mc, posX, posY) && mc.thePlayer.inventory.getItemStack() != null && eventButton == 1) {
			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			if(mc.thePlayer.inventory.getItemStack().stackSize > 1) {
				mc.thePlayer.inventory.setItemStack(mc.thePlayer.inventory.getItemStack().splitStack(mc.thePlayer.inventory.getItemStack().stackSize - 1));
			}
			else {
				mc.thePlayer.inventory.setItemStack(null);
			}
		}
		else {
			super.mouseClicked(posX, posY, eventButton);
			
			for(int kx = 0; kx < controlList.size(); kx++)
			{
				if(((GuiButton)controlList.get(kx)).mousePressed(mc, posX, posY)) {
					return;
				}
			}
			if (!searchBox.hovered(posX, posY))
				try {
					mouseClickedMethod.invoke(screen, new Object[] {posX, posY, eventButton});
				} 
				catch (Exception e) { e.printStackTrace(); } 
			}
		}
	}
	
	protected void actionPerformed(GuiButton guibutton)
    {
		boolean shiftHeld = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if(guibutton == buttonNextPage) {
			incIndex();
		}
		else if(guibutton == buttonPrevPage) {
			decIndex();
		}
		else if(guibutton == buttonOptions) {
			if(shiftHeld && mod_HowManyItems.getTabs().size() > 0) {
				if(guiBlock == null) {
					mod_HowManyItems.pushRecipe(screen, null, true);
				}
				else {
					mod_HowManyItems.pushTabBlock(screen, guiBlock);
				}
			}
			else {
				mc.displayGuiScreen(new GuiOptionsHMI(screen));
			}
		}
		else if(guibutton == buttonTimeDay || guibutton == buttonTimeNight || guibutton == buttonToggleRain) {
			if(!mc.theWorld.multiplayerWorld) {
				
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
				if(guibutton == buttonTimeDay) {
					mc.thePlayer.sendChatMessage(Config.mpTimeDayCommand);
				}
				else if(guibutton == buttonTimeNight) {
					mc.thePlayer.sendChatMessage(Config.mpTimeNightCommand);
				}
				else if(guibutton == buttonToggleRain) {
					try {
						WorldInfo worldInfo = (WorldInfo)worldInfoField.get(mc.theWorld);
						if(worldInfo.getRaining()) {
							mc.thePlayer.sendChatMessage(Config.mpRainOFFCommand);
						}
						else {
							mc.thePlayer.sendChatMessage(Config.mpRainONCommand);
						}
					} 
					catch (IllegalArgumentException e) { e.printStackTrace(); } 
					catch (IllegalAccessException e) { e.printStackTrace(); }
				}
			}
		}
		else if(!mc.theWorld.multiplayerWorld && guibutton == buttonHeal) {
			if(!mc.theWorld.multiplayerWorld) {
				mc.thePlayer.heal(100);
				mc.thePlayer.air = 300;
				if(mc.thePlayer.isBurning()) {
					mc.thePlayer.fire = -mc.thePlayer.fireResistance;
					mc.theWorld.playSoundAtEntity(mc.thePlayer, "random.fizz", 0.7F, 1.6F + (Utils.rand.nextFloat() - Utils.rand.nextFloat()) * 0.4F);
				}
			}
			else {
				mc.thePlayer.sendChatMessage(Config.mpHealCommand);
			}
		}
		else if(!mc.theWorld.multiplayerWorld && guibutton == buttonTrash) {
			if(mc.thePlayer.inventory.getItemStack() == null) {
				if(shiftHeld) {
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
				if(shiftHeld) {
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
		if(!searchBoxFocused() && Config.fastSearch && !mod_HowManyItems.keyHeldLastTick) {
			if(i != mc.gameSettings.keyBindInventory.keyCode && i != Config.allRecipes.keyCode && i != Config.toggleOverlay.keyCode
					&& (ChatAllowedCharacters.allowedCharacters.indexOf(c) >= 0 || (i == Keyboard.KEY_BACK && searchBox.getText().length() > 0))) {
				ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
				int i2 = scaledresolution.getScaledWidth();
				int j2 = scaledresolution.getScaledHeight();
				int posX = (Mouse.getEventX() * i2) / mc.displayWidth;
				int posY = j2 - (Mouse.getEventY() * j2) / mc.displayHeight - 1;
				if((Utils.hoveredItem(screen, posX, posY) == null && hoverItem == null) || (i != Config.pushRecipe.keyCode && i != Config.pushUses.keyCode)){
					if(!(screen instanceof GuiRecipeViewer) || i != Config.prevRecipe.keyCode)
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
				if(prevSearches.isEmpty()) currentItems = getCurrentList(Utils.itemList());
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
                		if(i == Config.allRecipes.keyCode && mc.thePlayer.inventory.getItemStack() == null) {
                			if (screen instanceof GuiRecipeViewer) {
                				((GuiRecipeViewer) screen).push(null, false);
                			}
                			else if (mod_HowManyItems.getTabs().size() > 0){
                				GuiRecipeViewer newgui = new GuiRecipeViewer(null, false, screen);
                				mc.currentScreen = newgui;
                				ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
                				int i2 = scaledresolution.getScaledWidth();
                				int j2 = scaledresolution.getScaledHeight();
                	            newgui.setWorldAndResolution(mc, i2, j2);
                			}
                		}
                		else if(i == Keyboard.KEY_ESCAPE && screen instanceof GuiRecipeViewer) {

                        	//("KEY TYPED");
                		}

                		//screen.keyTyped(c, i);
                	}
                    //else super.keyTyped(c, i);
            	}
        	}
        	else {
        		try {
					keyTypedMethod.invoke(screen, new Object[] {c, i});
				} catch (Exception e) { e.printStackTrace(); } 
        	}
        }
    }
	
	public static void resetItems() {
		currentItems = getCurrentList(Utils.itemList());
		prevSearches.clear();
	}
	
	public boolean mouseOverUI(Minecraft minecraft, int posX, int posY) {
		for(GuiButton button : (List<GuiButton>)controlList) {
			if(button.mousePressed(minecraft, posX, posY))
				return true;
		}
		if(searchBox.hovered(posX, posY))
			return true;
		if(posX > (xSize + screen.width) / 2) {
			return true;
		}
		return false;
	}
	
	public void setWorldAndResolution(Minecraft minecraft, int i, int j)
    {
		if(minecraft.currentScreen == this) screen.setWorldAndResolution(minecraft, i, j);
		super.setWorldAndResolution(minecraft, i, j);
    }

    protected void mouseMovedOrUp(int i, int j, int k)
    {
        super.mouseMovedOrUp(i, j, k);
        try {
        	mouseMovedOrUpMethod.invoke(screen, new Object[] {i, j, k});
		} catch (Exception e) { e.printStackTrace(); } 
    }
	
	public static boolean searchBoxFocused() {
		if(searchBox != null) return searchBox.isFocused;
		return false;
	}

	public void handleKeyInput() {
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
    	int k = (screen.width - xSize) / 2 + xSize + 1;
    	if(posX > k) {
    		int i = Mouse.getEventDWheel();
    		if(!Config.scrollInverted) {
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

	
	
	public static void clearSearchBox() {
		if(searchBox != null) {
			boolean wasFocused = searchBox.isFocused;
			searchBox.isFocused = true;
			searchBox.setText("");
			searchBox.isFocused = wasFocused;
			currentItems = getCurrentList(Utils.itemList());
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
			return new ArrayList<ItemStack>(Utils.itemList());
		}
		else {
			for(ItemStack currentItem : Utils.itemList()) {
				if(!hiddenItems.contains(currentItem)) {
					newList.add(currentItem);
				}
			}
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
				if(Config.overlayEnabled) {
					if(Config.cheatsEnabled) button.enabled2 = true;
					else if(button == buttonNextPage || button == buttonPrevPage || button == buttonOptions) button.enabled2 = true;
				}
				else {
					button.enabled2 = false;
				}
			}
			searchBox.isEnabled = Config.overlayEnabled;
		}
		if(!Config.overlayEnabled) {
			Utils.mc.currentScreen = screen;
			hoverItem = null;
		}
		Config.writeConfig();
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

	public void onTick() {
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int posX = (Mouse.getX() * res.getScaledWidth()) / mc.displayWidth;
        int posY = res.getScaledHeight() - (Mouse.getY() * res.getScaledHeight()) / mc.displayHeight - 1;
        Utils.preRender();
        drawScreen(posX, posY);
        if(mouseOverUI(mc, posX, posY)) {
        	for(; Mouse.next(); handleMouseInput()) { }
        }
        else if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
        	//used to unfocus search box by clicking off it
    		searchBox.mouseClicked(posX, posY, Mouse.getEventButton());
		}
        handleKeyInput();
		Utils.postRender();
	}



    public static String drawIDID = "mod_HowManyItems_DrawID";	
}
