import com.google.gson.*;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by lveeckha on 13/03/2016.
 */
public class DiffFields {
    public static void main(String[] args) throws IOException {
        String pathname = "";
        if (args.length == 1)
        {
            pathname = args[0];
        } else {
            System.out.println("Usage: DiffFields <file>");
            System.exit(1);
        }
        JsonElement el = getJsonObject(pathname);
        JsonElement i = el.getAsJsonObject().get("entities");
        for (JsonElement els : (JsonArray)i)
        {
            JsonElement fieldColumns = els.getAsJsonObject().get("columns");
            String fieldObjectName = null;
            String fieldObjectValue = "";
            List<String> keyValues = new ArrayList<String>();
            for (JsonElement fieldColumn : fieldColumns.getAsJsonArray())
            {
                String fieldName = fieldColumn.getAsJsonObject().get("name").toString();
                String fieldValue = fieldColumn.getAsJsonObject().get("value").toString();
                if (fieldName.equals("\"tbfl_name\"")) {
                    fieldObjectName = fieldValue;
                } else {
                    keyValues.add("["+ fieldName + ":" + fieldValue + "]");
                }
            }
            Collections.sort(keyValues);
            System.out.println(fieldObjectName + "=" + keyValues);
        }

    }

    private static List<Pair<String, String>> getEntry(String keyPrefix, JsonElement element)
    {
        List<Pair<String, String>> str = new ArrayList<Pair<String, String>>();
        if (element instanceof JsonPrimitive)
        {
            System.out.println(keyPrefix+ ":" + element.getAsString().replace('\n', ';'));
            return Arrays.asList(new Pair<String, String>(keyPrefix + "",element.getAsString().replace('\n', ';')));
        }
        if (element instanceof JsonArray)
        {
            JsonArray arr = (JsonArray) element;
            for (int i = 0; i < arr.size(); i++)
            {
                str.addAll(getEntry(keyPrefix + "." + i, arr.get(i)));
            }
            return str;
        }
        if (element instanceof JsonObject)
        {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
            {
                str.addAll(getEntry(keyPrefix + "." + entry.getKey(), entry.getValue()));
            }
        }
        return str;
    }

    private static JsonElement getJsonObject(String pathname) throws IOException {
        File file = new File(pathname);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        String jsonString = new String(data, "UTF-8");
        return new JsonParser().parse(jsonString);
    }
}
