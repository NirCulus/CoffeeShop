package com.coffeeshop.service;

import com.coffeeshop.model.BahanBaku;
import com.coffeeshop.model.MenuItem;
import com.coffeeshop.model.Topping;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Layanan Inventaris Stok Bahan Baku Kafe.
 * Mengimplementasikan Java Collection Framework (HashMap)
 * untuk pencarian instan O(1) berdasarkan ID bahan baku dan mutasi stok otomatis (auto-deduct).
 */
public class BahanBakuInventory {
    private Map<String, BahanBaku> inventory;
    private DatabaseService database;

    public BahanBakuInventory() {
        this.inventory = new HashMap<>();
        initMasterBahanBaku();
    }

    public BahanBakuInventory(DatabaseService database) throws java.sql.SQLException {
        this.inventory = new HashMap<>();
        this.database = database;
        initMasterBahanBaku();
        database.seedIngredients(inventory.values());
        inventory.clear();
        for (BahanBaku bahan : database.loadIngredients()) {
            inventory.put(bahan.getId(), bahan);
        }
    }

    private void initMasterBahanBaku() {
        tambahBahan(new BahanBaku("BB-01", "Biji Kopi Espresso", 2000.0, "gram", 300.0));
        tambahBahan(new BahanBaku("BB-02", "Fresh Milk", 5000.0, "ml", 1000.0));
        tambahBahan(new BahanBaku("BB-03", "Sirup Gula Cair", 2000.0, "ml", 500.0));
        tambahBahan(new BahanBaku("BB-04", "Bubuk Matcha Premium", 1000.0, "gram", 200.0));
        tambahBahan(new BahanBaku("BB-05", "Paper Cup & Tutup 12oz", 100.0, "pcs", 20.0));
        tambahBahan(new BahanBaku("BB-06", "Daging Steak Sapi", 25.0, "potong", 5.0));
        tambahBahan(new BahanBaku("BB-07", "Mie Kering Porsi", 50.0, "porsi", 10.0));
        tambahBahan(new BahanBaku("BB-08", "Adonan Pancake", 2000.0, "gram", 300.0));
        tambahBahan(new BahanBaku("BB-09", "Daging Ayam Katsu", 30.0, "potong", 5.0));
        tambahBahan(new BahanBaku("BB-10", "Keju Cheddar Slice", 40.0, "lembar", 10.0));
        tambahBahan(new BahanBaku("BB-11", "Kentang Beku (Fries)", 3000.0, "gram", 500.0));
        tambahBahan(new BahanBaku("BB-12", "Saus Tomat", 100.0, "sachet", 20.0));
        tambahBahan(new BahanBaku("BB-13", "Saus Sambal", 100.0, "sachet", 20.0));
    }

    public void tambahBahan(BahanBaku bahan) {
        if (bahan != null) {
            this.inventory.put(bahan.getId(), bahan);
        }
    }

    public BahanBaku getBahan(String idBahan) {
        return this.inventory.get(idBahan);
    }

    public Collection<BahanBaku> getAllBahanBaku() {
        return this.inventory.values();
    }

    /**
     * Memeriksa apakah stok bahan baku mencukupi untuk membuat menu pesanan beserta toppingnya.
     */
    public boolean cekKetersediaan(MenuItem item, int kuantitas) {
        if (item == null || kuantitas <= 0) return false;

        // Cek bahan utama menu
        for (Map.Entry<String, Double> entry : item.getResepBahan().entrySet()) {
            BahanBaku bb = inventory.get(entry.getKey());
            if (bb == null || bb.getStok() < (entry.getValue() * kuantitas)) {
                return false;
            }
        }

        // Cek bahan topping/add-on
        for (Topping t : item.getDaftarTopping()) {
            if (t.getIdBahanBaku() != null && !t.getIdBahanBaku().isEmpty()) {
                BahanBaku bb = inventory.get(t.getIdBahanBaku());
                if (bb == null || bb.getStok() < (t.getTakaranBahan() * kuantitas)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Mengurangi stok bahan baku secara otomatis saat pesanan berhasil dibayar (Auto-Deduct).
     */
    public boolean kurangiStokBerdasarkanMenu(MenuItem item, int kuantitas) {
        if (!cekKetersediaan(item, kuantitas)) {
            return false;
        }

        // Potong bahan utama resep
        for (Map.Entry<String, Double> entry : item.getResepBahan().entrySet()) {
            BahanBaku bb = inventory.get(entry.getKey());
            if (bb != null) {
                bb.kurangiStok(entry.getValue() * kuantitas);
            }
        }

        // Potong bahan topping/add-on
        for (Topping t : item.getDaftarTopping()) {
            if (t.getIdBahanBaku() != null && !t.getIdBahanBaku().isEmpty()) {
                BahanBaku bb = inventory.get(t.getIdBahanBaku());
                if (bb != null) {
                    bb.kurangiStok(t.getTakaranBahan() * kuantitas);
                }
            }
        }
        return true;
    }

    public void restock(String idBahan, double jumlah) {
        BahanBaku bb = inventory.get(idBahan);
        if (bb != null) {
            if (database != null && jumlah > 0) {
                try {
                    database.restock(idBahan, jumlah);
                } catch (java.sql.SQLException ex) {
                    throw new IllegalStateException("Gagal menyimpan restock ke database.", ex);
                }
            }
            bb.tambahStok(jumlah);
        }
    }

    /**
     * Mengembalikan daftar bahan baku yang berada di bawah ambang batas stok minimum (Low Stock Warning).
     */
    public List<BahanBaku> getPeringatanStokMenipis() {
        List<BahanBaku> lowStockList = new ArrayList<>();
        for (BahanBaku bb : inventory.values()) {
            if (bb.isLowStock()) {
                lowStockList.add(bb);
            }
        }
        return lowStockList;
    }
}
