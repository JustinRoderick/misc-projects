package com.nile.model;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Transaction {
    private String transactionId;
    private ZonedDateTime timestamp;
    private List<Item> items;
    private double total;

    public Transaction(List<Item> items, double total) {
        this.timestamp = ZonedDateTime.now(ZoneId.of("America/New_York"));
        this.transactionId = generateTransactionId();
        this.items = items;
        this.total = total;
    }

    private String generateTransactionId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        return timestamp.format(formatter);
    }

    public String toCSV() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm:ssa z");

        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            double discount = calculateDiscount(item.getQuantity());
            double itemTotal = item.getPrice() * item.getQuantity() * (1 - discount);

            sb.append(String.format("%s,%s,\"%s\",%.2f,%d,%.0f%%,%.2f,%s\n",
                    transactionId,
                    item.getItemId(),
                    item.getDescription().replace(",", ";"),
                    item.getPrice(),
                    item.getQuantity(),
                    discount * 100,
                    itemTotal,
                    timestamp.format(dateFormatter)
            ));
        }
        return sb.toString();
    }

    private double calculateDiscount(int quantity) {
        if (quantity >= 15) return 0.20;
        if (quantity >= 10) return 0.15;
        if (quantity >= 5) return 0.10;
        return 0.0;
    }
}