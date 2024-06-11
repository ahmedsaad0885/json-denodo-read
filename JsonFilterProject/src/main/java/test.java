import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class test {
    public static void main(String[] args) {
        List<JsonNode> result = JsonFilterExample.initializeAndPopulateJsonNode("src/main/resources/test.json");

        for (JsonNode node : result) {
            System.out.println(node.toPrettyString());
        }

        List<List<String>> result2 = JsonFilterExample.initializeAndPopulateList("src/main/resources/in2.json");

        for (List<String> list : result2) {
           // System.out.println(list);

        }
    }
}
