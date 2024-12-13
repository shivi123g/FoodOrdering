import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CanteenAppGUI extends JFrame {
    private JPanel menuPanel, ordersPanel;

    public CanteenAppGUI() {
        setTitle("Canteen Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize panels
        menuPanel = createMenuPanel();
        ordersPanel = createOrdersPanel();

        // Show orders panel by default
        setContentPane(menuPanel);
    }

    // Panel to display menu items
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        try (BufferedReader br = new BufferedReader(new FileReader(mess.MENU_FILE))) {
            String line;
            while ((line = br.readLine()) != null){
                String[] data = line.split(",");
                String category = data[0].trim();
                String name = data[1].trim();
                double price = Double.parseDouble(data[2].trim());
                boolean available = Boolean.parseBoolean(data[3].trim());
                JLabel categoryLabel = new JLabel(category);
                categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
                panel.add(categoryLabel);

                // Table for items in this category
                String[] columns = {"Item Name", "Price", "Available"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);
                JTable table = new JTable(model);

                model.addRow(new Object[]{name, price, available});

                // Add table to panel
                panel.add(new JScrollPane(table));

            }
        } catch (IOException e) {
            System.out.println("Error loading menu data: " + e.getMessage());
        }

        JButton toOrderButton = new JButton("View Orders");
        toOrderButton.addActionListener(e -> {
            setContentPane(ordersPanel);
            revalidate();
            repaint();
        });
        panel.add(toOrderButton, BorderLayout.SOUTH);
        return panel;
    }

    // Panel to display pending orders
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"Order Number", "Items Ordered", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        loadOrderData(model);  // Method to read data from file and populate the model
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton toMenuButton = new JButton("View Menu");
        toMenuButton.addActionListener(e -> {
            setContentPane(menuPanel);
            revalidate();
            repaint();
        });
        panel.add(toMenuButton, BorderLayout.SOUTH);
        return panel;
    }


    // Load order data
    private void loadOrderData(DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader(mess.ORDERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2]});
            }
        } catch (IOException e) {
            System.out.println("Error loading order data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Populate mess.menu with sample data if needed
        // For testing purposes, add data to mess.menu here or load from a file

        SwingUtilities.invokeLater(() -> new CanteenAppGUI().setVisible(true));
    }
}

