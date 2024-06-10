import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonFilterExample {

    public static void main(String[] args) {
        try {
            //initialize factory and parser to be utilized in streaming
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(new File("src/main/resources/customer_interactions_mega.json"));

            //mapper to convert the json content to jsonnode , then have two lists one for simple structures and one for complex ones
            ObjectMapper mapper = new ObjectMapper();
            //List<JsonNode> jsonNodeList = new ArrayList<>();
            //List<List<String>> listOfLists = new ArrayList<>();

            //a generic list variable to hold the results, determined at runtime based on the JSON complexity.
            List<?> resultList = null;

            //for determining if the json content is to be handled as list of lists or as list of jsonnode
            boolean isComplex = false;

            //determine if the file has complex structures using the first element only
            JsonNode firstNode = mapper.readTree(parser);
            isComplex = isComplexStructure(firstNode);
            System.out.println("Detected structure as "+ (isComplex? "complex": "simple"));

            if(isComplex){
                System.out.println("Extracting values to JsonNode list");
                resultList = new ArrayList<JsonNode>();
                extractValuesToJsonNodeList(firstNode, (List<JsonNode>) resultList);
            }
            else {
                System.out.println("extracting values to list of lists");
                resultList = new ArrayList<List<String>>();
                extractValuesToListOfLists(firstNode, (List<List<String>>) resultList);
            }

            while (!parser.isClosed()) {
               JsonToken token = parser.nextToken();

                //check if the token of a json array or a json object, is complex or not
                if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                    JsonNode node = mapper.readTree(parser);

                    if (isComplex) {
                        extractValuesToJsonNodeList(node, (List<JsonNode>) resultList);
                    } else {
                        extractValuesToListOfLists(node, (List<List<String>>) resultList);
                    }
                }
            }

            printDebugInfo(resultList, isComplex);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isComplexStructure(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getValue().isObject() || field.getValue().isArray()) {
                    System.out.println("Found complex field: " + field.getKey());
                    return true;
                }
            }
        } else if (node.isArray()) {
            if (node.size() > 0) {
                JsonNode firstElement = node.get(0);
                Iterator<Map.Entry<String, JsonNode>> fields = firstElement.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    if (field.getValue().isObject() || field.getValue().isArray()) {
                        System.out.println("Found complex field in array: " + field.getKey());
                        return true;
                    }
                }
            }
        }
        return false;
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

    private static void printDebugInfo(List<?> resultList, boolean isComplex) {
        if (isComplex) {
            System.out.println("JSON Node List:");
            for (JsonNode node : (List<JsonNode>) resultList) {
                System.out.println(node.toPrettyString());
            }
        } else {
            System.out.println("\nList of Lists:");
            for (List<String> list : (List<List<String>>) resultList) {
                System.out.println(list);
            }
        }
    }
}
