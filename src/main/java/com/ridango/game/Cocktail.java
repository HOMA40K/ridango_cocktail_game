package com.ridango.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class Cocktail {

    String name;
    String id;
    String category;
    String glass;
    HashMap<String, String> ingredients;
    String instructions;
    protected static HashMap<String, Boolean> usedCocktails = new HashMap<>();

    public Cocktail(String name, String id, String category, String glass, HashMap<String, String> ingredients, String instructions) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.glass = glass;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Cocktail() {
        Cocktail newCocktail = (Cocktail) getNewCocktailFromAPI();
        usedCocktails.put(newCocktail.id, true);

        this.name = newCocktail.name;
        this.id = newCocktail.id;
        this.category = newCocktail.category;
        this.glass = newCocktail.glass;
        this.ingredients = newCocktail.ingredients;
        this.instructions = newCocktail.instructions;
    }

    private static Object getNewCocktailFromAPI() {
        URL url;
        try {
            url = new URL("https://www.thecocktaildb.com/api/json/v1/1/random.php");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StringBuilder response = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return prettifyData(response.toString());
    }

    private static HashMap<String, String> prettifyIngredients(JSONObject cocktail) {
        HashMap<String, String> ingredients = new HashMap<>();
        for (int i = 1; i <= 15; i++) {
            String ingredient = "";
            try {
                ingredient = cocktail.getString("strIngredient" + i);
            } catch (Exception e) {
                break;
            }
            String measure = "";
            try {
                measure = cocktail.getString("strMeasure" + i);
            } catch (Exception e) {
                measure = "Unknown";
            }
            ingredients.put(ingredient, measure);
        }
        return ingredients;
    }

    private static JSONObject convertToJSON(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray jsonArray = jsonResponse.getJSONArray("drinks");
        return jsonArray.getJSONObject(0);
    }

    private static Object prettifyData(String response) {
        JSONObject cocktail = convertToJSON(response);

        String name = cocktail.getString("strDrink");
        String id = cocktail.getString("idDrink");
        String category = cocktail.getString("strCategory");
        String glass = cocktail.getString("strGlass");
        String instructions = cocktail.getString("strInstructions");

        HashMap<String, String> ingredients = prettifyIngredients(cocktail);

        return new Cocktail(name, id, category, glass, ingredients, instructions);
    }
}
