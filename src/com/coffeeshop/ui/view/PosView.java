package com.coffeeshop.ui.view;

import com.coffeeshop.model.*;
import com.coffeeshop.service.BahanBakuInventory;
import com.coffeeshop.service.DatabaseService;
import com.coffeeshop.service.LaporanKeuanganService;
import com.coffeeshop.service.OrderCart;
import com.coffeeshop.ui.ThemeColor;
import com.coffeeshop.ui.component.MenuCardPanel;
import com.coffeeshop.ui.component.PillButton;
import com.coffeeshop.ui.component.ProductImageHelper;
import com.coffeeshop.ui.dialog.CustomizationDialog;
import com.coffeeshop.ui.dialog.PaymentDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Tampilan Kasir POS 1:1 Sesuai Referensi Green Grounds Coffee POS.
 * Mengimplementasikan:
 * 1. Filter Kategori Hero Card (Coffee, Tea, Snack) + Submenu Pills.
 * 2. Menu Card Grid dengan Gambar Produk & Tombol (+) Putih Stroke Hijau.
 * 3. Right Sidebar Keranjang Pesanan 1:1 (Receipt #, Dine In/Take Away pills,
 *    Customer Name & Table, Order List dengan thumbnail & capsule simetris,
 *    Payment Details, dan Place Order pill button).
 * 4. KEDUA SIDEBAR BISA DI-MINIMIZE (Kiri & Kanan)!
 */
public class PosView extends JPanel {
    private List<MenuItem> masterMenu;
    private List<Topping> masterTopping;
    private BahanBakuInventory inventory;
    private LaporanKeuanganService laporanService;
    private DatabaseService database;
    private String idKasir;
    private Frame parentFrame;

    private OrderCart cart;

    // State Filter
    private String currentMainCategory = "COFFEE"; // "COFFEE", "TEA", "SNACK"
    private String currentSubCategory = "ALL";
    private String searchQuery = "";

    // State Minimizing Right Sidebar
    private boolean isCartMinimized = false;
    private JPanel rightCartPanel;
    private JPanel cartContentPanel;
    private JPanel cartMinimizedStrip;

    // Komponen Input Kasir
    private JTextField txtCustomerName;
    private JComboBox<String> cmbTable;
    private JenisPenyajian selectedPenyajian = JenisPenyajian.DINE_IN;

    // Komponen Menu
    private JTextField txtSearch;
    private JPanel categoryCardsContainer;
    private JPanel subcategoryPillPanel;
    private JPanel menuGridPanel;

    // Komponen Cart
    private JPanel cartItemsContainer;
    private JLabel lblSubtotalVal;
    private JLabel lblTaxVal;
    private JLabel lblTotalVal;
    private JButton btnPlaceOrder;

    public PosView(Frame parentFrame, List<MenuItem> masterMenu, List<Topping> masterTopping, 
                   BahanBakuInventory inventory, LaporanKeuanganService laporanService, String idKasir) {
        this(parentFrame, masterMenu, masterTopping, inventory, laporanService, idKasir, null);
    }

    public PosView(Frame parentFrame, List<MenuItem> masterMenu, List<Topping> masterTopping,
                   BahanBakuInventory inventory, LaporanKeuanganService laporanService,
                   String idKasir, DatabaseService database) {
        this.parentFrame = parentFrame;
        this.masterMenu = masterMenu;
        this.masterTopping = masterTopping;
        this.inventory = inventory;
        this.laporanService = laporanService;
        this.idKasir = idKasir;
        this.database = database;
        this.cart = new OrderCart();

        setLayout(new BorderLayout());
        setBackground(ThemeColor.BG_APP);

        initCatalogArea();
        initCartSidebar();

        refreshMenuGrid();
        refreshCartUI();
    }

    // =========================================================================
    // 1. AREA KATALOG TENGAH (Search + Category Cards + Submenu Pills + Grid)
    // =========================================================================
    private void initCatalogArea() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 18, 14, 18));

        // A. Top Header Area: Search Bar + 3 Kartu Kategori + Submenu Pills
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        // Search Bar Full Width dengan Icon Loop & Shortcut icon
        JPanel searchBar = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.setColor(ThemeColor.BORDER_CARD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        searchBar.setOpaque(false);
        searchBar.setPreferredSize(new Dimension(700, 44));
        searchBar.setMaximumSize(new Dimension(1000, 44));
        searchBar.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        JLabel lblSearch = new JLabel("🔍  Search");
        lblSearch.setFont(ThemeColor.FONT_REGULAR);
        lblSearch.setForeground(ThemeColor.TEXT_MUTED);

        txtSearch = new JTextField();
        txtSearch.setFont(ThemeColor.FONT_REGULAR);
        txtSearch.setBorder(null);
        txtSearch.setOpaque(false);
        //txtSearch.putClientProperty("JTextField.placeholderText", "Search menu...");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchQuery = txtSearch.getText().trim().toLowerCase();
                refreshMenuGrid();
            }
        });

        JLabel lblShortcut = new JLabel("⌘ K");
        lblShortcut.setFont(ThemeColor.FONT_SMALL);
        lblShortcut.setForeground(ThemeColor.TEXT_MUTED);

        searchBar.add(txtSearch, BorderLayout.CENTER);
        searchBar.add(lblShortcut, BorderLayout.EAST);
        topArea.add(searchBar);
        topArea.add(Box.createVerticalStrut(12));

        // 3 Kartu Kategori Hero (Coffee, Tea, Snack) Sesuai Referensi Dribbble
        categoryCardsContainer = new JPanel(new GridLayout(1, 3, 14, 0));
        categoryCardsContainer.setOpaque(false);
        categoryCardsContainer.setPreferredSize(new Dimension(700, 84));
        categoryCardsContainer.setMaximumSize(new Dimension(1000, 84));
        buildCategoryCards();
        topArea.add(categoryCardsContainer);
        topArea.add(Box.createVerticalStrut(10));

        // Submenu Pills Bar
        subcategoryPillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        subcategoryPillPanel.setOpaque(false);
        updateSubcategoryPills();
        topArea.add(subcategoryPillPanel);

        centerPanel.add(topArea, BorderLayout.NORTH);

        // B. Grid Kartu Menu
        menuGridPanel = new JPanel(new GridLayout(0, 4, 12, 12));
        menuGridPanel.setOpaque(false);

        JPanel gridWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        gridWrapper.setOpaque(false);
        gridWrapper.add(menuGridPanel);

        JScrollPane scrollMenu = new JScrollPane(gridWrapper);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(16);

        centerPanel.add(scrollMenu, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void buildCategoryCards() {
        categoryCardsContainer.removeAll();

        // Kartu 1: Coffee (Active / Selected)
        categoryCardsContainer.add(createCategoryHeroCard(
            "Coffee", "50 items", "Available", "COFFEE".equals(currentMainCategory), () -> {
                currentMainCategory = "COFFEE";
                currentSubCategory = "ALL";
                buildCategoryCards();
                updateSubcategoryPills();
                refreshMenuGrid();
            }
        ));

        // Kartu 2: Tea / Non-Coffee
        categoryCardsContainer.add(createCategoryHeroCard(
            "Tea & Drinks", "20 items", "Available", "TEA".equals(currentMainCategory), () -> {
                currentMainCategory = "TEA";
                currentSubCategory = "ALL";
                buildCategoryCards();
                updateSubcategoryPills();
                refreshMenuGrid();
            }
        ));

        // Kartu 3: Snack & Food
        categoryCardsContainer.add(createCategoryHeroCard(
            "Food & Snack", "30 items", "Available", "SNACK".equals(currentMainCategory), () -> {
                currentMainCategory = "SNACK";
                currentSubCategory = "ALL";
                buildCategoryCards();
                updateSubcategoryPills();
                refreshMenuGrid();
            }
        ));

        categoryCardsContainer.revalidate();
        categoryCardsContainer.repaint();
    }

    private JPanel createCategoryHeroCard(String title, String itemCount, String badgeText, 
                                          boolean isActive, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isActive) {
                    g2.setColor(ThemeColor.PRIMARY); // Hijau #025726
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                    g2.setColor(ThemeColor.BORDER_CARD);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Konten Teks Kiri
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // Badge Pill "Available"
        JLabel lblBadge = new JLabel(" " + badgeText + " ");
        lblBadge.setFont(ThemeColor.FONT_TINY);
        lblBadge.setOpaque(true);
        if (isActive) {
            lblBadge.setBackground(new Color(255, 255, 255, 40));
            lblBadge.setForeground(Color.WHITE);
        } else {
            lblBadge.setBackground(ThemeColor.ACCENT_LIGHT);
            lblBadge.setForeground(ThemeColor.PRIMARY);
        }
        lblBadge.setBorder(BorderFactory.createLineBorder(isActive ? Color.WHITE : ThemeColor.ACCENT_SAGE, 1, true));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ThemeColor.FONT_SUBTITLE);
        lblTitle.setForeground(isActive ? Color.WHITE : ThemeColor.TEXT_PRIMARY);

        JLabel lblCount = new JLabel(itemCount);
        lblCount.setFont(ThemeColor.FONT_SMALL);
        lblCount.setForeground(isActive ? new Color(220, 240, 225) : ThemeColor.TEXT_MUTED);

        textPanel.add(lblBadge);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblTitle);
        textPanel.add(lblCount);

        card.add(textPanel, BorderLayout.WEST);

        // Watermark Ilustrasi Transparan di Kanan
        JLabel lblGraphic = new JLabel(isActive ? "☕" : "🍃");
        lblGraphic.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 32));
        lblGraphic.setForeground(isActive ? new Color(255, 255, 255, 60) : new Color(0, 0, 0, 20));
        card.add(lblGraphic, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onClick.run();
            }
        });
        return card;
    }

    private void updateSubcategoryPills() {
        subcategoryPillPanel.removeAll();

        if ("COFFEE".equals(currentMainCategory)) {
            addPillToSubcategory("All Coffee", "ALL");
            addPillToSubcategory("Espresso", "ESPRESSO");
            addPillToSubcategory("Latte", "LATTE");
        } else if ("TEA".equals(currentMainCategory)) {
            addPillToSubcategory("All Drinks", "ALL");
            addPillToSubcategory("Matcha", "NON_COFFEE");
        } else {
            addPillToSubcategory("All Food", "ALL");
            addPillToSubcategory("Meals", "MEALS");
            addPillToSubcategory("Snacks", "SNACK");
            addPillToSubcategory("Dessert", "DESSERT");
        }

        subcategoryPillPanel.revalidate();
        subcategoryPillPanel.repaint();
    }

    private void addPillToSubcategory(String label, String subKey) {
        boolean active = currentSubCategory.equals(subKey);
        PillButton pill = new PillButton(label, active);
        pill.setFont(ThemeColor.FONT_SMALL);
        pill.setPreferredSize(new Dimension(95, 28));
        pill.addActionListener(e -> {
            currentSubCategory = subKey;
            updateSubcategoryPills();
            refreshMenuGrid();
        });
        subcategoryPillPanel.add(pill);
    }

    private void refreshMenuGrid() {
        menuGridPanel.removeAll();

        for (MenuItem item : masterMenu) {
            // Filter 1: Main Category
            if ("COFFEE".equals(currentMainCategory)) {
                if (item.getTipeMenu() != TipeMenu.MINUMAN) continue;
                if (item instanceof Minuman && ((Minuman) item).getSubKategori() != SubKategoriMinuman.COFFEE) continue;
            } else if ("TEA".equals(currentMainCategory)) {
                if (item.getTipeMenu() != TipeMenu.MINUMAN) continue;
                if (item instanceof Minuman && ((Minuman) item).getSubKategori() != SubKategoriMinuman.NON_COFFEE) continue;
            } else if ("SNACK".equals(currentMainCategory)) {
                if (item.getTipeMenu() != TipeMenu.MAKANAN) continue;
            }

            // Filter 2: Search Query
            if (!searchQuery.isEmpty() && !item.getNama().toLowerCase().contains(searchQuery)) {
                continue;
            }

            // Buat Menu Card 1:1
            MenuCardPanel card = new MenuCardPanel(item, this::openCustomizationDialog);
            menuGridPanel.add(card);
        }

        menuGridPanel.revalidate();
        menuGridPanel.repaint();
    }

    private void openCustomizationDialog(MenuItem item) {
        CustomizationDialog dialog = new CustomizationDialog(parentFrame, item, masterTopping);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            MenuItem customized = dialog.getCustomizedItem();
            cart.tambahItem(new OrderItem(customized, 1));
            refreshCartUI();
        }
    }

    // =========================================================================
    // 2. SIDEBAR KERANJANG PESANAN (1:1 DENGAN REFERENSI GREEN GROUNDS)
    // =========================================================================
    private void initCartSidebar() {
        rightCartPanel = new JPanel(new BorderLayout());
        rightCartPanel.setOpaque(false);
        rightCartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 16));

        // Panel Konten Lengkap Keranjang (Saat Expand)
        cartContentPanel = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cartContentPanel.setOpaque(false);
        cartContentPanel.setPreferredSize(new Dimension(340, 680));
        cartContentPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Strip Kecil Saat Keranjang Di-Minimize
        cartMinimizedStrip = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14));
        cartMinimizedStrip.setBackground(Color.WHITE);
        cartMinimizedStrip.setPreferredSize(new Dimension(48, 680));
        cartMinimizedStrip.setBorder(BorderFactory.createLineBorder(ThemeColor.BORDER_CARD, 1, true));
        cartMinimizedStrip.setVisible(false);

        JButton btnExpandCart = new JButton("🛒") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.PRIMARY);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeColor.FONT_BOLD);
                g2.drawString("🛒", 8, 22);
                g2.dispose();
            }
        };
        btnExpandCart.setPreferredSize(new Dimension(34, 34));
        btnExpandCart.setContentAreaFilled(false);
        btnExpandCart.setBorderPainted(false);
        btnExpandCart.setFocusPainted(false);
        btnExpandCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExpandCart.addActionListener(e -> toggleCartSidebar());
        cartMinimizedStrip.add(btnExpandCart);

        // A. Header Keranjang 1:1 (Tombol Minimize Hijau + Title Purchase Receipt + Icon List)
        JPanel cartTopHeader = new JPanel(new BorderLayout());
        cartTopHeader.setOpaque(false);

        // Tombol Bulat Hijau Minimize (>)
        JButton btnMinimizeCart = new JButton(">") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.PRIMARY);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeColor.FONT_BOLD);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(">")) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(">", tx, ty);
                g2.dispose();
            }
        };
        btnMinimizeCart.setPreferredSize(new Dimension(32, 32));
        btnMinimizeCart.setContentAreaFilled(false);
        btnMinimizeCart.setBorderPainted(false);
        btnMinimizeCart.setFocusPainted(false);
        btnMinimizeCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMinimizeCart.addActionListener(e -> toggleCartSidebar());
        cartTopHeader.add(btnMinimizeCart, BorderLayout.WEST);

        // Judul Struk "Purchase Receipt #27362"
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel lblRecTitle = new JLabel("Purchase Receipt");
        lblRecTitle.setFont(ThemeColor.FONT_BOLD);
        lblRecTitle.setForeground(ThemeColor.TEXT_PRIMARY);
        JLabel lblRecNum = new JLabel("#" + (System.currentTimeMillis() % 100000));
        lblRecNum.setFont(ThemeColor.FONT_SMALL);
        lblRecNum.setForeground(ThemeColor.TEXT_MUTED);
        titlePanel.add(lblRecTitle);
        titlePanel.add(lblRecNum);
        cartTopHeader.add(titlePanel, BorderLayout.CENTER);

        // Tombol Bulat Icon List (≡)
        JButton btnList = new JButton("≡") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(ThemeColor.BORDER_CARD);
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(ThemeColor.TEXT_PRIMARY);
                g2.setFont(ThemeColor.FONT_BOLD);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth("≡")) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("≡", tx, ty);
                g2.dispose();
            }
        };
        btnList.setPreferredSize(new Dimension(32, 32));
        btnList.setContentAreaFilled(false);
        btnList.setBorderPainted(false);
        btnList.setFocusPainted(false);
        cartTopHeader.add(btnList, BorderLayout.EAST);

        // B. Service Type Segmented Capsule Pills: [Dine In] [Take Away] [Order Online]
        JPanel orderTypePills = new JPanel(new GridLayout(1, 3, 4, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 247, 245));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        orderTypePills.setOpaque(false);
        orderTypePills.setPreferredSize(new Dimension(300, 34));

        PillButton btnDineIn = new PillButton("Dine In", true);
        PillButton btnTakeAway = new PillButton("Take Away", false);
        PillButton btnOnline = new PillButton("Order Online", false);

        btnDineIn.addActionListener(e -> {
            btnDineIn.setActive(true); btnTakeAway.setActive(false); btnOnline.setActive(false);
            selectedPenyajian = JenisPenyajian.DINE_IN;
        });
        btnTakeAway.addActionListener(e -> {
            btnDineIn.setActive(false); btnTakeAway.setActive(true); btnOnline.setActive(false);
            selectedPenyajian = JenisPenyajian.TAKEAWAY;
        });
        btnOnline.addActionListener(e -> {
            btnDineIn.setActive(false); btnTakeAway.setActive(false); btnOnline.setActive(true);
            selectedPenyajian = JenisPenyajian.TAKEAWAY;
        });

        orderTypePills.add(btnDineIn);
        orderTypePills.add(btnTakeAway);
        //orderTypePills.add(btnOnline);

        // C. Customer Name & Table Input Pills Row
        JPanel customerRow = new JPanel(new GridLayout(1, 2, 8, 0));
        customerRow.setOpaque(false);

        JPanel pnlCust = new JPanel();
        pnlCust.setLayout(new BoxLayout(pnlCust, BoxLayout.Y_AXIS));
        pnlCust.setOpaque(false);
        JLabel lblCustTitle = new JLabel("Customer name");
        lblCustTitle.setFont(ThemeColor.FONT_TINY);
        lblCustTitle.setForeground(ThemeColor.TEXT_MUTED);
        txtCustomerName = new JTextField("Muadz");
        txtCustomerName.setFont(ThemeColor.FONT_SMALL);
        txtCustomerName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColor.BORDER_CARD, 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        pnlCust.add(lblCustTitle);
        pnlCust.add(Box.createVerticalStrut(2));
        pnlCust.add(txtCustomerName);

        JPanel pnlTable = new JPanel();
        pnlTable.setLayout(new BoxLayout(pnlTable, BoxLayout.Y_AXIS));
        pnlTable.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Table");
        lblTableTitle.setFont(ThemeColor.FONT_TINY);
        lblTableTitle.setForeground(ThemeColor.TEXT_MUTED);
        cmbTable = new JComboBox<>(new String[]{"B12 - Indoor", "A01 - Outdoor", "C03 - Bar Area"});
        cmbTable.setFont(ThemeColor.FONT_SMALL);
        cmbTable.setBackground(Color.WHITE);
        pnlTable.add(lblTableTitle);
        //pnlTable.add(Box.createVerticalStrut(2));
        pnlTable.add(cmbTable);

        //customerRow.add(pnlCust);
        //customerRow.add(pnlTable);

        // Gabungkan Top Sidebar
        JPanel cartTopCombined = new JPanel();
        cartTopCombined.setLayout(new BoxLayout(cartTopCombined, BoxLayout.Y_AXIS));
        cartTopCombined.setOpaque(false);
        cartTopCombined.add(cartTopHeader);
        cartTopCombined.add(Box.createVerticalStrut(10));
        cartTopCombined.add(orderTypePills);
        cartTopCombined.add(Box.createVerticalStrut(8));
        cartTopCombined.add(customerRow);
        cartTopCombined.add(Box.createVerticalStrut(10));

        JLabel lblOrderList = new JLabel("Order list");
        lblOrderList.setFont(ThemeColor.FONT_BOLD);
        lblOrderList.setForeground(ThemeColor.TEXT_PRIMARY);
        cartTopCombined.add(lblOrderList, BorderLayout.SOUTH);

        cartContentPanel.add(cartTopCombined, BorderLayout.NORTH);

        // D. List Item Pesanan (Scrollable)
        cartItemsContainer = new JPanel();
        cartItemsContainer.setLayout(new BoxLayout(cartItemsContainer, BoxLayout.Y_AXIS));
        cartItemsContainer.setBackground(Color.WHITE);

        JScrollPane scrollCart = new JScrollPane(cartItemsContainer);
        scrollCart.setBorder(null);
        scrollCart.setOpaque(false);
        scrollCart.getViewport().setBackground(Color.WHITE);
        scrollCart.getVerticalScrollBar().setUnitIncrement(12);
        cartContentPanel.add(scrollCart, BorderLayout.CENTER);

        // E. Payment Details & Place Order Pill Button (Bottom)
        JPanel cartFooter = new JPanel();
        cartFooter.setLayout(new BoxLayout(cartFooter, BoxLayout.Y_AXIS));
        cartFooter.setOpaque(false);
        cartFooter.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblPayDetails = new JLabel("Payment Details");
        lblPayDetails.setFont(ThemeColor.FONT_BOLD);
        lblPayDetails.setForeground(ThemeColor.TEXT_PRIMARY);
        cartFooter.add(lblPayDetails);
        cartFooter.add(Box.createVerticalStrut(6));

        lblSubtotalVal = new JLabel("Rp 0");
        lblTaxVal = new JLabel("Rp 0");
        lblTotalVal = new JLabel("Rp 0");

        cartFooter.add(createDetailRow("Subtotal", lblSubtotalVal, false));
        cartFooter.add(Box.createVerticalStrut(3));
        cartFooter.add(createDetailRow("Tax (10%)", lblTaxVal, false));
        cartFooter.add(Box.createVerticalStrut(4));
        cartFooter.add(createDetailRow("Total", lblTotalVal, true));
        cartFooter.add(Box.createVerticalStrut(12));

        // Place Order Pill Button Row: [→] [ Place Order Rp xx.xxx >> ]
        JPanel placeOrderRow = new JPanel(new BorderLayout(8, 0));
        placeOrderRow.setOpaque(false);

        // Tombol Bulat Kiri dengan Panah (→)
        JButton btnCircleArrow = new JButton("→") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.PRIMARY);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeColor.FONT_BOLD);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth("→")) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("→", tx, ty);
                g2.dispose();
            }
        };
        btnCircleArrow.setPreferredSize(new Dimension(42, 42));
        btnCircleArrow.setContentAreaFilled(false);
        btnCircleArrow.setBorderPainted(false);
        btnCircleArrow.setFocusPainted(false);
        btnCircleArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCircleArrow.addActionListener(e -> onCheckoutClicked());

        // Tombol Panjang Pill Place Order
        btnPlaceOrder = new JButton("Place Order  Rp 0  >>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ThemeColor.PRIMARY_HOVER : ThemeColor.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeColor.FONT_BOLD);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnPlaceOrder.setPreferredSize(new Dimension(250, 42));
        btnPlaceOrder.setContentAreaFilled(false);
        btnPlaceOrder.setBorderPainted(false);
        btnPlaceOrder.setFocusPainted(false);
        btnPlaceOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPlaceOrder.addActionListener(e -> onCheckoutClicked());

        placeOrderRow.add(btnCircleArrow, BorderLayout.WEST);
        placeOrderRow.add(btnPlaceOrder, BorderLayout.CENTER);
        cartFooter.add(placeOrderRow);

        cartContentPanel.add(cartFooter, BorderLayout.SOUTH);

        rightCartPanel.add(cartContentPanel, BorderLayout.CENTER);
        rightCartPanel.add(cartMinimizedStrip, BorderLayout.EAST);

        add(rightCartPanel, BorderLayout.EAST);
    }

    private JPanel createDetailRow(String label, JLabel valLabel, boolean isBold) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(isBold ? ThemeColor.FONT_BOLD : ThemeColor.FONT_SMALL);
        lbl.setForeground(isBold ? ThemeColor.TEXT_PRIMARY : ThemeColor.TEXT_MUTED);

        valLabel.setFont(isBold ? ThemeColor.FONT_BOLD : ThemeColor.FONT_SMALL);
        valLabel.setForeground(isBold ? ThemeColor.TEXT_PRIMARY : ThemeColor.TEXT_MUTED);

        pnl.add(lbl, BorderLayout.WEST);
        pnl.add(valLabel, BorderLayout.EAST);
        return pnl;
    }

    private void toggleCartSidebar() {
        isCartMinimized = !isCartMinimized;
        cartContentPanel.setVisible(!isCartMinimized);
        cartMinimizedStrip.setVisible(isCartMinimized);
        revalidate();
        repaint();
    }

    public OrderCart getCart() {
        return cart;
    }

    public void refreshCartUI() {
        cartItemsContainer.removeAll();

        if (cart.isEmpty()) {
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
            emptyPanel.setOpaque(false);
            JLabel lblEmpty = new JLabel("<html><center>Belum ada item pesanan<br><small style='color: #707A73;'>Pilih menu di sebelah kiri</small></center></html>");
            lblEmpty.setFont(ThemeColor.FONT_SMALL);
            lblEmpty.setForeground(ThemeColor.TEXT_MUTED);
            emptyPanel.add(lblEmpty);
            cartItemsContainer.add(emptyPanel);
        } else {
            for (OrderItem oi : cart.getItems()) {
                cartItemsContainer.add(createCartItemCard(oi));
                cartItemsContainer.add(Box.createVerticalStrut(6));
            }
        }

        updateCartSummaryNumbers();
        cartItemsContainer.revalidate();
        cartItemsContainer.repaint();
    }

    // Kartu Item Pesanan 1:1 Sesuai Referensi Green Grounds
    private JPanel createCartItemCard(OrderItem oi) {
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(ThemeColor.BORDER_CARD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 10));
        card.setMaximumSize(new Dimension(310, 68));
        card.setPreferredSize(new Dimension(300, 68));

        // 1. Thumbnail Produk Kiri
        BufferedImage thumb = ProductImageHelper.getProductImage(oi.getItem(), 46, 46);
        JLabel lblThumb = new JLabel(new ImageIcon(thumb));
        lblThumb.setPreferredSize(new Dimension(46, 46));
        card.add(lblThumb, BorderLayout.WEST);

        // 2. Info Nama, Subteks, dan Pill Kustomisasi Tengah
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblName = new JLabel(oi.getItem().getNama());
        lblName.setFont(ThemeColor.FONT_BOLD);
        lblName.setForeground(ThemeColor.TEXT_PRIMARY);

        JLabel lblSub = new JLabel(String.format("Rp %,.0f x%d", oi.getItem().hitungHarga(), oi.getKuantitas()));
        lblSub.setFont(ThemeColor.FONT_TINY);
        lblSub.setForeground(ThemeColor.TEXT_MUTED);

        // Tag Pill Kustomisasi
        String opt = "Normal";
        if (oi.getItem() instanceof Minuman) {
            opt = ((Minuman) oi.getItem()).getSugarLevel() + " Sugar";
        } else if (oi.getItem() instanceof Makanan) {
            opt = "Pedas Lvl " + ((Makanan) oi.getItem()).getLevelPedas();
        }
        JLabel lblOpt = new JLabel(" " + opt + " ");
        lblOpt.setFont(ThemeColor.FONT_TINY);
        lblOpt.setForeground(ThemeColor.TEXT_MUTED);
        lblOpt.setOpaque(true);
        lblOpt.setBackground(new Color(245, 247, 245));
        lblOpt.setBorder(BorderFactory.createLineBorder(ThemeColor.BORDER_CARD, 1, true));

        info.add(lblName);
        info.add(lblSub);
        info.add(Box.createVerticalStrut(2));
        info.add(lblOpt);
        card.add(info, BorderLayout.CENTER);

        // 3. Harga Kanan Atas & Kapsul Kuantitas Simetris [- qty +]
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        JLabel lblPrice = new JLabel(String.format("Rp %,.0f", oi.hitungSubtotal()));
        lblPrice.setFont(ThemeColor.FONT_BOLD);
        lblPrice.setForeground(ThemeColor.TEXT_PRIMARY);
        lblPrice.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Kapsul Kuantitas Simetris [- qty +]
        JPanel qtyCapsule = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 247, 245));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.setColor(ThemeColor.BORDER_CARD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        qtyCapsule.setOpaque(false);
        qtyCapsule.setPreferredSize(new Dimension(74, 24));
        qtyCapsule.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton btnMin = new JButton("-");
        btnMin.setFont(ThemeColor.FONT_BOLD);
        btnMin.setForeground(ThemeColor.TEXT_PRIMARY);
        btnMin.setContentAreaFilled(false);
        btnMin.setBorderPainted(false);
        btnMin.setMargin(new Insets(0, 0, 0, 0));
        btnMin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMin.setPreferredSize(new Dimension(18, 20));

        JLabel lblQty = new JLabel(String.valueOf(oi.getKuantitas()));
        lblQty.setFont(ThemeColor.FONT_SMALL);
        lblQty.setForeground(ThemeColor.TEXT_PRIMARY);

        JButton btnPlus = new JButton("+");
        btnPlus.setFont(ThemeColor.FONT_BOLD);
        btnPlus.setForeground(ThemeColor.TEXT_PRIMARY);
        btnPlus.setContentAreaFilled(false);
        btnPlus.setBorderPainted(false);
        btnPlus.setMargin(new Insets(0, 0, 0, 0));
        btnPlus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPlus.setPreferredSize(new Dimension(18, 20));

        btnMin.addActionListener(e -> {
            if (oi.getKuantitas() > 1) {
                oi.setKuantitas(oi.getKuantitas() - 1);
            } else {
                cart.getItems().remove(oi);
            }
            refreshCartUI();
        });

        btnPlus.addActionListener(e -> {
            oi.setKuantitas(oi.getKuantitas() + 1);
            refreshCartUI();
        });

        qtyCapsule.add(btnMin);
        qtyCapsule.add(lblQty);
        qtyCapsule.add(btnPlus);

        rightCol.add(lblPrice);
        rightCol.add(Box.createVerticalStrut(6));
        rightCol.add(qtyCapsule);
        card.add(rightCol, BorderLayout.EAST);

        return card;
    }

    private void updateCartSummaryNumbers() {
        double subtotal = cart.hitungSubtotal();
        double tax = cart.hitungNominalPajak();
        double total = cart.hitungTotalAkhir();

        lblSubtotalVal.setText(String.format("Rp %,.0f", subtotal));
        lblTaxVal.setText(String.format("Rp %,.0f", tax));
        lblTotalVal.setText(String.format("Rp %,.0f", total));

        btnPlaceOrder.setText(String.format("Place Order   Rp %,.0f   >>", total));
        btnPlaceOrder.setEnabled(!cart.isEmpty());
    }

    private void onCheckoutClicked() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong!", "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PaymentDialog paymentDialog = new PaymentDialog(parentFrame, cart, idKasir, laporanService, database);
        paymentDialog.setVisible(true);

        if (paymentDialog.isTransactionCompleted()) {
            for (OrderItem oi : cart.getItems()) {
                inventory.kurangiStokBerdasarkanMenu(oi.getItem(), oi.getKuantitas());
            }

            cart.clear();
            refreshCartUI();
        }
    }
}
