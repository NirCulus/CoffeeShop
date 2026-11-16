package com.coffeeshop.model;

/**
 * Subkategori khusus untuk menu Minuman.
 */
public enum SubKategoriMinuman {
    COFFEE("Kopi"),
    NON_COFFEE("Non-Kopi");

    private final String label;

    SubKategoriMinuman(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
