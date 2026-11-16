package com.coffeeshop.ui.component;

import com.coffeeshop.model.OrderItem;
import com.coffeeshop.ui.ThemeColor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Komponen Baris Item di dalam Keranjang Pesanan (Order Cart Sidebar).
 * Memuat nama, rincian kustomisasi, tombol kuantitas [ - qty + ], dan tombol hapus.
 */
public class OrderItemPanel extends JPanel {
    private OrderItem orderItem;
    private OnOrderChangeListener listener;

    public interface OnOrderChangeListener {
        void onQuantityChanged(OrderItem item, int newQty);
        void onItemRemoved(OrderItem item);
    }

    public OrderItemPanel(OrderItem orderItem, OnOrderChangeListener listener) {
        this.orderItem = orderItem;
        this.listener = listener;

        setLayout(new BorderLayout(8, 4));
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 4, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColor.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            )
        ));

        // 1. Kiri: Info Nama Menu & Kustomisasi
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblNama = new JLabel("<html><body style='width: 140px;'><b>" + 
            orderItem.getItem().getNama() + "</b></body></html>");
        lblNama.setFont(ThemeColor.FONT_BOLD);
        lblNama.setForeground(ThemeColor.TEXT_PRIMARY);

        String detailText = orderItem.getItem().getDetailPesanan();
        if (detailText.length() > 50) {
            detailText = detailText.substring(0, 47) + "...";
        }
        JLabel lblDetail = new JLabel("<html><body style='width: 140px; color: #69756E; font-size: 9px;'>" + 
            detailText + "</body></html>");
        lblDetail.setFont(ThemeColor.FONT_SMALL);

        JLabel lblHarga = new JLabel(String.format("Rp %,.0f", orderItem.hitungSubtotal()));
        lblHarga.setFont(ThemeColor.FONT_BOLD);
        lblHarga.setForeground(ThemeColor.PRIMARY_DARK);

        infoPanel.add(lblNama);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(lblDetail);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(lblHarga);

        add(infoPanel, BorderLayout.CENTER);

        // 2. Kanan: Kontrol Kuantitas Pill [- qty +] & Tombol Hapus [X]
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setOpaque(false);

        // Tombol Hapus (x) di pojok atas
        JPanel delWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        delWrapper.setOpaque(false);

        JButton btnDel = new JButton("x");
        btnDel.setFont(ThemeColor.FONT_SMALL);
        btnDel.setForeground(ThemeColor.TEXT_MUTED);
        btnDel.setContentAreaFilled(false);
        btnDel.setBorderPainted(false);
        btnDel.setFocusPainted(false);
        btnDel.setMargin(new Insets(0, 0, 0, 0));
        btnDel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDel.addActionListener(e -> {
            if (listener != null) listener.onItemRemoved(orderItem);
        });
        delWrapper.add(btnDel);
        actionPanel.add(delWrapper);

        actionPanel.add(Box.createVerticalStrut(4));

        // Capsule Kuantitas [- qty +]
        JPanel qtyCapsule = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(ThemeColor.ACCENT_SAGE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        qtyCapsule.setOpaque(false);
        qtyCapsule.setPreferredSize(new Dimension(84, 26));

        JButton btnMinus = createPillSmallBtn("-");
        JLabel lblQty = new JLabel(String.valueOf(orderItem.getKuantitas()));
        lblQty.setFont(ThemeColor.FONT_BOLD);
        lblQty.setForeground(ThemeColor.PRIMARY_DARK);
        JButton btnPlus = createPillSmallBtn("+");

        btnMinus.addActionListener(e -> {
            int current = orderItem.getKuantitas();
            if (current > 1) {
                orderItem.setKuantitas(current - 1);
                if (listener != null) listener.onQuantityChanged(orderItem, current - 1);
            } else {
                if (listener != null) listener.onItemRemoved(orderItem);
            }
        });

        btnPlus.addActionListener(e -> {
            int current = orderItem.getKuantitas();
            orderItem.setKuantitas(current + 1);
            if (listener != null) listener.onQuantityChanged(orderItem, current + 1);
        });

        qtyCapsule.add(btnMinus);
        qtyCapsule.add(lblQty);
        qtyCapsule.add(btnPlus);

        actionPanel.add(qtyCapsule);
        add(actionPanel, BorderLayout.EAST);
    }

    private JButton createPillSmallBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(ThemeColor.FONT_BOLD);
        btn.setForeground(ThemeColor.PRIMARY_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(20, 20));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeColor.BG_CARD);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
