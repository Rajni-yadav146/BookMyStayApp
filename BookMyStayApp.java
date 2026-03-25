import java.util.*;

// ------------------- Reservation -------------------
class Reservation {
    private String reservationId;
    private String customerName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String reservationId, String customerName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
    }

    public String getReservationId() { return reservationId; }
    public String getCustomerName() { return customerName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isCancelled() { return isCancelled; }

    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public String toString() {
        return reservationId + " | " + customerName + " | " +
               roomType + " | " + roomId +
               (isCancelled ? " | CANCELLED" : "");
    }
}

// ------------------- Inventory Service -------------------
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void increment(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public void printInventory() {
        System.out.println("\n📦 Current Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// ------------------- Booking Service -------------------
class BookingService {
    private Map<String, Reservation> reservations = new HashMap<>();
    private Set<String> allocatedRoomIds = new HashSet<>();
    private InventoryService inventoryService;

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public Reservation createBooking(String customer, String roomType) {
        if (!inventoryService.isAvailable(roomType)) {
            System.out.println("❌ No availability for " + customer);
            return null;
        }

        String roomId = generateRoomId(roomType);
        String reservationId = generateReservationId();

        inventoryService.decrement(roomType);
        allocatedRoomIds.add(roomId);

        Reservation r = new Reservation(reservationId, customer, roomType, roomId);
        reservations.put(reservationId, r);

        System.out.println("✅ Booking Confirmed: " + r);
        return r;
    }

    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" +
               UUID.randomUUID().toString().substring(0, 5);
    }

    private String generateReservationId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 6);
    }

    public Map<String, Reservation> getReservations() {
        return reservations;
    }

    public void removeRoomId(String roomId) {
        allocatedRoomIds.remove(roomId);
    }
}

// ------------------- Cancellation Service -------------------
class CancellationService {
    private Stack<String> rollbackStack = new Stack<>();
    private InventoryService inventoryService;
    private BookingService bookingService;

    public CancellationService(InventoryService inventoryService, BookingService bookingService) {
        this.inventoryService = inventoryService;
        this.bookingService = bookingService;
    }

    public void cancelBooking(String reservationId) {

        Map<String, Reservation> reservations = bookingService.getReservations();

        if (!reservations.containsKey(reservationId)) {
            System.out.println("❌ Cancellation Failed: Reservation not found -> " + reservationId);
            return;
        }

        Reservation r = reservations.get(reservationId);

        if (r.isCancelled()) {
            System.out.println("❌ Already Cancelled: " + reservationId);
            return;
        }

        // Step 1: Push roomId to rollback stack (LIFO)
        rollbackStack.push(r.getRoomId());

        // Step 2: Restore inventory
        inventoryService.increment(r.getRoomType());

        // Step 3: Remove allocated room
        bookingService.removeRoomId(r.getRoomId());

        // Step 4: Mark reservation cancelled
        r.cancel();

        System.out.println("🔄 Booking Cancelled Successfully: " + r);
    }

    public void printRollbackStack() {
        System.out.println("\n🧱 Rollback Stack (Recent Releases): " + rollbackStack);
    }
}

// ------------------- Main Class -------------------
public class BookMyStayApp{
    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(inventoryService);
        CancellationService cancellationService =
                new CancellationService(inventoryService, bookingService);

        // Create bookings
        Reservation r1 = bookingService.createBooking("Alice", "Single");
        Reservation r2 = bookingService.createBooking("Bob", "Double");

        // Cancel valid booking
        if (r1 != null) {
            cancellationService.cancelBooking(r1.getReservationId());
        }

        // Try duplicate cancellation
        if (r1 != null) {
            cancellationService.cancelBooking(r1.getReservationId());
        }

        // Try invalid cancellation
        cancellationService.cancelBooking("RES-INVALID");

        // View system state
        inventoryService.printInventory();
        cancellationService.printRollbackStack();
    }
}