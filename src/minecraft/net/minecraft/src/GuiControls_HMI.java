package net.minecraft.src;

import org.lwjgl.input.Keyboard;

public class GuiControls_HMI extends GuiScreen {

	public GuiControls_HMI(GuiScreen guiscreen)
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
        	if(binds[buttonId] == mod_HowManyItems.toggleOverlay) {
        		for (int j = 0; j < mc.gameSettings.keyBindings.length; j++) {
        			if(mc.gameSettings.keyBindings[j] == mod_HowManyItems.toggleOverlay) {
        				mc.gameSettings.setKeyBinding(j, i);
        			}
        		}
        	}
            binds[buttonId].keyCode = i;
            ((GuiButton)controlList.get(buttonId)).displayString = Keyboard.getKeyName(i);
            buttonId = -1;
            Gui_HMI.onSettingChanged();
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
        Gui_HMI.onSettingChanged();
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
			mod_HowManyItems.pushRecipe, 
			mod_HowManyItems.pushUses, 
			mod_HowManyItems.prevRecipe, 
			mod_HowManyItems.allRecipes, 
			mod_HowManyItems.clearSearchBox, 
			mod_HowManyItems.focusSearchBox,
			mod_HowManyItems.toggleOverlay
			};
	
	private GuiScreen parentScreen;
}
