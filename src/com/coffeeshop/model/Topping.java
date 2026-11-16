package com.coffeeshop.model;

/**
 * Model Add-on / Topping untuk Makanan maupun Minuman.
 * Menyimpan informasi harga tambahan dan pemakaian bahan baku terkait.
 */
public class Topping {
    private String id;
    private String nama;
    private double hargaTambahan;
    private String idBahanBaku;
    private double takaranBahan;

    public Topping(String id, String nama, double hargaTambahan, String idBahanBaku, double takaranBahan) {
        this.id = id;
        this.nama = nama;
        this.hargaTambahan = hargaTambahan;
        this.idBahanBaku = idBahanBaku;
        this.takaranBahan = takaranBahan;
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

    public double getHargaTambahan() {
        return hargaTambahan;
    }

    public void setHargaTambahan(double hargaTambahan) {
        this.hargaTambahan = hargaTambahan;
    }

    public String getIdBahanBaku() {
        return idBahanBaku;
    }

    public void setIdBahanBaku(String idBahanBaku) {
        this.idBahanBaku = idBahanBaku;
    }

    public double getTakaranBahan() {
        return takaranBahan;
    }

    public void setTakaranBahan(double takaranBahan) {
        this.takaranBahan = takaranBahan;
    }

    @Override
    public String toString() {
        return nama + " (+Rp " + String.format("%,.0f", hargaTambahan) + ")";
    }
}
