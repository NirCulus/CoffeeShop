package com.coffeeshop.model;

/**
 * Model item pesanan di dalam keranjang kasir.
 * Menggabungkan objek MenuItem dengan kuantitas dan catatan khusus.
 */
public class OrderItem {
    private MenuItem item;
    private int kuantitas;
    private String catatanKhusus;

    public OrderItem(MenuItem item, int kuantitas, String catatanKhusus) {
        this.item = item;
        this.kuantitas = Math.max(1, kuantitas);
        this.catatanKhusus = catatanKhusus == null ? "" : catatanKhusus;
    }

    public OrderItem(MenuItem item, int kuantitas) {
        this(item, kuantitas, "");
    }

    /**
     * Menghitung subtotal untuk item ini berdasarkan harga polimorfis MenuItem * kuantitas.
     */
    public double hitungSubtotal() {
        return item.hitungHarga() * kuantitas;
    }

    public MenuItem getItem() {
        return item;
    }

    public void setItem(MenuItem item) {
        this.item = item;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = Math.max(1, kuantitas);
    }

    public String getCatatanKhusus() {
        return catatanKhusus;
    }

    public void setCatatanKhusus(String catatanKhusus) {
        this.catatanKhusus = catatanKhusus;
    }

    @Override
    public String toString() {
        return String.format("%dx %s @Rp %,.0f = Rp %,.0f %s",
            kuantitas, 
            item.getDetailPesanan(), 
            item.hitungHarga(), 
            hitungSubtotal(),
            (catatanKhusus.isEmpty() ? "" : "(Catatan: " + catatanKhusus + ")"));
    }
}
