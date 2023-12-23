package battleship;

import java.util.Scanner;

class Field {
    private final PointState[][] fieldArray;
    private int yTarget = 0;
    private int xTarget = 0;

    public Field(int x_length, int y_length) {
        this.fieldArray = new PointState[y_length][x_length];
        for (int y = 0; y < y_length; y++) {
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
            checkInputY(yAxisStart);
            checkInputY(yAxisEnd);
            checkInputX(xAxisStart);
            checkInputX(xAxisEnd);
            checkInput(yAxisStart, yAxisEnd, xAxisStart, xAxisEnd);
            checkProximity(yAxisStart, yAxisEnd, xAxisStart, xAxisEnd);
            for (int y = yAxisStart; y <= yAxisEnd; y++) {
                for (int x = xAxisStart; x <= xAxisEnd; x++) {
                    this.assignCoordinateValue(y, x, PointState.SHIP);
                }
            }
        } catch (Exception e) {
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
        int yEndClamped = Math.min(yValue1 + padding, intToIndex(fieldArray.length));
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        for (int y = 0; y < this.fieldArray.length; y++) {
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
            this.value = value;
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
