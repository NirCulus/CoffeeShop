package com.coffeeshop.ui.component;

import com.coffeeshop.model.MenuItem;
import com.coffeeshop.ui.ThemeColor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Kartu Menu Visual 1:1 Sesuai Referensi Green Grounds Coffee POS.
 * Menampilkan gambar produk di bagian atas, nama dan harga di kiri bawah,
 * serta tombol (+) berbentuk lingkaran putih dengan border hijau di kanan bawah.
 */
public class MenuCardPanel extends JPanel {
    private MenuItem item;
    private boolean hovered = false;
    private OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(MenuItem item);
    }

    public MenuCardPanel(MenuItem item, OnCardClickListener listener) {
        this.item = item;
        this.listener = listener;

        setLayout(new BorderLayout(0, 4));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setPreferredSize(new Dimension(175, 190));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 1. Gambar Produk di Bagian Atas
        BufferedImage prodImg = ProductImageHelper.getProductImage(item, 130, 130);
        JLabel lblImage = new JLabel(new ImageIcon(prodImg));
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        add(lblImage, BorderLayout.CENTER);

        // 2. Baris Bawah: Nama, Harga, dan Tombol (+) Lingkaran Putih Stroke Hijau
        JPanel bottomRow = new JPanel(new BorderLayout(6, 0));
        bottomRow.setOpaque(false);

        // Teks Nama & Harga di Kiri
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblNama = new JLabel(item.getNama());
        lblNama.setFont(ThemeColor.FONT_CARD_TITLE);
        lblNama.setForeground(ThemeColor.TEXT_PRIMARY);

        JLabel lblHarga = new JLabel(String.format("Rp %,.0f", item.getHargaDasar()));
        lblHarga.setFont(ThemeColor.FONT_SMALL);
        lblHarga.setForeground(ThemeColor.TEXT_MUTED);

        textPanel.add(lblNama);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblHarga);
        bottomRow.add(textPanel, BorderLayout.CENTER);

        // Tombol (+) Lingkaran Putih dengan Stroke Hijau (#025726)
        JButton btnAdd = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int d = Math.min(getWidth(), getHeight()) - 2;

                // Lingkaran Putih/Terang
                g2.setColor(getModel().isRollover() ? ThemeColor.ACCENT_LIGHT : Color.WHITE);
                g2.fillOval(1, 1, d, d);

                // Border/Stroke Hijau #025726
                g2.setColor(ThemeColor.PRIMARY);
                g2.drawOval(1, 1, d, d);

                // Tanda (+) Hijau
                g2.setFont(ThemeColor.FONT_BOLD);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth("+")) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("+", tx, ty);

                g2.dispose();
            }
        };
        btnAdd.setPreferredSize(new Dimension(32, 32));
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> triggerClick());
        bottomRow.add(btnAdd, BorderLayout.EAST);

        add(bottomRow, BorderLayout.SOUTH);

        // Mouse Listener untuk hover efek
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                triggerClick();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    private void triggerClick() {
        if (listener != null) {
            listener.onCardClick(item);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Kartu Putih Bersih dengan Sudut Membulat 22px
        g2.setColor(hovered ? new Color(252, 254, 252) : ThemeColor.BG_CARD);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);

        // Border Halus (Lebih tebal dan hijau saat hover)
        g2.setColor(hovered ? ThemeColor.PRIMARY : ThemeColor.BORDER_CARD);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 22, 22);

        g2.dispose();
        super.paintComponent(g);
    }
}
