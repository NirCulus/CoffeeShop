package com.coffeeshop.ui.view;

import com.coffeeshop.model.RingkasanKeuanganDTO;
import com.coffeeshop.model.Transaksi;
import com.coffeeshop.service.LaporanKeuanganService;
import com.coffeeshop.ui.ThemeColor;
import com.coffeeshop.ui.component.RoundedPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 * Tampilan Laporan Keuangan & Rekap Transaksi Harian "Kopi Kepikiran".
 * Menampilkan ringkasan omzet bersih, pajak PB1 resto, settlement Tunai vs QRIS,
 * serta histori transaksi lengkap dan fitur cetak ulang/ekspor.
 */
public class LaporanView extends JPanel {
    private LaporanKeuanganService laporanService;
    private DefaultTableModel tableModel;
    private JTable table;

    private JLabel lblTotalOmzet;
    private JLabel lblTotalTransaksi;
    private JLabel lblSettlementKas;

    public LaporanView(LaporanKeuanganService laporanService) {
        this.laporanService = laporanService;

        setLayout(new BorderLayout(0, 14));
        setBackground(ThemeColor.BG_APP);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initUI();
        refreshReportData();
    }

    private void initUI() {
        // 1. Header & 3 Kartu Statistik Keuangan
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        JLabel lblTitle = new JLabel("Rekapitulasi Laporan Keuangan & Transaksi");
        lblTitle.setFont(ThemeColor.FONT_TITLE);
        lblTitle.setForeground(ThemeColor.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Rekapitulasi omzet penjualan harian, kewajiban PB1 restoran 10%, dan settlement kasir");
        lblSub.setFont(ThemeColor.FONT_SMALL);
        lblSub.setForeground(ThemeColor.TEXT_MUTED);

        topContainer.add(lblTitle);
        topContainer.add(Box.createVerticalStrut(2));
        topContainer.add(lblSub);
        topContainer.add(Box.createVerticalStrut(14));

        // 3 Kartu Finansial
        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 14, 0));
        kpiGrid.setOpaque(false);

        lblTotalOmzet = new JLabel("Rp 0");
        lblTotalTransaksi = new JLabel("0 Transaksi");
        lblSettlementKas = new JLabel("Tunai: Rp 0 | QRIS: Rp 0");

        kpiGrid.add(createKpiCard("TOTAL OMZET BERSIH", lblTotalOmzet, ThemeColor.PRIMARY_DARK));
        kpiGrid.add(createKpiCard("TRANSAKSI BERHASIL", lblTotalTransaksi, ThemeColor.PRIMARY));
        kpiGrid.add(createKpiCard("SETTLEMENT KAS MASUK", lblSettlementKas, ThemeColor.ACCENT_SAGE));

        topContainer.add(kpiGrid);
        add(topContainer, BorderLayout.NORTH);

        // 2. Tabel Histori Transaksi (Center)
        String[] columns = {"ID Transaksi", "Waktu", "Kasir ID", "Pembayaran", "Subtotal", "Diskon", "PB1 Resto (10%)", "Total Bersih"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(ThemeColor.FONT_REGULAR);
        table.getTableHeader().setFont(ThemeColor.FONT_BOLD);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(ThemeColor.PRIMARY_DARK);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 243, 240));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeColor.BORDER_LIGHT, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Tombol Aksi di Bawah: Cetak Ulang Struk & Ekspor File
        JPanel bottomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomActionPanel.setBackground(Color.WHITE);
        bottomActionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColor.BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JButton btnLihatStruk = new JButton("🖨️ Cetak Ulang Struk Terpilih");
        btnLihatStruk.setFont(ThemeColor.FONT_BOLD);
        btnLihatStruk.setForeground(ThemeColor.PRIMARY_DARK);
        btnLihatStruk.setBackground(ThemeColor.ACCENT_LIGHT);
        btnLihatStruk.setBorder(BorderFactory.createLineBorder(ThemeColor.ACCENT_SAGE, 1, true));
        btnLihatStruk.setFocusPainted(false);
        btnLihatStruk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLihatStruk.setPreferredSize(new Dimension(210, 36));
        btnLihatStruk.addActionListener(e -> showSelectedReceipt());

        JButton btnEkspor = new JButton("📄 Ekspor Laporan Rekap (.TXT)") {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ThemeColor.PRIMARY : ThemeColor.PRIMARY_DARK);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeColor.FONT_BOLD);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnEkspor.setPreferredSize(new Dimension(220, 36));
        btnEkspor.setContentAreaFilled(false);
        btnEkspor.setBorderPainted(false);
        btnEkspor.setFocusPainted(false);
        btnEkspor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEkspor.addActionListener(e -> exportReportToFile());

        bottomActionPanel.add(btnLihatStruk);
        bottomActionPanel.add(btnEkspor);

        add(bottomActionPanel, BorderLayout.SOUTH);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor) {
        RoundedPanel card = new RoundedPanel(14, Color.WHITE, ThemeColor.BORDER_LIGHT, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ThemeColor.FONT_SMALL);
        lblTitle.setForeground(ThemeColor.TEXT_MUTED);

        valueLabel.setFont(ThemeColor.FONT_TITLE);
        valueLabel.setForeground(accentColor);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        return card;
    }

    public void refreshReportData() {
        tableModel.setRowCount(0);
        List<Transaksi> list = laporanService.getRiwayatTransaksi();

        RingkasanKeuanganDTO dto = laporanService.buatRingkasanHarian(LocalDate.now());

        lblTotalOmzet.setText(String.format("Rp %,.0f", dto.getTotalOmzetBersih()));
        lblTotalTransaksi.setText(dto.getTotalTransaksi() + " Transaksi");
        lblSettlementKas.setText(String.format("Tunai: Rp %,.0f | QRIS: Rp %,.0f", dto.getTotalTunai(), dto.getTotalQRIS()));

        for (Transaksi t : list) {
            tableModel.addRow(new Object[]{
                t.getIdTransaksi(),
                t.getWaktuFormatted(),
                t.getIdKasir(),
                t.getMetodePembayaran().getLabel(),
                String.format("Rp %,.0f", t.getSubtotal()),
                t.getNominalDiskon() > 0 ? String.format("-Rp %,.0f", t.getNominalDiskon()) : "Rp 0",
                String.format("Rp %,.0f", t.getNominalPajak()),
                String.format("Rp %,.0f", t.getTotalAkhir())
            });
        }
    }

    private void showSelectedReceipt() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu baris transaksi pada tabel terlebih dahulu!", "Pilih Transaksi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idTrx = (String) tableModel.getValueAt(row, 0);
        Transaksi selectedTrx = null;
        for (Transaksi t : laporanService.getRiwayatTransaksi()) {
            if (t.getIdTransaksi().equals(idTrx)) {
                selectedTrx = t;
                break;
            }
        }

        if (selectedTrx != null) {
            JDialog dialog = new JDialog((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), "Cetak Ulang Struk - " + idTrx, true);
            dialog.setSize(440, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JTextArea txt = new JTextArea(selectedTrx.cetakStrukTeks());
            txt.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 12));
            txt.setEditable(false);
            txt.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            dialog.add(new JScrollPane(txt), BorderLayout.CENTER);

            JButton btnClose = new JButton("Tutup");
            btnClose.addActionListener(e -> dialog.dispose());
            dialog.add(btnClose, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }
    }

    private void exportReportToFile() {
        RingkasanKeuanganDTO dto = laporanService.buatRingkasanHarian(LocalDate.now());
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Laporan_Keuangan_Kopi_Kepikiran_" + LocalDate.now() + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(fileToSave)) {
                fw.write(dto.formatLaporanTeks());
                fw.write("\nRINCIAN SELURUH TRANSAKSI:\n");
                for (Transaksi t : laporanService.getRiwayatTransaksi()) {
                    fw.write(String.format("- %s | %s | Kasir: %s | %s | Total: Rp %,.0f\n",
                        t.getIdTransaksi(), t.getWaktuFormatted(), t.getIdKasir(), 
                        t.getMetodePembayaran().getLabel(), t.getTotalAkhir()));
                }
                JOptionPane.showMessageDialog(this, "Laporan keuangan berhasil disimpan ke:\n" + fileToSave.getAbsolutePath(), "Sukses Ekspor", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
