import java.util.*;

// Booking Request Model
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

// Inventory Service
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

    public void printInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// Booking Service
class BookingService {
    private Queue<BookingRequest> requestQueue = new LinkedList<>();
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> roomTypeMap = new HashMap<>();
    private InventoryService inventoryService;

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void addBookingRequest(BookingRequest request) {
        requestQueue.offer(request);
    }

    // Generate unique room ID
    private String generateRoomId(String roomType) {
        String roomId;
        do {
            roomId = roomType.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
        } while (allocatedRoomIds.contains(roomId));

        return roomId;
    }

    // Process Bookings (FIFO)
    public void processBookings() {
        while (!requestQueue.isEmpty()) {
            BookingRequest request = requestQueue.poll();
            String roomType = request.getRoomType();

            System.out.println("\nProcessing booking for: " + request.getCustomerName());

            // Check availability
            if (!inventoryService.isAvailable(roomType)) {
                System.out.println("❌ No rooms available for type: " + roomType);
                continue;
            }

            // Atomic allocation
            String roomId = generateRoomId(roomType);

            allocatedRoomIds.add(roomId);

            roomTypeMap.putIfAbsent(roomType, new HashSet<>());
            roomTypeMap.get(roomType).add(roomId);

            inventoryService.decrement(roomType);

            // Confirmation
            System.out.println("✅ Booking Confirmed!");
            System.out.println("Customer: " + request.getCustomerName());
            System.out.println("Room Type: " + roomType);
            System.out.println("Room ID: " + roomId);
        }
    }

    public void printAllocations() {
        System.out.println("\nRoom Allocations:");
        for (Map.Entry<String, Set<String>> entry : roomTypeMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// Main Class
public class BookMyStayApp {
    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(inventoryService);

        // Add booking requests (FIFO Queue)
        bookingService.addBookingRequest(new BookingRequest("Alice", "Single"));
        bookingService.addBookingRequest(new BookingRequest("Bob", "Double"));
        bookingService.addBookingRequest(new BookingRequest("Charlie", "Single"));
        bookingService.addBookingRequest(new BookingRequest("David", "Suite"));
        bookingService.addBookingRequest(new BookingRequest("Eve", "Suite")); // Should fail

        // Process bookings
        bookingService.processBookings();

        // Print results
        bookingService.printAllocations();
        inventoryService.printInventory();
    }
}