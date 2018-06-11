package scripts.bank_sorter;

import api.ClientContext;
import api.Components;
import api.PollingScript;
import minimal_json.Json;
import minimal_json.JsonArray;
import minimal_json.JsonObject;
import minimal_json.JsonValue;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Component;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Script.Manifest(name = "BankSort", properties = "client=4;", description = "")
public class Main extends PollingScript<ClientContext> implements PaintListener {
    private Component tab = ctx.widgets.component(12,23);
    private CopyOnWriteArrayList<Component> items = new CopyOnWriteArrayList<>();
    private Component curr = ctx.components.nil();
    private Component dest = ctx.components.nil();
    private int currIndex = 0;
    private int destIndex = 0;
    private SortType sortType = SortType.ALPHABETICAL;
    private boolean ready = false;
    @Override
    public void start() {
        super.start();
        for (Component c : tab.components()) {
            if (c.visible()) items.add(c);
        }
        StringJoiner sj = new StringJoiner(File.separator);
        sj.add("items_all_data.json");
        try {
            FileReader fr = new FileReader(sj.toString());
            JsonValue v = Json.parse(fr);
            array = v.asArray();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortType = (SortType) JOptionPane.showInputDialog(null, "Choose sort type",
                "Sort selection", JOptionPane.QUESTION_MESSAGE, null, // Use
                // default
                // icon
                SortType.values(), // Array of choices
                SortType.values()[1]); // Initial choice
        ready = true;
    }

    @Override
    public void poll() {
        ArrayList<Integer> sorted;
        Stream<Integer> stream = items.stream().map(Component::itemId);
        switch (sortType) {
            case ALPHABETICAL:
                stream = stream.sorted(Comparator.comparing(this::getItemName).thenComparing(Comparator.naturalOrder()));
                break;
            case MODEL_ID:
                stream = stream.sorted(Comparator.comparing(this::getModelId).thenComparing(this::getItemName).thenComparing(Comparator.naturalOrder()));
                break;
            case ITEM_ID:
                stream = stream.sorted();
                break;
        }
        sorted = stream.collect(Collectors.toCollection(ArrayList::new));
        for (Component c : items) {
            curr = c;
            currIndex = items.indexOf(c);
            destIndex = sorted.indexOf(c.itemId());
            dest = items.get(destIndex);
            if (c.centerPoint().x == dest.centerPoint().x && c.centerPoint().y == dest.centerPoint().y) continue;
            c.hover();
            ctx.input.drag(dest.centerPoint(),true);
            Condition.sleep(1000);
            return;
        }
        ctx.controller.stop();
    }

    private ArrayList<String> strings = new ArrayList<>();
    @Override
    public void repaint(Graphics g) {
        if (!ready) return;
//        for (Component c : items) {
//            g.drawPolygon(Components.RectangleToPolygon(c));
//        }
        g.setColor(Color.RED);
        g.drawPolygon(Components.RectangleToPolygon(curr));
        g.setColor(Color.GREEN);
        g.drawPolygon(Components.RectangleToPolygon(dest));
        g.setColor(Color.WHITE);
        strings.clear();
        strings.add(sortType.toString());
        strings.add("Curr " + currIndex + "-"+ getItemName(curr.itemId()));
        strings.add("Dest " + destIndex + "-"+ getItemName(dest.itemId()));
        Utils.simplePaint(g,strings);
    }
    private JsonArray array = new JsonArray();

    private String getItemName(int id) {
        if (array.size() <= 0) return "";
        return array.get(id).asObject().get("name").toString();
    }
    private String getModelId(int id) {
        if (array.size() <= 0) return "";
        StringJoiner sj = new StringJoiner("");
        JsonObject obj = array.get(id).asObject();
        sj.add(obj.get("modelSine").toString());
        sj.add(obj.get("modelZoom").toString());
        sj.add(obj.get("modelRotation1").toString());
        sj.add(obj.get("modelRotation2").toString());
//        sj.add(obj.get("modelId").toString());
        return sj.toString();
    }

    private enum SortType {
        ALPHABETICAL, MODEL_ID, ITEM_ID
    }
}
