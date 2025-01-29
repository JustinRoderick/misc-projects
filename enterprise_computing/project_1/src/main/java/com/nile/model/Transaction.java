package com.nile.model;

import java.time.LocalDateTime;
import java.util.List;

public class Transaction {
    private String transactionId;
    private LocalDateTime timestamp;
    private List<Item> items;
    private double total;

    public Transaction(List<Item> items, double total) {
        this.timestamp = LocalDateTime.now();
        this.transactionId = generateTransactionId();
        this.items = items;
        this.total = total;
    }

    private String generateTransactionId() {
        return timestamp.toString().replaceAll("[^0-9]", "").substring(0, 17);
    }

    public String toCSV() {
        return String.format("%s,%s,%.2f", transactionId, timestamp, total);
    }
}