package com.MVELService.evaluator.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class JsonHandler {

    ObjectMapper objectMapper = defaultObjectMapper();

    private static ObjectMapper defaultObjectMapper(){
        ObjectMapper defaultMapper = new ObjectMapper();
        return defaultMapper;
    }

    public JsonNode readJson(String jsonString) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(jsonString);

        return node;
    }

    public void fromJsonDictList(JsonNode node, List<HashMap<String, Object>> dictList) {

        if (node.isObject()){
            Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();

            HashMap<String, Object> newDict = new HashMap<>();
            while (iterator.hasNext()){
                Map.Entry<String, JsonNode> currentEntry = iterator.next();
                newDict.put(currentEntry.getKey(), unwrapJsonValue(currentEntry.getValue()));
            }

            dictList.add(newDict);
        }else if (node.isArray()){
            for (int i = 0; i < node.size(); i++){
                fromJsonDictList(node.get(i), dictList);
            }
        }
    }

    private static Object unwrapJsonValue(JsonNode input){

        Object returnValue = new Object();

        switch (input.getNodeType()){

            case STRING -> {
                returnValue = input.textValue();
                break;
            }

            case NUMBER -> {
                returnValue = input.intValue();
                break;
            }

            case BOOLEAN -> {
                returnValue = input.booleanValue();
                break;
            }

            case NULL -> {
                returnValue = null;
                break;
            }
        }

        return returnValue;
    }
}
