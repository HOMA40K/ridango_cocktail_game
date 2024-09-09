package com.ridango.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class APIController {

    @Autowired
    private PlayerScoreRepository playerScoreRepository;

    
    @GetMapping(value = "api/cocktail/random", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCocktail(HttpSession session) {
        
        Map<String, Object> gameState = (Map<String, Object>) session.getAttribute("gameState");

        if (gameState == null) {
            gameState = new HashMap<>();
            session.setAttribute("gameState", gameState);
        }
        
        Game.startNewCocktail(gameState);
        
        Cocktail cocktail = Game.getCurrentCocktail(gameState);
        Map<String, Object> response = new HashMap<>();

        response.put("nameWithUnderscores", gameState.get("nameWithUnderscores"));
        response.put("instructions", cocktail.instructions);
        response.put("glass", cocktail.glass);
        
        System.out.println("Response ready to be sent:");
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        return "Name with underscores: " + gameState.get("nameWithUnderscores") + "\nInstructions: " + cocktail.instructions + "\nGlass: " + cocktail.glass;
    }

    @GetMapping(value = "api/cocktail/guess", produces = MediaType.TEXT_PLAIN_VALUE)
    public String guessCocktail(@RequestParam String guess, HttpSession session) {
        
        Map<String, Object> gameState = (Map<String, Object>) session.getAttribute("gameState");
        if (gameState == null) {
            return "Error: No current cocktail. Start a new game first!";
        }
        
        Cocktail cocktail = Game.getCurrentCocktail(gameState);
        if (cocktail == null) {
            return "Error: No current cocktail. Start a new game first!";
        }
        
        Integer attempts = Game.getAttempts(gameState);
        Integer points = Game.getPoints(gameState);

        
        String result;
        if (Game.isGuessCorrect(cocktail, guess)) {
            result = Game.handleCorrectGuess(gameState, cocktail, attempts, points);
        } else {
            result = Game.handleIncorrectGuess(gameState, cocktail, attempts);
            
            if (attempts + 1 >= Game.MAX_POINTS) {
                return "Game over! You've run out of attempts. Please enter your name to save your score.";
            }
        }
        
        session.setAttribute("gameState", gameState);
        
        return result;
    }


    
    @PostMapping(value = "api/cocktail/saveScore", produces = MediaType.TEXT_PLAIN_VALUE)
    public String savePlayerScore(@RequestParam String playerName, HttpSession session) {
        
        Map<String, Object> gameState = (Map<String, Object>) session.getAttribute("gameState");

        if (gameState == null || !gameState.containsKey("points")) {
            return "Error: No game in progress!";
        }

        
        Integer points = Game.getPoints(gameState);

        
        PlayerScore playerScore = new PlayerScore(playerName, points);
        playerScoreRepository.save(playerScore);

        
        session.removeAttribute("gameState");

        return "Score saved! Player: " + playerName + ", Score: " + points;
    }

}
