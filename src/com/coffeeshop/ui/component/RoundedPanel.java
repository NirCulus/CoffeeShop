package com.coffeeshop.ui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Panel dengan sudut membulat (Rounded Corners) dan antialiasing halus.
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private int borderWidth;

    public RoundedPanel(int radius, Color bgColor) {
        this(radius, bgColor, null, 0);
    }

    public RoundedPanel(int radius, Color bgColor, Color borderColor, int borderWidth) {
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        setOpaque(false);
    }

    public void setBackgroundColor(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.borderColor = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gambar background membulat
        if (backgroundColor != null) {
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        // Gambar border jika ada
        if (borderColor != null && borderWidth > 0) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
