import java.util.*;

// ------------------- Booking Request -------------------
class BookingRequest {
    private String customerName;
    private String roomType;

    public BookingRequest(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }

    public String getCustomerName() { return customerName; }
    public String getRoomType() { return roomType; }
}

// ------------------- Thread-Safe Inventory -------------------
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single", 2);
        inventory.put("Double", 2);
    }

    // Critical section
    public synchronized boolean allocateRoom(String roomType) {
        int count = inventory.getOrDefault(roomType, 0);

        if (count <= 0) {
            return false;
        }

        // Simulate delay (to expose race condition if not synchronized)
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        inventory.put(roomType, count - 1);
        return true;
    }

    public synchronized void printInventory() {
        System.out.println("\n📦 Final Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// ------------------- Booking Processor (Runnable) -------------------
class BookingProcessor implements Runnable {

    private Queue<BookingRequest> queue;
    private InventoryService inventoryService;
    private Set<String> allocatedRooms;

    public BookingProcessor(Queue<BookingRequest> queue,
                            InventoryService inventoryService,
                            Set<String> allocatedRooms) {
        this.queue = queue;
        this.inventoryService = inventoryService;
        this.allocatedRooms = allocatedRooms;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request;

            // 🔒 Critical section: fetching from shared queue
            synchronized (queue) {
                if (queue.isEmpty()) {
                    return;
                }
                request = queue.poll();
            }

            processRequest(request);
        }
    }

    private void processRequest(BookingRequest req) {

        String threadName = Thread.currentThread().getName();

        boolean success = inventoryService.allocateRoom(req.getRoomType());

        if (success) {
            String roomId = generateRoomId(req.getRoomType());

            // 🔒 Critical section: unique room tracking
            synchronized (allocatedRooms) {
                allocatedRooms.add(roomId);
            }

            System.out.println("✅ [" + threadName + "] Booked for "
                    + req.getCustomerName() + " | Room: " + roomId);
        } else {
            System.out.println("❌ [" + threadName + "] Failed for "
                    + req.getCustomerName() + " (No Availability)");
        }
    }

    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" +
               UUID.randomUUID().toString().substring(0, 5);
    }
}

// ------------------- Main Class -------------------
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) throws InterruptedException {

        Queue<BookingRequest> bookingQueue = new LinkedList<>();
        InventoryService inventoryService = new InventoryService();
        Set<String> allocatedRooms = new HashSet<>();

        // Simulate multiple users
        bookingQueue.add(new BookingRequest("Alice", "Single"));
        bookingQueue.add(new BookingRequest("Bob", "Single"));
        bookingQueue.add(new BookingRequest("Charlie", "Single")); // extra
        bookingQueue.add(new BookingRequest("David", "Double"));
        bookingQueue.add(new BookingRequest("Eve", "Double"));
        bookingQueue.add(new BookingRequest("Frank", "Double"));   // extra

        // Create multiple threads
        Thread t1 = new Thread(new BookingProcessor(bookingQueue, inventoryService, allocatedRooms), "T1");
        Thread t2 = new Thread(new BookingProcessor(bookingQueue, inventoryService, allocatedRooms), "T2");
        Thread t3 = new Thread(new BookingProcessor(bookingQueue, inventoryService, allocatedRooms), "T3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        t1.join();
        t2.join();
        t3.join();

        // Final state
        inventoryService.printInventory();

        System.out.println("\n🧾 Allocated Room IDs:");
        for (String id : allocatedRooms) {
            System.out.println(id);
        }
    }
}