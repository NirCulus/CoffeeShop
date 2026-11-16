package com.coffeeshop.model;

/**
 * Kelas Makanan (Pilar Inheritance & Polymorphism).
 * Mewarisi MenuItem dan menambahkan karakteristik khas makanan kafe.
 */
public class Makanan extends MenuItem {
    private SubKategoriMakanan subKategori;
    private int levelPedas; // 0: Tidak Pedas, 1: Sedang, 2: Pedas, 3: Ekstra Pedas
    private boolean dipanaskan; // true: Sajikan hangat, false: Suhu ruang

    public Makanan(String id, String nama, double hargaDasar, SubKategoriMakanan subKategori) {
        super(id, nama, hargaDasar);
        this.subKategori = subKategori;
        this.levelPedas = 0;
        this.dipanaskan = true;
    }

    @Override
    public TipeMenu getTipeMenu() {
        return TipeMenu.MAKANAN;
    }

    @Override
    public String getSubKategoriLabel() {
        return subKategori.getLabel();
    }

    /**
     * Polimorfisme: Menghitung total harga makanan ditambah topping/add-on yang dipilih.
     */
    @Override
    public double hitungHarga() {
        return hargaDasar + hitungTotalHargaTopping();
    }

    @Override
    public String getDetailPesanan() {
        StringBuilder sb = new StringBuilder();
        sb.append(nama)
          .append(" (").append(penyajian.getLabel()).append(")")
          .append(" | Pedas: Lvl ").append(levelPedas)
          .append(dipanaskan ? " | Disajikan Hangat" : " | Suhu Ruang");

        if (!daftarTopping.isEmpty()) {
            sb.append(" | Add-on: ");
            for (int i = 0; i < daftarTopping.size(); i++) {
                sb.append(daftarTopping.get(i).getNama());
                if (i < daftarTopping.size() - 1) sb.append(", ");
            }
        }
        return sb.toString();
    }

    // Copy Constructor untuk instansiasi pesanan baru
    public Makanan copy() {
        Makanan copy = new Makanan(this.id, this.nama, this.hargaDasar, this.subKategori);
        copy.setTersedia(this.tersedia);
        copy.setPenyajian(this.penyajian);
        copy.setLevelPedas(this.levelPedas);
        copy.setDipanaskan(this.dipanaskan);
        for (Topping t : this.daftarTopping) {
            copy.addTopping(t);
        }
        for (var entry : this.resepBahan.entrySet()) {
            copy.tambahBahanResep(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    public SubKategoriMakanan getSubKategori() {
        return subKategori;
    }

    public void setSubKategori(SubKategoriMakanan subKategori) {
        this.subKategori = subKategori;
    }

    public int getLevelPedas() {
        return levelPedas;
    }

    public void setLevelPedas(int levelPedas) {
        this.levelPedas = Math.max(0, Math.min(3, levelPedas));
    }

    public boolean isDipanaskan() {
        return dipanaskan;
    }

    public void setDipanaskan(boolean dipanaskan) {
        this.dipanaskan = dipanaskan;
    }
}
