package com.coffeeshop.model;

/**
 * Model entitas untuk Karyawan / Kasir kafe.
 * Menerapkan prinsip Encapsulation untuk mengamankan data kredensial login.
 */
public class Karyawan {
    private String idKaryawan;
    private String nama;
    private String username;
    private String password;
    private String role; // "KASIR" atau "MANAJER"

    public Karyawan(String idKaryawan, String nama, String username, String password, String role) {
        this.idKaryawan = idKaryawan;
        this.nama = nama;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public boolean autentikasi(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(String idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return nama + " (" + role + " - " + idKaryawan + ")";
    }
}
