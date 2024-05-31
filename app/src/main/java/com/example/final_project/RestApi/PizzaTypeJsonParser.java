package com.example.final_project.RestApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PizzaTypeJsonParser {

    public static List<String> getPizzaTypesFromJson(String json) {
        List<String> pizzaTypes = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray typesArray = jsonObject.getJSONArray("types");
            for (int i = 0; i < typesArray.length(); i++) {
                pizzaTypes.add(typesArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pizzaTypes;
    }
}

