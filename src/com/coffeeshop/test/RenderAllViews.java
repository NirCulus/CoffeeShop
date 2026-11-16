package com.coffeeshop.test;

import com.coffeeshop.model.*;
import com.coffeeshop.service.*;
import com.coffeeshop.ui.ThemeColor;
import com.coffeeshop.ui.view.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RenderAllViews {
    public static void main(String[] args) throws Exception {
        AuthService authService = new AuthService();
        BahanBakuInventory inventory = new BahanBakuInventory();
        LaporanKeuanganService laporanService = new LaporanKeuanganService();
        authService.login("fauzi", "kasir123");

        // Transaksi sampel agar laporan keuangan ada isinya
        List<OrderItem> items1 = new ArrayList<>();
        items1.add(new OrderItem(new Makanan("MK-01", "Beef Steak Kafe", 55000.0, SubKategoriMakanan.MEALS), 1));
        items1.add(new OrderItem(new Minuman("MN-03", "Caffe Latte", 25000.0, SubKategoriMinuman.COFFEE), 1));
        Transaksi trx1 = new Transaksi("TRX-001", "KASIR-01", items1, 0.0, 100000.0, MetodePembayaran.TUNAI);
        laporanService.catatTransaksi(trx1);

        List<OrderItem> items2 = new ArrayList<>();
        items2.add(new OrderItem(new Makanan("MK-03", "Crispy Chicken Katsu", 28000.0, SubKategoriMakanan.MEALS), 2));
        items2.add(new OrderItem(new Minuman("MN-04", "Matcha Latte", 26000.0, SubKategoriMinuman.NON_COFFEE), 1));
        Transaksi trx2 = new Transaksi("TRX-002", "KASIR-01", items2, 0.10, 0.0, MetodePembayaran.QRIS);
        laporanService.catatTransaksi(trx2);

        // 1. Render Stok View
        JFrame frameStok = new JFrame();
        frameStok.setLayout(new BorderLayout());
        frameStok.add(new TopHeaderPanel(authService.getKasirAktif(), () -> {}), BorderLayout.NORTH);
        frameStok.add(new SidebarNavPanel(key -> {}), BorderLayout.WEST);
        StokView stokView = new StokView(inventory);
        frameStok.add(stokView, BorderLayout.CENTER);
        frameStok.setSize(1220, 720);
        frameStok.pack();
        frameStok.setSize(1220, 720);
        frameStok.validate();

        BufferedImage imgStok = new BufferedImage(1220, 720, BufferedImage.TYPE_INT_RGB);
        Graphics2D gStok = imgStok.createGraphics();
        frameStok.getContentPane().printAll(gStok);
        gStok.dispose();

        String artifactDir = "C:\\Users\\Fatih\\.gemini\\antigravity\\brain\\8a9ad1d5-caaa-485e-a8ff-645e50538cdd";
        ImageIO.write(imgStok, "png", new File(artifactDir + "\\kopi_kepikiran_stok_preview.png"));

        // 2. Render Laporan View
        JFrame frameLaporan = new JFrame();
        frameLaporan.setLayout(new BorderLayout());
        frameLaporan.add(new TopHeaderPanel(authService.getKasirAktif(), () -> {}), BorderLayout.NORTH);
        frameLaporan.add(new SidebarNavPanel(key -> {}), BorderLayout.WEST);
        LaporanView laporanView = new LaporanView(laporanService);
        frameLaporan.add(laporanView, BorderLayout.CENTER);
        frameLaporan.setSize(1220, 720);
        frameLaporan.pack();
        frameLaporan.setSize(1220, 720);
        frameLaporan.validate();

        BufferedImage imgLap = new BufferedImage(1220, 720, BufferedImage.TYPE_INT_RGB);
        Graphics2D gLap = imgLap.createGraphics();
        frameLaporan.getContentPane().printAll(gLap);
        gLap.dispose();

        ImageIO.write(imgLap, "png", new File(artifactDir + "\\kopi_kepikiran_laporan_preview.png"));

        System.out.println("Stok and Laporan preview images generated successfully!");
        System.exit(0);
    }
}
