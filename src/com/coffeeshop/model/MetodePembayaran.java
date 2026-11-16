package com.coffeeshop.model;

/**
 * Pilihan metode pembayaran transaksi kafe.
 */
public enum MetodePembayaran {
    TUNAI("Tunai (Cash)"),
    QRIS("QRIS / E-Wallet"),
    KARTU_DEBIT("Kartu Debit/Kredit");

    private final String label;

    MetodePembayaran(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
