import java.util.Scanner;

/**
 * UseCase2RoomInitialization
 *
 * Demonstrates initialization of different room types
 * and allows the user to choose a room to view availability.
 *
 * @author Student
 * @version 2.1
 */

public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=====================================");
        System.out.println("Book My Stay - Hotel Booking System");
        System.out.println("Version 2.1");
        System.out.println("=====================================");

        // Creating room objects (Polymorphism)
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Static availability variables
        int singleAvailability = 5;
        int doubleAvailability = 3;
        int suiteAvailability = 2;

        System.out.println("\nSelect Room Type:");
        System.out.println("1. Single Room");
        System.out.println("2. Double Room");
        System.out.println("3. Suite Room");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();

        System.out.println("\n--- Room Details ---\n");

        switch (choice) {

            case 1:
                singleRoom.displayRoomDetails();
                System.out.println("Available Rooms: " + singleAvailability);
                break;

            case 2:
                doubleRoom.displayRoomDetails();
                System.out.println("Available Rooms: " + doubleAvailability);
                break;

            case 3:
                suiteRoom.displayRoomDetails();
                System.out.println("Available Rooms: " + suiteAvailability);
                break;

            default:
                System.out.println("Invalid choice. Please restart the application.");
        }

        System.out.println("\nThank you for using Book My Stay.");
        scanner.close();
    }
}

/**
 * Abstract Room class defining common properties.
 * @version 2.0
 */
abstract class Room {

    private String roomType;
    private int beds;
    private int size;
    private double price;

    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq ft");
        System.out.println("Price     : $" + price);
    }
}

/**
 * Single Room implementation.
 * @version 2.0
 */
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 200, 100.0);
    }
}

/**
 * Double Room implementation.
 * @version 2.0
 */
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 350, 180.0);
    }
}

/**
 * Suite Room implementation.
 * @version 2.0
 */
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 600, 350.0);
    }
}