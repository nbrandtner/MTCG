package game.packs;

import game.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Package {
    private List<Card> cards;

    public Package() {
        this.cards = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        if (cards.size() < 5) {
            cards.add(card);
        } else {
            throw new IllegalStateException("Package can only contain 5 cards");
        }
    }
}