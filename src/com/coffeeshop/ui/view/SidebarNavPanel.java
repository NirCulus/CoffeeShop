package com.coffeeshop.ui.view;

import com.coffeeshop.ui.ThemeColor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Sidebar Navigasi Kiri yang dapat di-minimize/expand (Collapsible Sidebar).
 * Memuat navigasi utama: Kasir POS, Manajemen Stok, dan Laporan Keuangan.
 */
public class SidebarNavPanel extends JPanel {
    private boolean isMinimized = false;
    private static final int WIDTH_EXPANDED = 175;
    private static final int WIDTH_MINIMIZED = 68;

    private List<NavButton> navButtons = new ArrayList<>();
    private NavButton activeButton;
    private OnNavSelectListener listener;
    private JButton btnToggle;

    public interface OnNavSelectListener {
        void onNavSelect(String targetPage);
    }

    public SidebarNavPanel(OnNavSelectListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeColor.BORDER_LIGHT));
        setPreferredSize(new Dimension(WIDTH_EXPANDED, 700));

        // 1. Top Bar: Tombol Minimize / Expand
        JPanel topTogglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        topTogglePanel.setOpaque(false);

        btnToggle = new JButton("◀ Minimize") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ThemeColor.ACCENT_LIGHT : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(ThemeColor.BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnToggle.setFont(ThemeColor.FONT_SMALL);
        btnToggle.setForeground(ThemeColor.TEXT_MUTED);
        btnToggle.setContentAreaFilled(false);
        btnToggle.setBorderPainted(false);
        btnToggle.setFocusPainted(false);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.setPreferredSize(new Dimension(145, 32));
        btnToggle.addActionListener(e -> toggleSidebar());
        topTogglePanel.add(btnToggle);

        add(topTogglePanel, BorderLayout.NORTH);

        // 2. Center: Tombol-tombol Navigasi
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        NavButton btnPos = new NavButton("POS", "Kasir POS", "PAGE_POS", true);
        NavButton btnStok = new NavButton("STOK", "Stok Bahan", "PAGE_STOK", false);
        NavButton btnLaporan = new NavButton("REKAP", "Keuangan", "PAGE_LAPORAN", false);

        navButtons.add(btnPos);
        navButtons.add(btnStok);
        navButtons.add(btnLaporan);
        activeButton = btnPos;

        for (NavButton btn : navButtons) {
            menuContainer.add(btn);
            menuContainer.add(Box.createVerticalStrut(10));
        }

        add(menuContainer, BorderLayout.CENTER);
    }

    private void toggleSidebar() {
        isMinimized = !isMinimized;
        if (isMinimized) {
            setPreferredSize(new Dimension(WIDTH_MINIMIZED, getHeight()));
            btnToggle.setText("▶");
            btnToggle.setPreferredSize(new Dimension(48, 32));
        } else {
            setPreferredSize(new Dimension(WIDTH_EXPANDED, getHeight()));
            btnToggle.setText("◀ Minimize");
            btnToggle.setPreferredSize(new Dimension(145, 32));
        }

        for (NavButton btn : navButtons) {
            btn.updateState(isMinimized);
        }

        revalidate();
        repaint();
    }

    private class NavButton extends JButton {
        private String badge;
        private String label;
        private String pageKey;
        private boolean active;

        public NavButton(String badge, String label, String pageKey, boolean active) {
            this.badge = badge;
            this.label = label;
            this.pageKey = pageKey;
            this.active = active;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(ThemeColor.FONT_BOLD);
            updateState(false);

            addActionListener(e -> {
                for (NavButton b : navButtons) {
                    b.setActive(false);
                }
                setActive(true);
                activeButton = this;
                if (listener != null) {
                    listener.onNavSelect(pageKey);
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        public void updateState(boolean minimized) {
            if (minimized) {
                setText(badge);
                setPreferredSize(new Dimension(50, 44));
                setMaximumSize(new Dimension(50, 44));
            } else {
                setText("  " + label);
                setHorizontalAlignment(JButton.LEFT);
                setPreferredSize(new Dimension(155, 44));
                setMaximumSize(new Dimension(155, 44));
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                g2.setColor(ThemeColor.PRIMARY_DARK);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                setForeground(Color.WHITE);
            } else {
                if (getModel().isRollover()) {
                    g2.setColor(ThemeColor.ACCENT_LIGHT);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                setForeground(ThemeColor.TEXT_PRIMARY);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
