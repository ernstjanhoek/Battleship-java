package battleship;

class ShipProximityException extends Exception {
    @Override
    public String getMessage() {
        return "Error! You placed it too close to another one. Try again:";
    }
}
