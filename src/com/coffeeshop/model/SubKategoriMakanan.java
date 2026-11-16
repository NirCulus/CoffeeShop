package com.coffeeshop.model;

/**
 * Subkategori khusus untuk menu Makanan.
 */
public enum SubKategoriMakanan {
    MEALS("Makanan Berat"),
    SNACK("Snack & Kudapan"),
    DESSERT("Dessert & Manis");

    private final String label;

    SubKategoriMakanan(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
