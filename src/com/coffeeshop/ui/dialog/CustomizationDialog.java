package com.coffeeshop.ui.dialog;

import com.coffeeshop.model.*;
import com.coffeeshop.ui.ThemeColor;
import com.coffeeshop.ui.component.PillButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Modal Pop-Up Kustomisasi Menu (1:1 Bersih, Responsif, Tanpa Horizontal Scroll).
 * Teks dan opsi otomatis membungkus (word-wrap) sesuai lebar jendela modal.
 */
public class CustomizationDialog extends JDialog {
    private MenuItem originalItem;
    private MenuItem customizedItem;
    private boolean confirmed = false;

    private List<Topping> masterTopping;

    private int selectedPedas = 0;
    private boolean selectedHangat = true;

    private String selectedSize = "Regular";
    private String selectedSugar = "100%";
    private String selectedIce = "Normal";

    private JenisPenyajian selectedPenyajian = JenisPenyajian.DINE_IN;
    private List<Topping> selectedToppings = new ArrayList<>();

    private JLabel lblTotalHarga;
    private JButton btnConfirm;

    public CustomizationDialog(Frame parent, MenuItem item, List<Topping> masterTopping) {
        super(parent, "Kustomisasi: " + item.getNama(), true);
        this.originalItem = item;
        this.masterTopping = masterTopping;

        if (item instanceof Makanan) {
            this.customizedItem = ((Makanan) item).copy();
        } else if (item instanceof Minuman) {
            this.customizedItem = ((Minuman) item).copy();
        }

        setLayout(new BorderLayout());
        setSize(420, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(ThemeColor.BG_APP);

        initUI();
        updatePriceDisplay();
    }

    private void initUI() {
        // 1. Header Dialog
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.BORDER_CARD),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblTitle = new JLabel("<html><b>" + originalItem.getNama() + "</b></html>");
        lblTitle.setFont(ThemeColor.FONT_TITLE);
        lblTitle.setForeground(ThemeColor.PRIMARY);

        JLabel lblKategori = new JLabel(originalItem.getTipeMenu().getLabel() + " • " + originalItem.getSubKategoriLabel());
        lblKategori.setFont(ThemeColor.FONT_SMALL);
        lblKategori.setForeground(ThemeColor.TEXT_MUTED);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(lblTitle);
        headerText.add(lblKategori);

        headerPanel.add(headerText, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Konten Pilihan (Vertikal Saja, Tidak Ada Scroll Horisontal)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // A. Opsi Penyajian
        contentPanel.add(createSectionLabel("Jenis Penyajian:"));
        JPanel pnlPenyajian = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pnlPenyajian.setOpaque(false);
        PillButton btnDineIn = new PillButton("Dine-In", true);
        PillButton btnTakeaway = new PillButton("Takeaway", false);
        btnDineIn.addActionListener(e -> {
            btnDineIn.setActive(true); btnTakeaway.setActive(false);
            selectedPenyajian = JenisPenyajian.DINE_IN;
        });
        btnTakeaway.addActionListener(e -> {
            btnTakeaway.setActive(true); btnDineIn.setActive(false);
            selectedPenyajian = JenisPenyajian.TAKEAWAY;
        });
        pnlPenyajian.add(btnDineIn);
        pnlPenyajian.add(btnTakeaway);
        contentPanel.add(pnlPenyajian);
        contentPanel.add(Box.createVerticalStrut(10));

        // B. Opsi Minuman
        if (originalItem instanceof Minuman) {
            contentPanel.add(createSectionLabel("Ukuran Cup:"));
            JPanel pnlSize = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            pnlSize.setOpaque(false);
            PillButton btnReg = new PillButton("Regular", true);
            PillButton btnLarge = new PillButton("Large (+Rp 4.000)", false);
            btnReg.addActionListener(e -> {
                btnReg.setActive(true); btnLarge.setActive(false);
                selectedSize = "Regular";
                updatePriceDisplay();
            });
            btnLarge.addActionListener(e -> {
                btnLarge.setActive(true); btnReg.setActive(false);
                selectedSize = "Large";
                updatePriceDisplay();
            });
            pnlSize.add(btnReg);
            pnlSize.add(btnLarge);
            contentPanel.add(pnlSize);
            contentPanel.add(Box.createVerticalStrut(8));

            contentPanel.add(createSectionLabel("Sugar Level:"));
            JPanel pnlSugar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            pnlSugar.setOpaque(false);
            PillButton btnSugarNormal = new PillButton("Normal", true);
            PillButton btnSugarLess = new PillButton("Less Sugar", false);
            PillButton btnSugarNo = new PillButton("No Sugar", false);
            btnSugarNormal.addActionListener(e -> {
                btnSugarNormal.setActive(true); btnSugarLess.setActive(false); btnSugarNo.setActive(false);
                selectedSugar = "100%";
            });
            btnSugarLess.addActionListener(e -> {
                btnSugarNormal.setActive(false); btnSugarLess.setActive(true); btnSugarNo.setActive(false);
                selectedSugar = "50%";
            });
            btnSugarNo.addActionListener(e -> {
                btnSugarNormal.setActive(false); btnSugarLess.setActive(false); btnSugarNo.setActive(true);
                selectedSugar = "0%";
            });
            pnlSugar.add(btnSugarNormal);
            pnlSugar.add(btnSugarLess);
            pnlSugar.add(btnSugarNo);
            contentPanel.add(pnlSugar);
            contentPanel.add(Box.createVerticalStrut(8));

            contentPanel.add(createSectionLabel("Ice Level:"));
            JPanel pnlIce = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            pnlIce.setOpaque(false);
            PillButton btnIceNormal = new PillButton("Normal Ice", true);
            PillButton btnIceLess = new PillButton("Less Ice", false);
            PillButton btnIceNone = new PillButton("No Ice", false);
            btnIceNormal.addActionListener(e -> {
                btnIceNormal.setActive(true); btnIceLess.setActive(false); btnIceNone.setActive(false);
                selectedIce = "Normal";
            });
            btnIceLess.addActionListener(e -> {
                btnIceNormal.setActive(false); btnIceLess.setActive(true); btnIceNone.setActive(false);
                selectedIce = "Less Ice";
            });
            btnIceNone.addActionListener(e -> {
                btnIceNormal.setActive(false); btnIceLess.setActive(false); btnIceNone.setActive(true);
                selectedIce = "No Ice";
            });
            pnlIce.add(btnIceNormal);
            pnlIce.add(btnIceLess);
            pnlIce.add(btnIceNone);
            contentPanel.add(pnlIce);
            contentPanel.add(Box.createVerticalStrut(8));
        }

        // C. Opsi Makanan
        if (originalItem instanceof Makanan) {
            contentPanel.add(createSectionLabel("Tingkat Kepedasan:"));
            JPanel pnlPedas = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            pnlPedas.setOpaque(false);
            PillButton btnP0 = new PillButton("Lvl 0 (Tidak)", true);
            PillButton btnP1 = new PillButton("Lvl 1", false);
            PillButton btnP2 = new PillButton("Lvl 2", false);
            PillButton btnP3 = new PillButton("Lvl 3", false);

            btnP0.addActionListener(e -> {
                btnP0.setActive(true); btnP1.setActive(false); btnP2.setActive(false); btnP3.setActive(false);
                selectedPedas = 0;
            });
            btnP1.addActionListener(e -> {
                btnP0.setActive(false); btnP1.setActive(true); btnP2.setActive(false); btnP3.setActive(false);
                selectedPedas = 1;
            });
            btnP2.addActionListener(e -> {
                btnP0.setActive(false); btnP1.setActive(false); btnP2.setActive(true); btnP3.setActive(false);
                selectedPedas = 2;
            });
            btnP3.addActionListener(e -> {
                btnP0.setActive(false); btnP1.setActive(false); btnP2.setActive(false); btnP3.setActive(true);
                selectedPedas = 3;
            });

            pnlPedas.add(btnP0);
            pnlPedas.add(btnP1);
            pnlPedas.add(btnP2);
            pnlPedas.add(btnP3);
            contentPanel.add(pnlPedas);
            contentPanel.add(Box.createVerticalStrut(8));

            contentPanel.add(createSectionLabel("Suhu Makanan:"));
            JPanel pnlHangat = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            pnlHangat.setOpaque(false);
            PillButton btnHangat = new PillButton("Hangat", true);
            PillButton btnBiasa = new PillButton("Suhu Ruang", false);
            btnHangat.addActionListener(e -> {
                btnHangat.setActive(true); btnBiasa.setActive(false);
                selectedHangat = true;
            });
            btnBiasa.addActionListener(e -> {
                btnHangat.setActive(false); btnBiasa.setActive(true);
                selectedHangat = false;
            });
            pnlHangat.add(btnHangat);
            pnlHangat.add(btnBiasa);
            contentPanel.add(pnlHangat);
            contentPanel.add(Box.createVerticalStrut(8));
        }

        // D. Topping / Saus Tambahan (Word-Wrap)
        contentPanel.add(createSectionLabel("Pilihan Topping & Saus:"));
        JPanel pnlToppings = new JPanel(new GridLayout(0, 1, 4, 4));
        pnlToppings.setOpaque(false);

        for (Topping t : masterTopping) {
            JCheckBox cb = new JCheckBox(t.getNama() + " (+Rp " + String.format("%,.0f", t.getHargaTambahan()) + ")");
            cb.setFont(ThemeColor.FONT_SMALL);
            cb.setForeground(ThemeColor.TEXT_PRIMARY);
            cb.setOpaque(false);
            cb.addActionListener(e -> {
                if (cb.isSelected()) {
                    selectedToppings.add(t);
                } else {
                    selectedToppings.remove(t);
                }
                updatePriceDisplay();
            });
            pnlToppings.add(cb);
        }
        contentPanel.add(pnlToppings);

        // Hanya aktifkan vertical scroll, KUNCI horizontal scroll agar tidak memanjang kesamping
        JScrollPane scrollPane = new JScrollPane(contentPanel, 
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Bottom: Total Harga & Tombol Tambah ke Keranjang
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeColor.BORDER_CARD),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        JPanel priceInfo = new JPanel();
        priceInfo.setLayout(new BoxLayout(priceInfo, BoxLayout.Y_AXIS));
        priceInfo.setOpaque(false);

        JLabel lblSub = new JLabel("Total Harga");
        lblSub.setFont(ThemeColor.FONT_TINY);
        lblSub.setForeground(ThemeColor.TEXT_MUTED);

        lblTotalHarga = new JLabel();
        lblTotalHarga.setFont(ThemeColor.FONT_TITLE);
        lblTotalHarga.setForeground(ThemeColor.PRIMARY);

        priceInfo.add(lblSub);
        priceInfo.add(lblTotalHarga);
        bottomPanel.add(priceInfo, BorderLayout.WEST);

        btnConfirm = new JButton("Tambah Pesanan 🛒") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ThemeColor.PRIMARY_HOVER : ThemeColor.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeColor.FONT_BOLD);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnConfirm.setPreferredSize(new Dimension(190, 42));
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> applyAndClose());
        bottomPanel.add(btnConfirm, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeColor.FONT_BOLD);
        lbl.setForeground(ThemeColor.TEXT_PRIMARY);
        return lbl;
    }

    private void updatePriceDisplay() {
        double total = originalItem.getHargaDasar();
        if (originalItem instanceof Minuman && "Large".equalsIgnoreCase(selectedSize)) {
            total += Minuman.BIAYA_LARGE;
        }
        for (Topping t : selectedToppings) {
            total += t.getHargaTambahan();
        }
        lblTotalHarga.setText(String.format("Rp %,.0f", total));
    }

    private void applyAndClose() {
        customizedItem.setPenyajian(selectedPenyajian);
        customizedItem.clearTopping();
        for (Topping t : selectedToppings) {
            customizedItem.addTopping(t);
        }

        if (customizedItem instanceof Minuman) {
            Minuman m = (Minuman) customizedItem;
            m.setSize(selectedSize);
            m.setSugarLevel(selectedSugar);
            m.setIceLevel(selectedIce);
        } else if (customizedItem instanceof Makanan) {
            Makanan mk = (Makanan) customizedItem;
            mk.setLevelPedas(selectedPedas);
            mk.setDipanaskan(selectedHangat);
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public MenuItem getCustomizedItem() {
        return customizedItem;
    }
}
