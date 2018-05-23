package scripts.SarimSeagulls;

import org.powerbot.script.rt4.ClientContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SarimSeagullsGUI extends JFrame {
    private boolean done = false;
    private int action = 1;
    private JFrame frame = new JFrame("Sarim Gull Culler");
    private JButton button1 = new JButton("Start");
    private JComboBox comboBox = new JComboBox();

    public SarimSeagullsGUI() {
        setLayout(new BorderLayout(0,30));
        Container pane = frame.getContentPane();
        setContentPane(pane);
        comboBox.addItem("Withdraw bones");
        comboBox.addItem("Bury bones");
        comboBox.addItem("Just kill");
        button1.setPreferredSize(new Dimension(200, 50));
        pane.add(button1, BorderLayout.PAGE_END);
        comboBox.setPreferredSize(new Dimension(200, 50));
        pane.add(comboBox, BorderLayout.CENTER);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);

        button1.addActionListener(e -> {
            if (comboBox.getSelectedItem().equals("Withdraw bones")) action = 1;
            if (comboBox.getSelectedItem().equals("Bury bones")) action = 2;
            if (comboBox.getSelectedItem().equals("Just kill")) action = 3;
            done = true;
            dispose();
        });
    }

    public boolean done() {
        return done;
    }
    public int returnAction() {
        return action;
    }
}