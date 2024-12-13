import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Cartoperationtest {
    private Cart cart;
    private Food food;
    private Food food1;

    @BeforeEach
    void setUp() {

        cart = new Cart();
        cart.clearCart();
        food = new Food("Spring roll",50,"Chinese",true);
        food1 = new Food("Fries",20,"Snacks",true);
        // Add some items to the cart for testing
        cart.addItem(food, 15);
    }

    @Test
    void testAddItemUpdatesTotalPrice() {
        // Add a new item to the cart
        cart.addItem(food1,3);

        // Calculate the expected total price
        double expectedTotal = (3 * 20) + (15 * 50);

        // Verify the total price is updated correctly
        assertEquals(expectedTotal, cart.calculateTotal());
    }

    @Test
    void testModifyQuantityRecalculatesTotalPrice() {
        // Modify the quantity of an existing item
        cart.updateItem(food,10);

        // Calculate the expected total price
        double expectedTotal = (10 * 50);

        // Verify the total price is recalculated correctly
        assertEquals(expectedTotal, cart.calculateTotal());
    }

    @Test
    void testNegativeQuantityIsPrevented() {
        // Attempt to set a negative quantity for an item
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cart.updateItem(food1, -1); // Invalid quantity
        });

        // Verify the exception message
        assertEquals("Quantity cannot be negative.", exception.getMessage());

        // Verify the total price remains unchanged
        double expectedTotal = (15 * 50.0);
        assertEquals(expectedTotal, cart.calculateTotal(), "Total price should remain unchanged for invalid updates.");
    }
}
