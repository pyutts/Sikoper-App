/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author pyuts
 */

public class LaporanModel {
    private String nama;
    private String tanggalPinjam;
    private double jumlahPinjam;
    private String statusPinjam;
    private String tanggalSimpan;
    private double jumlahSimpan;

    public LaporanModel(String nama, String tanggalPinjam, double jumlahPinjam, String statusPinjam, String tanggalSimpan, double jumlahSimpan) {
        this.nama = nama;
        this.tanggalPinjam = tanggalPinjam;
        this.jumlahPinjam = jumlahPinjam;
        this.statusPinjam = statusPinjam;
        this.tanggalSimpan = tanggalSimpan;
        this.jumlahSimpan = jumlahSimpan;
    }

    public String getNama() {
        return nama;
    }

    public String getTanggalPinjam() {
        return tanggalPinjam;
    }

    public double getJumlahPinjam() {
        return jumlahPinjam;
    }

    public String getStatusPinjam() {
        return statusPinjam;
    }

    public String getTanggalSimpan() {
        return tanggalSimpan;
    }

    public double getJumlahSimpan() {
        return jumlahSimpan;
    }
}
