/* Name: Arjun Bhatheja
 * Course: CNT 4714 – Fall 2025
 * Assignment title: Project 1 – An Event-driven Enterprise Simulation
 * Date: Sunday September 7, 2025
 */
package DevelopmentalVersion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NileDotComGUI extends JFrame {
    // Inner class to store item details
    private static class CartItem {
        String itemID;
        String description;
        double unitPrice;
        int quantity;
        double discount;
        double subtotal;
        
        public CartItem(String itemID, String description, double unitPrice, int quantity, double discount, double subtotal) {
            this.itemID = itemID;
            this.description = description;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.discount = discount;
            this.subtotal = subtotal;
        }
        
        @Override
        public String toString() {
            DecimalFormat currency = new DecimalFormat("$#,##0.00");
            DecimalFormat percentage = new DecimalFormat("0%");
            return String.format("%s \"%s\" %s %d %s %s", 
                itemID, description, currency.format(unitPrice), quantity, 
                percentage.format(discount), currency.format(subtotal));
        }
        
        // Format for cart display
        public String toCartString(int itemNumber) {
            DecimalFormat currency = new DecimalFormat("$#,##0.00");
            return String.format("Item %d - SKU: %s, Desc: \"%s\", Price Ea. %s, Qty: %d, Total: %s", 
                itemNumber, itemID, description, currency.format(unitPrice), quantity, currency.format(subtotal));
        }
    }
    
    // Frame dimensions
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    
    // Constants
    private static final double TAX_RATE = 0.06;
    private static final double DISCOUNT_FOR_5_TO_9 = 0.10;
    private static final double DISCOUNT_FOR_10_TO_14 = 0.15;
    private static final double DISCOUNT_FOR_15_PLUS = 0.20;
    private static final int MAX_CART_SIZE = 5;
    
    // Application state variables
    private static int itemCount = 1;
    private static double orderSubtotal = 0.0;
    private static CartItem[] cartItems = new CartItem[MAX_CART_SIZE];
    private static int cartSize = 0;
    private static CartItem currentItem = null; // Store current item being processed
    
    // GUI Components
    private JLabel itemIDLabel;
    private JLabel quantityLabel;
    private JLabel detailsLabel;
    private JLabel subtotalLabel;
    private JLabel cartStatusLabel;
    
    private JTextField itemIDField;
    private JTextField quantityField;
    private JTextField detailsField;
    private JTextField subtotalField;
    
    private JTextArea cartArea;
    
    private JButton searchButton;
    private JButton addToCartButton;
    private JButton deleteLastButton;
    private JButton checkOutButton;
    private JButton newOrderButton;
    private JButton exitButton;
    
    // Event handlers
    private SearchButtonHandler searchHandler;
    private AddToCartButtonHandler addHandler;
    private DeleteButtonHandler deleteHandler;
    private CheckOutButtonHandler checkOutHandler;
    private NewOrderButtonHandler newOrderHandler;
    private ExitButtonHandler exitHandler;
    
    // Formatters
    private DecimalFormat currency = new DecimalFormat("$#,##0.00");
    private DecimalFormat percentage = new DecimalFormat("0%");
    
    public NileDotComGUI() {
        setTitle("Nile.Com - FALL 2025");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize components
        initializeComponents();
        
        // Create layout
        createLayout();
        
        // Initialize button states
        updateButtonStates();
        
        // Center the frame
        centerFrame();
    }
    
    private void initializeComponents() {
        // Initialize labels
        itemIDLabel = new JLabel("Enter item ID for Item #" + itemCount + ":", SwingConstants.RIGHT);
        quantityLabel = new JLabel("Enter quantity for Item #" + itemCount + ":", SwingConstants.RIGHT);
        detailsLabel = new JLabel("Details for Item #" + itemCount + ":", SwingConstants.RIGHT);
        subtotalLabel = new JLabel("Current Subtotal for " + cartSize + " item(s):", SwingConstants.RIGHT);
        cartStatusLabel = new JLabel("Your Shopping Cart Is Currently Empty", SwingConstants.CENTER);
        
        // Initialize text fields
        itemIDField = new JTextField();
        quantityField = new JTextField();
        detailsField = new JTextField();
        detailsField.setEditable(false);
        detailsField.setBackground(Color.LIGHT_GRAY); // Always light grey since always disabled
        subtotalField = new JTextField(currency.format(orderSubtotal));
        subtotalField.setEditable(false);
        subtotalField.setBackground(Color.LIGHT_GRAY); // Always light grey since always disabled
        
        // Initialize cart area
        cartArea = new JTextArea(6, 50);
        cartArea.setEditable(false);
        cartArea.setBackground(Color.WHITE);
        
        // Initialize buttons
        searchButton = new JButton("Search For Item #" + itemCount);
        addToCartButton = new JButton("Add Item #" + itemCount + " To Cart");
        deleteLastButton = new JButton("Delete Last Item From Cart");
        checkOutButton = new JButton("Check Out");
        newOrderButton = new JButton("Empty Cart - Start A New Order");
        exitButton = new JButton("Exit (Close App)");
        
                // Set button size and padding
        Dimension buttonSize = new Dimension(180, 35);
        searchButton.setPreferredSize(buttonSize);
        addToCartButton.setPreferredSize(buttonSize);
        deleteLastButton.setPreferredSize(buttonSize);
        checkOutButton.setPreferredSize(buttonSize);
        newOrderButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        // Initialize event handlers
        searchHandler = new SearchButtonHandler();
        addHandler = new AddToCartButtonHandler();
        deleteHandler = new DeleteButtonHandler();
        checkOutHandler = new CheckOutButtonHandler();
        newOrderHandler = new NewOrderButtonHandler();
        exitHandler = new ExitButtonHandler();
        
        // Register event handlers
        searchButton.addActionListener(searchHandler);
        addToCartButton.addActionListener(addHandler);
        deleteLastButton.addActionListener(deleteHandler);
        checkOutButton.addActionListener(checkOutHandler);
        newOrderButton.addActionListener(newOrderHandler);
        exitButton.addActionListener(exitHandler);
        
        // Set colors for labels
        itemIDLabel.setForeground(Color.YELLOW);
        quantityLabel.setForeground(Color.YELLOW);
        detailsLabel.setForeground(Color.RED);
        subtotalLabel.setForeground(Color.CYAN);
        cartStatusLabel.setForeground(Color.RED);
    }
    
    private void createLayout() {
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        
        // Create panels
        JPanel northPanel = new JPanel(new GridLayout(4, 2, 8, 4));
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel southPanel = new JPanel(new GridLayout(3, 2, 15, 12)); // Increased spacing between buttons
        
        // Add external spacing around the button panel
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // top, left, bottom, right
        
        // North panel - Input area
        northPanel.add(itemIDLabel);
        northPanel.add(itemIDField);
        northPanel.add(quantityLabel);
        northPanel.add(quantityField);
        northPanel.add(detailsLabel);
        northPanel.add(detailsField);
        northPanel.add(subtotalLabel);
        northPanel.add(subtotalField);
        
        // Center panel - Shopping cart
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.add(cartStatusLabel, BorderLayout.NORTH);
        cartPanel.add(new JScrollPane(cartArea), BorderLayout.CENTER);
        centerPanel.add(cartPanel, BorderLayout.CENTER);
        
        // South panel - Buttons
        southPanel.add(searchButton);
        southPanel.add(addToCartButton);
        southPanel.add(deleteLastButton);
        southPanel.add(checkOutButton);
        southPanel.add(newOrderButton);
        southPanel.add(exitButton);
        
        // Set panel colors
        northPanel.setBackground(Color.DARK_GRAY);
        centerPanel.setBackground(Color.LIGHT_GRAY);
        southPanel.setBackground(new Color(139, 69, 19)); // Brown color
        
        // Add panels to main container
        pane.add(northPanel, BorderLayout.NORTH);
        pane.add(centerPanel, BorderLayout.CENTER);
        pane.add(southPanel, BorderLayout.SOUTH);
    }
    
    private void updateButtonStates() {
        if (cartSize >= MAX_CART_SIZE) {
            searchButton.setEnabled(false);
            addToCartButton.setEnabled(false);
            itemIDField.setEditable(false);
            itemIDField.setBackground(Color.LIGHT_GRAY); // Light grey when disabled
            quantityField.setEditable(false);
            quantityField.setBackground(Color.LIGHT_GRAY); // Light grey when disabled
        } else {
            searchButton.setEnabled(true);
            itemIDField.setEditable(true);
            itemIDField.setBackground(Color.WHITE); // White when enabled
            quantityField.setEditable(true);
            quantityField.setBackground(Color.WHITE); // White when enabled

            addToCartButton.setEnabled(false);
        }
        
        deleteLastButton.setEnabled(cartSize > 0);
        checkOutButton.setEnabled(cartSize > 0);
        newOrderButton.setEnabled(true);
        exitButton.setEnabled(true);
    }
    
    private void centerFrame() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - WIDTH) / 2;
        int y = (screen.height - HEIGHT) / 2;
        setBounds(x, y, WIDTH, HEIGHT);
    }
    
    // Event handler classes
    private class SearchButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String itemID = itemIDField.getText().trim();
            String quantityStr = quantityField.getText().trim();
            
            if (itemID.isEmpty() || quantityStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both item ID and quantity", 
                    "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantity must be a positive number", 
                        "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid quantity", 
                    "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            searchForItem(itemID, quantity);
        }
    }
    
    private class AddToCartButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Check if cart is full
            if (cartSize >= MAX_CART_SIZE) {
                JOptionPane.showMessageDialog(null, 
                    "Shopping cart is full. Cannot add more items. Please check out or delete an item.", 
                    "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Add current item to cart
            if (currentItem != null && cartSize < MAX_CART_SIZE) {
                cartItems[cartSize] = currentItem;
                cartSize++;
                
                // Update order subtotal when item is actually added to cart
                orderSubtotal += currentItem.subtotal;
                
                // Update cart display
                updateCartDisplay();
                
                // Move to next item for input labels
                itemCount++;
                updateLabelsForNextItem();
                
                // Clear input fields but keep details showing the item just added
                itemIDField.setText("");
                quantityField.setText("");
                // Don't clear detailsField - keep showing the item that was just added
                
                // Clear current item reference
                currentItem = null;
                
                // Update button states
                if (cartSize < MAX_CART_SIZE) {
                    searchButton.setEnabled(true);
                }
                addToCartButton.setEnabled(false);
                
                // Re-enable delete and checkout buttons based on cart contents
                deleteLastButton.setEnabled(cartSize > 0);
                checkOutButton.setEnabled(cartSize > 0);
                
                updateButtonStates();
            }
        }
    }
    
    private class DeleteButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (cartSize > 0) {
                // Get the last item and remove its subtotal
                CartItem lastItem = cartItems[cartSize - 1];
                orderSubtotal -= lastItem.subtotal;
                
                // Remove last item from cart
                cartSize--;
                cartItems[cartSize] = null;
                itemCount--;
                
                // Update displays
                updateCartDisplay();
                updateLabelsForNextItem();
                
                // Clear input fields
                itemIDField.setText("");
                quantityField.setText("");
                detailsField.setText("");
                
                // Clear any currently searched item since we're going back
                currentItem = null;
                
                // Update details label to reflect the correct item number after deletion
                detailsLabel.setText("Details for Item #" + itemCount + ":");
                
                // Re-enable search button and disable add button since no item is currently searched
                searchButton.setEnabled(true);
                addToCartButton.setEnabled(false);
                
                // Re-enable editing if cart was full
                if (cartSize < MAX_CART_SIZE) {
                    itemIDField.setEditable(true);
                    itemIDField.setBackground(Color.WHITE); // White when enabled
                    quantityField.setEditable(true);
                    quantityField.setBackground(Color.WHITE); // White when enabled
                }
                
                // Update button states
                updateButtonStates();
            }
        }
    }
    
    private class CheckOutButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (cartSize > 0) {
                generateInvoice();
                writeTransactionToFile();
                
                // Disable input fields and some buttons after checkout
                itemIDField.setEditable(false);
                itemIDField.setBackground(Color.LIGHT_GRAY); // Light grey when disabled
                quantityField.setEditable(false);
                quantityField.setBackground(Color.LIGHT_GRAY); // Light grey when disabled
                searchButton.setEnabled(false);
                addToCartButton.setEnabled(false);
                deleteLastButton.setEnabled(false);
                checkOutButton.setEnabled(false);
            }
        }
    }
    
    private class NewOrderButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Reset everything
            cartSize = 0;
            itemCount = 1;
            orderSubtotal = 0.0;
            
            // Clear arrays
            for (int i = 0; i < cartItems.length; i++) {
                cartItems[i] = null;
            }
            
            // Reset GUI
            itemIDField.setText("");
            quantityField.setText("");
            detailsField.setText("");
            itemIDField.setEditable(true);
            itemIDField.setBackground(Color.WHITE); // White when enabled
            quantityField.setEditable(true);
            quantityField.setBackground(Color.WHITE); // White when enabled
            
            // Reset current item
            currentItem = null;
            
            updateCartDisplay();
            updateLabelsForNextItem();
            
            // Make sure details label is updated to Item #1
            detailsLabel.setText("Details for Item #" + itemCount + ":");
            
            updateButtonStates();
        }
    }
    
    private class ExitButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    
    // Helper methods
    private void searchForItem(String itemID, int quantity) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("inventory.csv"));
            String line;
            boolean found = false;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String fileItemID = parts[0].trim();
                    if (fileItemID.equals(itemID)) {
                        found = true;
                        String description = parts[1].trim().replace("\"", "");
                        boolean inStock = Boolean.parseBoolean(parts[2].trim());
                        int quantityOnHand = Integer.parseInt(parts[3].trim());
                        double unitPrice = Double.parseDouble(parts[4].trim());
                        
                        if (!inStock || quantityOnHand == 0) {
                            JOptionPane.showMessageDialog(null, 
                                "Sorry... that item is out of stock, please try another item", 
                                "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
                            itemIDField.setText("");
                            quantityField.setText("");
                            return;
                        }
                        
                        if (quantity > quantityOnHand) {
                            JOptionPane.showMessageDialog(null, 
                                "Insufficient inventory. Only " + quantityOnHand + " on hand. Please reduce quantity.", 
                                "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
                            quantityField.setText("");
                            return;
                        }
                        
                        // Calculate discount
                        double discount = 0.0;
                        if (quantity >= 15) discount = DISCOUNT_FOR_15_PLUS;
                        else if (quantity >= 10) discount = DISCOUNT_FOR_10_TO_14;
                        else if (quantity >= 5) discount = DISCOUNT_FOR_5_TO_9;
                        
                        double subtotal = quantity * unitPrice * (1 - discount);
                        // Don't update orderSubtotal here - only update when item is added to cart
                        
                        // Create and store the current item
                        currentItem = new CartItem(itemID, description, unitPrice, quantity, discount, subtotal);
                        
                        // Update details label and field for the item being searched
                        detailsLabel.setText("Details for Item #" + itemCount + ":");
                        detailsField.setText(currentItem.toString());
                        
                        // Don't update subtotal field here - keep showing current cart total
                        // subtotalField.setText(currency.format(orderSubtotal));
                        
                        // Enable add to cart button, disable search button
                        searchButton.setEnabled(false);
                        addToCartButton.setEnabled(true);
                        
                        // Keep delete and checkout buttons enabled based on current cart contents
                        // Don't disable them during search
                        deleteLastButton.setEnabled(cartSize > 0);
                        checkOutButton.setEnabled(cartSize > 0);
                        
                        break;
                    }
                }
            }
            
            if (!found) {
                JOptionPane.showMessageDialog(null, 
                    "Item ID " + itemID + " not in file", 
                    "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
                itemIDField.setText("");
                quantityField.setText("");
            }
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error: File not found", 
                "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: Problem reading from file", 
                "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ex) {
                // Ignore
            }
        }
    }
    
    private void updateCartDisplay() {
        if (cartSize == 0) {
            cartStatusLabel.setText("Your Shopping Cart Is Currently Empty");
            cartArea.setText("");
        } else {
            cartStatusLabel.setText("Your Shopping Cart Contains " + cartSize + " Item(s):");
            StringBuilder cartText = new StringBuilder();
            for (int i = 0; i < cartSize; i++) {
                cartText.append(cartItems[i].toCartString(i + 1)).append("\n");
            }
            cartArea.setText(cartText.toString());
        }
        
        // Update subtotal label
        subtotalLabel.setText("Current Subtotal for " + cartSize + " item(s):");
        subtotalField.setText(currency.format(orderSubtotal));
    }
    
    private void updateLabelsForNextItem() {
        itemIDLabel.setText("Enter item ID for Item #" + itemCount + ":");
        quantityLabel.setText("Enter quantity for Item #" + itemCount + ":");
        // Don't update details label here - it should keep showing the last added item
        // detailsLabel.setText("Details for Item #" + itemCount + ":");
        searchButton.setText("Search For Item #" + itemCount);
        addToCartButton.setText("Add Item #" + itemCount + " To Cart");
    }
    
    private void generateInvoice() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        String dateTime = dateFormat.format(new Date());
        
        double tax = orderSubtotal * TAX_RATE;
        double total = orderSubtotal + tax;
        
        StringBuilder invoice = new StringBuilder();
        invoice.append("Date: ").append(dateTime).append("\n\n");
        invoice.append("Number of line items: ").append(cartSize).append("\n\n");
        invoice.append("Item# / ID / Title / Price / Qty / Disc % / Subtotal:\n\n");
        
        for (int i = 0; i < cartSize; i++) {
            invoice.append((i + 1)).append(". ").append(cartItems[i].toString()).append("\n");
        }
        
        invoice.append("\n");
        invoice.append("Order subtotal: ").append(currency.format(orderSubtotal)).append("\n");
        invoice.append("Tax rate: ").append(percentage.format(TAX_RATE)).append("\n");
        invoice.append("Tax amount: ").append(currency.format(tax)).append("\n");
        invoice.append("ORDER TOTAL: ").append(currency.format(total)).append("\n\n");
        invoice.append("Thanks for shopping at Nile Dot Com!");
        
        JOptionPane.showMessageDialog(this, invoice.toString(), "FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void writeTransactionToFile() {
        try {
            SimpleDateFormat transactionFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
            String transactionID = transactionFormat.format(new Date());
            
            PrintWriter writer = new PrintWriter(new FileWriter("transactions.csv", true));
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, h:mm:ss a z");
            String dateTime = dateFormat.format(new Date());
            
            // Write each cart item as a separate line
            for (int i = 0; i < cartSize; i++) {
                CartItem item = cartItems[i];
                writer.printf("%s, %s, \"%s\", %.2f, %d, %.1f, $%.2f, %s%n", 
                    transactionID, item.itemID, item.description, 
                    item.unitPrice, item.quantity, item.discount, 
                    item.subtotal, dateTime);
            }
            writer.println();
            
            writer.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to transaction file", 
                "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NileDotComGUI().setVisible(true);
        });
    }
}