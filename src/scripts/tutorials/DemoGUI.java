package scripts.tutorials;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DemoGUI {
    private JFrame jFrame = new JFrame("Settings");
    private JTextField textField = new JTextField(7);
    private JComboBox<String> comboBox = new JComboBox<>();
    private JList<String> list = new JList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JSpinner spinner = new JSpinner();
    private JCheckBox checkBox = new JCheckBox("Check this out!");
    private JButton button = new JButton("Button");

    private ArrayList<String> strings;
    public DemoGUI(ArrayList<String> strings) {
        this.strings = strings;
        initComponents();
        addComponents();
    }

    public static void main(String[] args) {
        String s = "Hey now, you're an all-star, get your game on, go play Hey now, you're a rock star, get the show on, get paid And all that glitters is gold Only shooting stars break the mold";

        ArrayList<String> strings = new ArrayList<>(
                Arrays.asList(s.split(" "))
        );
        new DemoGUI(strings);
    }

    private void initComponents() {
        list.setModel(listModel);
        strings.forEach(s->listModel.addElement(s));

        //Set the range for the spinner
        spinner.setModel(new SpinnerNumberModel(0,0,strings.size()-1,1));
        //Set default width
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(7);

        //Print out values of fields when button pressed
        button.addActionListener(e -> {
            System.out.println("Text is " + textField.getText());
            System.out.println("Checkbox selected? " + checkBox.isSelected());
            System.out.println("Spinner " + spinner.getValue());
            System.out.println("Combobox " + comboBox.getSelectedItem());
        });

        //Change textfield and combobox when value changed
        spinner.addChangeListener(e -> {
            String s = strings.get((Integer)spinner.getValue());
            textField.setText(s);
            comboBox.removeAllItems();
            comboBox.addItem(s);
        });
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void addComponents() {
        JPanel main = new JPanel();
        BoxLayout boxLayout = new BoxLayout(main,BoxLayout.Y_AXIS);
        main.setLayout(boxLayout);
        main.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"JPanel new BoxLayout(main,BoxLayout.Y_AXIS)"));
        {
            JPanel p = new JPanel();
            p.setBorder(BorderFactory.createTitledBorder("JPanel"));
            p.add(new JLabel("Text here"));
            p.add(textField);
            main.add(p);
        }
        {
            JPanel p = new JPanel(new GridLayout(0,2));
            p.setBorder(BorderFactory.createTitledBorder("JPanel new GridLayout(0,2)"));
            p.add(new JLabel("Spinner here"));
            p.add(spinner);
            p.add(new JLabel("Combobox here"));
            p.add(comboBox);
            main.add(p);
        }
        {
            JPanel p = new JPanel(new GridLayout(1,0));
            p.setBorder(BorderFactory.createTitledBorder("JPanel new GridLayout(1,0)"));
            for (int i = 0; i < 7; i++) {
                p.add(new JLabel("  Label " + String.valueOf(i)));
            }
            main.add(p);
        }
        {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.setBorder(BorderFactory.createTitledBorder("JPanel new FlowLayout(FlowLayout.LEFT)"));
            p.add(button);
            main.add(p);
        }
        {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            p.setBorder(BorderFactory.createTitledBorder("JPanel new FlowLayout(FlowLayout.RIGHT)"));
            p.add(checkBox);
            {
                JPanel p0 = new JPanel(new GridLayout(0,2));
                p0.setBorder(BorderFactory.createTitledBorder("JPanel new GridLayout(0,2)"));
                for (int i = 0; i < 5; i++) {
                    p0.add(new JButton("blank"));
                }
                p.add(p0);
            }
            main.add(p);
        }

        jFrame.add(main);
        jFrame.setVisible(true);
        //Center frame on screen
        jFrame.setLocationRelativeTo(null);
        //Fit components into frame
        jFrame.pack();
    }
}
