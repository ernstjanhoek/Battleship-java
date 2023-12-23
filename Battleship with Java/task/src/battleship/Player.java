package battleship;

class Player {
    private final Field field;

    public Field getField() {
        return field;
    }

    private int health;

    Player(Field field) {
        this.field = field;
    }

    public void increaseHealth(int value) {
        this.health += value;
    }

    public void decreaseHealth() {
        this.health--;
    }

    public int getHealth() {
        return health;
    }
}
