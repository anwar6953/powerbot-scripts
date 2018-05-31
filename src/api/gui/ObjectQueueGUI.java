/**
 * GUI to select a subset of allObjects from a list of allObjects
 *
 * Useful for an AIO script with task queuing
 */

package api.gui;

import api.ClientContext;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;

public class ObjectQueueGUI<T> {
    private ClientContext ctx;
    private File configFile;
    private Properties prop = new Properties();

    private boolean done = false;
    private HashSet<T> allObjects;

    private final ArrayList<T> returnList = new ArrayList<>();

    public ObjectQueueGUI(ClientContext ctx, HashSet<T> allObjects) {
        this.ctx = ctx;
        this.allObjects = allObjects;
        initComponents();
        addComponents();
    }

    public ObjectQueueGUI(HashSet<T> allObjects) {
        this.allObjects = allObjects;
        initComponents();
        addComponents();
    }


    public static void main(String[] args) {
        HashSet<String> strings = new HashSet<>();
        strings.add("Shrimps");
        strings.add("Chicken");
        strings.add("Monkfish");
        strings.add("Bass");
        ObjectQueueGUI<String> gui = new ObjectQueueGUI<>(strings);
    }

    private void saveList() {
        if (ctx == null) return;
        StringBuilder builder = new StringBuilder();
        for (T s : returnList) {
            builder.append(s).append(",");
        }
        prop.setProperty("taskList",builder.toString());
        try {
            FileWriter fw = new FileWriter(configFile);
            prop.store(fw,"");
            fw.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void loadList() {
        if (ctx == null) return;
        configFile = new File(ctx.controller.script().getStorageDirectory(), "config.properties");
        try {
            System.out.print(ctx.controller.script().getStorageDirectory());
            FileReader fr = new FileReader(configFile);
            prop.load(fr);

            for (String s : prop.stringPropertyNames()) {
                System.out.print(s + "\n");
            }

            if (prop.getProperty("taskList") != null) {
                for (String s : prop.getProperty("taskList").split(",")) {
                    Optional<T> obj = allObjects.stream().filter(t->t.toString().equals(s)).findFirst();
                    obj.ifPresent(t->listModel1.addElement(t));
                }
            }
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JFrame frame = new JFrame("Settings");
    private JList<T> taskList = new JList<>();
    private DefaultListModel<T> listModel1 = new DefaultListModel<>();
    private JButton finishButton = new JButton("Finish");
    private JButton addButton = new JButton("Add");
    private JButton clearButton = new JButton("Clear");
    private JTextField filterBox = new JTextField();
    private JComboBox<T> filteredListBox = new JComboBox<>();
    private JPanel jPanel1 = new JPanel(new GridBagLayout());
    private GridBagConstraints c = new GridBagConstraints();

    private void initComponents() {
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
                return new StringSelection(taskList.getSelectedValue().toString());
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
                    T task = (T) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                    listModel1.add(dl.getIndex(), task);
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

        loadList();
        for (T s : allObjects) filteredListBox.addItem(s);

        filterBox.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                keyPressed(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                String filterString = filterBox.getText();
                filteredListBox.removeAllItems();
                for (T obj : allObjects) {
                    if (obj.toString().toLowerCase().contains(filterString.toLowerCase())
                            || filterString.isEmpty()) filteredListBox.addItem(obj);
                }
                if (filteredListBox.getItemCount() == 0) {
                    for (T t : allObjects) filteredListBox.addItem(t);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyPressed(e);
            }
        });

        addButton.addActionListener(e -> {
            T task = (T) filteredListBox.getSelectedItem();
            if (task == null) return;
            listModel1.addElement(task);
        });

        clearButton.addActionListener(e -> {
            listModel1.removeAllElements();
        });

        finishButton.addActionListener(e -> {
            for (Object obj : listModel1.toArray()) {
                T task = (T)obj;
                returnList.add(task);
            }
            done = true;
            System.out.println("Finished");
            System.out.println(returnList);
            saveList();
            frame.dispose();
        });

        KeyboardFocusManager
            .getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(e -> {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        addButton.doClick();
                    }
                }
                return false;
            });

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                done = true;
                System.out.println("Closed window");
            }
        };
        frame.addWindowListener(exitListener);
    }

    private void addComponents() {
        c.insets.top = 10;
        c.insets.bottom = 10;
        c.insets.left = 10;
        c.insets.right = 10;
        c.gridx = 0;
        c.gridheight = 4;
        taskList.setPreferredSize(new Dimension(200,200));
        jPanel1.add(taskList,c);

        c = new GridBagConstraints();
        c.weighty = 1;
        c.insets.top = 10;
        c.insets.bottom = 10;
        c.insets.left = 10;
        c.insets.right = 10;
        c.gridx = 1;
        c.gridwidth = 2;

        c.gridy = 0;
        c.ipady = 7;
        filterBox.setPreferredSize(new Dimension(150,15));
        jPanel1.add(filterBox,c);
        c.gridy = 1;
        filteredListBox.setPreferredSize(new Dimension(150,15));
        jPanel1.add(filteredListBox,c);

        c = new GridBagConstraints();
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.CENTER;
        c.ipadx = 35;
        c.insets.top = 10;
        c.insets.bottom = 10;
        c.insets.left = 10;
        c.insets.right = 10;
        c.gridx = 1;
        c.gridy = 2;
        jPanel1.add(addButton,c);
        c.gridx = 2;
        jPanel1.add(clearButton,c);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        jPanel1.add(finishButton,c);

        frame.add(jPanel1);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.pack();
    }



    /**
     * Gets returnList
     *
     * @return value of returnList
     */
    public ArrayList<T> getReturnList() {
        return returnList;
    }

    /**
     * Gets done
     *
     * @return value of done
     */
    public boolean isDone() {
        return done;
    }
}
