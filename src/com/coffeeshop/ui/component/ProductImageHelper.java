package com.coffeeshop.ui.component;

import com.coffeeshop.model.MenuItem;
import com.coffeeshop.model.Minuman;
import com.coffeeshop.ui.ThemeColor;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Utilitas untuk memuat gambar produk atau menggambar visual placeholder realistis
 * bergaya Green Grounds jika file asset gambar belum disisipkan oleh pengguna.
 */
public class ProductImageHelper {
    public static BufferedImage getProductImage(MenuItem item, int width, int height) {
        // 1. Cek apakah pengguna sudah menaruh asset gambar di folder assets/
        String filenameById = "assets/images/" + item.getId() + ".png";
        String filenameByName = "assets/images/" + item.getNama().toLowerCase().replaceAll("[^a-z0-9]", "_") + ".png";

        File fileId = new File(filenameById);
        File fileName = new File(filenameByName);

        try {
            if (fileId.exists()) {
                return scaleImage(ImageIO.read(fileId), width, height);
            } else if (fileName.exists()) {
                return scaleImage(ImageIO.read(fileName), width, height);
            }
        } catch (Exception ignored) {}

        // 2. Jika belum ada file gambar, render gambar ilustrasi vektor realistis
        return renderProceduralPlaceholder(item, width, height);
    }

    private static BufferedImage scaleImage(BufferedImage src, int w, int h) {
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return resized;
    }

    private static BufferedImage renderProceduralPlaceholder(MenuItem item, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = w / 2;
        int cy = h / 2;

        if (item instanceof Minuman) {
            // Render Gelas Kopi Kaca Modern dengan Es & Gradien Kopi Susu
            int gw = (int) (w * 0.44);
            int gh = (int) (h * 0.72);
            int gx = cx - gw / 2;
            int gy = cy - gh / 2 + 2;

            // Bayangan halus di bawah gelas
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillOval(gx + 2, gy + gh - 4, gw - 4, 10);

            // Lapisan Kopi (Gradien Espresso ke Susu Cream)
            boolean isMatcha = item.getNama().toLowerCase().contains("matcha");
            Color topLiquid = isMatcha ? new Color(106, 153, 78) : new Color(74, 44, 23);
            Color bottomLiquid = isMatcha ? new Color(216, 243, 220) : new Color(230, 204, 178);

            GradientPaint coffeeGradient = new GradientPaint(gx, gy + 10, topLiquid, gx, gy + gh, bottomLiquid);
            g2.setPaint(coffeeGradient);
            g2.fillRoundRect(gx + 3, gy + 6, gw - 6, gh - 8, 12, 12);

            // Lapisan Es Batu
            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillRoundRect(gx + 8, gy + 12, (gw - 16) / 2, 14, 4, 4);
            g2.fillRoundRect(gx + gw / 2, gy + 18, (gw - 16) / 2 - 2, 12, 4, 4);

            // Kaca Gelas (Outline & Kilau Refleksi Putih)
            g2.setColor(new Color(255, 255, 255, 160));
            g2.drawRoundRect(gx, gy, gw, gh, 14, 14);
            // Kilauan vertikal di kiri gelas
            g2.fillRect(gx + 4, gy + 8, 3, gh - 18);
        } else {
            // Render Makanan (Piring Keramik Bersih + Makanan Hangat)
            int pw = (int) (w * 0.72);
            int ph = (int) (h * 0.52);
            int px = cx - pw / 2;
            int py = cy - ph / 2 + 4;

            // Bayangan piring
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillOval(px + 4, py + 8, pw - 8, ph);

            // Piring Keramik Putih
            g2.setColor(Color.WHITE);
            g2.fillOval(px, py, pw, ph);
            g2.setColor(new Color(225, 230, 225));
            g2.drawOval(px, py, pw, ph);

            // Lingkaran dalam piring
            g2.setColor(new Color(248, 250, 248));
            g2.fillOval(px + 8, py + 5, pw - 16, ph - 10);

            // Makanan di atas piring (Warna Steak / Katsu / Pancake)
            if (item.getNama().toLowerCase().contains("steak")) {
                g2.setColor(new Color(92, 40, 20)); // Daging Panggang
                g2.fillRoundRect(cx - 24, cy - 12, 48, 22, 10, 10);
                g2.setColor(new Color(220, 100, 30)); // Saus
                g2.fillOval(cx - 10, cy - 6, 20, 10);
            } else if (item.getNama().toLowerCase().contains("pancake")) {
                g2.setColor(new Color(230, 175, 46)); // Golden Pancake
                g2.fillOval(cx - 22, cy - 10, 44, 20);
                g2.setColor(new Color(245, 210, 100)); // Butter kotak
                g2.fillRect(cx - 5, cy - 7, 10, 8);
            } else if (item.getNama().toLowerCase().contains("katsu")) {
                g2.setColor(new Color(205, 125, 30)); // Crispy Panko Katsu
                g2.fillRoundRect(cx - 25, cy - 12, 50, 22, 6, 6);
            } else {
                g2.setColor(new Color(220, 160, 40)); // Fries / Mie
                g2.fillRoundRect(cx - 22, cy - 10, 44, 20, 6, 6);
            }
        }

        g2.dispose();
        return img;
    }
}
