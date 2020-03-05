// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;


// Referenced classes of package net.minecraft.src:
//            Container, IInventory, Slot, EntityPlayer, 
//            ItemStack

public class ContainerRecipeViewer extends Container
{

    public ContainerRecipeViewer(InventoryRecipeViewer iinventory)
    {
    	inv = iinventory;
    	resetSlots();
    }
    
    public void resetSlots() {
    	super.slots.clear();
    	count = 0;
    }
    
    public void addSlot(int i, int j) {
    	addSlot(new Slot(inv, count++, i, j));
    }
   
    public boolean isUsableByPlayer(EntityPlayer entityplayer)
    {
        return inv.canInteractWith(entityplayer);
    }

    private int count;
    private InventoryRecipeViewer inv;
}
