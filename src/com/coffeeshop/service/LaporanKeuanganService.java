package com.coffeeshop.service;

import com.coffeeshop.model.MetodePembayaran;
import com.coffeeshop.model.RingkasanKeuanganDTO;
import com.coffeeshop.model.Transaksi;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

/**
 * Layanan Laporan Keuangan & Rekap Transaksi Penjualan Kafe.
 * Mengimplementasikan Java Collection Framework (ArrayList)
 * untuk menampung seluruh histori transaksi dan menghitung metrik finansial kafe.
 */
public class LaporanKeuanganService {
    private List<Transaksi> riwayatTransaksi;

    public LaporanKeuanganService() {
        this.riwayatTransaksi = new ArrayList<>();
    }

    public LaporanKeuanganService(DatabaseService database) throws SQLException {
        this.riwayatTransaksi = new ArrayList<>(database.loadTransactions());
    }

    public void catatTransaksi(Transaksi transaksi) {
        if (transaksi != null) {
            this.riwayatTransaksi.add(transaksi);
        }
    }

    public double hitungTotalOmzetBersih() {
        double total = 0;
        for (Transaksi t : riwayatTransaksi) {
            total += t.getTotalAkhir();
        }
        return total;
    }

    public double hitungTotalOmzetKotor() {
        double total = 0;
        for (Transaksi t : riwayatTransaksi) {
            total += t.getSubtotal();
        }
        return total;
    }

    public double hitungTotalPajakResto() {
        double total = 0;
        for (Transaksi t : riwayatTransaksi) {
            total += t.getNominalPajak();
        }
        return total;
    }

    public double hitungTotalDiskon() {
        double total = 0;
        for (Transaksi t : riwayatTransaksi) {
            total += t.getNominalDiskon();
        }
        return total;
    }

    public double hitungOmzetPerMetode(MetodePembayaran metode) {
        double total = 0;
        for (Transaksi t : riwayatTransaksi) {
            if (t.getMetodePembayaran() == metode) {
                total += t.getTotalAkhir();
            }
        }
        return total;
    }

    public List<Transaksi> filterByTanggal(LocalDate tanggal) {
        List<Transaksi> hasil = new ArrayList<>();
        if (tanggal == null) return hasil;
        for (Transaksi t : riwayatTransaksi) {
            if (t.getWaktuTransaksi().toLocalDate().isEqual(tanggal)) {
                hasil.add(t);
            }
        }
        return hasil;
    }

    public List<Transaksi> filterByKasir(String idKasir) {
        List<Transaksi> hasil = new ArrayList<>();
        if (idKasir == null) return hasil;
        for (Transaksi t : riwayatTransaksi) {
            if (t.getIdKasir().equalsIgnoreCase(idKasir)) {
                hasil.add(t);
            }
        }
        return hasil;
    }

    public RingkasanKeuanganDTO buatRingkasanHarian(LocalDate tanggal) {
        List<Transaksi> transaksiHarian = filterByTanggal(tanggal);
        double kotor = 0;
        double diskon = 0;
        double pajak = 0;
        double bersih = 0;
        double tunai = 0;
        double qris = 0;

        for (Transaksi t : transaksiHarian) {
            kotor += t.getSubtotal();
            diskon += t.getNominalDiskon();
            pajak += t.getNominalPajak();
            bersih += t.getTotalAkhir();
            if (t.getMetodePembayaran() == MetodePembayaran.TUNAI) {
                tunai += t.getTotalAkhir();
            } else if (t.getMetodePembayaran() == MetodePembayaran.QRIS) {
                qris += t.getTotalAkhir();
            }
        }

        return new RingkasanKeuanganDTO(
            tanggal, 
            transaksiHarian.size(), 
            kotor, 
            diskon, 
            pajak, 
            bersih, 
            tunai, 
            qris
        );
    }

    public List<Transaksi> getRiwayatTransaksi() {
        return riwayatTransaksi;
    }
}
