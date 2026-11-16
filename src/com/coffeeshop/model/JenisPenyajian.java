package com.coffeeshop.model;

/**
 * Pilihan jenis penyajian pesanan kafe.
 */
public enum JenisPenyajian {
    DINE_IN("Dine-In (Makan di Tempat)"),
    TAKEAWAY("Takeaway (Bungkus/Bawa Pulang)");

    private final String label;

    JenisPenyajian(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
