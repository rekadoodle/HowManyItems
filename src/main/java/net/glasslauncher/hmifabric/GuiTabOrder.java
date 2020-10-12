package net.glasslauncher.hmifabric;

import java.util.ArrayList;

import net.glasslauncher.hmifabric.tabs.Tab;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.OptionButton;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiTabOrder extends ScreenBase
{
	private ScreenBase parentScreen;
	
	private int right;
	private int bottom;
	
	private final int BUTTON_HEIGHT = 20;
	
	private final int left;
	private final int top;
	private final int slotHeight;
	
	private float scrollMultiplier;
	private int selectedButton = -1;
	private float amountScrolled = 0;
	

    private float initialClickY;
    private long lastClicked;
    private ArrayList<Tab> allTabs;
    private ArrayList<Tab> currentTabs;
    private Tab[] newOrder;
    private boolean[] tabEnabled;

	public GuiTabOrder(ScreenBase guiscreen)
	{
		initialClickY = -2F;
		lastClicked = 0L;
		parentScreen = guiscreen;
		left = 0;
        top = 32;
		slotHeight =  21;
		currentTabs = HowManyItems.getTabs();
		allTabs = HowManyItems.allTabs;
		newOrder = new Tab[allTabs.size()];
		tabEnabled = new boolean[allTabs.size()];
	}

	@Override
	public void init()
	{
		amountScrolled = 0;
		right = width + 80;
		bottom = height - 51;
		for(int i = 0; i < currentTabs.size(); i++) {
			newOrder[i] = currentTabs.get(i);
			tabEnabled[i] = true;
			buttons.add(new GuiButtonHMI(buttons.size(), -1, -1, BUTTON_HEIGHT, 3));
			if(i == 0) ((Button)buttons.get(buttons.size() - 1)).active = false;
			buttons.add(new GuiButtonHMI(buttons.size(), -1, -1, BUTTON_HEIGHT, 4));
			String s = currentTabs.get(i).TAB_CREATOR.getClass().getSimpleName().replaceFirst("mod_", "");
			s += " - " + currentTabs.get(i).name() + ": Enabled";
			buttons.add(new OptionButton(buttons.size(), -1, -1, 268, BUTTON_HEIGHT, s));
		}
		for(int i = 0; i < allTabs.size(); i++) {
			if(!currentTabs.contains(allTabs.get(i))) {
				newOrder[buttons.size() / 3] = allTabs.get(i);
				tabEnabled[buttons.size() / 3] = false;
				buttons.add(new GuiButtonHMI(buttons.size(), -1, -1, BUTTON_HEIGHT, 3));
				((Button)buttons.get(buttons.size() - 1)).active = false;
				buttons.add(new GuiButtonHMI(buttons.size(), -1, -1, BUTTON_HEIGHT, 4));
				((Button)buttons.get(buttons.size() - 1)).active = false;
				String s = allTabs.get(i).TAB_CREATOR.getClass().getSimpleName().replaceFirst("mod_", "");
				s += " - " + allTabs.get(i).name() + ": Disabled";
				buttons.add(new OptionButton(buttons.size(), -1, -1, 268, BUTTON_HEIGHT, s));
			}
		}
		if(buttons.size() >= 3) ((Button)buttons.get(buttons.size() - 2)).active = false;
		
		TranslationStorage stringtranslate = TranslationStorage.getInstance();
		buttons.add(new OptionButton(buttons.size(), width / 2 - 154, height - 39, "Gui Size: " + (Config.recipeViewerDraggableGui ? "Draggable" : "Auto")));
        buttons.add(new OptionButton(buttons.size(), width / 2 + 4, height - 39, stringtranslate.translate("gui.done")));
	}

	@Override
	public void onClose()
    {
		HowManyItems.tabOrderChanged(tabEnabled, newOrder);
    }

    @Override
	protected void buttonClicked(Button guibutton){
		if(guibutton.id == buttons.size() - 1){
			minecraft.openScreen(parentScreen);
		}
		else if(guibutton.id == buttons.size() - 2){
			Config.recipeViewerDraggableGui = !Config.recipeViewerDraggableGui;
        	guibutton.text = "Gui Size: " + (Config.recipeViewerDraggableGui ? "Draggable" : "Auto");
        	HowManyItems.onSettingChanged();
		}
		else {
			if(guibutton.id % 3 == 2) {
				tabEnabled[guibutton.id / 3] = !tabEnabled[guibutton.id / 3];
				if(tabEnabled[guibutton.id / 3]) {
					int pos = guibutton.text.lastIndexOf("Disabled");
				    if (pos > -1) {
				    	guibutton.text = guibutton.text.substring(0, pos) + "Enabled";
				    }
				}
				else {
					int pos = guibutton.text.lastIndexOf("Enabled");
				    if (pos > -1) {
				    	guibutton.text = guibutton.text.substring(0, pos) + "Disabled";
				    }
				}
			}
			else {
				int upOrDown = 0;
				if(guibutton.id % 3 == 0) {
					upOrDown = -1;
				}
				else if(guibutton.id % 3 == 1) {
					upOrDown = 1;
				}
				int index = guibutton.id / 3;
				boolean tempBool = tabEnabled[index];
				tabEnabled[index] = tabEnabled[index + upOrDown];
				tabEnabled[index + upOrDown] = tempBool;
				Tab tempTab = newOrder[index];
				newOrder[index] = newOrder[index + upOrDown];
				newOrder[index + upOrDown] = tempTab;
				String tempString = ((Button)buttons.get(index * 3 + 2)).text;
				((Button)buttons.get(index * 3 + 2)).text = ((Button)buttons.get((index + upOrDown) * 3 + 2)).text;
				((Button)buttons.get((index + upOrDown) * 3 + 2)).text = tempString;
			}
			for(int i = 0; i < newOrder.length; i++) {
				((Button)buttons.get(i * 3)).active = tabEnabled[i];
				((Button)buttons.get(i * 3 + 1)).active = tabEnabled[i];
				if(i == 0) ((Button)buttons.get(i * 3)).active = false;
				else if(i == newOrder.length - 1) ((Button)buttons.get(i * 3 + 1)).active = false;
			}
		}
	}

	public void updateScrolled(float amount){
		int i = getContentHeight() - (bottom - top - 4);
		if(i < 0) {
			i /= 2;
		}

		amountScrolled += amount;
		if(amountScrolled < 0.0F) {
			amountScrolled = 0.0F;
		} else if(amountScrolled > (float)i) {
			amountScrolled = i;
		}
	}

	@Override
	protected void keyPressed(char key, int keyId) {
		if(selectedButton >= 0) {
            if (keyId == 1) {
                //options.setKeyBinding(selectedButton, 0);
            }
            else {
            	//options.setKeyBinding(selectedButton, keyId);
            }
            selectedButton = -1;
		}
		else {
			super.keyPressed(key, keyId);
		}
	}

	@Override
	public void onMouseEvent() {
        int amount = Mouse.getEventDWheel();
        if(amount != 0 && getContentHeight() - (bottom - top - 4) > 0) {
        	if(amount > 0) {
    			amount = -1;
    		} else if(amount < 0) {
    			amount = 1;
    		}
        	updateScrolled((amount * slotHeight) / 2);
        }
		super.onMouseEvent();
	}

	protected int getContentHeight() {
		return getSize() * slotHeight + 2;
	}
	
	public int getSize() {
		//return 0;
		return allTabs.size();
	}

	@Override
	public void render(int mouseX, int mouseY, float f){
		//drawDefaultBackground();
		int size = getSize();
		int l = right / 2 + 124;
		int i1 = l + 6;
		if(Mouse.isButtonDown(0))
        {
            if(initialClickY == -1F)
            {
                boolean flag = true;
                if(mouseY >= top && mouseY <= bottom)
                {
                    int j1 = width / 2 - 110;
                    int k1 = width / 2 + 110;
                    int i2 = ((mouseY - top - 0) + (int)amountScrolled) - 4;
                    int k2 = i2 / slotHeight;
                    if(mouseX >= j1 && mouseX <= k1 && k2 >= 0 && i2 >= 0 && k2 < size)
                    {
                        boolean flag1 = System.currentTimeMillis() - lastClicked < 250L;
                        //elementClicked(k2, flag1);
                        //selectedElement = k2;
                        lastClicked = System.currentTimeMillis();
                    } else
                    if(mouseX >= j1 && mouseX <= k1 && i2 < 0)
                    {
                        //func_27255_a(mouseX - j1, ((mouseY - top) + (int)amountScrolled) - 4);
                        flag = false;
                    }
                    if(mouseX >= l && mouseX <= i1)
                    {
                        scrollMultiplier = -1F;
                        int i3 = getContentHeight() - (bottom - top - 4);
                        if(i3 < 1)
                        {
                            i3 = 1;
                        }
                        int l3 = (int)((float)((bottom - top) * (bottom - top)) / (float)getContentHeight());
                        if(l3 < 32)
                        {
                            l3 = 32;
                        }
                        if(l3 > bottom - top - 8)
                        {
                            l3 = bottom - top - 8;
                        }
                        scrollMultiplier /= (float)(bottom - top - l3) / (float)i3;
                    } else
                    {
                        scrollMultiplier = 1.0F;
                    }
                    if(flag)
                    {
                        initialClickY = mouseY;
                    } else
                    {
                        initialClickY = -2F;
                    }
                } else
                {
                    initialClickY = -2F;
                }
            } else
            if(initialClickY >= 0.0F)
            {
                amountScrolled -= ((float)mouseY - initialClickY) * scrollMultiplier;
                initialClickY = mouseY;
            }
        } else
        {
            initialClickY = -1F;
        }
        bindAmountScrolled();
		
		
		
		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(2912 /*GL_FOG*/);
		Tessellator tessellator = Tessellator.INSTANCE;
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, minecraft.textureManager.getTextureId("/gui/background.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f1 = 32F;
		tessellator.start();
		tessellator.colour(0x202020);
		tessellator.vertex(left, bottom, 0.0D, (float)left / f1, (float)(bottom + (int)amountScrolled) / f1);
		tessellator.vertex(right, bottom, 0.0D, (float)right / f1, (float)(bottom + (int)amountScrolled) / f1);
		tessellator.vertex(right, top, 0.0D, (float)right / f1, (float)(top + (int)amountScrolled) / f1);
		tessellator.vertex(left, top, 0.0D, (float)left / f1, (float)(top + (int)amountScrolled) / f1);
		tessellator.draw();
 
		int i2 = right / 2 - 92 - 16;
		int k2 = (top + 4) - (int)amountScrolled;
		for(int i3 = 0; i3 < size; i3++) {
			int k3 = k2 + i3 * slotHeight;
			int j4 = slotHeight - 4;
			if(k3 > bottom || k3 + j4 < top) {
				continue;
			}
			
			int left2 = width / 2 - 155;
			int start = (i3 * 3);
			for (int i = start; (i < start + 3); i++) {
				int offset = (i % 3) * 21;
				
				if (i < buttons.size()) {
					//drawString(fontRenderer, options.getKeyBindingDescription(i), left2 + offset + 70 + 6, k3 + 7, -1);
					Button button = (Button) buttons.get(i);
					if(selectedButton == i) {
						//button.text = "\247f> \247e??? \247f<";
					//} else if (duplicate && options.keyBindings[i].keyCode != 0) {
						 //button.text = (new StringBuilder()).append("\247c").append(options.getOptionDisplayString(i)).toString();
					} else {
						//button.text = options.getOptionDisplayString(i);
					}
					button.x = left2 + offset;
					button.y = k3;
					button.render(minecraft, mouseX, mouseY);
				}
			}
		}

		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
		byte byte0 = 4;
		overlayBackground(0, top, 255, 255);
		overlayBackground(bottom, height, 255, 255);
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glBlendFunc(770, 771);
		GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
		GL11.glShadeModel(7425 /*GL_SMOOTH*/);
		GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
		tessellator.start();
		tessellator.colour(0, 0);
		tessellator.vertex(left, top + byte0, 0.0D, 0.0D, 1.0D);
		tessellator.vertex(right, top + byte0, 0.0D, 1.0D, 1.0D);
		tessellator.colour(0, 255);
		tessellator.vertex(right, top, 0.0D, 1.0D, 0.0D);
		tessellator.vertex(left, top, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
		tessellator.start();
		tessellator.colour(0, 255);
		tessellator.vertex(left, bottom, 0.0D, 0.0D, 1.0D);
		tessellator.vertex(right, bottom, 0.0D, 1.0D, 1.0D);
		tessellator.colour(0, 0);
		tessellator.vertex(right, bottom - byte0, 0.0D, 1.0D, 0.0D);
		tessellator.vertex(left, bottom - byte0, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
		int contentHeight =getContentHeight() - (bottom - top - 4);
		if(contentHeight > 0) {
			int k4 = ((bottom - top) * (bottom - top)) / getContentHeight();
			if(k4 < 32) {
				k4 = 32;
			}
			if(k4 > bottom - top - 8) {
				k4 = bottom - top - 8;
			}
			int i5 = ((int)amountScrolled * (bottom - top - k4)) / contentHeight + top;
			if(i5 < top) {
				i5 = top;
			}
			tessellator.start();
			tessellator.colour(0, 255);
			tessellator.vertex(l, bottom, 0.0D, 0.0D, 1.0D);
			tessellator.vertex(i1, bottom, 0.0D, 1.0D, 1.0D);
			tessellator.vertex(i1, top, 0.0D, 1.0D, 0.0D);
			tessellator.vertex(l, top, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.start();
			tessellator.colour(0x808080, 255);
			tessellator.vertex(l, i5 + k4, 0.0D, 0.0D, 1.0D);
			tessellator.vertex(i1, i5 + k4, 0.0D, 1.0D, 1.0D);
			tessellator.vertex(i1, i5, 0.0D, 1.0D, 0.0D);
			tessellator.vertex(l, i5, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.start();
			tessellator.colour(0xc0c0c0, 255);
			tessellator.vertex(l, (i5 + k4) - 1, 0.0D, 0.0D, 1.0D);
			tessellator.vertex(i1 - 1, (i5 + k4) - 1, 0.0D, 1.0D, 1.0D);
			tessellator.vertex(i1 - 1, i5, 0.0D, 1.0D, 0.0D);
			tessellator.vertex(l, i5, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
		}
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		GL11.glShadeModel(7424 /*GL_FLAT*/);
		GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
		GL11.glDisable(3042 /*GL_BLEND*/);
		drawTextWithShadow(textManager, "Recipe Viewer Tab Order", width / 2, 20, 0xffffff);

		((Button)buttons.get(buttons.size()-2)).render(minecraft, mouseX, mouseY);
		((Button)buttons.get(buttons.size()-1)).render(minecraft, mouseX, mouseY);

	}
	
	 private void bindAmountScrolled()
	    {
	        int i = getContentHeight() - (bottom - top - 4);
	        if(i < 0)
	        {
	            i /= 2;
	        }
	        if(amountScrolled < 0.0F)
	        {
	            amountScrolled = 0.0F;
	        }
	        if(amountScrolled > (float)i)
	        {
	            amountScrolled = i;
	        }
	    }
	
	void overlayBackground(int top, int bottom, int k, int l) {
		Tessellator tessellator = Tessellator.INSTANCE;
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, minecraft.textureManager.getTextureId("/gui/background.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32F;
		tessellator.start();
		tessellator.colour(0x404040, l);
		tessellator.vertex(0.0D, bottom, 0.0D, 0.0D, (float)bottom / f);
		tessellator.vertex(right, bottom, 0.0D, (float)right / f, (float)bottom / f);
		tessellator.colour(0x404040, k);
		tessellator.vertex(right, top, 0.0D, (float)right / f, (float)top / f);
		tessellator.vertex(0.0D, top, 0.0D, 0.0D, (float)top / f);
		tessellator.draw();
	}
	
	
}
