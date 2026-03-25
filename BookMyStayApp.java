import java.util.*;

// ------------------- Booking Request -------------------
class BookingRequest {
    private String customerName;
    private String roomType;

    public BookingRequest(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// ------------------- Reservation -------------------
class Reservation {
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

    public String getReservationId() {
        return reservationId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    public String toString() {
        return reservationId + " | " + customerName + " | " + roomType + " | " + roomId;
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
}

// ------------------- Booking History -------------------
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        history.add(reservation); // maintains insertion order
    }

    public List<Reservation> getAllReservations() {
        return history;
    }
}

// ------------------- Booking Report Service -------------------
class BookingReportService {

    public void printAllBookings(List<Reservation> reservations) {
        System.out.println("\n📋 All Booking History:");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    public void printSummary(List<Reservation> reservations) {
        Map<String, Integer> countByType = new HashMap<>();

        for (Reservation r : reservations) {
            countByType.put(r.getRoomType(),
                    countByType.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("\n📊 Booking Summary (Room Type Count):");
        for (Map.Entry<String, Integer> entry : countByType.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// ------------------- Booking Service -------------------
class BookingService {
    private Queue<BookingRequest> queue = new LinkedList<>();
    private Set<String> allocatedRoomIds = new HashSet<>();
    private InventoryService inventoryService;
    private BookingHistory bookingHistory;

    public BookingService(InventoryService inventoryService, BookingHistory bookingHistory) {
        this.inventoryService = inventoryService;
        this.bookingHistory = bookingHistory;
    }

    public void addRequest(BookingRequest request) {
        queue.offer(request);
    }

    private String generateRoomId(String roomType) {
        String id;
        do {
            id = roomType.substring(0, 2).toUpperCase() + "-" +
                    UUID.randomUUID().toString().substring(0, 5);
        } while (allocatedRoomIds.contains(id));
        return id;
    }

    private String generateReservationId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 6);
    }

    public void processBookings() {
        while (!queue.isEmpty()) {
            BookingRequest req = queue.poll();

            if (!inventoryService.isAvailable(req.getRoomType())) {
                System.out.println("❌ No availability for " + req.getCustomerName());
                continue;
            }

            String roomId = generateRoomId(req.getRoomType());
            String reservationId = generateReservationId();

            allocatedRoomIds.add(roomId);
            inventoryService.decrement(req.getRoomType());

            Reservation reservation = new Reservation(
                    reservationId,
                    req.getCustomerName(),
                    req.getRoomType(),
                    roomId
            );

            // ✅ Store in booking history
            bookingHistory.addReservation(reservation);

            System.out.println("✅ Booking Confirmed: " + reservation);
        }
    }
}

// ------------------- Main Class -------------------
public class BookMyStayApp {
    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingHistory bookingHistory = new BookingHistory();
        BookingService bookingService = new BookingService(inventoryService, bookingHistory);
        BookingReportService reportService = new BookingReportService();

        // Add booking requests
        bookingService.addRequest(new BookingRequest("Alice", "Single"));
        bookingService.addRequest(new BookingRequest("Bob", "Double"));
        bookingService.addRequest(new BookingRequest("Charlie", "Single"));
        bookingService.addRequest(new BookingRequest("David", "Suite"));

        // Process bookings
        bookingService.processBookings();

        // Admin views reports
        List<Reservation> history = bookingHistory.getAllReservations();

        reportService.printAllBookings(history);
        reportService.printSummary(history);
    }
}