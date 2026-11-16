package com.coffeeshop.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Model entitas Transaksi Penjualan Kafe.
 * Mencatat rincian pembayaran, kalkulasi pajak & diskon, serta referensi idKasir yang bertugas.
 */
public class Transaksi {
    private String idTransaksi;
    private LocalDateTime waktuTransaksi;
    private String idKasir;
    private List<OrderItem> items;
    private double subtotal;
    private double diskonPersen; // Misal 0.10 untuk 10%
    private double nominalDiskon;
    private double pajakPersen; // Default 10% PB1 (0.10)
    private double nominalPajak;
    private double totalAkhir;
    private double uangDiterima;
    private double kembalian;
    private MetodePembayaran metodePembayaran;

    public Transaksi(String idTransaksi, String idKasir, List<OrderItem> items, 
                     double diskonPersen, double uangDiterima, MetodePembayaran metodePembayaran) {
        this.idTransaksi = idTransaksi;
        this.waktuTransaksi = LocalDateTime.now();
        this.idKasir = idKasir;
        this.items = new ArrayList<>(items);
        this.diskonPersen = diskonPersen;
        this.pajakPersen = 0.10; // 10% Pajak Restoran (PB1)
        this.metodePembayaran = metodePembayaran;
        this.uangDiterima = uangDiterima;

        hitungKalkulasi();
    }

    public Transaksi(String idTransaksi, LocalDateTime waktuTransaksi, String idKasir,
                     List<OrderItem> items, double subtotal, double diskonPersen,
                     double nominalDiskon, double pajakPersen, double nominalPajak,
                     double totalAkhir, double uangDiterima, double kembalian,
                     MetodePembayaran metodePembayaran) {
        this.idTransaksi = idTransaksi;
        this.waktuTransaksi = waktuTransaksi;
        this.idKasir = idKasir;
        this.items = new ArrayList<>(items);
        this.subtotal = subtotal;
        this.diskonPersen = diskonPersen;
        this.nominalDiskon = nominalDiskon;
        this.pajakPersen = pajakPersen;
        this.nominalPajak = nominalPajak;
        this.totalAkhir = totalAkhir;
        this.uangDiterima = uangDiterima;
        this.kembalian = kembalian;
        this.metodePembayaran = metodePembayaran;
    }

    private void hitungKalkulasi() {
        this.subtotal = 0;
        for (OrderItem item : items) {
            this.subtotal += item.hitungSubtotal();
        }

        this.nominalDiskon = this.subtotal * this.diskonPersen;
        double subtotalSetelahDiskon = this.subtotal - this.nominalDiskon;
        this.nominalPajak = subtotalSetelahDiskon * this.pajakPersen;
        this.totalAkhir = subtotalSetelahDiskon + this.nominalPajak;

        if (this.metodePembayaran == MetodePembayaran.TUNAI) {
            this.kembalian = Math.max(0, this.uangDiterima - this.totalAkhir);
        } else {
            // QRIS / Non-Tunai selalu uang pas
            this.uangDiterima = this.totalAkhir;
            this.kembalian = 0;
        }
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public LocalDateTime getWaktuTransaksi() {
        return waktuTransaksi;
    }

    public String getWaktuFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return waktuTransaksi.format(formatter);
    }

    public String getIdKasir() {
        return idKasir;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiskonPersen() {
        return diskonPersen;
    }

    public double getNominalDiskon() {
        return nominalDiskon;
    }

    public double getPajakPersen() {
        return pajakPersen;
    }

    public double getNominalPajak() {
        return nominalPajak;
    }

    public double getTotalAkhir() {
        return totalAkhir;
    }

    public double getUangDiterima() {
        return uangDiterima;
    }

    public double getKembalian() {
        return kembalian;
    }

    public MetodePembayaran getMetodePembayaran() {
        return metodePembayaran;
    }

    public String cetakStrukTeks() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("             KOPI KEPIKIRAN             \n");
        sb.append("       Modern Coffee & Eatery POS       \n");
        sb.append("========================================\n");
        sb.append(String.format("No. Transaksi : %s\n", idTransaksi));
        sb.append(String.format("Waktu         : %s\n", getWaktuFormatted()));
        sb.append(String.format("Kasir ID      : %s\n", idKasir));
        sb.append(String.format("Pembayaran    : %s\n", metodePembayaran.getLabel()));
        sb.append("----------------------------------------\n");
        for (OrderItem oi : items) {
            sb.append(String.format("%-24s Rp %,10.0f\n", 
                oi.getKuantitas() + "x " + oi.getItem().getNama(), oi.hitungSubtotal()));
            String detail = oi.getItem().getDetailPesanan();
            if (detail != null && !detail.trim().isEmpty()) {
                sb.append(String.format("   %s\n", detail));
            }
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal              : Rp %,11.0f\n", subtotal));
        if (nominalDiskon > 0) {
            sb.append(String.format("Diskon (%.0f%%)        : -Rp %,10.0f\n", 
                diskonPersen * 100, nominalDiskon));
        }
        sb.append(String.format("PB1 Restoran (10%%)    : Rp %,11.0f\n", nominalPajak));
        sb.append("----------------------------------------\n");
        sb.append(String.format("TOTAL AKHIR           : Rp %,11.0f\n", totalAkhir));
        sb.append(String.format("Uang Diterima         : Rp %,11.0f\n", uangDiterima));
        sb.append(String.format("Kembalian             : Rp %,11.0f\n", kembalian));
        sb.append("========================================\n");
        sb.append("   Terima kasih atas kunjungan Anda!    \n");
        sb.append("========================================\n");
        return sb.toString();
    }
}
