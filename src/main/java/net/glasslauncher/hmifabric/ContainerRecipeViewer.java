// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.glasslauncher.hmifabric;

import net.minecraft.container.ContainerBase;
import net.minecraft.container.slot.Slot;
import net.minecraft.entity.player.PlayerBase;

// Referenced classes of package net.minecraft.src:
//            Container, IInventory, Slot, EntityPlayer, 
//            ItemStack

public class ContainerRecipeViewer extends ContainerBase
{

    public ContainerRecipeViewer(InventoryRecipeViewer iinventory)
    {
    	//setting the windowId to -1 prevents server registering recipe clicks as inventory clicks
    	this.currentContainerId = -1;
    	inv = iinventory;
    	resetSlots();
    }

    public void resetSlots() {
    	super.slots.clear();
    	count = 0;
    }

    // Not an override. Custom method.
    public void addSlot(int i, int j) {
    	addSlot(new Slot(inv, count++, i, j));
    }

    @Override
    public boolean canUse(PlayerBase entityplayer)
    {
        return inv.canPlayerUse(entityplayer);
    }

    private int count;
    private InventoryRecipeViewer inv;
}
