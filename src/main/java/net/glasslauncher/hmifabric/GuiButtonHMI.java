package net.glasslauncher.hmifabric;

import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.item.ItemInstance;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

public class GuiButtonHMI extends Button {
	
	public int iconIndex = -1;
	public boolean tmiStyle = false;
	private ItemInstance item;

	public GuiButtonHMI(int id, int x, int y, int width, int height, String text) {
		super(id, x, y, width, height, text);
	}
	
	public GuiButtonHMI(int id, int x, int y, int width, int iconIndex) {
		super(id, x, y, width, width, "");
		this.iconIndex = iconIndex;
	}
	
	public GuiButtonHMI(int id, int x, int y, int width, int iconIndex, ItemInstance item) {
		this(id, x, y, width, iconIndex);
		this.item = item;
	}

	@Override
	public void render(Minecraft minecraft, int i, int j)
    {
        if(!visible)
        {
            return;
        }
        TextRenderer fontrenderer = minecraft.textRenderer;
        Utils.bindTexture("/gui/gui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean isHovered = i >= x && j >= y && i < x + width && j < y + height;
        int k = getYImage(isHovered);
        blit(x, y, 0, 46 + k * 20, width / 2, height);
        blit(x + width / 2, y, 200 - width / 2, 46 + k * 20, width / 2, height / 2);
        blit(x, y + height / 2, 0, 46 + k * 20 + 20 - height / 2, width / 2, height / 2);
        blit(x + width / 2, y + height / 2, 200 - width / 2, 46 + k * 20 + 20 - height / 2, width / 2, height / 2);
        postRender(minecraft, i, j);
        if(item != null && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
        	Utils.drawItemStack(x + 2, y + 2, item, true);
        }
        else if(iconIndex < 0) {
        	if(!active)
            {
                drawTextWithShadowCentred(fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xffa0a0a0);
            } else
            if(isHovered)
            {
                drawTextWithShadowCentred(fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xffffa0);
            } else
            {
                drawTextWithShadowCentred(fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xe0e0e0);
            }
        }
        else {
        	Utils.bindTexture();
        	blit(x, y, (iconIndex % 12)*21, (iconIndex / 12)*21, width, width);
        }
    }
}
