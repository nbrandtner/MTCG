package game.card;

public class Card {
    private String name;
    private String elementType;
    private int damage;

    public Card() {
        this.name = "Unknown";
        this.elementType = "Normal";
        this.damage = 0;
    }

    public Card(String name, String elementType, int damage) {
        this.name = name;
        this.elementType = elementType;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}