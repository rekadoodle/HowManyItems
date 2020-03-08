package net.minecraft.src.hmi.references.buildcraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import buildcraft.factory.GuiAutoCrafting;
import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;
import net.minecraft.src.hmi.TabUtils;
import net.minecraft.src.hmi.Utils;
import net.minecraft.src.hmi.references.aether.TabAether;

public class ConcreteHandler {
	

	private static Field buildcraftBlockField;

	public ConcreteHandler() {
		buildcraftBlockField = Utils.getField(BuildCraftFactory.class, new String[] {"autoWorkbenchBlock"});
		try {
			Block block = (Block) buildcraftBlockField.get(null);
			mod_HowManyItems.addEquivalentWorkbench(new ItemStack(block));
			mod_HowManyItems.addWorkBenchGui(GuiAutoCrafting.class);
			mod_HowManyItems.addGuiToBlock(GuiAutoCrafting.class, new ItemStack(block));
		} 
		catch (Exception e) { e.printStackTrace(); } 
	}
}