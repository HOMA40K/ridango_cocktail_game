package com.ridango.game;

import java.util.*;

public class Game {

    public static final int MAX_POINTS = 5;

    
    public static Cocktail getCurrentCocktail(Map<String, Object> gameState) {
        return (Cocktail) gameState.get("currentCocktail");
    }
    
    public static Integer getAttempts(Map<String, Object> gameState) {
        Integer attempts = (Integer) gameState.get("attempts");
        return attempts != null ? attempts : 0;
    }
    
    public static Integer getPoints(Map<String, Object> gameState) {
        Integer points = (Integer) gameState.get("points");
        if (points == null) {
            points = 0;
            gameState.put("points", points);
        }
        return points;
    }
    
    public static boolean isGuessCorrect(Cocktail cocktail, String guess) {
        String cocktailNameWithoutSpaces = cocktail.name.replaceAll("\\s", "").toLowerCase();
        String guessWithoutSpaces = guess.replaceAll("\\s", "").toLowerCase();
        if (cocktailNameWithoutSpaces.equalsIgnoreCase(guessWithoutSpaces)) {
            return true;
        }

        return cocktail.name.equalsIgnoreCase(guess);
    }
    
    public static String handleCorrectGuess(Map<String, Object> gameState, Cocktail cocktail, Integer attempts, Integer points) {
        
        int pointsEarned = MAX_POINTS - attempts;
        points += pointsEarned;
        gameState.put("points", points);

        System.out.println("Correct! The cocktail was: " + cocktail.name);
        System.out.println("You earned " + pointsEarned + " points. Total points: " + points);

        Cocktail newCocktail =  startNewCocktail(gameState);
        String namedWithUnderscores = NameWithUndersores(newCocktail.name);
        return "Correct! You earned " + pointsEarned + " points. Total points: " + points + ". New cocktail generated." +
                " \n new cocktail generated " + "\nName with underscores: " + namedWithUnderscores + "\nInstructions: " + newCocktail.instructions + "\nGlass: " + newCocktail.glass;
    }

    public static String handleIncorrectGuess(Map<String, Object> gameState, Cocktail cocktail, Integer attempts) {
        attempts++;
        gameState.put("attempts", attempts);

        
        String ingredientsHint = revealIngredients(cocktail, gameState, attempts);

        if (attempts >= MAX_POINTS) {
            System.out.println("Incorrect! You've run out of attempts. The correct answer was: " + cocktail.name);
            return "Game over! You've run out of attempts. The correct answer was: " + cocktail.name + ". Please enter your name to save your score.";
        } else {
            System.out.println("Incorrect! Try again. Attempts: " + attempts);

            
            String nameWithUnderscores = revealLetter(new StringBuilder((String) gameState.get("nameWithUnderscores")), cocktail.name, attempts);
            gameState.put("nameWithUnderscores", nameWithUnderscores);

            
            return "Incorrect! Try again. Attempts: " + attempts + ". Hint: " + nameWithUnderscores + "\nIngredients revealed:\n" + ingredientsHint;
        }
    }

    public static Cocktail startNewCocktail(Map<String, Object> gameState) {
        Cocktail newCocktail = new Cocktail();
        gameState.put("currentCocktail", newCocktail);
        gameState.put("attempts", 0);

        
        gameState.put("ingredientsList", new ArrayList<>(newCocktail.ingredients.entrySet()));

        String nameWithUnderscores = NameWithUndersores(newCocktail.name);
        gameState.put("nameWithUnderscores", nameWithUnderscores);

        System.out.println("New cocktail generated: " + newCocktail.name);
        return newCocktail;
    }
    
    public static String NameWithUndersores(String name) {
        StringBuilder nameWithUnderscores = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            if (Character.isLetter(name.charAt(i)) || Character.isDigit(name.charAt(i))) {
                nameWithUnderscores.append("_");
            } else {
                nameWithUnderscores.append(name.charAt(i));
            }
        }
        return nameWithUnderscores.toString();
    }
    
    public static String revealLetter(StringBuilder nameWithUnderscoresBuilder, String name, int attempts) {
        for (int i = 0; i < name.length(); i++) {
            if (nameWithUnderscoresBuilder.charAt(i) == '_' && Character.isLetter(name.charAt(i))) {
                nameWithUnderscoresBuilder.setCharAt(i, name.charAt(i));
                break;
            }
        }
        return nameWithUnderscoresBuilder.toString();
    }
    
    public static String revealIngredients(Cocktail cocktail, Map<String, Object> gameState, int attempts) {
        List<Map.Entry<String, String>> ingredientsList = (List<Map.Entry<String, String>>) gameState.get("ingredientsList");
        StringBuilder revealedIngredients = new StringBuilder();

        
        for (int i = 0; i < attempts && i < ingredientsList.size(); i++) {
            Map.Entry<String, String> ingredient = ingredientsList.get(i);
            revealedIngredients.append(ingredient.getKey()).append(": ").append(ingredient.getValue()).append("\n");
        }

        return revealedIngredients.toString();
    }
}
