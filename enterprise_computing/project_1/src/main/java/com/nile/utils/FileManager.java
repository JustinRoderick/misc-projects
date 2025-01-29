package com.nile.utils;

import com.nile.model.Item;
import org.apache.commons.csv.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    public static List<Item> loadInventory(String filename) throws IOException {
        List<Item> items = new ArrayList<>();

        InputStream inputStream = FileManager.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            throw new FileNotFoundException("Could not find resource: " + filename);
        }
        try (InputStreamReader reader = new InputStreamReader(inputStream);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withTrim()
                     .withIgnoreEmptyLines())) {
            for (CSVRecord record : parser) {

                String itemId = record.get(0).trim();
                String description = record.get(1).trim().replace("\"", "");
                boolean inStock = Boolean.parseBoolean(record.get(2).trim());
                int quantity = Integer.parseInt(record.get(3).trim());
                double price = Double.parseDouble(record.get(4).trim());

                items.add(new Item(itemId, description, inStock, quantity, price));
            }
        }
        return items;
    }

    public static void logTransaction(String filename, String data) throws IOException {
        Files.write(Paths.get(filename), (data + "\n").getBytes(), StandardOpenOption.APPEND);
    }
}