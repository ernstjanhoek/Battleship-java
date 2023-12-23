package battleship;

import java.util.Scanner;

class ShipData {
    private char yStart;
    private char yEnd;
    private int xStart;
    private int xEnd;
    private final int length;
    private final String name;

    ShipData(ShipType ship) {
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
        } catch (Exception e) {
            throw new CoordinateException();
        }
    }

    public String placementMessage() {
        return "Enter the coordinates of the " + this.name + " (" + this.length + " cells):";
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
