package game.deck;

import java.util.ArrayList;
import java.util.List;
import game.card.Card;

public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        if (cards.size() == 4) {
            this.cards = cards;
        } else {
            throw new IllegalArgumentException("Deck must contain exactly 4 cards");
        }
    }

    public void addCard(Card card) {
        if (cards.size() < 4) {
            cards.add(card);
        } else {
            throw new IllegalStateException("Deck can only contain 4 cards");
        }
    }
}