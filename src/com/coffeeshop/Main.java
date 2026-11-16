package com.coffeeshop;

import com.coffeeshop.model.*;
import com.coffeeshop.service.*;
import com.formdev.flatlaf.extras.FlatInspector;
import com.coffeeshop.ui.view.MainDashboardFrame;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry Point Utama Sistem Manajemen Coffee Shop & Kafe Modern "Kopi Kepikiran".
 * Menjalankan antarmuka grafis visual (GUI) modern berbasis Java Swing
 * dengan palet warna Green Grounds POS, sistem navigasi sidebar minimizable,
 * filter kategori 2-tier tombol pill, katalog menu visual, dan keranjang pesanan.
 */
public class Main {
    public static void main(String[] args) {
        // Terapkan System Look and Feel agar rendering font tajam
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        FlatInspector.install("ctrl shift alt X");

        // Sambungkan database dan muat data yang tersimpan
        DatabaseService database = new DatabaseService();
        BahanBakuInventory inventory;
        LaporanKeuanganService laporanService;
        List<MenuItem> masterMenu;
        List<Topping> masterTopping;
        try {
            database.initializeSchema();
            inventory = new BahanBakuInventory(database);
            laporanService = new LaporanKeuanganService(database);
            masterMenu = database.loadMenuItems();
            masterTopping = database.loadToppings();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Tidak dapat menyiapkan database MySQL.\n" +
                    "Periksa server, database, dan konfigurasi COFFEESHOP_DB_*.\n\n" + ex.getMessage(),
                "Koneksi Database Gagal",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
            return;
        }

        // Inisialisasi layanan bisnis
        AuthService authService = new AuthService();

        // Autentikasi Kasir Aktif Default (KASIR-01: Ahmad Fauzi)
        authService.login("fauzi", "kasir123");

        // Jalankan GUI di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainDashboardFrame frame = new MainDashboardFrame(
                authService, inventory, laporanService, masterMenu, masterTopping, database
            );
            frame.setVisible(true);
        });
    }
}
