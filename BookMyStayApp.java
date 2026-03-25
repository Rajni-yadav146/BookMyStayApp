import java.util.*;

// ------------------- Custom Exceptions -------------------
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class NoAvailabilityException extends Exception {
    public NoAvailabilityException(String message) {
        super(message);
    }
}

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

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) throws NoAvailabilityException {
        int count = inventory.getOrDefault(roomType, 0);

        if (count <= 0) {
            throw new NoAvailabilityException("No rooms available for type: " + roomType);
        }

        inventory.put(roomType, count - 1);
    }
}

// ------------------- Booking Validator -------------------
class BookingValidator {

    public static void validate(BookingRequest request, InventoryService inventoryService)
            throws InvalidRoomTypeException, NoAvailabilityException {

        if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }

        if (!inventoryService.isValidRoomType(request.getRoomType())) {
            throw new InvalidRoomTypeException("Invalid room type: " + request.getRoomType());
        }

        if (!inventoryService.isAvailable(request.getRoomType())) {
            throw new NoAvailabilityException("Room not available for type: " + request.getRoomType());
        }
    }
}

// ------------------- Booking Service -------------------
class BookingService {
    private Queue<BookingRequest> queue = new LinkedList<>();
    private Set<String> allocatedRoomIds = new HashSet<>();
    private InventoryService inventoryService;

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
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

            try {
                // ✅ Fail-Fast Validation
                BookingValidator.validate(req, inventoryService);

                // Safe allocation after validation
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

                System.out.println("✅ Booking Confirmed: " + reservation);

            } catch (InvalidRoomTypeException | NoAvailabilityException | IllegalArgumentException e) {
                // ✅ Graceful failure handling
                System.out.println("❌ Booking Failed for " + req.getCustomerName() + ": " + e.getMessage());
            } catch (Exception e) {
                // Catch-all safeguard
                System.out.println("⚠️ Unexpected error: " + e.getMessage());
            }
        }
    }
}

// ------------------- Main Class -------------------
public class BookMyStayApp {
    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(inventoryService);

        // Valid bookings
        bookingService.addRequest(new BookingRequest("Alice", "Single"));
        bookingService.addRequest(new BookingRequest("Bob", "Double"));

        // Invalid scenarios
        bookingService.addRequest(new BookingRequest("Charlie", "Luxury")); // Invalid type
        bookingService.addRequest(new BookingRequest("", "Single"));        // Empty name
        bookingService.addRequest(new BookingRequest("David", "Suite"));
        bookingService.addRequest(new BookingRequest("Eve", "Suite"));      // No availability

        // Process all bookings
        bookingService.processBookings();
    }
}