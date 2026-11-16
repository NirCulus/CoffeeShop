package com.coffeeshop.ui.view;

import com.coffeeshop.model.BahanBaku;
import com.coffeeshop.service.BahanBakuInventory;
import com.coffeeshop.ui.ThemeColor;
import com.coffeeshop.ui.component.RoundedPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Tampilan Manajemen Stok Bahan Baku Kafe (Inventory View).
 * Memuat kartu ringkasan status stok, tabel inventaris interaktif dengan pewarnaan alert,
 * dan form restock cepat.
 */
public class StokView extends JPanel {
    private BahanBakuInventory inventory;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cmbBahanRestock;
    private JTextField txtJumlahRestock;

    private JLabel lblTotalBahan;
    private JLabel lblBahanAman;
    private JLabel lblBahanMenipis;

    public StokView(BahanBakuInventory inventory) {
        this.inventory = inventory;

        setLayout(new BorderLayout(0, 14));
        setBackground(ThemeColor.BG_APP);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initUI();
        refreshTableData();
    }

    private void initUI() {
        // 1. Header & 3 Kartu Ringkasan Stok (KPI Cards)
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        JLabel lblTitle = new JLabel("Manajemen Inventaris Stok Bahan Baku");
        lblTitle.setFont(ThemeColor.FONT_TITLE);
        lblTitle.setForeground(ThemeColor.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Pantau sisa bahan kopi, susu, daging, bumbu, dan kemasan secara real-time");
        lblSub.setFont(ThemeColor.FONT_SMALL);
        lblSub.setForeground(ThemeColor.TEXT_MUTED);

        topContainer.add(lblTitle);
        topContainer.add(Box.createVerticalStrut(2));
        topContainer.add(lblSub);
        topContainer.add(Box.createVerticalStrut(14));

        // 3 Kartu KPI
        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 14, 0));
        kpiGrid.setOpaque(false);

        lblTotalBahan = new JLabel("0 Bahan");
        lblBahanAman = new JLabel("0 Bahan");
        lblBahanMenipis = new JLabel("0 Bahan");

        kpiGrid.add(createKpiCard("TOTAL BAHAN BAKU", lblTotalBahan, ThemeColor.PRIMARY_DARK));
        kpiGrid.add(createKpiCard("STOK AMAN", lblBahanAman, ThemeColor.PRIMARY));
        kpiGrid.add(createKpiCard("PERINGATAN MENIPIS", lblBahanMenipis, ThemeColor.DANGER_RED));

        topContainer.add(kpiGrid);
        topContainer.add(Box.createVerticalStrut(14));

        // Baris Pencarian
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.setFont(ThemeColor.FONT_REGULAR);
        txtSearch.putClientProperty("JTextField.placeholderText", "🔍 Cari nama bahan baku...");
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColor.BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshTableData();
            }
        });
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        topContainer.add(searchPanel);

        add(topContainer, BorderLayout.NORTH);

        // 2. Tabel Inventaris Modern (Center)
        String[] columns = {"ID Bahan", "Nama Bahan Baku", "Sisa Stok", "Satuan", "Batas Minimum", "Status Ketersediaan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(ThemeColor.FONT_REGULAR);
        table.getTableHeader().setFont(ThemeColor.FONT_BOLD);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(ThemeColor.PRIMARY_DARK);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 243, 240));

        // Custom Renderer untuk Cell Highlighting warna merah jika stok menipis
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getModel().getValueAt(row, 5);

                if (!isSelected) {
                    if ("⚠️ STOK MENIPIS".equals(status)) {
                        c.setBackground(new Color(254, 242, 242)); // Merah sangat soft
                        c.setForeground(ThemeColor.DANGER_RED);
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 252, 250));
                        c.setForeground(ThemeColor.TEXT_PRIMARY);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeColor.BORDER_LIGHT, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Form Cepat Restock Bahan Baku (South)
        JPanel bottomRestockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        bottomRestockPanel.setBackground(Color.WHITE);
        bottomRestockPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColor.BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblRestockTitle = new JLabel("Tambah Stok (Restock):");
        lblRestockTitle.setFont(ThemeColor.FONT_BOLD);
        lblRestockTitle.setForeground(ThemeColor.PRIMARY_DARK);

        cmbBahanRestock = new JComboBox<>();
        cmbBahanRestock.setFont(ThemeColor.FONT_SMALL);
        cmbBahanRestock.setPreferredSize(new Dimension(240, 32));

        txtJumlahRestock = new JTextField();
        txtJumlahRestock.setFont(ThemeColor.FONT_REGULAR);
        txtJumlahRestock.setPreferredSize(new Dimension(90, 32));
        txtJumlahRestock.putClientProperty("JTextField.placeholderText", "Jumlah");

        JButton btnRestock = new JButton("+ Tambah Stok") {
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
        btnRestock.setPreferredSize(new Dimension(130, 34));
        btnRestock.setContentAreaFilled(false);
        btnRestock.setBorderPainted(false);
        btnRestock.setFocusPainted(false);
        btnRestock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRestock.addActionListener(e -> executeRestock());

        bottomRestockPanel.add(lblRestockTitle);
        bottomRestockPanel.add(cmbBahanRestock);
        bottomRestockPanel.add(txtJumlahRestock);
        bottomRestockPanel.add(btnRestock);

        add(bottomRestockPanel, BorderLayout.SOUTH);
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

    public void refreshTableData() {
        tableModel.setRowCount(0);
        cmbBahanRestock.removeAllItems();

        String q = (txtSearch != null) ? txtSearch.getText().trim().toLowerCase() : "";
        int total = 0;
        int aman = 0;
        int menipis = 0;

        for (BahanBaku bb : inventory.getAllBahanBaku()) {
            total++;
            boolean isLow = bb.isLowStock();
            if (isLow) menipis++;
            else aman++;

            cmbBahanRestock.addItem(bb.getId() + " - " + bb.getNama());

            if (!q.isEmpty() && !bb.getNama().toLowerCase().contains(q) && !bb.getId().toLowerCase().contains(q)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                bb.getId(),
                bb.getNama(),
                String.format("%,.1f", bb.getStok()),
                bb.getSatuan(),
                String.format("%,.1f", bb.getStokMinimum()),
                isLow ? "⚠️ STOK MENIPIS" : "✓ Aman"
            });
        }

        lblTotalBahan.setText(total + " Bahan");
        lblBahanAman.setText(aman + " Bahan");
        lblBahanMenipis.setText(menipis + " Bahan");
    }

    private void executeRestock() {
        String selected = (String) cmbBahanRestock.getSelectedItem();
        if (selected == null) return;

        String idBahan = selected.split(" - ")[0];
        try {
            double jml = Double.parseDouble(txtJumlahRestock.getText().trim());
            if (jml <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah restock harus lebih dari 0!", "Input Salah", JOptionPane.WARNING_MESSAGE);
                return;
            }
            inventory.restock(idBahan, jml);
            txtJumlahRestock.setText("");
            refreshTableData();
            JOptionPane.showMessageDialog(this, "Stok berhasil ditambahkan!", "Sukses Restock", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka jumlah yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Menyimpan Stok", JOptionPane.ERROR_MESSAGE);
        }
    }
}
