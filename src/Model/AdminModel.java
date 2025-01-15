/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author pyuts
 */

public class AdminModel {
    private int id;
    private String kodeAdmin;
    private String nama;
    private String username;
    private String password;

    public AdminModel(int id, String kodeAdmin, String nama, String username, String password) {
        this.id = id;
        this.kodeAdmin = kodeAdmin;
        this.nama = nama;
        this.username = username;
        this.password = password;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKodeAdmin() { return kodeAdmin; }
    public void setKodeAdmin(String kodeAdmin) { this.kodeAdmin = kodeAdmin; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
