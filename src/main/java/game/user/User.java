package game.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import game.card.Card;
import game.deck.Deck;

import java.util.ArrayList;
import java.util.List;

public class User {
    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    private int coins;
    private List<Card> stack;
    private Deck deck;

    public User() {
        this.stack = new ArrayList<>();
        this.deck = new Deck();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public List<Card> getStack() {
        return stack;
    }

    public Deck getDeck() {
        return deck;
    }
}