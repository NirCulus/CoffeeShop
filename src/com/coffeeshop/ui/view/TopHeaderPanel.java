package com.coffeeshop.ui.view;

import com.coffeeshop.model.Karyawan;
import com.coffeeshop.ui.ThemeColor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Top Header 1:1 Sesuai Referensi Green Grounds Coffee POS.
 * Kiri: Logo Placeholder & Nama Kafe + Tanggal (tanpa slogan panjang).
 * Kanan: Total Orders Counter, Tombol Report, Notifikasi, dan Profil Kasir (Foto Bulat + Nama + Role).
 */
public class TopHeaderPanel extends JPanel {
    private JLabel lblJam;
    private OnHeaderActionListener listener;

    public interface OnHeaderActionListener {
        void onReportClick();
        void onLogout();
    }

    public TopHeaderPanel(Karyawan kasirAktif, Runnable onLogout) {
        this(kasirAktif, 20, new OnHeaderActionListener() {
            @Override
            public void onReportClick() {}
            @Override
            public void onLogout() {
                if (onLogout != null) onLogout.run();
            }
        });
    }

    public TopHeaderPanel(Karyawan kasirAktif, int totalOrders, OnHeaderActionListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 22, 10, 22));
        setPreferredSize(new Dimension(1160, 60));

        // 1. KIRI: LOGO / PLACEHOLDER GAMBAR + TANGGAL
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 2));
        leftPanel.setOpaque(false);

        // Logo Placeholder (Bisa membaca assets/logo.png jika ada, atau gambar vektor bulat)
        JPanel logoPlaceholder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                File logoFile = new File("assets/logo.png");
                if (logoFile.exists()) {
                    try {
                        g2.drawImage(ImageIO.read(logoFile), 0, 0, getWidth(), getHeight(), null);
                        g2.dispose();
                        return;
                    } catch (Exception ignored) {}
                }

                // Gambar lingkaran logo dengan cangkir kopi
                g2.setColor(ThemeColor.PRIMARY);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(8, 10, 14, 11, 4, 4);
                g2.drawArc(18, 11, 6, 6, -90, 180);
                g2.fillRect(6, 22, 18, 2);

                g2.dispose();
            }
        };
        logoPlaceholder.setPreferredSize(new Dimension(34, 34));
        logoPlaceholder.setOpaque(false);
        leftPanel.add(logoPlaceholder);

        // Nama Kafe "KOPI KEPIKIRAN" (Bold dua baris)
        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);

        JLabel lblBrand1 = new JLabel("KOPI");
        lblBrand1.setFont(ThemeColor.FONT_BOLD);
        lblBrand1.setForeground(ThemeColor.PRIMARY);

        JLabel lblBrand2 = new JLabel("KEPIKIRAN");
        lblBrand2.setFont(ThemeColor.FONT_BOLD);
        lblBrand2.setForeground(ThemeColor.PRIMARY);

        brandText.add(lblBrand1);
        brandText.add(lblBrand2);
        leftPanel.add(brandText);

        leftPanel.add(Box.createHorizontalStrut(14));

        // Tanggal di Samping Nama Kafe (Thursday, 23 June)
        lblJam = new JLabel();
        lblJam.setFont(ThemeColor.FONT_BOLD);
        lblJam.setForeground(ThemeColor.TEXT_PRIMARY);
        updateTanggal();
        leftPanel.add(lblJam);

        add(leftPanel, BorderLayout.WEST);

        // 2. KANAN: TOTAL ORDERS, REPORT BUTTON, NOTIF, DAN AVATAR KASIR
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 2));
        rightPanel.setOpaque(false);

        // Counter: Total Orders
        JLabel lblOrders = new JLabel("Total : " + totalOrders + " Orders");
        lblOrders.setFont(ThemeColor.FONT_SMALL);
        lblOrders.setForeground(ThemeColor.TEXT_MUTED);
        rightPanel.add(lblOrders);

        // Tombol Pill "Report [icon]"
        JButton btnReport = new JButton("Report  📄") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(ThemeColor.BORDER_CARD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnReport.setFont(ThemeColor.FONT_SMALL);
        btnReport.setForeground(ThemeColor.TEXT_PRIMARY);
        btnReport.setContentAreaFilled(false);
        btnReport.setBorderPainted(false);
        btnReport.setFocusPainted(false);
        btnReport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReport.setPreferredSize(new Dimension(86, 32));
        btnReport.addActionListener(e -> {
            if (listener != null) listener.onReportClick();
        });
        rightPanel.add(btnReport);

        // Tombol Notifikasi Bell Bulat [🔔 1]
        JPanel notifBell = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(ThemeColor.BORDER_CARD);
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

                // Icon Lonceng
                g2.setColor(ThemeColor.TEXT_PRIMARY);
                g2.setFont(ThemeColor.FONT_SMALL);
                g2.drawString("🔔", 9, 21);

                // Badge Titik Merah (1)
                g2.setColor(ThemeColor.DANGER_RED);
                g2.fillOval(20, 2, 10, 10);
                g2.dispose();
            }
        };
        notifBell.setPreferredSize(new Dimension(32, 32));
        notifBell.setOpaque(false);
        rightPanel.add(notifBell);

        // Avatar Kasir Bulat + Nama & Role
        JPanel profileBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        profileBox.setOpaque(false);

        // Foto / Avatar Kasir Bulat
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                File avatarFile = new File("assets/cashier.png");
                if (avatarFile.exists()) {
                    try {
                        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                        g2.drawImage(ImageIO.read(avatarFile), 0, 0, getWidth(), getHeight(), null);
                        g2.dispose();
                        return;
                    } catch (Exception ignored) {}
                }

                // Placeholder avatar realistis
                g2.setColor(new Color(230, 210, 195));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(ThemeColor.PRIMARY_DARK);
                g2.fillOval(10, 6, 14, 14); // Kepala
                g2.fillRoundRect(6, 20, 22, 14, 8, 8); // Badan
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(34, 34));
        avatarCircle.setOpaque(false);
        profileBox.add(avatarCircle);

        // Nama Kasir & Role di Bawahnya
        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);

        String namaKasir = (kasirAktif != null) ? kasirAktif.getNama() : "Ahmad Fauzi";
        String roleKasir = (kasirAktif != null) ? kasirAktif.getRole() : "Cashier";

        JLabel lblName = new JLabel(namaKasir);
        lblName.setFont(ThemeColor.FONT_BOLD);
        lblName.setForeground(ThemeColor.TEXT_PRIMARY);

        JLabel lblRole = new JLabel(roleKasir);
        lblRole.setFont(ThemeColor.FONT_TINY);
        lblRole.setForeground(ThemeColor.TEXT_MUTED);

        nameBox.add(lblName);
        nameBox.add(lblRole);
        profileBox.add(nameBox);

        rightPanel.add(profileBox);

        add(rightPanel, BorderLayout.EAST);
    }

    private void updateTanggal() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, dd MMMM", new Locale("id", "ID"));
        lblJam.setText(LocalDateTime.now().format(fmt));
    }
}
