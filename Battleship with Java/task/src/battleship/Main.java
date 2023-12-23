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