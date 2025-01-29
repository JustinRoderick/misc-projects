package com.nile.utils;

import com.nile.model.Item;
import java.util.List;

public class TaxDiscountsCalculator {
    private static final double TAX_RATE = 0.06;

    public static double calculateTotal(List<Item> items) {
        double subtotal = items.stream().mapToDouble(item -> item.getPrice()).sum();
        return subtotal * (1 + TAX_RATE);
    }
}