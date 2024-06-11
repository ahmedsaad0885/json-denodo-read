import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonFilterExample {

public static List<List<String>> initializeAndPopulateList(String filePath){
try{
    //initialize factory and parser to be utilized in streaming
    JsonFactory factory = new JsonFactory();
    JsonParser parser = factory.createParser(new File(filePath));

    //mapper to convert the json content to jsonnode , then have two lists one for simple structures and one for complex ones
    ObjectMapper mapper = new ObjectMapper();
    List<List<String>> listOfLists = new ArrayList<>();

    while (!parser.isClosed()) {
        JsonToken token = parser.nextToken();

        //check if the token of a json array or a json object, is complex or not
        if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
            JsonNode node = mapper.readTree(parser);

                extractValuesToListOfLists(node, listOfLists);

        }
    }
    return listOfLists;

} catch (Exception e) {
        e.printStackTrace();
    }List<List<String>> listOfLists = new ArrayList<>();
    return listOfLists;
}

public static List<JsonNode> initializeAndPopulateJsonNode(String filePath){
    try{
        //initialize factory and parser to be utilized in streaming
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(new File(filePath));

        //mapper to convert the json content to jsonnode , then have two lists one for simple structures and one for complex ones
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> jsonNodeList = new ArrayList<>();

        while (!parser.isClosed()) {
            JsonToken token = parser.nextToken();

            //check if the token of a json array or a json object, is complex or not
            if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                JsonNode node = mapper.readTree(parser);

                    extractValuesToJsonNodeList(node, jsonNodeList);
            }
        }

        return jsonNodeList;
    } catch (Exception e) {
        e.printStackTrace();
    }
    List<JsonNode> jsonNodeList = new ArrayList<>();
    return jsonNodeList;
}
    private static void extractValuesToJsonNodeList(JsonNode node, List<JsonNode> jsonNodeList) {
        // if the node is an array iterate through the array and add it to the list
        if (node.isArray()) {
            for (JsonNode arrayNode : node) {
                jsonNodeList.add(arrayNode);
            }
        } else {
            jsonNodeList.add(node);
        }
    }

    private static void extractValuesToListOfLists(JsonNode node, List<List<String>> listOfLists) {
        //if the node is an array iterate through it and write it
        if (node.isArray()) {
            for (JsonNode arrayNode : node) {
                List<String> values = new ArrayList<>();
                arrayNode.fields().forEachRemaining(entry -> values.add(entry.getValue().asText()));
                listOfLists.add(values);
            }
        } else {
            List<String> values = new ArrayList<>();
            node.fields().forEachRemaining(entry -> values.add(entry.getValue().asText()));
            listOfLists.add(values);
        }
    }

}
