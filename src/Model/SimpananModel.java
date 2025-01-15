/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author pyuts
 */

import java.time.LocalDate;

public class SimpananModel {
    private int no;
    private int id;
    private String kodeTransaksi;
    private String kodeMember;
    private String namaAnggota;
    private LocalDate tanggal;
    private double jumlah;
    private String jenisTransaksi;

    public SimpananModel(int no, int id, String kodeTransaksi, String kodeMember, String namaAnggota, LocalDate tanggal, double jumlah, String jenisTransaksi) {
        this.no = no;
        this.id = id;
        this.kodeTransaksi = kodeTransaksi;
        this.kodeMember = kodeMember;
        this.namaAnggota = namaAnggota;
        this.tanggal = tanggal;
        this.jumlah = jumlah;
        this.jenisTransaksi = jenisTransaksi;
    }

    // Getters and Setters
    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKodeTransaksi() { return kodeTransaksi; }
    public void setKodeTransaksi(String kodeTransaksi) { this.kodeTransaksi = kodeTransaksi; }

    public String getKodeMember() { return kodeMember; }
    public void setKodeMember(String kodeMember) { this.kodeMember = kodeMember; }

    public String getNamaAnggota() { return namaAnggota; }
    public void setNamaAnggota(String namaAnggota) { this.namaAnggota = namaAnggota; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getJenisTransaksi() { return jenisTransaksi; }
    public void setJenisTransaksi(String jenisTransaksi) { this.jenisTransaksi = jenisTransaksi; }

    // Alias for jenisTransaksi
    public String getTipe() { return jenisTransaksi; }
    public void setTipe(String tipe) { this.jenisTransaksi = tipe; }
}