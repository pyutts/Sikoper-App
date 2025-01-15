/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author pyuts
 */

public class AnggotaModel {
    private int id;
    private String kodeMember;
    private String nama;
    private String noTelepon;
    private String alamat;

    public AnggotaModel(int id, String kodeMember, String nama, String noTelepon, String alamat) {
        this.id = id;
        this.kodeMember = kodeMember;
        this.nama = nama;
        this.noTelepon = noTelepon;
        this.alamat = alamat;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKodeMember() { return kodeMember; }
    public void setKodeMember(String kodeMember) { this.kodeMember = kodeMember; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
}