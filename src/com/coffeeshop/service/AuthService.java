package com.coffeeshop.service;

import com.coffeeshop.model.Karyawan;
import java.util.HashMap;
import java.util.Map;

/**
 * Layanan autentikasi login karyawan/kasir kafe.
 * Menggunakan HashMap untuk lookup cepat berdasarkan username.
 */
public class AuthService {
    private Map<String, Karyawan> akunKaryawan;
    private Karyawan kasirAktif;

    public AuthService() {
        this.akunKaryawan = new HashMap<>();
        initDummyKaryawan();
    }

    private void initDummyKaryawan() {
        tambahKaryawan(new Karyawan("KASIR-01", "Ahmad Fauzi", "fauzi", "kasir123", "KASIR"));
        tambahKaryawan(new Karyawan("KASIR-02", "Siti Aisyah", "siti", "kasir123", "KASIR"));
        tambahKaryawan(new Karyawan("MGR-01", "Budi Pratama", "admin", "admin123", "MANAJER"));
    }

    public void tambahKaryawan(Karyawan k) {
        if (k != null) {
            akunKaryawan.put(k.getUsername().toLowerCase(), k);
        }
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) return false;
        Karyawan k = akunKaryawan.get(username.toLowerCase().trim());
        if (k != null && k.autentikasi(password)) {
            this.kasirAktif = k;
            return true;
        }
        return false;
    }

    public void logout() {
        this.kasirAktif = null;
    }

    public boolean isSudahLogin() {
        return this.kasirAktif != null;
    }

    public Karyawan getKasirAktif() {
        return kasirAktif;
    }
}
