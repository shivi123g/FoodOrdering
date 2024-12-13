import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


class Food  implements Serializable {
    private String name;
    private double price;
    private boolean available;
    private int stock;
    private String category;
    private List<Review> reviews;

    public Food(String name, double price, String category,  boolean available)  {
        this.name = name;
        this.price = price;
        this.available = available;
        this.category = category;
        this.stock = 5;
    }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }

    public void setPrice(double price) { this.price = price; }
    public void setAvailability(boolean available) { this.available = available; }

    public void addReview(Review review) {
        reviews.add(review);
    }
    public int getStock() {
        return stock;
    }
    public boolean reduceStock(int quantity) {
        if (quantity <= stock) {
            stock -= quantity;
            return true;
        }
        return false;
    }

    // Method to get all reviews
    public List<Review> getReviews() {
        return new ArrayList<>(reviews); // Return a copy of the reviews list
    }
    @Override
    public String toString() {
        return "FoodItem{name='" + name + "', price=" + price + ", available=" + available + '}';
    }
}

enum OrderStatus {
    PENDING, PREPARING, OUT_FOR_DELIVERY, COMPLETED, DENIED, CANCELLED,REFUNDED;
}

class Customer implements Serializable {
    private String name;
    private Cart cart;
    private List<Order> orderHistory;
    private boolean isVIP;

    public Customer(String name, boolean isVIP) {
        this.name = name;
        this.isVIP = isVIP;
        this.cart = new Cart();
        this.orderHistory = new ArrayList<>();
    }

    public String getName() { return name; }
    public boolean isVIP() { return isVIP; }
    public void setOrderHistory(List<Order> orderHistory) { this.orderHistory = orderHistory; }

    public void upgradeToVIP() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Give 200 rupees to become VIP : ");
        int amount = scanner.nextInt();
        if (amount >= 200) { // Example amount
            this.isVIP = true;
            System.out.println("Congratulations, you are now a VIP customer!");
        }
    }
    public void viewMenu() {
        mess.menu.forEach((category, items) -> {
            System.out.println(category + ":");
            items.forEach(item -> System.out.println("  - " + item));
        });
    }
    public List<Food> searchMenu( String keyword) {
        List < Food> result = mess.menu.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("Search results for keyword '" + keyword + "':");
        result.forEach(System.out::println);

        return result;
    }

    public List<Food> filterByCategory (String category) {
        List < Food> result = mess.menu.getOrDefault(category, Collections.emptyList());

        System.out.println("Items in category '" + category + "':");
        result.forEach(System.out::println);

        return result;
    }

    // Sort menu items by price
    public List<Food> sortByPrice( boolean ascending) {
        List < Food> result = mess.menu.values().stream()
                .flatMap(Collection::stream)
                .sorted(ascending ? Comparator.comparing(Food::getPrice)
                        :Comparator.comparing(Food::getPrice).reversed())
                .collect(Collectors.toList());
        String order = ascending ? "ascending" : "descending";
        System.out.println("Menu items sorted by price in " + order + " order:");
        result.forEach(System.out::println);

        return result;

    }
    public void addToCart(String name,int quantity) {

        if (quantity <= 0) {
            System.out.println("Invalid quantity");
        }else{
            Optional <Food> foodItem = mess.menu.values().stream()
                    .flatMap(Collection::stream)
                    .filter(food -> food.getName().equalsIgnoreCase(name))
                    .findFirst();

            // If food item is found, add it to the cart; otherwise, notify the user
            if (foodItem.isPresent()) {
                if (foodItem.get().isAvailable()){
                    if (foodItem.get().getStock() >= quantity) {
                        if (foodItem.get().reduceStock(quantity)) {
                            cart.addItem(foodItem.get(), quantity);
                            System.out.println("Added " + quantity + " of " + name + " to the cart.");
                        }

                    }else {
                        throw new IllegalArgumentException("Item is out of stock.");
                    }

                }else{
                    throw new IllegalArgumentException("Item is not available.");

                }

            } else {
                throw new IllegalArgumentException("Item not found in the menu.");
            }

        }
        // Search for the food item in the menu

    }

    public void modifyCart() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Give  food name :");
        String name = scanner.nextLine();
        System.out.print("Give  food new quantity :");
        int newquantity = scanner.nextInt();
        // Search for the food item in the menu
        Optional <Food> foodItem = mess.menu.values().stream()
                .flatMap(Collection::stream)
                .filter(food -> food.getName().equalsIgnoreCase(name))
                .findFirst();

        // If food item is found, add it to the cart; otherwise, notify the user
        if (foodItem.isPresent()) {
            cart.updateItem(foodItem.get(), newquantity);
            System.out.println("Modified " + newquantity + " of " + name + " to the cart.");
        } else {
            System.out.println("Item not found in the menu.");
        }

    }
    public List <Order> getOrderHistory() {
        return orderHistory;
    }

    public void removeFromCart() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Give  food name :");
        String name = scanner.nextLine();
        // Search for the food item in the menu
        Optional <Food> foodItem = mess.menu.values().stream()
                .flatMap(Collection::stream)
                .filter(food -> food .getName().equalsIgnoreCase(name))
                .findFirst();

        // If food item is found, add it to the cart; otherwise, notify the user
        if (foodItem.isPresent()) {
            cart.removeItem(foodItem.get());
            System.out.println("Removed item from the cart. " );
        } else {
            System.out.println("Item not found in the menu.");
        }

    }

    public double viewCartTotal() {
        double result =  cart.calculateTotal();
        System.out.println("Total: " + result);
        return result;
    }

    public void checkout() {
        Order order = cart.placeOrder(this);
        mess.pendingOrders.add(order);
        if (order != null) {
            orderHistory.add(order);
            System.out.println("Order placed successfully with ID: " + order.getId());
        }
    }

    // Order tracking and history
    public void viewOrderStatus() {
        Order order = orderHistory.get(orderHistory.size() - 1);
        System.out.println("Order " + order.getId() + " is currently " + order.getStatus());
    }
    public void cancelOrder() {
        Order order = orderHistory.get(orderHistory.size() - 1);
        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PREPARING) {
            for(Order order1  : mess.pendingOrders) {
                if (order.getId() == order1.getId()) {
                    order.setStatus(OrderStatus.CANCELLED);
                }
            }
            System.out.println("Order " + order.getId() + " has been cancelled.");
            System.out.println("The amount paid will be refunded");
        }
        else{
            System.out.println("Order " + order.getId() + " can not be cancelled.");
        }
    }

    public void viewOrderHistory() {
        orderHistory.forEach(order -> System.out.println("Order Items: " + order.getItems() + ", Status: " + order.getStatus()));
    }

    // Item review functionality
    public void addReview() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Give  food name :");
        String name = scanner.nextLine();
        System.out.print("Give rating:");
        int rating = scanner.nextInt();
        System.out.print("Give review text :");
        String reviewText = scanner.nextLine();
        // Search for the food item in the menu
        Optional <Food> foodItem = mess.menu.values().stream()
                .flatMap(Collection::stream)
                .filter(food -> food.getName().equalsIgnoreCase(name))
                .findFirst();

        // If food item is found, add it to the cart; otherwise, notify the user
        if (foodItem.isPresent()) {
            Food food = foodItem.get();
            food.addReview(new Review(name, reviewText, rating));
        } else {
            System.out.println("Item not found in the menu.");
        }


    }

    public void viewReviews() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Give  food name :");
        String name = scanner.nextLine();
        Optional <Food> foodItem = mess.menu.values().stream()
                .flatMap(Collection::stream)
                .filter(food -> food.getName().equalsIgnoreCase(name))
                .findFirst();
        if (foodItem.isPresent()) {
            Food food = foodItem.get();
            System.out.println("Reviews for " + food.getName() + ":");
            for (Review review : food.getReviews()) {
                System.out.println(review);
            }

        }
    }



    @Override
    public String toString() { return "Customer{name='" + name + "', isVIP=" + isVIP + '}'; }
}


class Order implements Serializable {
    private static int idCounter = 0;
    private int id;
    private Customer customer;
    private Instant timestamp;
    private List<OrderItem> items;
    private String Request;
    private OrderStatus status;

    public Order(Customer customer, List<OrderItem> items, String Request) {
        this.id = idCounter++;
        this.customer = customer;
        this.items = items;
        this.Request = Request;
        this.status = OrderStatus.PENDING;
        this.timestamp = Instant.now();
    }

    public Customer getCustomer() { return customer; }
    public List<OrderItem> getItems() { return items; }
    public String getRequest() { return Request; }
    public OrderStatus getStatus() { return status; }
    public int getId() { return id; }
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setStatus(OrderStatus status) { this.status = status; }

    public double calculateTotalCost() {
        return items.stream()
                .mapToDouble(item -> item.getFood().getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", customer=" + customer + ", items=" + items + ", specialRequest='" + Request + "', status=" + status + '}';
    }
}

class OrderItem implements Serializable {
    private Food food;
    private int quantity;

    public OrderItem(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() {
        return food;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return food.getName() + " x" + quantity;
    }
}

class Cart implements Serializable{
    private Map<Food, Integer> items;
    private Customer customer;
    private String cartFile;

    public Cart() {
        this.items = new HashMap<>();
        cartFile = "cart.ser";


    }

    public void addItem(Food food, int quantity) {
        items.put(food, items.getOrDefault(food, 0) + quantity);
        saveCartToFile();
    }

    public void updateItem(Food food, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
        if (items.containsKey(food)) {
            items.put(food, quantity);
            saveCartToFile();
        }
    }

    public void removeItem(Food food) {
        items.remove(food);
        saveCartToFile();
    }

    public double calculateTotal() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    // Checkout process to place an order
    public Order placeOrder(Customer customer) {
        if (items.isEmpty()) {
            System.out.println("Cart is empty. Please add items before checking out.");
            return null;
        }
        List<OrderItem> orderItems = items.entrySet().stream()
                .map(entry -> new OrderItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        Scanner scanner = new Scanner(System.in);
        System.out.print("What is your request? : ");
        String request =  scanner.nextLine();
        clearCart(); // Clear the cart after placing an order
        saveCartToFile();
        return new Order(customer, orderItems, request);


    }

    void clearCart() {
        items.clear();
        saveCartToFile();

    }

    // Save the cart to a file
    private void saveCartToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cartFile))) {
            oos.writeObject(this.items);
            System.out.println("Cart saved to file.");
        } catch (IOException e) {
            System.out.println("Error saving cart to file: " + e.getMessage());
        }
    }

    // Load the cart from a file
    private void loadCartFromFile() {
        File file = new File(cartFile);
        if (!file.exists()) {
            System.out.println("No previous cart data found.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cartFile))) {
            items = (Map<Food, Integer>) ois.readObject();
            System.out.println("Cart loaded from file.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading cart from file: " + e.getMessage());
        }
    }


}
class Review implements Serializable {
    private String customerName;
    private String reviewText;
    private int rating; // Rating out of 5

    public Review(String customerName, String reviewText, int rating) {
        this.customerName = customerName;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Review by " + customerName + " - Rating: " + rating + "/5 - " + reviewText;
    }
}



class Admin {
    private double totalRefundAmount;
    public static PriorityQueue<Order> processedOrders; // Queue for completed orders
    public static PriorityQueue<Order> canceledOrders;

    public Admin() {
        this.totalRefundAmount = 0.0;

        this.processedOrders = new PriorityQueue<>(Comparator.comparing(Order::getTimestamp));
        this.canceledOrders = new PriorityQueue<>(Comparator.comparing(Order::getTimestamp));


    }
    public void refundforcancel(){
        for(Order order : mess.pendingOrders) {
            if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DENIED) {
                processRefund(order);
                mess.pendingOrders.remove(order);
                canceledOrders.remove(order);
                System.out.println("Refund for cancelled order: " + order);

            }
        }

    }


    public void addFoodItem(String name, double price, String category, boolean available) {
        mess.menu.computeIfAbsent(category, k -> new ArrayList<>()).add(new Food(name, price, category, available));
    }

    public void updateFoodItem(String name, double price, boolean available) {
        mess.menu.values().forEach(items -> items.stream()
                .filter(item -> item.getName().equals(name))
                .forEach(item -> {
                    item.setPrice(price);
                    item.setAvailability(available);
                })
        );
    }

    public void removeFoodItem(String name) {
        mess.menu.values().forEach(items -> items.removeIf(item -> item.getName().equals(name)));
        for (Order order : mess.pendingOrders) {
            if (order.getItems().stream().anyMatch(i -> i.getFood().getName().equals(name))) {
                order.setStatus(OrderStatus.DENIED);
                processRefund(order);// Process refund for denied orders
                canceledOrders.add(order);
                mess.pendingOrders.remove(order);
            }
        }
    }

    public void updateOrderStatus(int orderId, OrderStatus status) {
        for (Order order : mess.pendingOrders) {
            if (order.getId() == orderId) {
                order.setStatus(status);
                if (status == OrderStatus.CANCELLED || status == OrderStatus.DENIED) {
                    processRefund(order);
                    canceledOrders.add(order);  // Add to canceled orders queue
                    mess.pendingOrders.remove(order);
                } else if (status == OrderStatus.COMPLETED) {
                    processedOrders.add(order);
                    mess.pendingOrders.remove(order);
                }
            }
        }
    }


    public static void displayMenuByCategory() {
        if (mess.menu.isEmpty()) {
            System.out.println("Menu is empty.");
            return;
        }

        System.out.println("Menu:");
        mess.menu.forEach((category, foodList) -> {
            System.out.println("\nCategory: " + category);
            foodList.forEach(food -> System.out.println("  " + food));
        });
    }

    // Method to process refunds for canceled or denied orders
    private void processRefund(Order order) {
        double refundAmount = order.calculateTotalCost();
        totalRefundAmount += refundAmount;
        order.setStatus(OrderStatus.REFUNDED);
        System.out.println("Refund processed for Order ID: " + order.getId() + ", Amount: " + refundAmount);
    }

    // Generate a daily report including total refund amount
    public void generateDailyReport() {
        int totalPendingOrders = mess.pendingOrders.size();
        int totalProcessedOrders = processedOrders.size();
        int totalCanceledOrders = canceledOrders.size();

        // Calculate total sales for processed orders
        double totalSales = processedOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToDouble(item -> item.getFood().getPrice() * item.getQuantity())
                .sum();

        // Track food item frequency for popularity
        Map<Food, Integer> itemFrequencyMap = new HashMap<>();

        for (Order order : processedOrders) {
            for (OrderItem item : order.getItems()) {
                Food food = item.getFood();
                int quantity = item.getQuantity();
                itemFrequencyMap.put(food, itemFrequencyMap.getOrDefault(food, 0) + quantity);
            }
        }

        // Find the most popular item
        Food mostPopularItem = null;
        int maxCount = 0;
        for (Map.Entry<Food, Integer> entry : itemFrequencyMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostPopularItem = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        // Print the daily report
        System.out.println("Daily Report:");
        System.out.println("Total Pending Orders: " + totalPendingOrders);
        System.out.println("Total Processed Orders: " + totalProcessedOrders + ", Total Sales: ₹" + totalSales);
        System.out.println("Total Canceled Orders: " + totalCanceledOrders + ", Total Refunds Issued: ₹" + totalRefundAmount);

        if (mostPopularItem != null) {
            System.out.println("Most Popular Item: " + mostPopularItem.getName() + " (Ordered " + maxCount + " times)");
        } else {
            System.out.println("No items ordered today.");
        }
    }
}
class DataPersistence {

    public static void saveMenu(TreeMap<String, List<Food>> menu) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("neww_menu.ser"))) {
            oos.writeObject(menu);
            System.out.println("Menu saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TreeMap<String, List<Food>> loadMenu() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("neww_menu.ser"))) {
            return (TreeMap<String, List<Food>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new TreeMap<>();
        }
    }

    public static void saveOrderHistory(Customer customer) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(customer.getName() + "_newworderHistory.ser"))) {
            oos.writeObject(customer.getOrderHistory());
            System.out.println("Order history for " + customer.getName() + " saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Order> loadOrderHistory(Customer customer) {
        File file = new File(customer.getName() + "_newworderHistory.ser");

        // Check if the file exists
        if (!file.exists()) {
            System.out.println("Order history file not found for " + customer.getName() + ". Returning empty order history.");
            return new ArrayList<>();  // Return an empty list if the file is missing
        }

        // Attempt to load the order history if the file exists
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Order>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static PriorityQueue<Order> loadCancelledOrders() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("neww_cancelled_orders.ser"))) {
            return (PriorityQueue<Order>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous cancelled orders found. Starting with an empty queue.");
            return new PriorityQueue<>(Comparator.comparing((Order o) -> !o.getCustomer().isVIP())
                    .thenComparing(Order::getTimestamp));
        }
    }

    public static void saveCancelledOrders(PriorityQueue<Order> pendingOrders) {
        // Filter cancelled orders
        PriorityQueue<Order> cancelledOrders = pendingOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.CANCELLED)
                .collect(Collectors.toCollection(() ->
                        new PriorityQueue<>(Comparator.comparing((Order o) -> !o.getCustomer().isVIP())
                                .thenComparing(Order::getTimestamp))));

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("neww_cancelled_orders.ser"))) {
            oos.writeObject(cancelledOrders);
        } catch (IOException e) {
            System.out.println("Error saving cancelled orders: " + e.getMessage());
        }
    }

}

public class mess {
    public static PriorityQueue<Order> pendingOrders = new PriorityQueue<>(
            Comparator.comparing((Order o) -> !o.getCustomer().isVIP())
                    .thenComparing(Order::getTimestamp)
    );
    public static final String ORDERS_FILE = "orders.csv";
    public static TreeMap<String, List<Food>> menu = new TreeMap<>();
    public static final String MENU_FILE = "menu.csv";

    public static void saveMenu() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MENU_FILE))) {
            for (Map.Entry<String, List<Food>> entry : menu.entrySet()) {
                String category = entry.getKey();
                for (Food item : entry.getValue()) {
                    bw.write(category + "," + item.getName() + "," + item.getPrice() + "," + item.isAvailable());
                    bw.newLine();
                }
            }
            System.out.println("Menu saved successfully to " + MENU_FILE);
        } catch (IOException e) {
            System.out.println("Error saving menu to file: " + e.getMessage());
        }
    }





    // Save current orders to file
    public static void saveOrdersToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : pendingOrders) {
                bw.write(order.getId() + "," + order.getCustomer().getName() + "," +
                        order.getStatus());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving orders to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        menu = DataPersistence.loadMenu();
        pendingOrders = DataPersistence.loadCancelledOrders();
        saveMenu();
        saveOrdersToFile();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("You are\n" +
                    "1. Customer\n" +
                    "2. Admin\n" +
                    "3. Exit\n" +
                    "Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                Customer customer;
                System.out.print("Enter customer name: ");
                String customerName = scanner.nextLine();
                System.out.print("Are you VIP?[yes/no] ");
                String vip = scanner.nextLine();
                if(vip == "yes"){
                    customer = new Customer(customerName, true);
                    customer.setOrderHistory(DataPersistence.loadOrderHistory(customer));
                }
                else{
                    customer = new Customer(customerName, false);
                    customer.setOrderHistory(DataPersistence.loadOrderHistory(customer));
                }


                while (true) {
                    System.out.println("1. View Menu");
                    System.out.println("2. Upgrade to VIP");
                    System.out.println("3. Filter by category");
                    System.out.println("4. Sort by price");
                    System.out.println("5. Search an item");
                    System.out.println("6. Add an item to cart");
                    System.out.println("7. Modify Cart");
                    System.out.println("8. Remove an item from cart");
                    System.out.println("9. View Cart Total");
                    System.out.println("10. Place order");
                    System.out.println("11. View Order Status");
                    System.out.println("12. Cancel order");
                    System.out.println("13. View past orders");
                    System.out.println("14. Add a review for food");
                    System.out.println("15. View a review for food");
                    System.out.println("16. Exit");

                    System.out.print("What do you want to do? ");
                    int customerChoice = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    switch (customerChoice) {
                        case 1 -> customer.viewMenu();
                        case 2 -> customer.upgradeToVIP();
                        case 3 -> {
                            System.out.print("Enter category: ");
                            String category = scanner.nextLine();
                            customer.filterByCategory(category);
                        }
                        case 4 -> {
                            System.out.println("1. Low to High");
                            System.out.println("2. High to Low");
                            int sortChoice = scanner.nextInt();
                            scanner.nextLine();
                            customer.sortByPrice(sortChoice == 1);
                        }
                        case 5 -> {
                            System.out.print("Enter item name: ");
                            String itemName = scanner.nextLine();
                            customer.searchMenu(itemName);
                        }
                        case 6 -> {
                            System.out.print("Give  food name :");
                            String name = scanner.nextLine();
                            System.out.print("Give  food quantity :");
                            int quantity = scanner.nextInt();
                            customer.addToCart(name,quantity);
                        }
                        case 7 -> customer.modifyCart();
                        case 8 -> {
                            customer.removeFromCart();
                        }
                        case 9 -> customer.viewCartTotal();
                        case 10 -> {
                            customer.checkout();
                            saveOrdersToFile();
                        }

                        case 11 -> customer.viewOrderStatus();
                        case 12 -> {
                            customer.cancelOrder();
                            saveOrdersToFile();
                        }
                        case 13 -> customer.viewOrderHistory();
                        case 14 -> {
                            customer.addReview();
                        }
                        case 15 -> {
                            customer.viewReviews();
                        }
                        case 16 -> {
                            DataPersistence.saveOrderHistory(customer);
                            DataPersistence.saveCancelledOrders(pendingOrders);
                            System.out.println("Exiting customer menu.");
                            break;
                        }
                        default -> System.out.println("Invalid option. Please try again.");
                    }
                    if (customerChoice == 16) break;
                }
            } else if (choice == 2) {
                Admin admin = new Admin();

                // Placeholder for Admin interface logic
                while (true) {
                    System.out.println("1. Add a new food item to menu");
                    System.out.println("2.  Update Food Item");
                    System.out.println("3. Remove Food Item");
                    System.out.println("4. Update order status");
                    System.out.println("5. Display Menu");
                    System.out.println("6. See the daily report");
                    System.out.println("7. Refund for all cancel orders");
                    System.out.println("8. Exit");
                    System.out.print("What do you want to do? ");
                    int adminChoice = scanner.nextInt();
                    scanner.nextLine();
                    switch (adminChoice) {
                        case 1 -> {
                            System.out.print("Enter food name: ");
                            String foodName = scanner.nextLine();
                            System.out.print("Enter food price: ");
                            double foodPrice = scanner.nextDouble();
                            scanner.nextLine();
                            System.out.print("Enter category: ");
                            String category = scanner.nextLine();
                            admin.addFoodItem(foodName, foodPrice, category, true);
                            saveMenu();
                        }
                        case 2 -> {
                            System.out.print("Enter food name: ");
                            String foodName = scanner.nextLine();
                            System.out.print("Enter food new price: ");
                            int newFoodPrice = scanner.nextInt();
                            System.out.print("Enter availability [True/False]: ");
                            boolean availability = scanner.nextBoolean();
                            admin.updateFoodItem(foodName, newFoodPrice, availability);
                            saveMenu();
                        }
                        case 3 -> {
                            System.out.print("Enter food name: ");
                            String foodName = scanner.nextLine();
                            admin.removeFoodItem(foodName);
                            saveMenu();
                        }
                        case 4 -> {
                            System.out.print("Enter order id: ");
                            int orderId = scanner.nextInt();
                            System.out.println(" Choose the status of the order ");
                            System.out.println("1. PREPARING");
                            System.out.println("2. OUT_FOR_DELIVERY");
                            System.out.println("3. COMPLETED");
                            System.out.println("4. CANCELED");
                            System.out.println("5. DENIED");
                            int choose = scanner.nextInt();
                            scanner.nextLine();
                            switch (choose) {
                                case 1 -> {
                                    admin.updateOrderStatus(orderId, OrderStatus.PREPARING);
                                    saveOrdersToFile();

                                }
                                case 2 -> {
                                    admin.updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY);
                                    saveOrdersToFile();

                                }
                                case 3 -> {
                                    admin.updateOrderStatus(orderId, OrderStatus.COMPLETED);
                                    saveOrdersToFile();
                                }
                                case 4 -> {
                                    admin.updateOrderStatus(orderId, OrderStatus.CANCELLED);
                                    saveOrdersToFile();
                                }
                                case 5 -> {
                                    admin.updateOrderStatus(orderId, OrderStatus.DENIED);
                                    saveOrdersToFile();

                                }
                            }


                        }
                        case 5 -> {
                            admin.displayMenuByCategory();
                        }
                        case 6 -> {
                            admin.generateDailyReport();
                            ;
                        }
                        case 7 -> {
                            admin.refundforcancel();
                            saveOrdersToFile();
                        }
                        case 8 -> {
                            DataPersistence.saveMenu(menu);
                            DataPersistence.saveCancelledOrders(pendingOrders);
                            System.out.println("Exiting admin menu.");
                            break;
                        }
                        default -> System.out.println("Invalid option. Please try again.");
                    }
                    if (adminChoice == 8) break;
                }





            } else if (choice == 3) {
                System.out.println("Thank you for using the system. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please select a valid option.");
            }
        }
        scanner.close();
    }
}







