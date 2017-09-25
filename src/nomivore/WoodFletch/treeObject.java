package nomivore.WoodFletch;


import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;

import java.awt.*;

public class treeObject {
    String treeName;
    int logID;
    Tile dest;
    int level;
    Component widget;

    public treeObject(String s, int i, Tile t, int l, Component w) {
        treeName = s;
        logID = i;
        dest = t;
        level = l;
        widget = w;
    }
}
