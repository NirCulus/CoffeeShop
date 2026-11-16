package com.coffeeshop.test;

import com.coffeeshop.model.*;
import com.coffeeshop.service.*;
import com.coffeeshop.ui.view.MainDashboardFrame;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class RenderPreview {
    public static void main(String[] args) throws Exception {
        AuthService authService = new AuthService();
        BahanBakuInventory inventory = new BahanBakuInventory();
        LaporanKeuanganService laporanService = new LaporanKeuanganService();
        authService.login("fauzi", "kasir123");

        // Master Topping
        List<Topping> masterTopping = new ArrayList<>();
        Topping sausTomat = new Topping("TOP-01", "Saus Tomat", 1000.0, "BB-12", 1.0);
        Topping sausSambal = new Topping("TOP-02", "Saus Sambal", 1000.0, "BB-13", 1.0);
        Topping extraKeju = new Topping("TOP-03", "Extra Keju Slice", 4000.0, "BB-10", 1.0);
        Topping extraShot = new Topping("TOP-04", "Extra Espresso Shot", 5000.0, "BB-01", 9.0);
        masterTopping.add(sausTomat);
        masterTopping.add(sausSambal);
        masterTopping.add(extraKeju);
        masterTopping.add(extraShot);

        // Master Menu
        List<MenuItem> masterMenu = new ArrayList<>();
        Minuman latte = new Minuman("MN-03", "Caffe Latte", 25000.0, SubKategoriMinuman.COFFEE);
        Makanan steak = new Makanan("MK-01", "Beef Steak Kafe", 55000.0, SubKategoriMakanan.MEALS);
        Makanan mie = new Makanan("MK-02", "Mie Goreng Spesial Kafe", 22000.0, SubKategoriMakanan.MEALS);
        Makanan katsu = new Makanan("MK-03", "Crispy Chicken Katsu", 28000.0, SubKategoriMakanan.MEALS);
        Makanan fries = new Makanan("MK-04", "French Fries Crispy", 16000.0, SubKategoriMakanan.SNACK);
        Makanan pancake = new Makanan("MK-05", "Fluffy Pancake Maple", 20000.0, SubKategoriMakanan.DESSERT);

        masterMenu.add(steak);
        masterMenu.add(mie);
        masterMenu.add(katsu);
        masterMenu.add(fries);
        masterMenu.add(pancake);
        masterMenu.add(latte);

        MainDashboardFrame frame = new MainDashboardFrame(
            authService, inventory, laporanService, masterMenu, masterTopping
        );

        // Tambah sampel pesanan ke keranjang POS
        Makanan sampleSteak = steak.copy();
        sampleSteak.setLevelPedas(2);
        sampleSteak.addTopping(sausTomat);
        sampleSteak.addTopping(sausSambal);

        Minuman sampleLatte = latte.copy();
        sampleLatte.setSize("Large");
        sampleLatte.addTopping(extraShot);

        // Ambil PosView dari frame
        // Set ukuran dan layout standard
        frame.setSize(1220, 720);
        frame.pack();
        frame.setSize(1220, 720);
        frame.validate();

        BufferedImage img = new BufferedImage(1220, 720, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        frame.getContentPane().printAll(g2);
        g2.dispose();

        String artifactDir = "C:\\Users\\Fatih\\.gemini\\antigravity\\brain\\8a9ad1d5-caaa-485e-a8ff-645e50538cdd";
        ImageIO.write(img, "png", new File(artifactDir + "\\kopi_kepikiran_pos_preview.png"));
        ImageIO.write(img, "png", new File("e:\\CoffeeShop\\kopi_kepikiran_pos_preview.png"));

        long len = new File("e:\\CoffeeShop\\kopi_kepikiran_pos_preview.png").length();
        System.out.println("Frame printAll rendered! File size: " + len + " bytes");
        System.exit(0);
    }
}
