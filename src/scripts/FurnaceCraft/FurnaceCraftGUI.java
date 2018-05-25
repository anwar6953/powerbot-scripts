package scripts.FurnaceCraft;

import scripts.ID;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class FurnaceCraftGUI extends JFrame {
    private boolean done = false;
    private JFrame frame = new JFrame("FurnaceCraft");
    private JButton button1 = new JButton("Start");
    private JButton button2 = new JButton("Add to list");
//    private JTextField combobox2 = new JTextField();
//    private JTextField combobox1 = new JTextField();
    private JComboBox<GEM> combobox1 = new JComboBox(GEM.values());
    private JComboBox<JEWELLERY> combobox2 = new JComboBox(JEWELLERY.values());
    private JList list1 = new JList();
    private JList list2 = new JList();
    private DefaultListModel listModel1 = new DefaultListModel();
    private DefaultListModel listModel2 = new DefaultListModel();

    public FurnaceCraftGUI() {
        setLayout(new BorderLayout());
        Container pane = frame.getContentPane();
        setContentPane(pane);

        combobox2.setMaximumSize(new Dimension(75, 20));
        combobox2.setPreferredSize(new Dimension(75, 20));
        combobox1.setMaximumSize(new Dimension(75, 20));
        combobox1.setPreferredSize(new Dimension(75, 20));
        button2.setMaximumSize(new Dimension(50, 50));
        button2.setPreferredSize(new Dimension(50, 50));

        JPanel subPanel = new JPanel();
        subPanel.add(combobox1);
        subPanel.add(combobox2);
        subPanel.add(button2);

        pane.add(subPanel,BorderLayout.LINE_END);

        button1.setMaximumSize(new Dimension(200, 50));
        button1.setPreferredSize(new Dimension(200, 50));
        pane.add(button1, BorderLayout.PAGE_END);

        list1.setPreferredSize(new Dimension(150, 250));
        list1.setMaximumSize(new Dimension(150, 250));
        pane.add(list1, BorderLayout.LINE_START);

        list2.setPreferredSize(new Dimension(150, 250));
        list2.setMaximumSize(new Dimension(150, 250));
        pane.add(list2, BorderLayout.CENTER);

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        list1.setModel(listModel1);
        list2.setModel(listModel2);

        List<craftObj> displayList = new ArrayList();

        displayList.add(new craftObj(GEM.DIAMOND,JEWELLERY.BRACELET));
        displayList.add(new craftObj(GEM.EMERALD,JEWELLERY.AMULET));

        for (craftObj i : displayList) {
            listModel2.addElement(i.name);
        }

        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int index = list1.locationToIndex(e.getPoint());
                    String elm = (String)listModel1.elementAt(index);
                    listModel1.removeElementAt(index);
                    listModel2.addElement(elm);
                }
            }
        });

        list2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int index = list2.locationToIndex(e.getPoint());
                    String elm = (String)listModel2.elementAt(index);
                    listModel1.addElement(elm);
                    listModel2.removeElementAt(index);
                }
            }
        });


        button1.addActionListener(e -> {
            for(int i = 0; i< list1.getModel().getSize();i++){
//                    System.out.println(list1.getModel().getElementAt(i));
                for (craftObj it : displayList) {
                    if (it.name.equals(list1.getModel().getElementAt(i))) {
//                            System.out.println(list1.getModel().getElementAt(i));
//                            System.out.printf("%d, %d",it.a, it.b);
                        EdgeCraft.craftList.add(it);
                    }
                }
            }
            done = true;
            dispose();
        });

        button2.addActionListener(e -> {
            GEM g = (GEM)combobox1.getSelectedItem();
            JEWELLERY j = (JEWELLERY)combobox2.getSelectedItem();
            craftObj n = new craftObj(g,j);
            displayList.add(n);
            listModel1.addElement(n.name);
        });
    }

    public boolean done() {
        return done;
    }


    private enum GEM {
        SAPPHIRE(ID.SAPPHIRE,1),
        EMERALD(ID.EMERALD,2),
        RUBY(ID.RUBY,3),
        DIAMOND(ID.DIAMOND,4),
        DRAGONSTONE(ID.DRAGONSTONE,5);

        private int id;
        private int offset;

        private GEM(int i, int o) {
            id = i;
            offset = o;
        }

    }

    public static int
            CRAFT_RING_BASE = 7,
            CRAFT_NECKLACE_BASE = 21,
            CRAFT_AMULET_BASE = 34,
            CRAFT_BRACELET_BASE = 47,
            CRAFT_SAPPHIRE_NECKLACE = 22;

    private enum JEWELLERY {
        RING(CRAFT_RING_BASE,ID.RING_MOULD),
        AMULET(CRAFT_AMULET_BASE,ID.AMULET_MOULD),
        NECKLACE(CRAFT_NECKLACE_BASE,ID.NECKLACE_MOULD),
        BRACELET(CRAFT_BRACELET_BASE,ID.BRACELET_MOULD);

        private int type;
        private int toolID;
        private JEWELLERY(int t, int i) {
            type = t;
            toolID = i;
        }
    }

    public class craftObj {
        int gemID;
        int toolID;
        int component;
        String name;

        public craftObj (GEM g, JEWELLERY j) {
            gemID = g.id;
            toolID = j.toolID;
            component = j.type + g.offset;
            name = g.toString() + " " + j.toString();
        }
    }
}