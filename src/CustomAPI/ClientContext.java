package CustomAPI;

public class ClientContext extends org.powerbot.script.rt4.ClientContext {
    public static final int invalidItemID = -1;

    public final Inventory inventory;
    public final Bank bank;
    public final Players players;
    public final Movement movement;
    public final Skills skills;

    public ClientContext(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
        this.inventory = new Inventory(ctx);
        this.bank = new Bank(ctx);
        this.players = new Players(ctx);
        this.movement = new Movement(ctx);
        this.skills = new Skills(ctx);
    }


    public static class itemSkillPair {
        int ID;
        int level;
        public itemSkillPair(int ID, int level) {
            this.ID = ID;
            this.level = level;
        }
    }
}
