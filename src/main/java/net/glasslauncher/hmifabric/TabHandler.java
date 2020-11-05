package net.glasslauncher.hmifabric;

import java.util.ArrayList;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.item.ItemInstance;

public abstract class TabHandler {

	public abstract void loadTabs(ClientModInitializer basemod);
	
	public void registerItems(ArrayList<ItemInstance> itemList) { }
}
