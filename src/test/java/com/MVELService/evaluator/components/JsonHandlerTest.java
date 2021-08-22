package com.MVELService.evaluator.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonHandlerTest {

    private JsonHandler jsonHandler;
    private String simpleJsonExample;

    @BeforeEach
    void setUp() {
        jsonHandler = new JsonHandler();
        simpleJsonExample = "[{ \"firstName\": \"First1\", \"lastName\": \"Last1\", \"age\": 15 }, { \"firstName\": \"First2\", \"lastName\": \"Last2\", \"age\": 9 }]";
    }

    @Test
    void readJson() throws JsonProcessingException {

        JsonNode node = jsonHandler.readJson(simpleJsonExample);

        assertEquals(node.get(0).get("firstName").textValue(), "First1");
        assertEquals(node.get(1).get("firstName").textValue(), "First2");
    }

    @Test
    void fromJsonDictList() throws JsonProcessingException {

        JsonNode node = jsonHandler.readJson(simpleJsonExample);

        List<HashMap<String, Object>> dictList = new ArrayList<>();

        jsonHandler.fromJsonDictList(node, dictList);

        assertTrue((Integer)dictList.get(0).get("age") > 10, "First object age should be greater than 10 and castable to Integer.");
        assertFalse((Integer)dictList.get(1).get("age") > 10, "Second object age should be less than 10 and castable to Integer.");
    }
}