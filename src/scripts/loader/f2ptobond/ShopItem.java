package scripts.loader.f2ptobond;

import api.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GeItem;

public class ShopItem {
    private ClientContext ctx;
    private Component component;
    private int minimumStock;
    private int id;
    private int gePrice;
    private String interact = "";

    public ShopItem(ClientContext ctx, int minimumStock, int id) {
        this.ctx = ctx;
        this.minimumStock = minimumStock;
        this.id = id;
        this.component = ctx.components.nil();
    }

    public ShopItem(ClientContext ctx, int minimumStock, int id, String interact) {
        this.ctx = ctx;
        this.minimumStock = minimumStock;
        this.id = id;
        this.component = ctx.components.nil();
        this.interact = interact;
    }

    public Component getComponent() {
        if (component.equals(ctx.components.nil())) {
            component = ctx.components.select().itemId(this.id).poll();
        }
        return component;
    }

    public int getGePrice() {
        return gePrice;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public String getInteract() {
        return interact;
    }
}
