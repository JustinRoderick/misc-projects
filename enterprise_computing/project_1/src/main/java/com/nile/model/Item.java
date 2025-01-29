package com.nile.model;

public class Item {
    private String itemId;
    private String description;
    private Boolean inStock;
    private int quantity;
    private double price;

    public Item(String itemId, String description, Boolean inStock, int quantity, double price) {
        this.itemId = itemId;
        this.description = description;
        this.inStock = inStock;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemId() { return itemId; }
    public String getDescription() { return description; }
    public Boolean getInStock() { return inStock; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}