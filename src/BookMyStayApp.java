import java.util.*;

/**
 * UseCase5BookingRequestQueue
 *
 * Demonstrates handling booking requests using a FIFO queue
 * in the Book My Stay Hotel Booking System.
 *
 * @version 5.1
 */
public class BookMyStayApp{

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        System.out.println("=====================================");
        System.out.println("Book My Stay - Hotel Booking System");
        System.out.println("Version 5.1 - Booking Request Queue");
        System.out.println("=====================================");

        System.out.println("\nSubmit booking requests (type 'done' to finish):");

        while (true) {
            System.out.print("\nGuest Name: ");
            String name = scanner.nextLine();
            if (name.equalsIgnoreCase("done")) {
                break;
            }

            System.out.print("Room Type (Single Room / Double Room / Suite Room): ");
            String roomType = scanner.nextLine();

            // Create a reservation request
            Reservation reservation = new Reservation(name, roomType);

            // Add to booking queue
            requestQueue.addRequest(reservation);

            System.out.println("Booking request submitted for " + name + " (" + roomType + ")");
        }

        // Display queued requests
        System.out.println("\n--- Current Booking Queue ---");
        requestQueue.displayQueue();

        scanner.close();
    }
}

/**
 * Reservation
 *
 * Represents a guest booking request
 *
 * @version 5.0
 */
class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return guestName + " -> " + roomType;
    }
}

/**
 * BookingRequestQueue
 *
 * Manages booking requests in a FIFO queue
 *
 * @version 5.0
 */
class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add a booking request
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    // Display all requests in order
    public void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("No booking requests in the queue.");
            return;
        }

        int position = 1;
        for (Reservation res : queue) {
            System.out.println(position + ". " + res);
            position++;
        }
    }

    // Get and remove the next request (for later processing)
    public Reservation getNextRequest() {
        return queue.poll();
    }
}