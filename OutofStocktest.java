import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutofStocktest {
    private Customer customer;
    private Admin admin;
    private Food food;

    @BeforeEach
    void setUp() {
         admin = new Admin();
         customer= new Customer("Dhruv",true);
         food = new Food("Spring roll",50,"Chinese",true);
         admin.addFoodItem("Spring roll",50,"Chinese",true);
    }

    @Test
    void testOrderOutOfStockItem() {
        // Attempt to order an out-of-stock item
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customer.addToCart("Spring roll",5);
        });

        // Verify the error message
        assertEquals("Item is out of stock.", exception.getMessage());

        // Ensure the order is not added to the orders list
    }

    @Test
    void testOrderInStockItem() {
        // Attempt to order an in-stock item
        assertDoesNotThrow(() -> {
            customer.addToCart("Spring roll",2);
        });

        // Verify the order is added

    }
}