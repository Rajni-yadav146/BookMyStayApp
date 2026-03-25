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
}

// ------------------- Add-On Service -------------------
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
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

// ------------------- Booking Service -------------------
class BookingService {
    private Queue<BookingRequest> queue = new LinkedList<>();
    private Set<String> allocatedRoomIds = new HashSet<>();
    private InventoryService inventoryService;

    // Store reservations
    private Map<String, Reservation> reservations = new HashMap<>();

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void addRequest(BookingRequest request) {
        queue.offer(request);
    }

    private String generateRoomId(String roomType) {
        String id;
        do {
            id = roomType.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
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

            reservations.put(reservationId, reservation);

            System.out.println("✅ Booking Confirmed: " + reservationId +
                    " | Room: " + roomId +
                    " | Customer: " + req.getCustomerName());
        }
    }

    public Map<String, Reservation> getReservations() {
        return reservations;
    }
}

// ------------------- Add-On Service Manager -------------------
class AddOnServiceManager {

    // Map<ReservationId, List<Services>>
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add services to reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
    }

    // Get services for reservation
    public List<AddOnService> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total cost
    public double calculateTotalCost(String reservationId) {
        double total = 0;
        for (AddOnService s : getServices(reservationId)) {
            total += s.getCost();
        }
        return total;
    }

    public void printServices(String reservationId) {
        List<AddOnService> services = getServices(reservationId);

        System.out.println("\nAdd-On Services for " + reservationId + ":");
        for (AddOnService s : services) {
            System.out.println("- " + s);
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// ------------------- Main Class -------------------
public class BookMyStayApp {
    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(inventoryService);
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // Step 1: Create bookings
        bookingService.addRequest(new BookingRequest("Alice", "Single"));
        bookingService.addRequest(new BookingRequest("Bob", "Double"));

        bookingService.processBookings();

        // Step 2: Get reservation IDs
        Map<String, Reservation> reservations = bookingService.getReservations();

        // Step 3: Add services
        for (String resId : reservations.keySet()) {

            addOnManager.addService(resId, new AddOnService("Breakfast", 500));
            addOnManager.addService(resId, new AddOnService("Airport Pickup", 1200));

            // Print services
            addOnManager.printServices(resId);
        }
    }
}