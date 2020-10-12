package net.glasslauncher.hmifabric;

import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.OptionButton;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.input.Keyboard;

public class GuiControlsHMI extends ScreenBase {

	public GuiControlsHMI(ScreenBase guiscreen)
    {
        parentScreen = guiscreen;
    }
	
	private Button buttonDone;
	private int buttonId = -1;
	
	private int func_20080_j()
    {
        return width / 2 - 155;
    }

    @Override
	public void init()
    {
        
        int i = func_20080_j();
        for(int j = 0; j < binds.length; j++)
        {
            buttons.add(new OptionButton(j, i + (j % 2) * 160, height / 6 + 24 * (j >> 1), 70, 20, Keyboard.getKeyName(binds[j].key)));
        }
        
        buttons.add(buttonDone = new Button(-1, width / 2 - 100, height / 6 + 168, "Done"));
    }

    @Override
	protected void mouseClicked(int i, int j, int k)
    {
		if(buttonId > -1 && k == 0) {
			for(int l = 0; l < buttons.size(); l++)
            {
                Button guibutton = (Button)buttons.get(l);
                if(guibutton.id == buttonId) {
                	if(!guibutton.isMouseOver(minecraft, i, j))
                    {
                        guibutton.text = Keyboard.getKeyName(binds[l].key);
                        buttonId = -1;
                        break;
                    }
                }
            }
		}
        super.mouseClicked(i, j, k);
    }

    @Override
	protected void keyPressed(char c, int i)
    {
        if(buttonId >= 0)
        {
        	if(i == 1) i = 0;
        	if(binds[buttonId] == Config.toggleOverlay) {
        		for (int j = 0; j < minecraft.options.keyBindings.length; j++) {
        			if(minecraft.options.keyBindings[j] == Config.toggleOverlay) {
        				minecraft.options.method_1226(j, i);
        				break;
        			}
        		}
        	}
            binds[buttonId].key = i;
            ((Button)buttons.get(buttonId)).text = Keyboard.getKeyName(i);
            buttonId = -1;
            HowManyItems.onSettingChanged();
        } else
        {
            super.keyPressed(c, i);
        }
    }

    @Override
	protected void buttonClicked(Button guibutton)
    {
         if(guibutton == buttonDone)
        {
            //minecraft.options.saveOptions();
            minecraft.openScreen(parentScreen);
            return;
        }
        else
        {
             buttonId = guibutton.id;
             guibutton.text = "> " + Keyboard.getKeyName(binds[guibutton.id].key) + " <";
        }
         HowManyItems.onSettingChanged();
    }

    @Override
	public void render(int i, int j, float f)
    {
        renderBackground();
        drawTextWithShadowCentred(textManager, "HMI Keybinds", width / 2, 20, 0xffffff);
        int k = func_20080_j();
        for(int l = 0; l < binds.length; l++)
        {
            drawTextWithShadow(textManager, binds[l].name, k + (l % 2) * 160 + 70 + 6, height / 6 + 24 * (l >> 1) + 7, -1);
        }

        super.render(i, j, f);
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
	
	private ScreenBase parentScreen;
}
