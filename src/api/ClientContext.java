package api;

import org.powerbot.bot.ScriptController;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;

import java.awt.*;

public class ClientContext extends org.powerbot.script.rt4.ClientContext {
    public static final int invalidItemID = -1;

    public final Inventory inventory;
    public final Bank bank;
    public final Players players;
    public final Movement movement;
    public final Skills skills;
    public final Magic magic;
    public final DepositBox depositBox;
    public final Camera camera;

//    public final Components components;

    public ClientContext(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
        this.inventory = new Inventory(ctx);
        this.bank = new Bank(ctx);
        this.players = new Players(ctx);
        this.movement = new Movement(ctx);
        this.skills = new Skills(ctx);
        this.magic = new Magic(ctx);
        this.depositBox = new DepositBox(ctx);
        this.camera = new Camera(ctx);
//        this.components = new Components(ctx);
    }
}
