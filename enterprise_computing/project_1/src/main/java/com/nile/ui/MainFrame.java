package com.nile.ui;

import com.nile.model.Item;
import com.nile.model.Transaction;
import com.nile.utils.FileManager;
import com.nile.utils.TaxDiscountsCalculator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private List<Item> cart = new ArrayList<>();
    private List<Item> Inventory;
    private int item_num = 1;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JTextField itemIdField, quantityField;
    private JTextArea previousItem, currentTotal;
    private JLabel itemIdLabel, quantityLabel, previousItemLabel, currentTotalLabel;
    private JButton addButton, searchButton;

    public MainFrame() {
        setTitle("Nile Dot Com");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try{
            Inventory = FileManager.loadInventory("inventory.csv");
        }catch(IOException e){
            JOptionPane.showMessageDialog(this, "Failed to load inventory file!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        itemIdField = new JTextField();
        quantityField = new JTextField();
        previousItem = new JTextArea();
        currentTotal = new JTextArea();
        previousItem.setEditable(false);
        previousItem.setLineWrap(true);
        previousItem.setWrapStyleWord(true);
        currentTotal.setEditable(false);
        currentTotal.setLineWrap(true);
        currentTotal.setWrapStyleWord(true);
        itemIdLabel = new JLabel("Enter Item ID for item #1:");
        quantityLabel = new JLabel("Enter quantity for item #1:");
        previousItemLabel = new JLabel("Details for item #1:");
        currentTotalLabel = new JLabel("Current Subtotal for 0 items(s)");
        inputPanel.add(itemIdLabel);
        inputPanel.add(itemIdField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);
        inputPanel.add(previousItemLabel);
        inputPanel.add(previousItem);
        inputPanel.add(currentTotalLabel);
        inputPanel.add(currentTotal);


        searchButton = new JButton("Search for Item #1");
        addButton = new JButton("Add Item #1 to Cart");
        JButton deleteButton = new JButton("Delete Last Item added to Cart");
        JButton emptyCartButton = new JButton("Empty Cart");
        JButton checkoutButton = new JButton("Check Out");
        JButton exitButton = new JButton("Exit");


//       String[] columns = {"Item ID", "Description", "Price", "Quantity"};
//        cartTable = new JTable(new Object[0][4], columns);
        String[] columns = {"Item ID", "Description", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(tableModel);


        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(emptyCartButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);


        searchButton.addActionListener(e -> searchItem());
        addButton.addActionListener(e -> addToCart());
        deleteButton.addActionListener(e -> deleteItem());
        emptyCartButton.addActionListener(e -> clearCart());
        checkoutButton.addActionListener(e -> checkout());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void updateItemNumber() {
        int nextItemNum = cart.size() + 1;
        itemIdLabel.setText("Enter Item ID for item #" + nextItemNum + ":");
        quantityLabel.setText("Enter quantity for item #" + nextItemNum + ":");
        previousItemLabel.setText("Details for item #" + nextItemNum + ":");
        currentTotalLabel.setText("Current Subtotal for " + cart.size() + " items(s):");
        addButton.setText("Add Item #" + nextItemNum);
        searchButton.setText("Search for Item #" + nextItemNum);
    }

    private void searchItem() {
        String itemId = itemIdField.getText().trim();
        int quantity = Integer.parseInt(quantityField.getText().trim());

        // Search for the item in the inventory
        Item foundItem = null;
        for (Item item : Inventory) {
            if (item.getItemId().equalsIgnoreCase(itemId)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem == null) {
            JOptionPane.showMessageDialog(this, "Item ID " + itemId +  " not found in inventory!", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (foundItem.getQuantity() < quantity) {
            JOptionPane.showMessageDialog(this, "Not enough stock available for that item!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            previousItem.setText(foundItem.getItemId() + " " + foundItem.getDescription() + " " + foundItem.getPrice() + " " + foundItem.getQuantity());
            // Display item details or enable "Add to Cart" button
            // Also disable search button
            JOptionPane.showMessageDialog(this, "Item found: " + foundItem.getDescription(), "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addToCart() {
        // Add item to cart and update table
        String itemId = itemIdField.getText().trim();
        int quantity = Integer.parseInt(quantityField.getText().trim());

        // Find the item in the inventory
        Item foundItem = null;
        for (Item item : Inventory) {
            if (item.getItemId().equalsIgnoreCase(itemId)) {
                foundItem = item;
                break;
            }
        }

        // Add item to cart
        Item cartItem = new Item(foundItem.getItemId(), foundItem.getDescription(), foundItem.getInStock(), quantity, foundItem.getPrice());
        cart.add(cartItem);

        // Update UI

        updateCartTable();

        // Clear input fields
        quantityField.setText("");
        itemIdField.setText("");

        updateItemNumber();
    }

    private void updateCartTable() {
        tableModel.setRowCount(0);// Clear existing rows

        for (Item item : cart) {
            tableModel.addRow(new Object[]{
                    item.getItemId(),
                    item.getDescription(),
                    item.getPrice(),
                    item.getQuantity()
            });
        }
    }

    private void checkout() {
        // Calculate total and log transaction
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate total
        double total = TaxDiscountsCalculator.calculateTotal(cart);

        // Create transaction
        Transaction transaction = new Transaction(cart, total);

        // Log transaction
        try {
            FileManager.logTransaction("resources/transactions.csv", transaction.toCSV());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to log transaction!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Show invoice
        JOptionPane.showMessageDialog(this, "Total: $" + total, "Invoice", JOptionPane.INFORMATION_MESSAGE);

        // Clear cart
        cart.clear();
        updateItemNumber();
        updateCartTable();
    }

    private void deleteItem() {
        if (!cart.isEmpty()) {
            cart.removeLast();
            updateCartTable();
            updateItemNumber();
            // update current subtotal
        }
    }

    private void clearCart() {
        cart.clear();
        updateCartTable();
        updateItemNumber();
        // update current subtotal

    }
}