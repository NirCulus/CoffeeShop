package com.coffeeshop.service;

import com.coffeeshop.model.OrderItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Layanan Keranjang Belanja Kasir (Order Cart).
 * Mengimplementasikan Java Collection Framework (ArrayList)
 * untuk menampung item pesanan secara dinamis dan mempertahankan urutan input kasir.
 */
public class OrderCart {
    private List<OrderItem> items;
    private double diskonPersen; // Nilai 0.0 sampai 1.0 (misal 0.10 untuk 10%)
    public static final double PAJAK_PERSEN = 0.10; // 10% PB1 Restoran

    public OrderCart() {
        this.items = new ArrayList<>();
        this.diskonPersen = 0.0;
    }

    public void tambahItem(OrderItem item) {
        if (item != null) {
            this.items.add(item);
        }
    }

    public void hapusItem(int index) {
        if (index >= 0 && index < items.size()) {
            this.items.remove(index);
        }
    }

    public void clear() {
        this.items.clear();
        this.diskonPersen = 0.0;
    }

    public double hitungSubtotal() {
        double subtotal = 0;
        for (OrderItem oi : items) {
            subtotal += oi.hitungSubtotal();
        }
        return subtotal;
    }

    public double hitungNominalDiskon() {
        return hitungSubtotal() * diskonPersen;
    }

    public double hitungNominalPajak() {
        double subtotalSetelahDiskon = hitungSubtotal() - hitungNominalDiskon();
        return subtotalSetelahDiskon * PAJAK_PERSEN;
    }

    public double hitungTotalAkhir() {
        return (hitungSubtotal() - hitungNominalDiskon()) + hitungNominalPajak();
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public int getJumlahItem() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getDiskonPersen() {
        return diskonPersen;
    }

    public void setDiskonPersen(double diskonPersen) {
        this.diskonPersen = Math.max(0.0, Math.min(1.0, diskonPersen));
    }
}
