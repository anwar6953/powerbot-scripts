package nomivore.WoodFletch;


import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;

import java.awt.*;

public class treeObject {
    String treeName;
    int logID;
    Tile dest;
    int level;

    public treeObject(String s, int i, Tile t, int l) {
        treeName = s;
        logID = i;
        dest = t;
        level = l;
    }
}
