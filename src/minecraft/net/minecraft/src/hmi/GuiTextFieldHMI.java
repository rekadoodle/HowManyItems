package net.minecraft.src.hmi;

import net.minecraft.src.*;

public class GuiTextFieldHMI extends GuiTextField {

	public GuiTextFieldHMI(GuiScreen guiscreen, FontRenderer fontrenderer, int i, int j, int k, int l, String s) {
		super(guiscreen, fontrenderer, i, j, k, l, s);
		xPos = i;
        yPos = j;
        width = k;
        height = l;
	}

	public boolean hovered(int posX, int posY)
    {
        return isEnabled && posX >= xPos && posX < xPos + width && posY >= yPos && posY < yPos + height;
    }
	
	@Override
	public void mouseClicked(int posX, int posY, int eventButton) {
        super.mouseClicked(posX, posY, eventButton);
        if(this.isFocused && eventButton == 1) {
        	GuiOverlay.clearSearchBox();
        }
    }
	
	private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
}
