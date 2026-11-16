package com.coffeeshop.service;

import com.coffeeshop.model.BahanBaku;
import com.coffeeshop.model.Makanan;
import com.coffeeshop.model.MenuItem;
import com.coffeeshop.model.MetodePembayaran;
import com.coffeeshop.model.Minuman;
import com.coffeeshop.model.OrderItem;
import com.coffeeshop.model.SubKategoriMakanan;
import com.coffeeshop.model.SubKategoriMinuman;
import com.coffeeshop.model.TipeMenu;
import com.coffeeshop.model.Transaksi;
import com.coffeeshop.model.Topping;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {
    private static final String DEFAULT_URL =
        "jdbc:mysql://localhost:3306/kopi_kepikiran?useSSL=false&serverTimezone=UTC";
    private final String url;
    private final String username;
    private final String password;

    public DatabaseService() {
        this.url = environmentOrDefault("COFFEESHOP_DB_URL", DEFAULT_URL);
        this.username = environmentOrDefault("COFFEESHOP_DB_USER", "root");
        this.password = environmentOrDefault("COFFEESHOP_DB_PASSWORD", "");
    }

    private static String environmentOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null ? defaultValue : value;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public void initializeSchema() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS ingredients (" +
                "id VARCHAR(20) PRIMARY KEY, " +
                "name VARCHAR(120) NOT NULL, " +
                "stock DECIMAL(12,3) NOT NULL DEFAULT 0, " +
                "unit VARCHAR(30) NOT NULL, " +
                "minimum_stock DECIMAL(12,3) NOT NULL DEFAULT 0) ENGINE=InnoDB"
            );
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "id VARCHAR(40) PRIMARY KEY, " +
                "occurred_at DATETIME NOT NULL, " +
                "cashier_id VARCHAR(40) NOT NULL, " +
                "payment_method VARCHAR(30) NOT NULL, " +
                "subtotal DECIMAL(14,2) NOT NULL, " +
                "discount_rate DECIMAL(7,4) NOT NULL, " +
                "discount_amount DECIMAL(14,2) NOT NULL, " +
                "tax_rate DECIMAL(7,4) NOT NULL, " +
                "tax_amount DECIMAL(14,2) NOT NULL, " +
                "total DECIMAL(14,2) NOT NULL, " +
                "amount_received DECIMAL(14,2) NOT NULL, " +
                "change_amount DECIMAL(14,2) NOT NULL, " +
                "INDEX idx_transactions_occurred_at (occurred_at)) ENGINE=InnoDB"
            );
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS transaction_items (" +
                "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "transaction_id VARCHAR(40) NOT NULL, " +
                "product_id VARCHAR(40) NOT NULL, " +
                "product_name VARCHAR(160) NOT NULL, " +
                "quantity INT NOT NULL, " +
                "unit_price DECIMAL(14,2) NOT NULL, " +
                "line_total DECIMAL(14,2) NOT NULL, " +
                "detail TEXT NOT NULL, " +
                "CONSTRAINT fk_transaction_items_transaction " +
                "FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE) ENGINE=InnoDB"
            );
        }
    }

    public void seedIngredients(Collection<BahanBaku> ingredients) throws SQLException {
        String sql = "INSERT IGNORE INTO ingredients (id, name, stock, unit, minimum_stock) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (BahanBaku ingredient : ingredients) {
                statement.setString(1, ingredient.getId());
                statement.setString(2, ingredient.getNama());
                statement.setDouble(3, ingredient.getStok());
                statement.setString(4, ingredient.getSatuan());
                statement.setDouble(5, ingredient.getStokMinimum());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public List<BahanBaku> loadIngredients() throws SQLException {
        List<BahanBaku> ingredients = new ArrayList<>();
        String sql = "SELECT id, name, stock, unit, minimum_stock FROM ingredients ORDER BY id";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                ingredients.add(new BahanBaku(
                    result.getString("id"),
                    result.getString("name"),
                    result.getDouble("stock"),
                    result.getString("unit"),
                    result.getDouble("minimum_stock")
                ));
            }
        }
        return ingredients;
    }

    public List<MenuItem> loadMenuItems() throws SQLException {
        Map<String, MenuItem> productsById = new LinkedHashMap<>();
        String sql = "SELECT id, name, product_type, subcategory, base_price, is_available " +
                     "FROM products ORDER BY id";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                String id = result.getString("id");
                String name = result.getString("name");
                String category = result.getString("subcategory");
                double price = result.getDouble("base_price");
                MenuItem product;
                try {
                    TipeMenu type = TipeMenu.valueOf(result.getString("product_type"));
                    if (type == TipeMenu.MINUMAN) {
                        product = new Minuman(id, name, price, SubKategoriMinuman.valueOf(category));
                    } else {
                        product = new Makanan(id, name, price, SubKategoriMakanan.valueOf(category));
                    }
                } catch (IllegalArgumentException ex) {
                    throw new SQLException("Jenis atau subkategori menu tidak dikenal untuk " + id, ex);
                }
                product.setTersedia(result.getBoolean("is_available"));
                productsById.put(id, product);
            }
        }

        String recipeSql = "SELECT product_id, ingredient_id, amount FROM recipe_ingredients";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(recipeSql)) {
            while (result.next()) {
                MenuItem product = productsById.get(result.getString("product_id"));
                if (product != null) {
                    product.tambahBahanResep(
                        result.getString("ingredient_id"),
                        result.getDouble("amount")
                    );
                }
            }
        }
        return new ArrayList<>(productsById.values());
    }

    public List<Topping> loadToppings() throws SQLException {
        List<Topping> toppings = new ArrayList<>();
        String sql = "SELECT id, name, additional_price, ingredient_id, ingredient_amount " +
                     "FROM toppings ORDER BY id";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                toppings.add(new Topping(
                    result.getString("id"),
                    result.getString("name"),
                    result.getDouble("additional_price"),
                    result.getString("ingredient_id"),
                    result.getDouble("ingredient_amount")
                ));
            }
        }
        return toppings;
    }

    public void restock(String ingredientId, double amount) throws SQLException {
        String sql = "UPDATE ingredients SET stock = stock + ? WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, amount);
            statement.setString(2, ingredientId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Bahan baku tidak ditemukan di database: " + ingredientId);
            }
        }
    }

    public void completeSale(Transaksi transaction) throws SQLException {
        String insertTransaction =
            "INSERT INTO transactions (id, occurred_at, cashier_id, payment_method, subtotal, " +
            "discount_rate, discount_amount, tax_rate, tax_amount, total, amount_received, change_amount) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItem =
            "INSERT INTO transaction_items (transaction_id, product_id, product_name, quantity, " +
            "unit_price, line_total, detail) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(insertTransaction)) {
                    statement.setString(1, transaction.getIdTransaksi());
                    statement.setTimestamp(2, Timestamp.valueOf(transaction.getWaktuTransaksi()));
                    statement.setString(3, transaction.getIdKasir());
                    statement.setString(4, transaction.getMetodePembayaran().name());
                    statement.setDouble(5, transaction.getSubtotal());
                    statement.setDouble(6, transaction.getDiskonPersen());
                    statement.setDouble(7, transaction.getNominalDiskon());
                    statement.setDouble(8, transaction.getPajakPersen());
                    statement.setDouble(9, transaction.getNominalPajak());
                    statement.setDouble(10, transaction.getTotalAkhir());
                    statement.setDouble(11, transaction.getUangDiterima());
                    statement.setDouble(12, transaction.getKembalian());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(insertItem)) {
                    for (OrderItem orderItem : transaction.getItems()) {
                        MenuItem product = orderItem.getItem();
                        statement.setString(1, transaction.getIdTransaksi());
                        statement.setString(2, product.getId());
                        statement.setString(3, product.getNama());
                        statement.setInt(4, orderItem.getKuantitas());
                        statement.setDouble(5, product.hitungHarga());
                        statement.setDouble(6, orderItem.hitungSubtotal());
                        statement.setString(7, product.getDetailPesanan());
                        statement.addBatch();

                        for (Map.Entry<String, Double> ingredient : product.getResepBahan().entrySet()) {
                            deductIngredient(connection, ingredient.getKey(),
                                ingredient.getValue() * orderItem.getKuantitas());
                        }
                        for (Topping topping : product.getDaftarTopping()) {
                            if (topping.getIdBahanBaku() != null && !topping.getIdBahanBaku().isEmpty()) {
                                deductIngredient(connection, topping.getIdBahanBaku(),
                                    topping.getTakaranBahan() * orderItem.getKuantitas());
                            }
                        }
                    }
                    statement.executeBatch();
                }
                connection.commit();
            } catch (SQLException ex) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                throw ex;
            }
        }
    }

    private void deductIngredient(Connection connection, String ingredientId, double amount)
            throws SQLException {
        String sql = "UPDATE ingredients SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, amount);
            statement.setString(2, ingredientId);
            statement.setDouble(3, amount);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Stok bahan baku tidak mencukupi atau ID tidak ditemukan: " + ingredientId);
            }
        }
    }

    public List<Transaksi> loadTransactions() throws SQLException {
        List<Transaksi> transactions = new ArrayList<>();
        String sql = "SELECT id, occurred_at, cashier_id, payment_method, subtotal, discount_rate, " +
                     "discount_amount, tax_rate, tax_amount, total, amount_received, change_amount " +
                     "FROM transactions ORDER BY occurred_at DESC";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                String transactionId = result.getString("id");
                List<OrderItem> items = loadTransactionItems(connection, transactionId);
                transactions.add(new Transaksi(
                    transactionId,
                    result.getTimestamp("occurred_at").toLocalDateTime(),
                    result.getString("cashier_id"),
                    items,
                    result.getDouble("subtotal"),
                    result.getDouble("discount_rate"),
                    result.getDouble("discount_amount"),
                    result.getDouble("tax_rate"),
                    result.getDouble("tax_amount"),
                    result.getDouble("total"),
                    result.getDouble("amount_received"),
                    result.getDouble("change_amount"),
                    MetodePembayaran.valueOf(result.getString("payment_method"))
                ));
            }
        }
        return transactions;
    }

    private List<OrderItem> loadTransactionItems(Connection connection, String transactionId)
            throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT product_id, product_name, quantity, unit_price, detail " +
                     "FROM transaction_items WHERE transaction_id = ? ORDER BY id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transactionId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String detail = result.getString("detail");
                    double unitPrice = result.getDouble("unit_price");
                    MenuItem snapshot = new MenuItem(
                        result.getString("product_id"),
                        result.getString("product_name"),
                        unitPrice
                    ) {
                        @Override
                        public TipeMenu getTipeMenu() {
                            return TipeMenu.MAKANAN;
                        }

                        @Override
                        public String getSubKategoriLabel() {
                            return "Riwayat Transaksi";
                        }

                        @Override
                        public double hitungHarga() {
                            return getHargaDasar();
                        }

                        @Override
                        public String getDetailPesanan() {
                            return detail;
                        }
                    };
                    items.add(new OrderItem(snapshot, result.getInt("quantity")));
                }
            }
        }
        return items;
    }
}
