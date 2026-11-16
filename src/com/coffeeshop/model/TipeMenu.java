package com.coffeeshop.model;

/**
 * Dua Kategori Menu Utama kafe.
 */
public enum TipeMenu {
    MAKANAN("Makanan"),
    MINUMAN("Minuman");

    private final String label;

    TipeMenu(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
