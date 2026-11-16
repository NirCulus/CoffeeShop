package com.coffeeshop.model;

/**
 * Kelas Minuman (Pilar Inheritance & Polymorphism).
 * Mewarisi MenuItem dan menambahkan opsi kustomisasi: Size, Sugar Level, Ice Level.
 */
public class Minuman extends MenuItem {
    private SubKategoriMinuman subKategori;
    private String size; // "Regular" atau "Large" (+Rp 4.000)
    private String sugarLevel; // "100%", "50%", "0%"
    private String iceLevel; // "Normal", "Less", "None"

    public static final double BIAYA_LARGE = 4000.0;

    public Minuman(String id, String nama, double hargaDasar, SubKategoriMinuman subKategori) {
        super(id, nama, hargaDasar);
        this.subKategori = subKategori;
        this.size = "Regular";
        this.sugarLevel = "100%";
        this.iceLevel = "Normal";
    }

    @Override
    public TipeMenu getTipeMenu() {
        return TipeMenu.MINUMAN;
    }

    @Override
    public String getSubKategoriLabel() {
        return subKategori.getLabel();
    }

    /**
     * Polimorfisme: Menghitung total harga minuman berdasarkan ukuran cup (Size)
     * dan total harga topping tambahan (misal Extra Shot atau Boba).
     */
    @Override
    public double hitungHarga() {
        double total = hargaDasar;
        if ("Large".equalsIgnoreCase(size)) {
            total += BIAYA_LARGE;
        }
        total += hitungTotalHargaTopping();
        return total;
    }

    @Override
    public String getDetailPesanan() {
        StringBuilder sb = new StringBuilder();
        sb.append(nama)
          .append(" [").append(size).append("]")
          .append(" (").append(penyajian.getLabel()).append(")")
          .append(" | Sugar: ").append(sugarLevel)
          .append(" | Ice: ").append(iceLevel);

        if (!daftarTopping.isEmpty()) {
            sb.append(" | Topping: ");
            for (int i = 0; i < daftarTopping.size(); i++) {
                sb.append(daftarTopping.get(i).getNama());
                if (i < daftarTopping.size() - 1) sb.append(", ");
            }
        }
        return sb.toString();
    }

    // Copy Constructor untuk instansiasi pesanan baru
    public Minuman copy() {
        Minuman copy = new Minuman(this.id, this.nama, this.hargaDasar, this.subKategori);
        copy.setTersedia(this.tersedia);
        copy.setPenyajian(this.penyajian);
        copy.setSize(this.size);
        copy.setSugarLevel(this.sugarLevel);
        copy.setIceLevel(this.iceLevel);
        for (Topping t : this.daftarTopping) {
            copy.addTopping(t);
        }
        for (var entry : this.resepBahan.entrySet()) {
            copy.tambahBahanResep(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    public SubKategoriMinuman getSubKategori() {
        return subKategori;
    }

    public void setSubKategori(SubKategoriMinuman subKategori) {
        this.subKategori = subKategori;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(String sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public String getIceLevel() {
        return iceLevel;
    }

    public void setIceLevel(String iceLevel) {
        this.iceLevel = iceLevel;
    }
}
