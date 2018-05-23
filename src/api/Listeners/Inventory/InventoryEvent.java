package api.Listeners.Inventory;

import org.powerbot.script.rt4.Item;

import java.util.EventObject;

public class InventoryEvent extends EventObject {

    private final int inventorySlot;
    private final Item oldItem;
    private final Item newItem;
    private final int oldStacksize;
    private final int newStacksize;

    public InventoryEvent(int inventorySlot, Item oldItem, Item newItem, int oldStacksize, int newStacksize) {
        super(inventorySlot);
        this.inventorySlot = inventorySlot;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.oldStacksize = oldStacksize;
        this.newStacksize = newStacksize;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public Item getOldItem() {
        return oldItem;
    }

    public Item getNewItem() {
        return newItem;
    }

    public int getOldStacksize() {
        return oldStacksize;
    }

    public int getNewStacksize() {
        return newStacksize;
    }
}