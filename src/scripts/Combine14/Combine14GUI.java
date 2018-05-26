package scripts.Combine14;

import scripts.ID;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Combine14GUI {
    private boolean done = false;
    private JFrame frame = new JFrame("Combine14");
    private JButton button1 = new JButton("Start");
    private JButton button2 = new JButton("Add to list");
    private JTextField textField1 = new JTextField(7);
    private JTextField textField2 = new JTextField(7);
    private JTextField textField1q = new JTextField(7);
    private JTextField textField2q = new JTextField(7);
    private ArrayList<JTextField> tfList = new ArrayList<>();
    private JList list1 = new JList();
    private JList list2 = new JList();
    private JComboBox<IDPair> dropList = new JComboBox();
    private DefaultListModel listModel1 = new DefaultListModel();
    private DefaultListModel listModel2 = new DefaultListModel();

    public Combine14GUI() {
        frame.setLayout(new BorderLayout());
        Container pane = frame.getContentPane();
        frame.setContentPane(pane);

        textField1.setMaximumSize(textField1.getPreferredSize());
        textField1.setToolTipText("ID of item 1");
        textField1.setText(textField1.getToolTipText());
        textField2.setMaximumSize(textField2.getPreferredSize());
        textField2.setToolTipText("ID of item 2");
        textField2.setText(textField2.getToolTipText());
        button2.setMaximumSize(button2.getPreferredSize());

        textField1q.setMaximumSize(textField1q.getPreferredSize());
        textField1q.setToolTipText("Quantity 1");
        textField1q.setText(textField1q.getToolTipText());
        textField2q.setMaximumSize(textField2q.getPreferredSize());
        textField2q.setToolTipText("Quantity 2");
        textField2q.setText(textField2q.getToolTipText());
        tfList.add(textField1);
        tfList.add(textField1q);
        tfList.add(textField2);
        tfList.add(textField2q);

        JPanel subPanel1 = new JPanel();
        subPanel1.add(textField1);
        subPanel1.add(textField2);
        subPanel1.add(button2);
        JPanel subPanel2 = new JPanel();
        subPanel2.add(new JLabel("0 for all, default 14-All"));
        JPanel subPanel3 = new JPanel();
        subPanel3.add(textField1q);
        subPanel3.add(textField2q);
        JPanel borderSubPanel = new JPanel(new BorderLayout());
        borderSubPanel.add(subPanel1,BorderLayout.NORTH);
        borderSubPanel.add(subPanel2,BorderLayout.CENTER);
        borderSubPanel.add(subPanel3,BorderLayout.SOUTH);

        pane.add(borderSubPanel,BorderLayout.LINE_END);

        button1.setMaximumSize(new Dimension(200, 50));
        button1.setPreferredSize(new Dimension(200, 50));
        pane.add(button1, BorderLayout.PAGE_END);

        list1.setPreferredSize(new Dimension(100, 100));
        list1.setMaximumSize(new Dimension(100, 100));
        pane.add(list1, BorderLayout.LINE_START);

        list2.setPreferredSize(new Dimension(100, 100));
        list1.setMaximumSize(new Dimension(100, 100));
        pane.add(list2, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        list1.setModel(listModel1);
        list2.setModel(listModel2);

        final List<IDPair> displayList = new ArrayList();

//        displayList.add(new IDPair(ID.DOUGH_PIZZA,ID.TOMATO,"Incomplete pizza"));
//        displayList.add(new IDPair(ID.PIZZA_INCOMPLETE,ID.CHEESE,"Uncooked pizza"));
        displayList.add(new IDPair(ID.JUG_OF_WATER_1937, ID.GRAPES_1987,14,14,"Wine maker"));
        displayList.add(new IDPair(1755,1623,1,14,"Cut sapphires"));
        displayList.add(new IDPair(227,249,14,14,"Unfinished guam potions"));

        for (IDPair i : displayList) {
            listModel2.addElement(i.name);
            dropList.addItem(i);
        }

        textField1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField1.getText().equals(textField1.getToolTipText())) {
                    textField1.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField1.getText().isEmpty()) {
                    textField1.setText(textField1.getToolTipText());
                }
            }
        });
        textField2.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField2.getText().equals(textField2.getToolTipText())) {
                    textField2.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField2.getText().isEmpty()) {
                    textField2.setText(textField2.getToolTipText());
                }
            }
        });
        textField1q.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField1q.getText().equals(textField1q.getToolTipText())) {
                    textField1q.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField1q.getText().isEmpty()) {
                    textField1q.setText(textField1q.getToolTipText());
                }
            }
        });
        textField2q.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField2q.getText().equals(textField2q.getToolTipText())) {
                    textField2q.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField2q.getText().isEmpty()) {
                    textField2q.setText(textField2q.getToolTipText());
                }
            }
        });

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
                for (IDPair it : displayList) {
                    if (it.name.equals(list1.getModel().getElementAt(i))) {
//                            System.out.println(list1.getModel().getElementAt(i));
//                            System.out.printf("%d, %d",it.id1, it.id2);
                        Combine14.itemList.add(new IDPair(it.id1, it.id2, it.id1q, it.id2q, it.name));
                    }
                }
            }
            done = true;
            frame.dispose();
        });

        button2.addActionListener(e -> {
            int a = readIntTF(textField1);
            int b = readIntTF(textField2);
            int aq = readIntTF(textField1q);
            int bq = readIntTF(textField2q);
            if (checkValid(a,b)) {
                String s = String.format("%d(%s) %d(%s)", a, formatQuantity(aq,true), b, formatQuantity(bq,false));
                displayList.add(new IDPair(a, b, aq, bq, s));
                listModel1.addElement(s);

            }
            for (JTextField tf : tfList) {
                tf.setText(tf.getToolTipText());
            }
        });
    }

    public boolean done() {
        return done;
    }

    public class IDPair {
        int id1;
        int id2;
        int id1q;
        int id2q;
        String name;

        public IDPair(int id1, int id2, int id1q, int id2q, String n) {
            this.id1 = id1;
            this.id2 = id2;
            this.id1q = id1q;
            this.id2q = id2q;
            this.name = n;
        }
    }


    private int readIntTF(JTextField tf) {
        int value = -1;
        try {
            value = Integer.parseInt(tf.getText());
        } catch (NumberFormatException nfe) {
        }
        return value;
    }

    private boolean checkValid(int... arr) {
        boolean valid = true;
        for (int i : arr) {
            if (i == -1) valid = false;
        }
        return valid;
    }

    private String formatQuantity(int i,boolean b) {
        switch (i) {
            case -1:
                if (b) {
                    return "14";
                } else {
                    return "All";
                }
            case 0:
                return "All";
            default:
                return String.valueOf(i);
        }
    }
}