package scripts.AIOThiever;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;


public class ThiefGUI {
    private boolean done = false;
    private JFrame frame = new JFrame("Thief UI");

    public ThiefGUI() {
    }

    public void setVisible() {
        frame.setVisible(true);
    }
}
