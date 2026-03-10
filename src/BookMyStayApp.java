import java.util.*;

/**
 * UseCase4RoomSearch
 *
 * Demonstrates read-only room search with user input
 * in the Book My Stay Hotel Booking System.
 *
 * @version 4.1
 */

public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=====================================");
        System.out.println("Book My Stay - Hotel Booking System");
        System.out.println("Version 4.1");
        System.out.println("=====================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);
        inventory.addRoomType("Suite Room", 0);

        // Initialize room objects
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Search service
        RoomSearchService searchService = new RoomSearchService(inventory, rooms);

        System.out.println("\nSearch Options:");
        System.out.println("1. View All Available Rooms");
        System.out.println("2. Check Specific Room Type");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {

            System.out.println("\n--- Available Rooms ---");
            searchService.displayAvailableRooms();

        } else if (choice == 2) {

            System.out.print("\nEnter room type (Single Room / Double Room / Suite Room): ");
            String roomType = scanner.nextLine();

            searchService.searchRoom(roomType);

        } else {

            System.out.println("Invalid choice.");
        }

        scanner.close();
    }
}

/**
 * RoomInventory
 * Centralized inventory state holder
 * @version 4.0
 */

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }
}

/**
 * RoomSearchService
 * Handles read-only search operations
 * @version 4.0
 */

class RoomSearchService {

    private RoomInventory inventory;
    private List<Room> rooms;

    public RoomSearchService(RoomInventory inventory, List<Room> rooms) {
        this.inventory = inventory;
        this.rooms = rooms;
    }

    public void displayAvailableRooms() {

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getRoomType());

            if (available > 0) {

                room.displayDetails();
                System.out.println("Available Rooms: " + available);
                System.out.println("----------------------------------");
            }
        }
    }

    public void searchRoom(String type) {

        for (Room room : rooms) {

            if (room.getRoomType().equalsIgnoreCase(type)) {

                int available = inventory.getAvailability(type);

                if (available > 0) {

                    room.displayDetails();
                    System.out.println("Available Rooms: " + available);

                } else {

                    System.out.println("Sorry, this room type is currently unavailable.");
                }

                return;
            }
        }

        System.out.println("Invalid room type.");
    }
}

/**
 * Abstract Room domain model
 * @version 4.0
 */

abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected double price;

    public Room(String type, int beds, int size, double price) {
        this.roomType = type;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq ft");
        System.out.println("Price     : $" + price);
    }
}

/**
 * Single Room
 * @version 4.0
 */
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 200, 100.0);
    }
}

/**
 * Double Room
 * @version 4.0
 */
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 350, 180.0);
    }
}

/**
 * Suite Room
 * @version 4.0
 */
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 600, 350.0);
    }
}