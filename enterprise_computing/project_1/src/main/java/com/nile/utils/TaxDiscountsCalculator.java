package com.nile.utils;

import com.nile.model.Item;
import java.util.List;

public class TaxDiscountsCalculator {
    public static double calculateTotal(List<Item> items) {
        double subtotal = 0;
        for(Item item : items) {
            // Calculate discount based on item quant
            double discount = calculateDiscount(item.getQuantity());
            subtotal += item.getPrice() * item.getQuantity() * (1 - discount);
        }
        // Add tax
        return subtotal * 1.06;
    }

    private static double calculateDiscount(int quantity) {
        if(quantity >= 15) return 0.20;
        if(quantity >= 10) return 0.15;
        if(quantity >= 5) return 0.10;
        return 0.0;
    }
}