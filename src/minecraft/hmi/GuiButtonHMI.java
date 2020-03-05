package hmi;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class GuiButtonHMI extends GuiButton {
	
	public int iconIndex = -1;
	public boolean tmiStyle = false;
	private ItemStack item;

	public GuiButtonHMI(int id, int x, int y, int width, int height, String text) {
		super(id, x, y, width, height, text);
	}
	
	public GuiButtonHMI(int id, int x, int y, int width, int iconIndex) {
		super(id, x, y, width, width, "");
		this.iconIndex = iconIndex;
	}
	
	public GuiButtonHMI(int id, int x, int y, int width, int iconIndex, ItemStack item) {
		this(id, x, y, width, iconIndex);
		this.item = item;
	}

	public void drawButton(Minecraft minecraft, int i, int j)
    {
        if(!enabled2)
        {
            return;
        }
        FontRenderer fontrenderer = minecraft.fontRenderer;
        Utils.bindTexture("/gui/gui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean isHovered = i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
        int k = getHoverState(isHovered);
        drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height / 2);
        drawTexturedModalRect(xPosition, yPosition + height / 2, 0, 46 + k * 20 + 20 - height / 2, width / 2, height / 2);
        drawTexturedModalRect(xPosition + width / 2, yPosition + height / 2, 200 - width / 2, 46 + k * 20 + 20 - height / 2, width / 2, height / 2);
        mouseDragged(minecraft, i, j);
        if(item != null && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
        	Utils.drawItemStack(xPosition + 2, yPosition + 2, item, true);
        }
        else if(iconIndex < 0) {
        	if(!enabled)
            {
                drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xffa0a0a0);
            } else
            if(isHovered)
            {
                drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xffffa0);
            } else
            {
                drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xe0e0e0);
            }
        }
        else {
        	Utils.bindTexture();
        	drawTexturedModalRect(xPosition, yPosition, (iconIndex % 12)*21, (iconIndex / 12)*21, width, width);
        }
    }
}
