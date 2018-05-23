package api.Listeners.Inventory;

import api.Listeners.EventDispatcher;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

public class InventoryEventSource implements Runnable {
    private final EventDispatcher dispatcher;
    private final ClientContext ctx;
    private final int inventorySlots = 28;
//    private final Map<Integer, Item> inventoryCache;
    private Item[] inventory;
    private Pair[] inventoryCache = new Pair[28];

    public InventoryEventSource(EventDispatcher dispatcher, ClientContext ctx) {
        this.ctx = ctx;
//        this.inventoryCache = new HashMap<Integer, Item>();

        for (int i = 0; i < inventorySlots; i++) {
            Item item = getInventoryItem(i);
            inventoryCache[i] = new Pair(item,item.stackSize());
        }
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        while (dispatcher.isRunning() || !ctx.controller.isStopping()) {
            inventory = ctx.inventory.items();
            for (int i = 0; i < inventorySlots; i++) {

                Pair o = inventoryCache[i];
                Item item = inventory[i];
                if (item == null || item.id() <= 0) continue;
                Pair n = new Pair(item,item.stackSize());

                if (o.item == null || n.item == null) continue;

                if (o.item.id() != n.item.id() || o.stackSize != n.stackSize) {
                    dispatcher.fireEvent(new InventoryEvent(i, o.item, n.item, o.stackSize, n.stackSize));
                    inventoryCache[i] = n;
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private Item getInventoryItem(int inventoryIndex) {
        return ctx.inventory.itemAt(inventoryIndex);
    }

    private class Pair {
        Item item;
        int stackSize;

        Pair(Item item, int stackSize) {
            this.item = item;
            this.stackSize = stackSize;
        }
    }
}
