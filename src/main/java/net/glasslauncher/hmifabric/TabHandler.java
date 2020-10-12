package net.glasslauncher.hmifabric;

import java.util.ArrayList;

import net.minecraft.item.ItemInstance;
import net.modificationstation.stationloader.api.common.mod.StationMod;

public abstract class TabHandler {

	public abstract void loadTabs(StationMod basemod);
	
	public void registerItems(ArrayList<ItemInstance> itemList) { }
}
