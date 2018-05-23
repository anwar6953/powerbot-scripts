package scripts.GEAfker;



import api.ClientContext;
import scripts.ID;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    private boolean done = false;
    private JPanel mainPanel1;
    private JButton button1;
    private JList<String> list1;
    private JList<String> list2;
    private DefaultListModel<String> listModel1 = new DefaultListModel<>();
    private DefaultListModel<String> listModel2 = new DefaultListModel<>();

    private List<Task> allTasks = new ArrayList<Task>();

    public GUI(ClientContext ctx) {
        JFrame frame = new JFrame("GE Afker");
        frame.setContentPane(mainPanel1);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        list1.setModel(listModel1);
        list2.setModel(listModel2);

//        allTasks.add(new Pineapple(ctx));
        allTasks.add(new HighAlch(ctx));
        allTasks.add(new Composter(ctx));
//        allTasks.add(new DartFletch(ctx));
        allTasks.add(new BoltTipper(ctx));
        allTasks.add(new BoltEnchanter(ctx));
        allTasks.add(new LongFletch(ctx));
        allTasks.add(new FlaxSpin(ctx));
        allTasks.add(new PlankMake(ctx));
        allTasks.add(new Humidify(ctx));
//        allTasks.add(new StringBow(ctx));
        allTasks.add(new GenericCombiner(ctx,ID.BOW_YEW_LONG_U,ID.BOWSTRING,false,"You add", "Yew longs","String yews"));
        allTasks.add(new GenericCombiner(ctx,ID.BOW_MAGIC_LONG_U,ID.BOWSTRING,false,"You add", "Magic longs","String magic"));
        allTasks.add(new HerbClean(ctx));
        allTasks.add(new UnfPotions(ctx));
        allTasks.add(new XericFab(ctx));
//        allTasks.add(new GenericCombiner(ctx,ID.VIAL_WATER,ID.HERB_CADANTINE_CLEAN,false,"You put", "Unf potions","Cadantine potions"));
//        allTasks.add(new GenericCombiner(ctx,ID.VIAL_WATER,2481,false,"You put", "Unf potions","Lantadyme potions"));
//        allTasks.add(new PotionMake(ctx));
//        allTasks.add(new GenericCombiner(ctx,ID.PIZZA_PLAIN,ID.PINEAPPLE_RING,false,"add the", "Pizzas","Top pizzas"));
//        allTasks.add(new YT_AnchovyPizza(ctx));
//        allTasks.add(new GenericCombiner(ctx,ID.ORB_AIR,ID.BATTLESTAFF,false,"lololo", "Battlestaves","Air bstaff"));

//        allTasks.add(new YT_JewelEnchanter(ctx));

        for (Task t : allTasks) {
            listModel2.addElement(t.getName());
        }

        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int index = list1.locationToIndex(e.getPoint());
                    String elm = listModel1.elementAt(index);
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
                    String elm = listModel2.elementAt(index);
                    listModel1.addElement(elm);
                    listModel2.removeElementAt(index);
                }
            }
        });


        button1.addActionListener(e -> {
            for(int i = 0; i< list1.getModel().getSize();i++){
//                    System.out.println(list1.getModel().getElementAt(i));
                for (Task t : allTasks) {
                    if (t.getName().equals(list1.getModel().getElementAt(i))) {
                        GEAfker.taskList.add(t);
                    }
                }
            }
            done = true;
            frame.dispose();
        });
    }

    public boolean done() {
        return done;
    }
}
