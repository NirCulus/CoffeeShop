package com.coffeeshop.model;

/**
 * Model entitas untuk inventaris stok bahan baku kafe.
 * Menerapkan prinsip Encapsulation dengan validasi agar stok tidak bernilai negatif.
 */
public class BahanBaku {
    private String id;
    private String nama;
    private double stok;
    private String satuan;
    private double stokMinimum;

    public BahanBaku(String id, String nama, double stok, String satuan, double stokMinimum) {
        this.id = id;
        this.nama = nama;
        this.stok = Math.max(0, stok);
        this.satuan = satuan;
        this.stokMinimum = stokMinimum;
    }

    public boolean kurangiStok(double jumlah) {
        if (jumlah <= 0) return true;
        if (this.stok >= jumlah) {
            this.stok -= jumlah;
            return true;
        }
        return false;
    }

    public void tambahStok(double jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
        }
    }

    public boolean isLowStock() {
        return this.stok <= this.stokMinimum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getStok() {
        return stok;
    }

    public void setStok(double stok) {
        this.stok = Math.max(0, stok);
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public double getStokMinimum() {
        return stokMinimum;
    }

    public void setStokMinimum(double stokMinimum) {
        this.stokMinimum = stokMinimum;
    }

    @Override
    public String toString() {
        return String.format("%s - %s: %.1f %s %s", 
            id, nama, stok, satuan, (isLowStock() ? "[PERINGATAN STOK MENIPIS]" : ""));
    }
}
