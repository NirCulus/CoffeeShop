package com.coffeeshop.ui.component;

import com.coffeeshop.ui.ThemeColor;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Komponen Tombol Berbentuk Kapsul/Pill (Pill Button) khas Green Grounds POS.
 * Mendukung state aktif/inaktif dan transisi warna hover yang mulus.
 */
public class PillButton extends JButton {
    private boolean active = false;
    private boolean hovered = false;
    private Color activeBg = ThemeColor.PRIMARY_DARK;
    private Color activeFg = Color.WHITE;
    private Color inactiveBg = Color.WHITE;
    private Color inactiveFg = ThemeColor.TEXT_PRIMARY;
    private Color borderColor = ThemeColor.BORDER_LIGHT;
    private int radius = 24;

    public PillButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(ThemeColor.FONT_BOLD.deriveFont(14f));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
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

    public PillButton(String text, boolean active) {
        this(text);
        setActive(active);
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    public void setCustomColors(Color activeBg, Color activeFg, Color inactiveBg, Color inactiveFg) {
        this.activeBg = activeBg;
        this.activeFg = activeFg;
        this.inactiveBg = inactiveBg;
        this.inactiveFg = inactiveFg;
        repaint();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Tentukan warna background
        Color currentBg;
        if (active) {
            currentBg = hovered ? ThemeColor.PRIMARY : activeBg;
            setForeground(activeFg);
        } else {
            currentBg = hovered ? ThemeColor.ACCENT_LIGHT : inactiveBg;
            setForeground(inactiveFg);
        }

        // Gambar body pill
        g2.setColor(currentBg);
        g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

        // Gambar border pill jika inaktif
        if (!active) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
