package scripts.mini_tests;

import jdk.nashorn.internal.ir.debug.JSONWriter;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.*;
import minimal_json.Json;
import minimal_json.JsonArray;
import minimal_json.JsonValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringJoiner;

public class JsonTest {

    public static void main(String[] args) {
        StringJoiner sj = new StringJoiner(File.separator);
        sj.add("items_useful_data.json");
        try {
            JsonValue v = Json.parse(new FileReader(sj.toString()));
            JsonArray array = v.asArray();
            for (int i = 0; i < 10; i++) {
                System.out.println(array.get(i).asObject().get(String.valueOf(i)).asObject().get("name").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
