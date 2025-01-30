/*
Name: Justin Roderick
Course: CNT 4714 – Spring 2025
Assignment title: Project 1 – An Event-driven Enterprise Simulation
Date: Wed January 29, 2025
 */

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
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneId;

public class MainFrame extends JFrame {
    private List<Item> cart = new ArrayList<>();
    private List<Item> Inventory;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JTextField itemIdField, quantityField;
    private JTextArea previousItem, currentTotal;
    private JLabel itemIdLabel, quantityLabel, previousItemLabel, currentTotalLabel, cartStatusLabel;
    private JButton addButton, searchButton, deleteButton, emptyCartButton, checkoutButton;

    public MainFrame() {
        setTitle("Nile Dot Com");
        setSize(1300, 600);
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
        deleteButton = new JButton("Delete Last Item added to Cart");
        emptyCartButton = new JButton("Empty Cart");
        checkoutButton = new JButton("Check Out");
        JButton exitButton = new JButton("Exit");
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        checkoutButton.setEnabled(false);

        cartStatusLabel = new JLabel("Your Shopping Cart is currently empty");

        String[] columns = {"Item#", "SKU", "Description", "Price Each", "Qty", "Discount %", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(tableModel);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(inputPanel, BorderLayout.NORTH);
        cartStatusLabel = new JLabel("Your Shopping Cart is currently empty");
        cartStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartStatusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        northPanel.add(cartStatusLabel, BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
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
    private void updateButtonStates() {
        boolean cartEmpty = cart.isEmpty();
        deleteButton.setEnabled(!cartEmpty);
        checkoutButton.setEnabled(!cartEmpty);
        emptyCartButton.setEnabled(!cartEmpty);
        addButton.setEnabled(false);
        searchButton.setEnabled(true);

        if(cartEmpty) {
            cartStatusLabel.setText("Your Shopping Cart is currently empty");
        } else {
            cartStatusLabel.setText("Your Shopping Cart Currently Contains " + cart.size() + " Item(s)");
        }
    }

    private void updateItemNumber() {
        int nextItemNum = cart.size() + 1;
        itemIdLabel.setText("Enter Item ID for item #" + nextItemNum + ":");
        quantityLabel.setText("Enter quantity for item #" + nextItemNum + ":");
        previousItemLabel.setText("Details for item #" + (nextItemNum -1)  + ":");
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
            quantityField.setText("");
            itemIdField.setText("");
        } else if (!foundItem.getInStock()){
            JOptionPane.showMessageDialog(this, "Sorry... That item is currently out of stock!", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (foundItem.getQuantity() < quantity) {
            JOptionPane.showMessageDialog(this, "Not enough stock available for that item! Only "+ foundItem.getQuantity()+" on hand currently.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            double discount = calculateDiscount(quantity);
            double itemTotal = quantity * foundItem.getPrice() * (1 - discount);
            previousItem.setText(String.format(
                    "SKU: %s | Desc: \"%s\" | Price Each: $%.2f | Qty: %d | Discount: %.0f%% | Total: $%.2f",
                    foundItem.getItemId(),
                    foundItem.getDescription(),
                    foundItem.getPrice(),
                    quantity,
                    discount * 100,
                    itemTotal
            ));
            addButton.setEnabled(true);
            searchButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Item found: " + foundItem.getDescription(), "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private double calculateDiscount(int quantity) {
        if(quantity >= 15) return 0.20;
        if(quantity >= 10) return 0.15;
        if(quantity >= 5) return 0.10;
        return 0.0;
    }

    private void addToCart() {
        // Add item to cart and update table
        String itemId = itemIdField.getText().trim();
        int quantity = Integer.parseInt(quantityField.getText().trim());

        Item foundItem = null;
        for (Item item : Inventory) {
            if (item.getItemId().equalsIgnoreCase(itemId)) {
                foundItem = item;
                break;
            }
        }
        Item cartItem = new Item(foundItem.getItemId(), foundItem.getDescription(), foundItem.getInStock(), quantity, foundItem.getPrice());
        cart.add(cartItem);

        updateCartTable();
        updateButtonStates();
        updateSubtotalDisplay();
        quantityField.setText("");
        itemIdField.setText("");
        updateItemNumber();
    }

    private void updateCartTable() {
        tableModel.setRowCount(0);
        int itemNumber = 1;
        for (Item item : cart) {
            double discount = calculateDiscount(item.getQuantity());
            double total = item.getPrice() * item.getQuantity() * (1 - discount);
            tableModel.addRow(new Object[]{
                    "Item " + itemNumber++,
                    item.getItemId(),
                    "\"" + item.getDescription() + "\"",
                    String.format("$%.2f", item.getPrice()),
                    item.getQuantity(),
                    String.format("%.0f%%", discount * 100),
                    String.format("$%.2f", total)
            });
        }
    }
    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        itemIdField.setEnabled(false);
        quantityField.setEnabled(false);
        searchButton.setEnabled(false);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        checkoutButton.setEnabled(false);

        double subtotal = 0;
        StringBuilder invoice = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm:ssa z");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        invoice.append("Date: ").append(now.format(dtf)).append("\n");
        invoice.append("Number of Items: ").append(cart.size()).append("\n\n");
        invoice.append("Item#/ ID/ Title / Price / Qty / Disc% / Subtotal:\n");

        int itemNum = 1;
        for(Item item : cart) {
            double discount = calculateDiscount(item.getQuantity());
            double itemTotal = item.getPrice() * item.getQuantity() * (1 - discount);
            subtotal += itemTotal;

            invoice.append(String.format("%d. %s \"%s\" $%.2f %d %.0f%% $%.2f\n",
                    itemNum++,
                    item.getItemId(),
                    item.getDescription(),
                    item.getPrice(),
                    item.getQuantity(),
                    discount * 100,
                    itemTotal
            ));
        }

        double tax = subtotal * 0.06;
        double total = subtotal + tax;

        invoice.append("\nOrder Subtotal: $").append(String.format("%.2f", subtotal)).append("\n");
        invoice.append("Tax rate: 6%\n");
        invoice.append("Tax amount: $").append(String.format("%.2f", tax)).append("\n");
        invoice.append("ORDER TOTAL: $").append(String.format("%.2f", total));

        JOptionPane.showMessageDialog(this, invoice.toString(), "Invoice", JOptionPane.INFORMATION_MESSAGE);

        // Log transaction
        try {
            FileManager.logTransaction("src/main/resources/transactions.csv", new Transaction(cart, total).toCSV());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving transaction!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Reset UI
        cart.clear();
        updateCartTable();
        updateItemNumber();
        updateButtonStates();
        updateSubtotalDisplay();
        previousItem.setText("");
        itemIdField.setEnabled(true);
        quantityField.setEnabled(true);
    }

    private void deleteItem() {
        if (!cart.isEmpty()) {
            cart.removeLast();
            updateCartTable();
            updateItemNumber();
            updateButtonStates();
            updateSubtotalDisplay();
            previousItem.setText("");
            previousItemLabel.setText("Details for item #" + cart.size()  + ":");
        }
    }

    private void clearCart() {
        cart.clear();
        updateCartTable();
        updateItemNumber();
        updateButtonStates();
        updateSubtotalDisplay();
    }

    private void updateSubtotalDisplay() {
        double subtotal = TaxDiscountsCalculator.calculateTotal(cart) / 1.06;
        currentTotal.setText(String.format("$%.2f", subtotal));
    }
}