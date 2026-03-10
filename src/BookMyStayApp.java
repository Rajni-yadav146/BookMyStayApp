import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * UseCase3InventorySetup
 * Demonstrates centralized room inventory using HashMap
 * @version 3.1
 */

public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=====================================");
        System.out.println("Book My Stay - Hotel Booking System");
        System.out.println("Version 3.1");
        System.out.println("=====================================");

        RoomInventory inventory = new RoomInventory();

        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);
        inventory.addRoomType("Suite Room", 2);

        System.out.println("\n--- Current Room Inventory ---");
        inventory.displayInventory();

        System.out.print("\nEnter room type to check availability: ");
        String roomType = scanner.nextLine().trim();

        int available = inventory.getAvailability(roomType);

        System.out.println("\nAvailable " + roomType + " : " + available);

        System.out.print("\nEnter new availability count for " + roomType + ": ");
        int newCount = scanner.nextInt();

        inventory.updateAvailability(roomType, newCount);

        System.out.println("\n--- Updated Room Inventory ---");
        inventory.displayInventory();

        scanner.close();
    }
}

/**
 * RoomInventory class
 * Manages room availability
 * @version 3.0
 */

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        if (inventory.containsKey(roomType)) {
            return inventory.get(roomType);
        }
        return 0;
    }

    public void updateAvailability(String roomType, int newCount) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, newCount);
        } else {
            System.out.println("Room type not found in inventory.");
        }
    }

    public void displayInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");
        }
    }
}

/**
 * Abstract Room class
 * @version 3.0
 */

abstract class Room {
    String roomType;
}