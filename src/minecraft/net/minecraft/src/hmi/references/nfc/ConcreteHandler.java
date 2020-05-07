package net.minecraft.src.hmi.references.nfc;

import net.minecraft.src.*;
import net.minecraft.src.hmi.TabHandler;

public class ConcreteHandler extends TabHandler {

	@Override
	public void loadTabs(BaseMod basemod) {
		mod_HowManyItems.addTab(new TabBrickOven(basemod));
	}
}
