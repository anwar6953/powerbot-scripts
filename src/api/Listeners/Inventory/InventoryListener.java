package api.Listeners.Inventory;

import java.util.EventListener;

public interface InventoryListener extends EventListener {
    void onInventoryChange(InventoryEvent inventoryEvent);
}
