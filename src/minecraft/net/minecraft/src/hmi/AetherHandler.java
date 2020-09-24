package net.minecraft.src.hmi;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiScreen;

public abstract class AetherHandler extends TabHandler {

	public abstract boolean isInventory(GuiScreen screen);
	
	public abstract GuiScreen newInv(EntityPlayer player);
}
