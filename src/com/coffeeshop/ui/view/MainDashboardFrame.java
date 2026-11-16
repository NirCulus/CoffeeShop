package com.coffeeshop.ui.view;

import com.coffeeshop.model.*;
import com.coffeeshop.service.*;
import com.coffeeshop.service.DatabaseService;
import com.coffeeshop.ui.ThemeColor;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Jendela Utama Aplikasi "Kopi Kepikiran" (Single-Window Dashboard Frame).
 * Menampung TopHeaderPanel, SidebarNavPanel (collapsible), dan CardLayout
 * untuk transisi mulus antara layar POS Kasir, Stok Bahan, dan Laporan Keuangan.
 */
public class MainDashboardFrame extends JFrame {
    private AuthService authService;
    private BahanBakuInventory inventory;
    private LaporanKeuanganService laporanService;
    private DatabaseService database;
    private List<MenuItem> masterMenu;
    private List<Topping> masterTopping;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private PosView posView;
    private StokView stokView;
    private LaporanView laporanView;
    private SidebarNavPanel sidebarNav;
    private TopHeaderPanel topHeader;

    public MainDashboardFrame(AuthService authService, BahanBakuInventory inventory, 
                              LaporanKeuanganService laporanService, List<MenuItem> masterMenu, 
                              List<Topping> masterTopping) {
        this(authService, inventory, laporanService, masterMenu, masterTopping, null);
    }

    public MainDashboardFrame(AuthService authService, BahanBakuInventory inventory,
                              LaporanKeuanganService laporanService, List<MenuItem> masterMenu,
                              List<Topping> masterTopping, DatabaseService database) {
        super("Kopi Kepikiran - Cafe Management & Point of Sale System");
        this.authService = authService;
        this.inventory = inventory;
        this.laporanService = laporanService;
        this.masterMenu = masterMenu;
        this.masterTopping = masterTopping;
        this.database = database;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 680);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(ThemeColor.BG_APP);
        setLayout(new BorderLayout());

        initViews();
    }

    private void initViews() {
        // 1. Top Header (Logo di kiri, Profil kasir di kanan)
        Karyawan kasirAktif = authService.getKasirAktif();
        topHeader = new TopHeaderPanel(kasirAktif, this::onLogout);
        add(topHeader, BorderLayout.NORTH);

        // 2. Sidebar Navigasi Kiri (Bisa di-minimize)
        sidebarNav = new SidebarNavPanel(this::switchPage);
        add(sidebarNav, BorderLayout.WEST);

        // 3. Konten Utama (CardLayout: POS, Stok, Laporan)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);

        posView = new PosView(this, masterMenu, masterTopping, inventory, laporanService,
            (kasirAktif != null ? kasirAktif.getIdKaryawan() : "KASIR-01"), database);
        stokView = new StokView(inventory);
        laporanView = new LaporanView(laporanService);

        mainContentPanel.add(posView, "PAGE_POS");
        mainContentPanel.add(stokView, "PAGE_STOK");
        mainContentPanel.add(laporanView, "PAGE_LAPORAN");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void switchPage(String targetPage) {
        cardLayout.show(mainContentPanel, targetPage);

        // Refresh data otomatis saat halaman dibuka
        if ("PAGE_STOK".equals(targetPage)) {
            stokView.refreshTableData();
        } else if ("PAGE_LAPORAN".equals(targetPage)) {
            laporanView.refreshReportData();
        }
    }

    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin keluar (logout)?", 
            "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            System.exit(0);
        }
    }
}
