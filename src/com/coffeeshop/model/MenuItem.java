package com.coffeeshop.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kelas Abstrak (Pilar Abstraction) yang menjadi kelas induk bagi seluruh menu kafe.
 * Menerapkan Java Collection Framework (List/ArrayList dan Map/HashMap)
 * untuk mengelola daftar topping serta resep komposisi bahan baku.
 */
public abstract class MenuItem {
    protected String id;
    protected String nama;
    protected double hargaDasar;
    protected boolean tersedia;
    protected JenisPenyajian penyajian;
    protected List<Topping> daftarTopping;
    protected Map<String, Double> resepBahan; // ID Bahan Baku -> Jumlah Kebutuhan

    public MenuItem(String id, String nama, double hargaDasar) {
        this.id = id;
        this.nama = nama;
        this.hargaDasar = hargaDasar;
        this.tersedia = true;
        this.penyajian = JenisPenyajian.DINE_IN;
        this.daftarTopping = new ArrayList<>();
        this.resepBahan = new HashMap<>();
    }

    // Method Abstrak (Wajib diimplementasikan oleh kelas anak secara polimorfis)
    public abstract TipeMenu getTipeMenu();
    public abstract String getSubKategoriLabel();
    public abstract double hitungHarga();
    public abstract String getDetailPesanan();

    // Manajemen Topping
    public void addTopping(Topping topping) {
        if (topping != null) {
            this.daftarTopping.add(topping);
        }
    }

    public void removeTopping(Topping topping) {
        this.daftarTopping.remove(topping);
    }

    public void clearTopping() {
        this.daftarTopping.clear();
    }

    // Manajemen Resep Komposisi Bahan Baku (Map)
    public void tambahBahanResep(String idBahanBaku, double jumlah) {
        this.resepBahan.put(idBahanBaku, jumlah);
    }

    public double hitungTotalHargaTopping() {
        double total = 0;
        for (Topping t : daftarTopping) {
            total += t.getHargaTambahan();
        }
        return total;
    }

    // Getter dan Setter
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

    public double getHargaDasar() {
        return hargaDasar;
    }

    public void setHargaDasar(double hargaDasar) {
        this.hargaDasar = hargaDasar;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public JenisPenyajian getPenyajian() {
        return penyajian;
    }

    public void setPenyajian(JenisPenyajian penyajian) {
        this.penyajian = penyajian;
    }

    public List<Topping> getDaftarTopping() {
        return daftarTopping;
    }

    public Map<String, Double> getResepBahan() {
        return resepBahan;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Rp %,.0f", getSubKategoriLabel(), nama, hitungHarga());
    }
}
