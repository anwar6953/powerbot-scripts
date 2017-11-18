package nomivore.Combine14;

import nomivore.ID;

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
    private JList list1 = new JList();
    private JList list2 = new JList();
    private JComboBox<IDPair> dropList = new JComboBox();
    private DefaultListModel listModel1 = new DefaultListModel();
    private DefaultListModel listModel2 = new DefaultListModel();

    public Combine14GUI() {
        frame.setLayout(new BorderLayout());
        Container pane = frame.getContentPane();
        frame.setContentPane(pane);

        Dimension dTextfield = new Dimension(75, 20);
        textField1.setMaximumSize(textField1.getPreferredSize());
        textField1.setToolTipText("ID of item 1");
        textField1.setText(textField1.getToolTipText());
        textField2.setMaximumSize(textField2.getPreferredSize());
        textField2.setToolTipText("ID of item 2");
        textField2.setText(textField2.getToolTipText());
        button2.setMaximumSize(button2.getPreferredSize());

        JPanel subPanel = new JPanel();
        subPanel.add(textField1);
        subPanel.add(textField2);
        subPanel.add(button2);

        pane.add(subPanel,BorderLayout.LINE_END);

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
        displayList.add(new IDPair(ID.JUG_WATER,ID.GRAPES,"Wine maker"));
        displayList.add(new IDPair(1755,1623,"Cut sapphires"));
        displayList.add(new IDPair(227,249,"Unfinished guam potions"));

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
//                            System.out.printf("%d, %d",it.a, it.b);
                        Combine14.itemList.add(new IDPair(it.a, it.b, it.name));
                    }
                }
            }
            done = true;
            frame.dispose();
        });

        button2.addActionListener(e -> {
            int a = readIntTF(textField1);
            int b = readIntTF(textField2);
            String s = String.format("%d %d",a,b);
            displayList.add(new IDPair(a,b,s));
            listModel1.addElement(s);
        });
    }

    public boolean done() {
        return done;
    }

    public class IDPair {
        int a;
        int b;
        String name;

        public IDPair(int a, int b, String n) {
            this.a = a;
            this.b = b;
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

}