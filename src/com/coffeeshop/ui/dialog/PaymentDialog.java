package com.coffeeshop.ui.dialog;

import com.coffeeshop.model.MetodePembayaran;
import com.coffeeshop.model.Transaksi;
import com.coffeeshop.service.DatabaseService;
import com.coffeeshop.service.LaporanKeuanganService;
import com.coffeeshop.service.OrderCart;
import com.coffeeshop.ui.ThemeColor;
import com.coffeeshop.ui.component.PillButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Modal Pop-Up Pembayaran Kasir (Payment Modal).
 * Mendukung metode Tunai (dengan quick cash buttons & kembalian) dan QRIS.
 */
public class PaymentDialog extends JDialog {
    private OrderCart cart;
    private String idKasir;
    private LaporanKeuanganService laporanService;
    private DatabaseService database;
    private boolean transactionCompleted = false;
    private Transaksi resultingTransaction;

    private MetodePembayaran selectedMethod = MetodePembayaran.TUNAI;
    private double totalTagihan;
    private double uangDiterima = 0;
    private double uangKembalian = 0;

    private JTextField txtBayar;
    private JLabel lblKembalian;
    private PillButton btnTunai;
    private PillButton btnQris;
    private JPanel cashSection;

    public PaymentDialog(Frame parent, OrderCart cart, String idKasir, LaporanKeuanganService laporanService) {
        this(parent, cart, idKasir, laporanService, null);
    }

    public PaymentDialog(Frame parent, OrderCart cart, String idKasir,
                         LaporanKeuanganService laporanService, DatabaseService database) {
        super(parent, "Pembayaran Kasir - Kopi Kepikiran", true);
        this.cart = cart;
        this.idKasir = idKasir;
        this.laporanService = laporanService;
        this.database = database;
        this.totalTagihan = cart.hitungTotalAkhir();
        this.uangDiterima = totalTagihan;

        setLayout(new BorderLayout());
        setSize(480, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(ThemeColor.BG_APP);

        initUI();
    }

    private void initUI() {
        // 1. Header: Total Pembayaran
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblSub = new JLabel("TOTAL TAGIHAN PEMBAYARAN");
        lblSub.setFont(ThemeColor.FONT_SMALL);
        lblSub.setForeground(ThemeColor.TEXT_MUTED);

        JLabel lblTotal = new JLabel(String.format("Rp %,.0f", totalTagihan));
        lblTotal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        lblTotal.setForeground(ThemeColor.PRIMARY_DARK);

        headerPanel.add(lblSub);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblTotal);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center: Pilihan Metode Pembayaran & Input Bayar
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Metode Bayar Pills
        JLabel lblMetode = new JLabel("Pilih Metode Pembayaran:");
        lblMetode.setFont(ThemeColor.FONT_BOLD);
        lblMetode.setForeground(ThemeColor.TEXT_PRIMARY);
        centerPanel.add(lblMetode);
        centerPanel.add(Box.createVerticalStrut(8));

        JPanel pnlMetode = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlMetode.setOpaque(false);
        btnTunai = new PillButton("💵 Tunai (Cash)", true);
        btnQris = new PillButton("📱 QRIS / E-Wallet", false);

        btnTunai.addActionListener(e -> {
            btnTunai.setActive(true);
            btnQris.setActive(false);
            selectedMethod = MetodePembayaran.TUNAI;
            cashSection.setVisible(true);
            calculateChange();
        });

        btnQris.addActionListener(e -> {
            btnTunai.setActive(false);
            btnQris.setActive(true);
            selectedMethod = MetodePembayaran.QRIS;
            cashSection.setVisible(false);
            uangDiterima = totalTagihan;
            uangKembalian = 0;
            lblKembalian.setText("Rp 0 (Pembayaran Digital Otomatis)");
        });

        pnlMetode.add(btnTunai);
        pnlMetode.add(btnQris);
        centerPanel.add(pnlMetode);
        centerPanel.add(Box.createVerticalStrut(16));

        // Bagian Khusus Tunai (Quick Cash Buttons + Kembalian)
        cashSection = new JPanel();
        cashSection.setLayout(new BoxLayout(cashSection, BoxLayout.Y_AXIS));
        cashSection.setOpaque(false);

        JLabel lblNominal = new JLabel("Uang yang Diterima (Rp):");
        lblNominal.setFont(ThemeColor.FONT_BOLD);
        lblNominal.setForeground(ThemeColor.TEXT_PRIMARY);
        cashSection.add(lblNominal);
        cashSection.add(Box.createVerticalStrut(6));

        txtBayar = new JTextField(String.format("%.0f", totalTagihan));
        txtBayar.setFont(ThemeColor.FONT_TITLE);
        txtBayar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColor.BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtBayar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });
        cashSection.add(txtBayar);
        cashSection.add(Box.createVerticalStrut(10));

        // Tombol Quick Cash
        JPanel quickCashPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        quickCashPanel.setOpaque(false);
        quickCashPanel.add(createQuickCashBtn("Uang Pas", totalTagihan));
        quickCashPanel.add(createQuickCashBtn("20.000", 20000));
        quickCashPanel.add(createQuickCashBtn("50.000", 50000));
        quickCashPanel.add(createQuickCashBtn("100.000", 100000));
        cashSection.add(quickCashPanel);
        cashSection.add(Box.createVerticalStrut(14));

        // Display Kembalian
        JLabel lblKembalianTitle = new JLabel("Uang Kembalian:");
        lblKembalianTitle.setFont(ThemeColor.FONT_BOLD);
        lblKembalianTitle.setForeground(ThemeColor.TEXT_PRIMARY);
        cashSection.add(lblKembalianTitle);
        cashSection.add(Box.createVerticalStrut(4));

        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(ThemeColor.FONT_TITLE);
        lblKembalian.setForeground(ThemeColor.PRIMARY);
        cashSection.add(lblKembalian);

        centerPanel.add(cashSection);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom: Tombol Selesaikan Pembayaran
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeColor.BORDER_LIGHT));

        JButton btnSubmit = new JButton("✓ Selesaikan Pembayaran & Cetak Struk") {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ThemeColor.PRIMARY : ThemeColor.PRIMARY_DARK);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeColor.FONT_BOLD);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnSubmit.setPreferredSize(new Dimension(380, 46));
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(e -> processPayment());
        bottomPanel.add(btnSubmit);

        add(bottomPanel, BorderLayout.SOUTH);
        calculateChange();
    }

    private JButton createQuickCashBtn(String text, double nominal) {
        JButton btn = new JButton(text);
        btn.setFont(ThemeColor.FONT_SMALL);
        btn.setForeground(ThemeColor.PRIMARY_DARK);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(ThemeColor.BORDER_LIGHT, 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 30));
        btn.addActionListener(e -> {
            txtBayar.setText(String.format("%.0f", nominal));
            calculateChange();
        });
        return btn;
    }

    private void calculateChange() {
        if (selectedMethod == MetodePembayaran.QRIS) {
            uangDiterima = totalTagihan;
            uangKembalian = 0;
            lblKembalian.setText("Rp 0");
            return;
        }

        try {
            String txt = txtBayar.getText().replaceAll("[^0-9]", "");
            uangDiterima = txt.isEmpty() ? 0 : Double.parseDouble(txt);
            uangKembalian = uangDiterima - totalTagihan;
            if (uangKembalian < 0) {
                lblKembalian.setText("Uang Kurang Rp " + String.format("%,.0f", Math.abs(uangKembalian)));
                lblKembalian.setForeground(ThemeColor.DANGER_RED);
            } else {
                lblKembalian.setText("Rp " + String.format("%,.0f", uangKembalian));
                lblKembalian.setForeground(ThemeColor.PRIMARY);
            }
        } catch (NumberFormatException ex) {
            lblKembalian.setText("Input tidak valid");
            lblKembalian.setForeground(ThemeColor.DANGER_RED);
        }
    }

    private void processPayment() {
        calculateChange();
        if (selectedMethod == MetodePembayaran.TUNAI && uangDiterima < totalTagihan) {
            JOptionPane.showMessageDialog(this, 
                "Uang yang diterima masih kurang dari total tagihan!", 
                "Pembayaran Kurang", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Generate Transaction ID unik
        String trxId = "TRX-" + UUID.randomUUID().toString();
        this.resultingTransaction = new Transaksi(
            trxId,
            idKasir,
            cart.getItems(),
            cart.getDiskonPersen(),
            uangDiterima,
            selectedMethod
        );

        if (database != null) {
            try {
                database.completeSale(resultingTransaction);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Transaksi tidak tersimpan dan pembayaran dibatalkan.\n" + ex.getMessage(),
                    "Gagal Menyimpan Transaksi",
                    JOptionPane.ERROR_MESSAGE
                );
                resultingTransaction = null;
                return;
            }
        }

        laporanService.catatTransaksi(resultingTransaction);
        transactionCompleted = true;

        // Tampilkan Struk Digital Dialog
        showReceiptDialog(resultingTransaction);
        dispose();
    }

    private void showReceiptDialog(Transaksi trx) {
        JDialog receiptDialog = new JDialog(this, "Struk Pembayaran Digital - Kopi Kepikiran", true);
        receiptDialog.setSize(440, 327);
        receiptDialog.setLocationRelativeTo(this);
        receiptDialog.setLayout(new BorderLayout());

        JTextArea txtReceipt = new JTextArea(trx.cetakStrukTeks());
        txtReceipt.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 12));
        txtReceipt.setEditable(false);
        txtReceipt.setLineWrap(true);
        txtReceipt.setWrapStyleWord(true);
        txtReceipt.setBackground(Color.WHITE);
        txtReceipt.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane scroll = new JScrollPane(txtReceipt,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        receiptDialog.add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Tutup & Siap Pesanan Baru");
        btnClose.setFont(ThemeColor.FONT_BOLD);
        btnClose.setBackground(ThemeColor.PRIMARY_DARK);
        btnClose.setForeground(Color.WHITE);
        btnClose.setPreferredSize(new Dimension(400, 40));
        btnClose.addActionListener(e -> receiptDialog.dispose());
        receiptDialog.add(btnClose, BorderLayout.SOUTH);

        receiptDialog.setVisible(true);
    }

    public boolean isTransactionCompleted() {
        return transactionCompleted;
    }

    public Transaksi getResultingTransaction() {
        return resultingTransaction;
    }
}
