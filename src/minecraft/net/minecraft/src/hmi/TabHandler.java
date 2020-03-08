package net.minecraft.src.hmi;

import java.util.ArrayList;

import net.minecraft.src.BaseMod;
import net.minecraft.src.ItemStack;

public abstract class TabHandler {

	public abstract void loadTabs(BaseMod basemod);
	
	public void registerItems(ArrayList<ItemStack> itemList) { }
}
