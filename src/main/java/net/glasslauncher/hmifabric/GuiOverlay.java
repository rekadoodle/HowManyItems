package net.glasslauncher.hmifabric;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.container.slot.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemInstance;
import net.minecraft.level.Level;
import net.minecraft.level.LevelProperties;
import net.minecraft.util.CharacterUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

public class GuiOverlay extends ScreenBase {

	public static ContainerBase screen;
	private final int BUTTON_HEIGHT = 20;
	private static ArrayList<ItemInstance> currentItems;
	
	public static ItemInstance hoverItem;
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
	
	private ItemInstance guiBlock;
	
	public static ArrayList<ItemInstance> hiddenItems;
	public static String[] mpSpawnCommand;
	public static boolean showHiddenItems = false;
	
	public int xSize = 0;
	public int ySize = 0;
 	
	public GuiOverlay(ContainerBase gui) {
		super();
		if(hiddenItems == null) hiddenItems = Utils.hiddenItems;
		if(currentItems == null) currentItems = getCurrentList(Utils.itemList());
		screen = gui;
		lastKeyTimeout = System.currentTimeMillis() + 200L;
		lastKey = Keyboard.getEventKey();
		
		if(HowManyItems.getTabs().size() > 0) guiBlock = TabUtils.getItemFromGui(screen);
		
		init(Utils.getMC(), screen.width, screen.height);
		
	}
	
	private static Field xSizeField = Utils.getField(ContainerBase.class, new String[] {"containerWidth", "field_1152"});
	private static Field ySizeField = Utils.getField(ContainerBase.class, new String[] {"containerHeight", "field_1153"});
	private static Method mouseClickedMethod = Utils.getMethod(ScreenBase.class, new String[] {"mouseClicked", "method_124"}, new Class<?>[] {int.class, int.class, int.class});
	private static Method keyTypedMethod = Utils.getMethod(ScreenBase.class, new String[] {"keyPressed", "method_117"}, new Class<?>[] {char.class, int.class});
	private static Method mouseMovedOrUpMethod = Utils.getMethod(ScreenBase.class, new String[] {"mouseReleased", "method_128"}, new Class<?>[] {int.class, int.class, int.class});
	
	private static Field worldInfoField = Utils.getField(Level.class, new String[] {"properties", "field_220"});

	@Override
	public void init() {
		try {
			xSize = xSizeField.getInt(screen);
			ySize = ySizeField.getInt(screen);
		} 
		catch (Exception e) { e.printStackTrace(); }
		buttons.clear();
		int k = (screen.width - xSize) / 2 + 1;
        int l = (screen.height - ySize) / 2;
        String search = "";
		if (searchBox != null) search = searchBox.method_1876();
		int searchBoxX = k + xSize + 1;
		int searchBoxWidth = screen.width - k - xSize - BUTTON_HEIGHT - 2;
		if(Config.centredSearchBar) {
			searchBoxX -= xSize;
			searchBoxWidth = xSize - BUTTON_HEIGHT - 3;
		}
		int id = 0;
		searchBox = new GuiTextFieldHMI(screen, textManager, searchBoxX, screen.height - BUTTON_HEIGHT + 1, searchBoxWidth, BUTTON_HEIGHT - 4, search);
		searchBox.method_1878((searchBoxWidth - 10) / 6);
		buttons.add(buttonOptions = new GuiButtonHMI(id++, searchBoxX + searchBoxWidth + 1, screen.height - BUTTON_HEIGHT - 1, BUTTON_HEIGHT, Config.cheatsEnabled ? 1 : 0, guiBlock));
		buttons.add(buttonNextPage = new GuiButtonHMI(id++, screen.width - (screen.width - k - xSize) / 3, 0, (screen.width - k - xSize) / 3, BUTTON_HEIGHT, "Next"));
		buttons.add(buttonPrevPage = new GuiButtonHMI(id++, k + xSize, 0, (screen.width - k - xSize) / 3, BUTTON_HEIGHT, "Prev"));
		if(Config.cheatsEnabled) {
			boolean mp = minecraft.level.isClient;
			if(!mp || !Config.mpTimeDayCommand.isEmpty()) 
				buttons.add(buttonTimeDay = new GuiButtonHMI(id++, 0, 0, BUTTON_HEIGHT, 12));
			if(!mp || !Config.mpTimeNightCommand.isEmpty()) 
				buttons.add(buttonTimeNight = new GuiButtonHMI(id++, BUTTON_HEIGHT, 0, BUTTON_HEIGHT, 13));
			if(!mp || !Config.mpRainOFFCommand.isEmpty() || !Config.mpRainONCommand.isEmpty()) 
				buttons.add(buttonToggleRain = new GuiButtonHMI(id++, BUTTON_HEIGHT * 2, 0, BUTTON_HEIGHT, 14));
			if(!mp || !Config.mpHealCommand.isEmpty()) 
				buttons.add(buttonHeal = new GuiButtonHMI(id++, BUTTON_HEIGHT * 3, 0, BUTTON_HEIGHT, 15));
			if(!mp) 
				buttons.add(buttonTrash = new GuiButtonHMI(id++, 0, screen.height - BUTTON_HEIGHT - 1, 60, BUTTON_HEIGHT, "Trash"));
		}
	}

	public void drawScreen(int posX, int posY) {
		boolean shiftHeld = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if(shiftHeld && HowManyItems.getTabs().size() > 0) {
			buttonOptions.iconIndex = 2;
			if(buttonTrash != null) {
				buttonTrash.text = "Delete ALL";
			}
		}
		else {
			buttonOptions.iconIndex = Config.cheatsEnabled ? 1 : 0;
			if(buttonTrash != null) {
				buttonTrash.text = "Trash";
			}
		}
		
		int k = (screen.width - xSize) / 2 + xSize + 1;
		int w = screen.width - (screen.width - xSize) / 2 - xSize - 1;

		Utils.disableLighting();
		for(int kx = 0; kx < buttons.size(); kx++)
		{
			((Button)buttons.get(kx)).render(minecraft, posX, posY);
		}
		searchBox.method_1883();
		
		//DRAW ITEMS + TOOLTIPS
		
		int x = 0;
		int y = 0;
		Boolean itemHovered = false;
		PlayerInventory inventoryplayer = minecraft.player.inventory;
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
			else if(showHiddenItems && hoverItem != null && currentItems.indexOf(hoverItem) < i && hoverItem.itemId == currentItems.get(i).itemId && shiftHeld && !Mouse.isButtonDown(0)) {
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
        		ItemInstance currentItem = currentItems.get(i);
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
		textManager.drawTextWithShadow(page, screen.width - w/2 - textManager.getTextWidth(page)/2, 6, 0xffffff);
		buttonNextPage.active = buttonPrevPage.active = itemsPerPage < currentItems.size();
        if(inventoryplayer.getCursorItem() != null)
        {
        	Utils.drawItemStack(posX - 8, posY - 8, inventoryplayer.getCursorItem(), true);
        }
		if(!itemHovered) {
			hoverItem = null;
		}
		String s = "";
		if (inventoryplayer.getCursorItem() == null && hoverItem != null) {
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
					if(shiftHeld && hoverItem.method_719()) {
						s = "Unhide all items with same ID and higher dmg";
					}
					else {
						s = "Unhide " + Utils.getNiceItemName(hoverItem);
					}
				}
				else {
					if(shiftHeld && hoverItem.method_719()) {
						s = "Hide all items with same ID and higher dmg";
					}
					else {
						s = "Hide " + Utils.getNiceItemName(hoverItem);
					}
				}
			}
		}
		else if(Config.cheatsEnabled && inventoryplayer.getCursorItem() != null && (hoverItem != null || (posX > k + (w % 18)/2 && posY > screen.height - BUTTON_HEIGHT + (canvasHeight % 18) /2 - canvasHeight
				&& posX < screen.width - (w % 18)/2 && posY > BUTTON_HEIGHT + (canvasHeight % 18) /2  && posY < BUTTON_HEIGHT + canvasHeight)))
		{
			s = "Delete " + Utils.getNiceItemName(inventoryplayer.getCursorItem());
		}
		else if(buttonOptions.isMouseOver(minecraft, posX, posY))
		{
			if(!shiftHeld || HowManyItems.getTabs().size() == 0) {
				s = "Settings";
			}
			else if(guiBlock != null) {
				s = "View " + Utils.getNiceItemName(guiBlock, false) + " Recipes";
			}
			else {
				s = "View All Recipes";
			}
		}
		else if(Config.cheatsEnabled && !minecraft.level.isClient && buttonTimeDay.isMouseOver(minecraft, posX, posY))
		{
			s = "Set time to day";
		}
		else if(Config.cheatsEnabled && !minecraft.level.isClient && buttonTimeNight.isMouseOver(minecraft, posX, posY))
		{
			s = "Set time to night";
		}
		else if(Config.cheatsEnabled && !minecraft.level.isClient && buttonToggleRain.isMouseOver(minecraft, posX, posY))
		{
			s = "Toggle rain";
		}
		else if(Config.cheatsEnabled && !minecraft.level.isClient && buttonHeal.isMouseOver(minecraft, posX, posY))
		{
			s = "Heal";
		}
		else if(Config.cheatsEnabled && !minecraft.level.isClient && buttonTrash.isMouseOver(minecraft, posX, posY))
		{
			if(inventoryplayer.getCursorItem() == null) {
				if(shiftHeld) {
					s = "Delete ALL Items";
				}
				else s = "Drag item here to delete";
			}
			else {
				if(shiftHeld) {
					s = "Delete ALL " + Utils.getNiceItemName(inventoryplayer.getCursorItem());
				}
				else s = "Delete " + Utils.getNiceItemName(inventoryplayer.getCursorItem());
			}
		}
		if(s.length() > 0)
		{
			int k1 = posX;
			int i2 = posY;
			int j2 = textManager.getTextWidth(s);
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
		else if(inventoryplayer.getCursorItem() == null && Utils.hoveredItem(screen, posX, posY) != null) {
			ItemInstance item = Utils.hoveredItem(screen, posX, posY);
			s = TranslationStorage.getInstance().translate(item.getTranslationKey() + ".name");
			int k1 = posX;
			int i2 = posY;
			int j2 = textManager.getTextWidth(s);
			if(k1 + 9 <= k && k1 + j2 + 15 > k) {
				Utils.drawRect(k, i2 - 15, k1 + j2 + 15, i2 - 1, 0xc0000000);
				textManager.drawTextWithShadow(s, k1 + 12, i2 - 12, -1);
			}
			if(s.length() == 0) {
				Utils.drawTooltip(Utils.getNiceItemName(item), k1, i2);
			}
			else if(Config.showItemIDs) {
				s = " " + item.itemId;
				if(item.method_719()) s+= ":" + item.getDamage();
				int j3 = textManager.getTextWidth(s);
				Utils.drawRect(k1 + j2 + 15, i2 - 15, k1 + j2 + j3 + 15, i2 + 8 - 9, 0xc0000000);
				textManager.drawTextWithShadow(s, k1 + j2 + 12, i2 - 12, -1);
			}
		}
	}
	

	public static long guiClosedCooldown = 0L;
	private ItemInstance draggingFrom = null;

	@Override
	public void mouseClicked(int posX, int posY, int eventButton) {
		boolean shiftHeld = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if(System.currentTimeMillis() > guiClosedCooldown) {
		int k = (screen.width - xSize) / 2 + xSize + 1;
		int w = screen.width - (screen.width - xSize) / 2 - xSize - 1;

		int canvasHeight = screen.height - BUTTON_HEIGHT * 2;
		if(Config.centredSearchBar) canvasHeight += BUTTON_HEIGHT;
		searchBox.method_1879(posX, posY, eventButton);
		if(!showHiddenItems) {
		if(hoverItem != null && minecraft.player.inventory.getCursorItem() == null) {
			if(minecraft.player.inventory.getCursorItem() == null && Config.cheatsEnabled) {
				
				if(eventButton == 0 || eventButton == 1) {
					if(!minecraft.level.isClient) {
						ItemInstance spawnedItem = hoverItem.copy();
						if(eventButton == 0) spawnedItem.count = hoverItem.method_709();
						else spawnedItem.count = 1;
						minecraft.player.inventory.method_671(spawnedItem);
					}
					else if(Config.mpGiveCommand.length() > 0) {
						NumberFormat numberformat = NumberFormat.getIntegerInstance();
			            numberformat.setGroupingUsed(false);
			            MessageFormat messageformat = new MessageFormat(Config.mpGiveCommand);
			            messageformat.setFormatByArgumentIndex(1, numberformat);
			            messageformat.setFormatByArgumentIndex(2, numberformat);
			            messageformat.setFormatByArgumentIndex(3, numberformat);
			            Object aobj[] = {
			                minecraft.player.name, hoverItem.itemId, (eventButton == 0) ? hoverItem.method_709() : 1, Integer.valueOf(hoverItem.getDamage())
			            };
			            minecraft.player.sendChatMessage(messageformat.format((aobj)));
					}
				}
			}
			else if(minecraft.player.inventory.getCursorItem() == null) {
				HowManyItems.pushRecipe(screen, hoverItem, eventButton == 1);
			}
			
		}
		
		}
		else {
			if(hoverItem != null && minecraft.player.inventory.getCursorItem() == null) {
				if(hiddenItems.contains(hoverItem)) {
					if(shiftHeld) {
						for(int i = currentItems.indexOf(hoverItem); currentItems.get(i).itemId == hoverItem.itemId && i < currentItems.size(); i++) {
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
						for(int i = currentItems.indexOf(hoverItem); currentItems.get(i).itemId == hoverItem.itemId && i < currentItems.size(); i++) {
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
		if((minecraft.player.inventory.getCursorItem() != null && !minecraft.level.isClient && (hoverItem != null || (posX > k + (w % 18)/2 && posY > screen.height - BUTTON_HEIGHT + (canvasHeight % 18) /2 - canvasHeight
				&& posX < screen.width - (w % 18)/2 && posY > BUTTON_HEIGHT + (canvasHeight % 18) /2  && posY < BUTTON_HEIGHT + canvasHeight))) && Config.cheatsEnabled) {
			if(eventButton == 0) {
				minecraft.player.inventory.setCursorItem(null);
			}
			else if(eventButton == 1) {
				minecraft.player.inventory.setCursorItem(minecraft.player.inventory.getCursorItem().split(minecraft.player.inventory.getCursorItem().count - 1));
			}
		}
		else if(Config.cheatsEnabled && !minecraft.level.isClient && buttonTrash.isMouseOver(minecraft, posX, posY) && minecraft.player.inventory.getCursorItem() != null && eventButton == 1) {
			minecraft.soundHelper.playSound("random.click", 1.0F, 1.0F);
			if(minecraft.player.inventory.getCursorItem().count > 1) {
				minecraft.player.inventory.setCursorItem(minecraft.player.inventory.getCursorItem().split(minecraft.player.inventory.getCursorItem().count - 1));
			}
			else {
				minecraft.player.inventory.setCursorItem(null);
			}
		}
		else {
			super.mouseClicked(posX, posY, eventButton);
			
			for(int kx = 0; kx < buttons.size(); kx++)
			{
				if(((Button)buttons.get(kx)).isMouseOver(minecraft, posX, posY)) {
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

	@Override
	protected void buttonClicked(Button guibutton)
    {
		boolean shiftHeld = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if(guibutton == buttonNextPage) {
			incIndex();
		}
		else if(guibutton == buttonPrevPage) {
			decIndex();
		}
		else if(guibutton == buttonOptions) {
			if(shiftHeld && HowManyItems.getTabs().size() > 0) {
				if(guiBlock == null) {
					HowManyItems.pushRecipe(screen, null, true);
				}
				else {
					HowManyItems.pushTabBlock(screen, guiBlock);
				}
			}
			else {
				minecraft.openScreen(new GuiOptionsHMI(screen));
			}
		}
		else if(guibutton == buttonTimeDay || guibutton == buttonTimeNight || guibutton == buttonToggleRain) {
			if(!minecraft.level.isClient) {
				
				try {
					LevelProperties worldInfo = (LevelProperties)worldInfoField.get(minecraft.level);
					if(guibutton == buttonTimeDay) {
						long l = worldInfo.getTime() + 24000L;
		                worldInfo.setTime(l - l % 24000L);
					}
					else if(guibutton == buttonTimeNight) {
						long l = worldInfo.getTime() + 24000L;
		                worldInfo.setTime(l - (l % 24000L) + 13000L);
					}
					else {
						worldInfo.setThundering(!worldInfo.isThundering());
						worldInfo.setRaining(!worldInfo.isRaining());
					}
				} 
				catch (IllegalArgumentException e) { e.printStackTrace(); } 
				catch (IllegalAccessException e) { e.printStackTrace(); }
			}
			else {
				if(guibutton == buttonTimeDay) {
					minecraft.player.sendChatMessage(Config.mpTimeDayCommand);
				}
				else if(guibutton == buttonTimeNight) {
					minecraft.player.sendChatMessage(Config.mpTimeNightCommand);
				}
				else if(guibutton == buttonToggleRain) {
					try {
						LevelProperties worldInfo = (LevelProperties)worldInfoField.get(minecraft.level);
						if(worldInfo.isRaining()) {
							minecraft.player.sendChatMessage(Config.mpRainOFFCommand);
						}
						else {
							minecraft.player.sendChatMessage(Config.mpRainONCommand);
						}
					} 
					catch (IllegalArgumentException e) { e.printStackTrace(); } 
					catch (IllegalAccessException e) { e.printStackTrace(); }
				}
			}
		}
		else if(!minecraft.level.isClient && guibutton == buttonHeal) {
			if(!minecraft.level.isClient) {
				minecraft.player.addHealth(100);
				minecraft.player.air = 300;
				if(minecraft.player.method_1359()) {
					minecraft.player.fire = -minecraft.player.field_1646;
					minecraft.level.playSound(minecraft.player, "random.fizz", 0.7F, 1.6F + (Utils.rand.nextFloat() - Utils.rand.nextFloat()) * 0.4F);
				}
			}
			else {
				minecraft.player.sendChatMessage(Config.mpHealCommand);
			}
		}
		else if(!minecraft.level.isClient && guibutton == buttonTrash) {
			if(minecraft.player.inventory.getCursorItem() == null) {
				if(shiftHeld) {
					if(!(screen instanceof GuiRecipeViewer) && System.currentTimeMillis() > deleteAllWaitUntil)
                    {
                        for(int i = 0; i < screen.container.slots.size(); i++)
                        {
                            Slot slot = (Slot)screen.container.slots.get(i);
                            slot.setStack((ItemInstance)null);
                        }

                    }
				}
			}
			else {
				if(shiftHeld) {
					for(int i = 0; i < screen.container.slots.size(); i++)
                    {
                        Slot slot = (Slot)screen.container.slots.get(i);
                        if(slot.hasItem() && slot.getItem().isEqualIgnoreFlags(minecraft.player.inventory.getCursorItem()))
                        slot.setStack((ItemInstance)null);
                    }
					deleteAllWaitUntil = System.currentTimeMillis() + 1000L;
				}
				minecraft.player.inventory.setCursorItem(null);
			}
		}
    }
	
	private static long deleteAllWaitUntil = 0L;

	@Override
	protected void keyPressed(char c, int i)
    {
		if(!searchBoxFocused() && Config.fastSearch && !HowManyItems.keyHeldLastTick) {
			if(!Utils.keyEquals(i, minecraft.options.inventoryKey) && !Utils.keyEquals(i, Config.allRecipes) && !Utils.keyEquals(i, Config.toggleOverlay)
					&& (CharacterUtils.validCharacters.indexOf(c) >= 0 || (i == Keyboard.KEY_BACK && searchBox.method_1876().length() > 0))) {
				ScreenScaler scaledresolution = new ScreenScaler(minecraft.options, minecraft.actualWidth, minecraft.actualHeight);
				int i2 = scaledresolution.getScaledWidth();
				int j2 = scaledresolution.getScaledHeight();
				int posX = (Mouse.getEventX() * i2) / minecraft.actualWidth;
				int posY = j2 - (Mouse.getEventY() * j2) / minecraft.actualHeight - 1;
				if((Utils.hoveredItem(screen, posX, posY) == null && hoverItem == null) || (!Utils.keyEquals(i, Config.pushRecipe) && !Utils.keyEquals(i, Config.pushUses))){
					if(!(screen instanceof GuiRecipeViewer) || !Utils.keyEquals(i, Config.prevRecipe))
						if(System.currentTimeMillis() > lastKeyTimeout)
					searchBox.field_2420 = true;
				}
        	}
        }
		if(searchBoxFocused()) {
        	Keyboard.enableRepeatEvents(true);
        	if(i == Keyboard.KEY_ESCAPE) {
        		Keyboard.enableRepeatEvents(false);
        		searchBox.method_1881(false);
        	}
			else searchBox.method_1877(c, i);
        	if(searchBox.method_1876().length() > lastSearch.length()) {
				prevSearches.push(currentItems);
				currentItems = getCurrentList(currentItems);
			}else if(searchBox.method_1876().length() == 0) {
				resetItems();
			}
			else if(searchBox.method_1876().length() < lastSearch.length()) {
				if(prevSearches.isEmpty()) currentItems = getCurrentList(Utils.itemList());
				else currentItems = prevSearches.pop();
			}
			lastSearch = searchBox.method_1876();
        }
        else {
        	Keyboard.enableRepeatEvents(false);
        	if(modTickKeyPress) {
        		if(modTickKeyPress && (i != lastKey || System.currentTimeMillis() > lastKeyTimeout)) {
            		//System.out.println(screen.getClass().getSimpleName() + " "+ c + " " + lastKey + " " + lastKeyTimeout);
        			lastKey = i;
        			lastKeyTimeout = System.currentTimeMillis() + 200L;
                	if(minecraft.currentScreen == this) {
                		if(Utils.keyEquals(i, Config.allRecipes) && minecraft.player.inventory.getCursorItem() == null) {
                			if (screen instanceof GuiRecipeViewer) {
                				((GuiRecipeViewer) screen).push(null, false);
                			}
                			else if (HowManyItems.getTabs().size() > 0){
                				GuiRecipeViewer newgui = new GuiRecipeViewer(null, false, screen);
                				minecraft.currentScreen = newgui;
                				ScreenScaler scaledresolution = new ScreenScaler(minecraft.options, minecraft.actualWidth, minecraft.actualHeight);
                				int i2 = scaledresolution.getScaledWidth();
                				int j2 = scaledresolution.getScaledHeight();
                	            newgui.init(minecraft, i2, j2);
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
		for(Button button : (List<Button>)buttons) {
			if(button.isMouseOver(minecraft, posX, posY))
				return true;
		}
		if(searchBox.hovered(posX, posY))
			return true;
		if(posX > (xSize + screen.width) / 2) {
			return true;
		}
		return false;
	}

	@Override
	public void init(Minecraft minecraft, int i, int j)
    {
		if(minecraft.currentScreen == this)
			screen.init(minecraft, i, j);
		super.init(minecraft, i, j);
    }

    @Override
    protected void mouseReleased(int i, int j, int k)
    {
        super.mouseReleased(i, j, k);
        try {
        	mouseMovedOrUpMethod.invoke(screen, new Object[] {i, j, k});
		} catch (Exception e) { e.printStackTrace(); } 
    }
	
	public static boolean searchBoxFocused() {
		if(searchBox != null) return searchBox.field_2420;
		return false;
	}

	public void handleKeyInput() {
		if(searchBoxFocused()) {
			while( Keyboard.next()) {
				modTickKeyPress = false;
				if(Keyboard.getEventKeyState())
				{
					keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
				}
			}
		}
		else {
			if(Keyboard.getEventKeyState())
			{
				modTickKeyPress = true;
				keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			}
		}
	}

	private static int lastKey = -1;
	private static long lastKeyTimeout = 0L;

	@Override
	public void onMouseEvent()
    {
    	int posX = (Mouse.getEventX() * screen.width) / minecraft.actualWidth;
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
        super.onMouseEvent();
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
			boolean wasFocused = searchBox.field_2420;
			searchBox.field_2420 = true;
			searchBox.method_1880("");
			searchBox.field_2420 = wasFocused;
			currentItems = getCurrentList(Utils.itemList());
			prevSearches.clear();
		}
	}
	
	private static ArrayList<ItemInstance> getCurrentList(ArrayList<ItemInstance> listToSearch){
		index = 0;
		ArrayList<ItemInstance> newList = new ArrayList<>();
		if(searchBox != null && searchBox.method_1876().length() > 0) {
			for(ItemInstance currentItem : listToSearch) {
				String s = ("" + TranslationStorage.getInstance().translate(currentItem.getTranslationKey())).trim();
				if(s.toLowerCase().contains(searchBox.method_1876().toLowerCase()) && (showHiddenItems || !hiddenItems.contains(currentItem))) {
					newList.add(currentItem);
				}
			}
		}
		else if(showHiddenItems) {
			return new ArrayList<>(Utils.itemList());
		}
		else {
			for(ItemInstance currentItem : Utils.itemList()) {
				if(!hiddenItems.contains(currentItem)) {
					newList.add(currentItem);
				}
			}
		}
		return newList;
	}
	private static Stack<ArrayList<ItemInstance>> prevSearches = new Stack<>();
	private static String lastSearch = "";
	public boolean modTickKeyPress = false;

	public void toggle() {
		if(buttonNextPage != null) {
			for(Object obj : buttons) {
				Button button = (Button)obj;
				if(Config.overlayEnabled) {
					if(Config.cheatsEnabled) button.visible = true;
					else if(button == buttonNextPage || button == buttonPrevPage || button == buttonOptions) button.visible = true;
				}
				else {
					button.visible = false;
				}
			}
			searchBox.field_2421 = Config.overlayEnabled;
		}
		if(!Config.overlayEnabled) {
			Utils.getMC().currentScreen = screen;
			hoverItem = null;
		}
		Config.writeConfig();
	}

	public static void focusSearchBox() {
		if(searchBox != null) {
			if(searchBox.field_2420 = !searchBox.field_2420) {
				Keyboard.enableRepeatEvents(false);
			}
		}
	}
	
	public static boolean emptySearchBox() {
		if(searchBox != null) {
			return searchBox.method_1876().length() == 0;
		}
		return false;
	}

	public void onTick() {
		ScreenScaler res = new ScreenScaler(minecraft.options, minecraft.actualWidth, minecraft.actualHeight);
        int posX = (Mouse.getX() * res.getScaledWidth()) / minecraft.actualWidth;
        int posY = res.getScaledHeight() - (Mouse.getY() * res.getScaledHeight()) / minecraft.actualHeight - 1;
        Utils.preRender();
        drawScreen(posX, posY);
        if(mouseOverUI(minecraft, posX, posY)) {
        	for(; Mouse.next(); onMouseEvent()) { }
        }
        else if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
        	//used to unfocus search box by clicking off it
    		searchBox.method_1879(posX, posY, Mouse.getEventButton());
		}
        handleKeyInput();
		Utils.postRender();
	}



    public static String drawIDID = "mod_HowManyItems_DrawID";	
}
