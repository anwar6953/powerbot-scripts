package scripts.AIOHerblore;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import static scripts.AIOHerblore.CombineObjj.*;
import static scripts.AIOHerblore.Herbalist.*;

public class HerbloreGUI {
    private boolean done = false;
    private long features = 0;
    private JFrame frame = new JFrame("Settings");
    private JList<String> taskList;
    private DefaultListModel<String> listModel1 = new DefaultListModel<>();

    private JButton finishButton = new JButton("Finish");
    private JButton addButton = new JButton("Add");
    private JButton clearButton = new JButton("Clear");
    private JTextField filterBox = new JTextField();
    private JComboBox<String> filteredListBox = new JComboBox<String>();
    private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>() {{
        add(new JCheckBox("See thread for access"));
    }};
    private JCheckBox checkAmulet = new JCheckBox("Use Amulet of Chemistry");

    HerbloreGUI() {
        taskList = setupList();

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        rightPanel.add(panelComboBoxes());
        rightPanel.add(panelButtons());
        rightPanel.add(checkAmulet);

        JPanel panel1 = new JPanel();
        panel1.setBorder(BorderFactory.createLineBorder(Color.black));
        panel1.add(taskList);
        panel1.add(rightPanel);

        JPanel panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createLineBorder(Color.black));
        for (JCheckBox cb : checkBoxes) {
            panel2.add(cb);
        }

        JTabbedPane jtp = new JTabbedPane();
        jtp.addTab("Task list",null,panel1,"Set tasks and order");
//        jtp.addTab("Anti-pattern (WIP)",null,panel2,"Toggle anti-pattern features");
        frame.add(jtp);
        frame.getRootPane().setDefaultButton(addButton);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }


    private JList<String> setupList() {
        JList<String> taskList = new JList<>();
        taskList.setModel(listModel1);
        taskList.setDragEnabled(true);
        taskList.setDropMode(DropMode.INSERT);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //https://stackoverflow.com/questions/16586562/reordering-jlist-with-drag-and-drop
        taskList.setTransferHandler(new TransferHandler() {
            private int index;
            private boolean beforeIndex = false; //Start with `false` therefore if it is removed from or added to the list it still works

            @Override
            public int getSourceActions(JComponent comp) {
                return MOVE;
            }

            @Override
            public Transferable createTransferable(JComponent comp) {
                index = taskList.getSelectedIndex();
                return new StringSelection(taskList.getSelectedValue());
            }

            @Override
            public void exportDone(JComponent comp, Transferable trans, int action) {
                if (action == MOVE) {
                    if(beforeIndex)
                        listModel1.remove(index + 1);
                    else
                        listModel1.remove(index);
                }
            }

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                    listModel1.add(dl.getIndex(), s);
                    beforeIndex = dl.getIndex() < index;
                    return true;
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        taskList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (listModel1.isEmpty()) return;
                    int index = taskList.getSelectedIndex();
                    if (index == -1) index = taskList.getLastVisibleIndex();
                    listModel1.removeElementAt(index);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        taskList.setPreferredSize(new Dimension(200, 100));
//        taskList.setBorder(BorderFactory.createTitledBorder("Task list"));

        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    if (listModel1.isEmpty()) return;
                    int index = taskList.locationToIndex(e.getPoint());
                    listModel1.removeElementAt(index);
                }
            }
        });

        return taskList;
    }

    private JPanel panelComboBoxes() {
        filterBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
        filteredListBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
        for (CombineObjj c : allItems) {
            filteredListBox.addItem(c.name);
        }

        filterBox.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                keyPressed(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                String filterString = filterBox.getText();
                filteredListBox.removeAllItems();
                for (CombineObjj c : allItems) {
                    if (c.name.toLowerCase().contains(filterString.toLowerCase())
                            || filterString.isEmpty()) filteredListBox.addItem(c.name);
                }
                if (filteredListBox.getItemCount() == 0) {
                    for (CombineObjj c : allItems) {
                        filteredListBox.addItem(c.name);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyPressed(e);
            }
        });


        filterBox.setPreferredSize(new Dimension(150, 20));
        filteredListBox.setPreferredSize(new Dimension(150, 20));

        JLabel label1 = new JLabel("Filter");
        label1.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label2 = new JLabel("Product");
        label2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel1 = new JPanel();
        panel1.add(label1);
        panel1.add(filterBox);
        JPanel panel2 = new JPanel();
        panel2.add(label2);
        panel2.add(filteredListBox);

        JPanel allPanel = new JPanel();
        allPanel.setLayout(new BoxLayout(allPanel,BoxLayout.Y_AXIS));
        allPanel.add(panel1);
        allPanel.add(panel2);
        return allPanel;
    }

    private JPanel panelButtons() {
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        finishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel allPanel = new JPanel();
        allPanel.setLayout(new BoxLayout(allPanel,BoxLayout.Y_AXIS));
        JPanel addClear = new JPanel();
        addButton.addActionListener(e -> {
            String taskName = (String)filteredListBox.getSelectedItem();
            if (taskName.isEmpty()) return;
            listModel1.addElement(taskName);
        });

        clearButton.addActionListener(e -> {
            listModel1.removeAllElements();
        });

        addClear.add(addButton);
        addClear.add(clearButton);

        finishButton.addActionListener(e -> {
            for (Object obj : listModel1.toArray()) {
                String s = (String)obj;
                for (CombineObjj c : allItems) {
                    if (s.equals(c.name)) {
                        System.out.print("Added " + s + "\n");
                        addTaskList(c);
                    }
                }
            }
            for (int i = 0; i < checkBoxes.size(); i++) {
                JCheckBox cb = checkBoxes.get(i);
                if (cb.isSelected()) {
                    features |= 1L << i;
                } else {
                    features |= 0L << i;
                }
                System.out.print(Long.toBinaryString(features) + "\n");
            }
            setUseAmulet(checkAmulet.isSelected());
            done = true;
            frame.dispose();
        });

        allPanel.add(addClear);
        allPanel.add(finishButton);
        return allPanel;
    }

    public boolean isDone() {
        return done;
    }

    public long getFeatures() {
        return features;
    }

    public void setVisible() {
        frame.setVisible(true);
    }

}
