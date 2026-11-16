package com.coffeeshop.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object (DTO) untuk Ringkasan Laporan Keuangan Kafe.
 * Memuat agregasi omzet harian, pajak resto, dan breakdown metode bayar.
 */
public class RingkasanKeuanganDTO {
    private LocalDate tanggal;
    private int totalTransaksi;
    private double totalOmzetKotor;
    private double totalDiskon;
    private double totalPajakResto;
    private double totalOmzetBersih;
    private double totalTunai;
    private double totalQRIS;

    public RingkasanKeuanganDTO(LocalDate tanggal, int totalTransaksi, double totalOmzetKotor, 
                                double totalDiskon, double totalPajakResto, double totalOmzetBersih, 
                                double totalTunai, double totalQRIS) {
        this.tanggal = tanggal;
        this.totalTransaksi = totalTransaksi;
        this.totalOmzetKotor = totalOmzetKotor;
        this.totalDiskon = totalDiskon;
        this.totalPajakResto = totalPajakResto;
        this.totalOmzetBersih = totalOmzetBersih;
        this.totalTunai = totalTunai;
        this.totalQRIS = totalQRIS;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public int getTotalTransaksi() {
        return totalTransaksi;
    }

    public double getTotalOmzetKotor() {
        return totalOmzetKotor;
    }

    public double getTotalDiskon() {
        return totalDiskon;
    }

    public double getTotalPajakResto() {
        return totalPajakResto;
    }

    public double getTotalOmzetBersih() {
        return totalOmzetBersih;
    }

    public double getTotalTunai() {
        return totalTunai;
    }

    public double getTotalQRIS() {
        return totalQRIS;
    }

    public String formatLaporanTeks() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("        LAPORAN REKAP KEUANGAN & OMZET KAFE         \n");
        sb.append("====================================================\n");
        sb.append(String.format("Tanggal Laporan        : %s\n", tanggal.format(fmt)));
        sb.append(String.format("Total Struk Transaksi  : %d transaksi\n", totalTransaksi));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Total Penjualan Kotor  : Rp %,12.0f\n", totalOmzetKotor));
        sb.append(String.format("Total Potongan Diskon  : -Rp %,11.0f\n", totalDiskon));
        sb.append(String.format("Total PB1 Resto (10%%)  : Rp %,12.0f\n", totalPajakResto));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("TOTAL OMZET BERSIH     : Rp %,12.0f\n", totalOmzetBersih));
        sb.append("====================================================\n");
        sb.append("RINCIAN PENERIMAAN KAS (SETTLEMENT):\n");
        sb.append(String.format("1. Uang Tunai di Laci  : Rp %,12.0f\n", totalTunai));
        sb.append(String.format("2. Saldo QRIS / Digital: Rp %,12.0f\n", totalQRIS));
        sb.append("====================================================\n");
        return sb.toString();
    }
}
