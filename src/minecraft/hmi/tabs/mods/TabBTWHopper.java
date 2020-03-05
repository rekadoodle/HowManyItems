package hmi.tabs.mods;

import java.util.Map;

import hmi.Utils;
import net.minecraft.src.BaseMod;
import net.minecraft.src.mod_FCBetterThanWolves;

public class TabBTWHopper extends TabGenericBlock {

	public TabBTWHopper(BaseMod tabCreator, Map recipes) {
		super(tabCreator, 2, 2, recipes, 140, 45, 3, 3, mod_FCBetterThanWolves.fcHopper, 0);
		slots[0] = new Integer[] {2, HEIGHT / 2 - 5};
		slots[1] = new Integer[] {WIDTH / 2 - slotOffsetX - 9, HEIGHT / 2 - 16};
	}

	public void draw(int x, int y, int recipeOnThisPageIndex, int cursorX, int cursorY) {
		drawSlotsAndArrows(x, y);
		Utils.drawScaledItem(getBlockToDraw(), x + WIDTH / 2 - 14, y + HEIGHT / 2, 31);
	}
}
