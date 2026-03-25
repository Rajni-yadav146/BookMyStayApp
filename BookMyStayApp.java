import java.io.*;
import java.util.*;

// ------------------- Reservation -------------------
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String customerName;
    private String roomType;
    private String roomId;

    public Reservation(String reservationId, String customerName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getReservationId() { return reservationId; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return reservationId + " | " + customerName + " | " + roomType + " | " + roomId;
    }
}

// ------------------- System State (Wrapper) -------------------
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<String, Integer> inventory;
    List<Reservation> bookingHistory;

    public SystemState(Map<String, Integer> inventory, List<Reservation> bookingHistory) {
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
    }
}

// ------------------- Inventory Service -------------------
class InventoryService implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public boolean isAvailable(String type) {
        return inventory.getOrDefault(type, 0) > 0;
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<String, Integer> data) {
        this.inventory = data;
    }

    public void printInventory() {
        System.out.println("\n📦 Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// ------------------- Booking Service -------------------
class BookingService {
    private List<Reservation> history = new ArrayList<>();
    private InventoryService inventory;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void book(String customer, String type) {
        if (!inventory.isAvailable(type)) {
            System.out.println("❌ No availability for " + customer);
            return;
        }

        String resId = "RES-" + UUID.randomUUID().toString().substring(0, 5);
        String roomId = type.substring(0, 2).toUpperCase() + "-" +
                        UUID.randomUUID().toString().substring(0, 5);

        inventory.decrement(type);

        Reservation r = new Reservation(resId, customer, type, roomId);
        history.add(r);

        System.out.println("✅ Booked: " + r);
    }

    public List<Reservation> getHistory() {
        return history;
    }

    public void setHistory(List<Reservation> history) {
        this.history = history;
    }

    public void printHistory() {
        System.out.println("\n📋 Booking History:");
        for (Reservation r : history) {
            System.out.println(r);
        }
    }
}

// ------------------- Persistence Service -------------------
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    // Save state
    public static void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("\n💾 System state saved successfully.");

        } catch (IOException e) {
            System.out.println("❌ Error saving state: " + e.getMessage());
        }
    }

    // Load state
    public static SystemState load() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("⚠️ No previous state found. Starting fresh.");
            return null;
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("♻️ Restoring system state...");
            return (SystemState) ois.readObject();

        } catch (Exception e) {
            System.out.println("❌ Corrupted state. Starting fresh.");
            return null;
        }
    }
}

// ------------------- Main Class -------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Try to restore state
        SystemState loadedState = PersistenceService.load();

        InventoryService inventory = new InventoryService();
        BookingService bookingService = new BookingService(inventory);

        // Step 2: Restore if available
        if (loadedState != null) {
            inventory.setInventory(loadedState.inventory);
            bookingService.setHistory(loadedState.bookingHistory);
            System.out.println("✅ State restored successfully.");
        }

        // Step 3: Perform operations
        bookingService.book("Alice", "Single");
        bookingService.book("Bob", "Double");

        // Step 4: View current state
        bookingService.printHistory();
        inventory.printInventory();

        // Step 5: Save state before shutdown
        SystemState currentState = new SystemState(
                inventory.getInventory(),
                bookingService.getHistory()
        );

        PersistenceService.save(currentState);
    }
}