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

public class PinjamanModel {
    private int no;
    private int id;
    private String kodePinjam;
    private String kodeMember;
    private String namaAnggota;
    private LocalDate tanggal;
    private double jumlah;
    private String status;

    public PinjamanModel(int no, int id, String kodePinjam, String kodeMember, String namaAnggota, LocalDate tanggal, double jumlah, String status) {
        this.no = no;
        this.id = id;
        this.kodePinjam = kodePinjam;
        this.kodeMember = kodeMember;
        this.namaAnggota = namaAnggota;
        this.tanggal = tanggal;
        this.jumlah = jumlah;
        this.status = status;
    }

    // Getters and Setters
    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKodePinjam() { return kodePinjam; }
    public void setKodePinjam(String kodePinjam) { this.kodePinjam = kodePinjam; }

    public String getKodeMember() { return kodeMember; }
    public void setKodeMember(String kodeMember) { this.kodeMember = kodeMember; }

    public String getNamaAnggota() { return namaAnggota; }
    public void setNamaAnggota(String namaAnggota) { this.namaAnggota = namaAnggota; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
