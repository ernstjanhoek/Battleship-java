package battleship;

class ShipLocationException extends Exception {
    @Override
    public String getMessage() {
        return "Error! Wrong ship location! Try again:";
    }
}
