package battleship;

class ShipLengthException extends Exception {
    String message;

    ShipLengthException(String name) {
        this.message = "Error! Wrong length of the " + name + "! Try again:";
    }

    public String getMessage() {
        return message;
    }
}
