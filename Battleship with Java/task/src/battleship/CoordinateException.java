package battleship;

class CoordinateException extends Exception {
    @Override
    public String getMessage() {
        return "Error! You entered wrong coordinates! Try again:";
    }
}
