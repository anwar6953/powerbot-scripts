package scripts.AIOHerblore;

class ItemObj {
    public int getId() {
        return id;
    }

    public int getQ() {
        return q;
    }

    private int id = -1;
    private int q = 0;

    ItemObj(int id, int q) {
        this.id = id;
        this.q = q;
    }
}
