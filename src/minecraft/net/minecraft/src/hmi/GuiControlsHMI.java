package net.minecraft.src.hmi;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.*;
import net.minecraft.src.hmi.GuiOverlay;

public class GuiControlsHMI extends GuiScreen {

	public GuiControlsHMI(GuiScreen guiscreen)
    {
        parentScreen = guiscreen;
    }
	
	private GuiButton buttonDone;
	private int buttonId = -1;
	
	private int func_20080_j()
    {
        return width / 2 - 155;
    }
	
	public void initGui()
    {
        
        int i = func_20080_j();
        for(int j = 0; j < binds.length; j++)
        {
            controlList.add(new GuiSmallButton(j, i + (j % 2) * 160, height / 6 + 24 * (j >> 1), 70, 20, Keyboard.getKeyName(binds[j].keyCode)));
        }
        
        controlList.add(buttonDone = new GuiButton(-1, width / 2 - 100, height / 6 + 168, "Done"));
    }
	
	protected void mouseClicked(int i, int j, int k)
    {
		if(buttonId > -1 && k == 0) {
			for(int l = 0; l < controlList.size(); l++)
            {
                GuiButton guibutton = (GuiButton)controlList.get(l);
                if(guibutton.id == buttonId) {
                	if(!guibutton.mousePressed(mc, i, j))
                    {
                        guibutton.displayString = Keyboard.getKeyName(binds[l].keyCode);
                        buttonId = -1;
                        break;
                    }
                }
            }
		}
        super.mouseClicked(i, j, k);
    }
	
	protected void keyTyped(char c, int i)
    {
        if(buttonId >= 0)
        {
        	if(i == 1) i = 0;
        	if(binds[buttonId] == Config.toggleOverlay) {
        		for (int j = 0; j < mc.gameSettings.keyBindings.length; j++) {
        			if(mc.gameSettings.keyBindings[j] == Config.toggleOverlay) {
        				mc.gameSettings.setKeyBinding(j, i);
        				break;
        			}
        		}
        	}
            binds[buttonId].keyCode = i;
            ((GuiButton)controlList.get(buttonId)).displayString = Keyboard.getKeyName(i);
            buttonId = -1;
            mod_HowManyItems.onSettingChanged();
        } else
        {
            super.keyTyped(c, i);
        }
    }
	
	protected void actionPerformed(GuiButton guibutton)
    {
         if(guibutton == buttonDone)
        {
            //mc.gameSettings.saveOptions();
            mc.displayGuiScreen(parentScreen);
            return;
        }
        else
        {
             buttonId = guibutton.id;
             guibutton.displayString = (new StringBuilder()).append("> ").append(Keyboard.getKeyName(binds[guibutton.id].keyCode)).append(" <").toString();
        }
         mod_HowManyItems.onSettingChanged();
    }
	
	public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "HMI Keybinds", width / 2, 20, 0xffffff);
        int k = func_20080_j();
        for(int l = 0; l < binds.length; l++)
        {
            drawString(fontRenderer, binds[l].keyDescription, k + (l % 2) * 160 + 70 + 6, height / 6 + 24 * (l >> 1) + 7, -1);
        }

        super.drawScreen(i, j, f);
    }
	
	private static KeyBinding[] binds = { 
			Config.pushRecipe, 
			Config.pushUses, 
			Config.prevRecipe, 
			Config.allRecipes, 
			Config.clearSearchBox, 
			Config.focusSearchBox,
			Config.toggleOverlay
			};
	
	private GuiScreen parentScreen;
}
