package com.coffeeshop.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Design System 1:1 "Green Grounds Coffee POS" untuk "Kopi Kepikiran".
 * Menggunakan warna hijau primer #025726 yang segar dan tidak kusam,
 * dengan latar belakang ivory/cream dan kartu rounded presisi.
 */
public class ThemeColor {
    // Hijau Primer Segar (Sesuai Permintaan: #025726)
    public static final Color PRIMARY = new Color(2, 87, 38);            // #025726 - Vibrant Forest Emerald
    public static final Color PRIMARY_DARK = new Color(1, 68, 29);       // #01441D - Deep Green
    public static final Color PRIMARY_HOVER = new Color(4, 110, 48);     // #046E30 - Light Hover Green
    
    // Warna Aksen & Badge
    public static final Color ACCENT_SAGE = new Color(82, 183, 136);     // #52B788
    public static final Color ACCENT_LIGHT = new Color(232, 245, 233);   // #E8F5E9 - Light Mint Pill
    public static final Color BADGE_RED_BG = new Color(254, 226, 226);   // #FEE2E2 - Soft Red Pill
    public static final Color BADGE_RED_FG = new Color(220, 38, 38);     // #DC2626 - Alert Red Text
    public static final Color DANGER_RED = new Color(220, 38, 38);

    // Latar Belakang & Permukaan (1:1 Referensi Dribbble)
    public static final Color BG_CANVAS = new Color(88, 107, 88);        // #586B58 - Outer Sage Backdrop
    public static final Color BG_APP = new Color(244,242, 227);         // #f4f2e3 - Warm Ivory Cream
    public static final Color BG_CARD = Color.WHITE;                     // #FFFFFF - Pure White Card
    public static final Color BORDER_CARD = new Color(230, 235, 230);    // #E6EBE6 - Subtle Thin Border
    public static final Color BORDER_LIGHT = new Color(225, 230, 225);

    // Palet Teks
    public static final Color TEXT_PRIMARY = new Color(20, 28, 22);      // #141C16 - Dark Charcoal
    public static final Color TEXT_MUTED = new Color(112, 122, 115);     // #707A73 - Neutral Muted Gray
    public static final Color TEXT_LIGHT = Color.WHITE;

    // Tipografi
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 19);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_CARD_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font FONT_PRICE = new Font("Segoe UI", Font.BOLD, 14);
}
