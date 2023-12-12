package battleship;

import java.util.Scanner;

    public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();
        for (int i = 0; i < 2; i++) {
            System.out.println(game.activePlayer.getPlayer() + ", place your ships to the game field\n");
            Player activePlayer = new Player(new Field(10, 10));
            System.out.println(activePlayer.getField().displayField(Field.DisplayEnum.OPEN));
            ShipType[] fleet = {
                    new ShipType(5, "Aircraft Carrier"),
                    new ShipType(4, "Battleship"),
                    new ShipType(3, "Submarine"),
                    new ShipType(3, "Cruiser"),
                    new ShipType(2, "Destroyer")
            };
            for (ShipType ship : fleet) {
                ShipData shipData = new ShipData(ship);
                System.out.println(shipData.placementMessage());
                boolean shipPlaced = false;
                while (!shipPlaced) {
                    try {
                        shipData.scanCoordinates(scanner);
                        activePlayer.getField().placeShip(shipData);
                        System.out.println(activePlayer.getField().displayField(Field.DisplayEnum.OPEN));
                        activePlayer.increaseHealth(ship.length());
                        shipPlaced = true;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            game.setActivePlayerData(activePlayer);
            game.transitionPlayer();
            System.out.println("Press Enter and pass the move to another player\n...");
            Main.magicRead(scanner);
        }
        while (game.getActivePlayerData().getHealth() > 0 ) {
            System.out.print(game.getOtherPlayerData().getField().displayField(Field.DisplayEnum.FOW));
            System.out.println("---------------------");
            System.out.println(game.getActivePlayerData().getField().displayField(Field.DisplayEnum.OPEN));
            System.out.println(game.activePlayer.getPlayer() + ", it's your turn:");
            try {
                System.out.println("Take a shot!");
                Field.FireResultEnum hit = game.getOtherPlayerData().getField().fire(scanner, game.getOtherPlayerData());
                switch (hit) {
                    case HIT:
                        System.out.println("You hit a ship!");
                        break;
                    case MISSED:
                        System.out.println("You missed!");
                        break;
                    case DESTROYED:
                        if (game.getOtherPlayerData().getHealth() == 0) {
                            System.out.println("You sank the last ship. You won. Congratulations!");
                        } else {
                            System.out.println("You sank a ship! Specify a new target:");
                        }
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            game.transitionPlayer();
            System.out.println("Press Enter and pass the move to another player\n...");
            Main.magicRead(scanner);
        }
    }
    static void magicRead(Scanner scanner) {
            scanner.nextLine();
            scanner.nextLine();
    }
}
class CoordinateException extends Exception {
    @Override
    public String getMessage() {
        return "Error! You entered wrong coordinates! Try again:";
    }
}
class ShipLocationException extends Exception {
    @Override
    public String getMessage() {
        return "Error! Wrong ship location! Try again:";
    }
}
class ShipProximityException extends Exception {
    @Override
    public String getMessage() {
        return "Error! You placed it too close to another one. Try again:";
    }
}
class ShipLengthException extends Exception {
    String message;
    ShipLengthException(String name) {
        this.message = "Error! Wrong length of the " + name + "! Try again:";
    }
    public String getMessage() {
        return message;
    }
}
record ShipType(int length, String name) { }
class ShipData {
    private char yStart;
    private char yEnd;
    private int xStart;
    private int xEnd;
    private final int length;
    private final String name;
    ShipData(ShipType ship)  {
        this.name = ship.name();
        this.length = ship.length();
    }
    /*
    Toevoegen dat coordinaten van ook van grotere waarde naar lagere waarde ingevoerd kunnen worden: bv. A8 A6
     */
    public void scanCoordinates(Scanner scanner) throws CoordinateException {
        try {
            String input0 = scanner.next();
            this.yStart = input0.charAt(0);
            String subString0 = input0.substring(1);
            this.xStart = Integer.parseInt(subString0);

            String input1 = scanner.next();
            this.yEnd = input1.charAt(0);
            String subString1 = input1.substring(1);
            this.xEnd = Integer.parseInt(subString1);
        } catch(Exception e) {
            throw new CoordinateException();
        }
    }
    public String placementMessage() {
        return 	"Enter the coordinates of the " + this.name +" (" + this.length + " cells):";
    }
    /*
    checkLength met 3 values en name string checkt of de lengte van de boot overeenkomt met ingevoerde coordinaten
     */
    public void checkLength() throws ShipLengthException {
        int yAbsolute = Math.abs((this.yStart - this.yEnd)) + 1; // voeg 1 toe omdat vanwege index nummmers/ lengte gevraagd
        int xAbsolute = Math.abs((this.xStart - this.xEnd)) + 1;
        if (!(yAbsolute == this.length || xAbsolute == this.length)) {
            throw new ShipLengthException(this.name);
        }
    }
    public char getyStart() {
        return yStart;
    }
    public char getyEnd() {
        return yEnd;
    }
    public int getxStart() {
        return xStart;
    }
    public int getxEnd() {
        return xEnd;
    }
}
class Field  {
    private final PointState[][] fieldArray;
    private int yTarget = 0;
    private int xTarget = 0;
    public Field(int x_length, int y_length) {
        this.fieldArray = new PointState[y_length][x_length];
        for (int y = 0; y < y_length; y++ ) {
            for (int x = 0; x < x_length; x++) {
                this.fieldArray[y][x] = PointState.EMPTY;
            }
        }
    }
    public void placeShip(ShipData ship) throws Exception {
        try {
            int yAxisStart;
            int yAxisEnd;
            int xAxisStart;
            int xAxisEnd;
            int multipliedCoord0 = (charToIndex(ship.getyStart()) + 1) * ship.getxStart();
            int multipliedCoord1 = (charToIndex(ship.getyEnd()) + 1) * ship.getxEnd();
            /*
            Programma verwerkt in for loops van komende functies de coordinaten van input van laag naar
            hoog. Als het eerst ingevoerde coordinaat [yAxisStart, xAxisStart] hogere waardes heeft dan
            het tweede ingevoerde coordinaat [yAxisEnd, xAxisEnd], dan werkt het programma niet.
            Door de x en y coordinaten met elkaar te vermenigvuldigen en de uitkomsten met elkaar te vergelijken is voor ieder coordinatenpaar te bepalen
            of deze 'groter' (meer naar links en naar onderen) dan een ander. Dit werkt voor zowel paartjes in de y- als x-lengte.
            Als de beide vermenigvuldigingen gelijk zijn, dan is er sprake van een ongeldig coordinaat:
               1  2  3  4  5
            A  1  2  3  4  5
            B  2  4  6  8  10
            C  3  6  9  12 15
            D  4  8  12 16 20
            E  5  10 15 20 25
            (Ongeldige coordinaten worden met checkInput methods later eruit gefilterd)
             */
            if (multipliedCoord0 <= multipliedCoord1) {
                yAxisStart = charToIndex(ship.getyStart());
                xAxisStart = intToIndex(ship.getxStart());
                yAxisEnd = charToIndex(ship.getyEnd());
                xAxisEnd = intToIndex(ship.getxEnd());
            } else {
                yAxisStart = charToIndex(ship.getyEnd());
                xAxisStart = intToIndex(ship.getxEnd());
                yAxisEnd = charToIndex(ship.getyStart());
                xAxisEnd = intToIndex(ship.getxStart());
            }
            ship.checkLength();
            checkInputY(yAxisStart); checkInputY(yAxisEnd);
            checkInputX(xAxisStart); checkInputX(xAxisEnd);
            checkInput(yAxisStart, yAxisEnd, xAxisStart, xAxisEnd);
            checkProximity(yAxisStart, yAxisEnd, xAxisStart, xAxisEnd);
            for (int y = yAxisStart; y <= yAxisEnd; y++) {
                for (int x = xAxisStart; x <= xAxisEnd; x++) {
                    this.assignCoordinateValue(y, x, PointState.SHIP);
                }
            }
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /*
    checkInput met 4 values checkt of de start en eind coordinate  van de boot 1-dimensionaal zijn
    en of boot niet diagonaal geplaatst wordt? dus bij exception throws ShipLocationException
     */
    private void checkInput(int yValue0, int yValue1, int xValue0, int xValue1) throws ShipLocationException {
        if (yValue0 != yValue1 && xValue0 != xValue1) {
            throw new ShipLocationException();
        }
    }
    /*
    checkProximity kijkt of er geen boten rondom de te plaatsen boot liggen
     */
    private void checkProximity(int yValue0, int yValue1, int xValue0, int xValue1) throws ShipProximityException {
        int padding = 1;
        int yStartClamped = Math.max(yValue0 - padding, 0);
        int yEndClamped =  Math.min(yValue1 + padding, intToIndex(fieldArray.length));
        int xStartClamped = Math.max(xValue0 - padding, 0);
        int xEndClamped = Math.min(xValue1 + padding, intToIndex(fieldArray[0].length));
        for (int y = yStartClamped; y <= yEndClamped; y++) {
            for (int x = xStartClamped; x <= xEndClamped; x++) {
                if (fieldArray[y][x] == PointState.SHIP) {
                    throw new ShipProximityException();
                }
            }
        }
    }
    /*
    checkInput met 1 value checkt of ingevoerde coordinaten binnen het veld liggen
    dus bij exception throws CoordinateException.
     */
    private void checkInputY(int value) throws CoordinateException {
        if (value < 0 || value >= fieldArray.length) {
            throw new CoordinateException();
        }
    }
    private void checkInputX(int xValue) throws CoordinateException {
        if (xValue < 0 || xValue >= this.fieldArray[0].length) {
            throw new CoordinateException();
        }
    }
    public FireResultEnum fire(Scanner scanner, Player player) throws CoordinateException {
        try {
            scanCoordinate(scanner);
            PointState pointValue = fieldArray[this.yTarget][this.xTarget];
            if (pointValue == PointState.SHIP) {
                this.assignCoordinateValue(this.yTarget, this.xTarget, PointState.SUNK);
                player.decreaseHealth();
                if (this.checkShipSunk()) {
                    return FireResultEnum.DESTROYED;
                } else {
                    return FireResultEnum.HIT;
                }
            } else if (pointValue == PointState.SUNK) {
                return FireResultEnum.HIT;
            } else {
                this.assignCoordinateValue(this.yTarget, this.xTarget, PointState.MISSED);
                return FireResultEnum.MISSED;
            }
        } catch(Exception e) {
            throw new CoordinateException();
        }
    }
    enum FireResultEnum {
        HIT, MISSED, DESTROYED
    }
    private void scanCoordinate(Scanner scanner) throws CoordinateException {
        try {
            String input0 = scanner.next();
            this.yTarget = charToIndex(input0.charAt(0));
            String subString0 = input0.substring(1);
            this.xTarget = intToIndex(Integer.parseInt(subString0));
        } catch(Exception e) {
            throw new CoordinateException();
        }
    }
    private boolean checkShipSunk() {
        /* Check met loop of velden in bepaalde richting SUNK('X'), SHIP('O'),of MISSED('X')/EMPTY('~') zijn.
        Bij MISSED of EMPTY: Stop met zoeken in die richting.
        Bij SUNK: Blijf zoeken in die richting.
        Bij SHIP: Stop met zoeken in alle richtingen: throw exception en return false ;
        Als er geen loops meer lopen (in alle richtingen is EMPTY gevonden): return true;
        Clamp loops tussen 0(min index) en intToIndex(fielArray.length)(max index)
         */
        try {
            for (int y = this.yTarget; y >= 0; --y) {
                if (fieldEmptyOrShip(y, this.xTarget)) {
                    break;
                }
            }
            for (int y = this.yTarget; y < fieldArray.length; ++y) {
                if (fieldEmptyOrShip(y, this.xTarget)) {
                    break;
                }
            }
            for (int x = this.xTarget; x >= 0; --x) {
                if (fieldEmptyOrShip(this.yTarget, x)) {
                    break;
                }
            }
            for (int x = this.xTarget; x < fieldArray.length; ++x) {
                if (fieldEmptyOrShip(this.yTarget, x)) {
                    break;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    private boolean fieldEmptyOrShip(int yValue, int xValue) throws Exception {
        /*
        MISSED/EMPTY is true;
        SUNK is false;
        SHIP is exception;
         */
        PointState point = fieldArray[yValue][xValue];
        return switch (point) {
            case EMPTY, MISSED -> true;
            case SUNK -> false;
            case SHIP -> throw new Exception();
        };
    }
    private int intToIndex(int value) {
        return value - 1;
    }
    private int charToIndex(char value) {
        return (int) value - 65;
    }
    private void assignCoordinateValue(int y_axis, int x_axis, PointState value) {
        this.fieldArray[y_axis][x_axis] = value;
    }
    public String displayField(DisplayEnum enumOption) { // Game.GameState gameState, Game.ActivePlayer activePlayer)  {
        StringBuilder out = new StringBuilder("  ");
        for (int i = 1; i <= this.fieldArray.length; i++) {
            out.append(i).append(" ");
        }
        out.append("\n");
        for (int y = 0; y < this.fieldArray.length; y++ ) {
            out.append((char) ('A' + y)).append(" ");
            for (int x = 0; x < this.fieldArray[0].length; x++) {
                out.append(fieldArray[y][x].returnValue(enumOption)).append(" ");
            }
            out.append("\n");
        }
        return out.toString();
    }
    enum PointState {
        EMPTY('~'),
        MISSED('M'),
        SHIP('O'),
        SUNK('X');
        private final char value;
        PointState(char value) {
            this.value =  value;
        }
        public char returnValue(DisplayEnum enumOption) {
            if (enumOption == DisplayEnum.FOW && this == SHIP) {
                return PointState.EMPTY.value;
            } else {
                return this.value;
            }
        }
    }
    enum DisplayEnum {
        OPEN, FOW
    }
}
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
class Game {
    Game() {
        this.activePlayer = ActivePlayer.PLAYER_1;
    }
    private Player playerOneData;
    private Player playerTwoData;
    ActivePlayer activePlayer;
    public Player getActivePlayerData() {
        if (activePlayer == ActivePlayer.PLAYER_1) {
            return this.playerOneData;
        } else {
            return this.playerTwoData;
        }
    }
    public Player getOtherPlayerData() {
        if (activePlayer == ActivePlayer.PLAYER_1) {
            return this.playerTwoData;
        } else {
            return this.playerOneData;
        }
    }
    public void setActivePlayerData(Player playerData) {
        if (activePlayer == ActivePlayer.PLAYER_1) {
            this.playerOneData = playerData;
        } else {
            this.playerTwoData = playerData;
        }

    }
    @SuppressWarnings("unused")
    public void setOtherPlayerData(Player playerData) {
        if (activePlayer == ActivePlayer.PLAYER_1) {
            this.playerTwoData = playerData;
        } else {
            this.playerOneData = playerData;
        }
    }
    public void transitionPlayer() {
        switch (this.activePlayer) {
            case PLAYER_1:
                this.activePlayer = ActivePlayer.PLAYER_2;
                break;
            case PLAYER_2:
                this.activePlayer = ActivePlayer.PLAYER_1;
                break;
        }
    }
    enum ActivePlayer {
        PLAYER_1("Player 1"),
        PLAYER_2("Player 2");
        ActivePlayer(String string) {
            this.player = string;
        }
        private final String player;
        public String getPlayer() {
            return player;
        }
    }
}
