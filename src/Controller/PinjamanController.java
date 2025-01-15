/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import Config.Connect;
import Config.UniqueCodeGenerator;
import Model.AnggotaModel;
import Model.PinjamanModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * FXML Controller class
 *
 * @author pyuts
 */

public class PinjamanController {

    @FXML
    private DatePicker dpTanggal;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<PinjamanModel> tablePinjaman;
    @FXML
    private TableColumn<PinjamanModel, Integer> colNo;
    @FXML
    private TableColumn<PinjamanModel, String> colKodePinjam;
    @FXML
    private TableColumn<PinjamanModel, String> colKodeMember;
    @FXML
    private TableColumn<PinjamanModel, String> colNamaAnggota;
    @FXML
    private TableColumn<PinjamanModel, LocalDate> colTanggal;
    @FXML
    private TableColumn<PinjamanModel, Double> colJumlah;
    @FXML
    private TableColumn<PinjamanModel, String> colStatus;
    @FXML
    private ComboBox<String> cbKodeMember;
    @FXML
    private TextField tfNamaAnggota;
    @FXML
    private TextField tfJumlah;

    private ObservableList<PinjamanModel> dataPinjaman;
    private HashMap<String, String> idNamaMap = new HashMap<>();

    @FXML
    public void initialize() {
        dataPinjaman = FXCollections.observableArrayList();
        setupTable();
        muatDataTabel();
        isiComboBoxKodeMember();
        cbStatus.setItems(FXCollections.observableArrayList(
                "pending",
                "diterima",
                "ditolak"));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue));
    }

    private void setupTable() {
        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colKodePinjam.setCellValueFactory(new PropertyValueFactory<>("kodePinjam"));
        colKodeMember.setCellValueFactory(new PropertyValueFactory<>("kodeMember"));
        colNamaAnggota.setCellValueFactory(new PropertyValueFactory<>("namaAnggota"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colJumlah.setCellFactory(column -> new TableCell<PinjamanModel, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText("Rp " + String.format("%,.2f", item));
                }
            }
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tablePinjaman.setItems(dataPinjaman);
    }

    private void muatDataTabel() {
        dataPinjaman.clear();
        try (Connection connection = Connect.connect()) {
            String query = "SELECT p.id, p.kode_pinjam, m.kode_member, m.nama, p.tgl_pinjam, " +
                          "p.pinjam_amount, p.pinjam_status " +
                          "FROM pinjam p " +
                          "JOIN member m ON p.member_id = m.id";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                dataPinjaman.add(new PinjamanModel(
                    no++,
                    rs.getInt("id"),
                    rs.getString("kode_pinjam"),
                    rs.getString("kode_member"),
                    rs.getString("nama"),
                    rs.getDate("tgl_pinjam").toLocalDate(),
                    rs.getDouble("pinjam_amount"),
                    rs.getString("pinjam_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void tambahData(ActionEvent event) {
        try {
            String kodePinjam = UniqueCodeGenerator.generateUniqueCode("PIN", 8);
            String kodeMember = cbKodeMember.getValue();
            LocalDate tanggal = dpTanggal.getValue();
            double jumlah = Double.parseDouble(tfJumlah.getText());
            String status = cbStatus.getValue();
    
            try (Connection connection = Connect.connect()) {
                // Get member_id from kode_member
                String getMemberId = "SELECT id FROM member WHERE kode_member = ?";
                PreparedStatement psGetId = connection.prepareStatement(getMemberId);
                psGetId.setString(1, kodeMember);
                ResultSet rs = psGetId.executeQuery();
                
                if (rs.next()) {
                    int memberId = rs.getInt("id");
                    String query = "INSERT INTO pinjam (kode_pinjam, member_id, tgl_pinjam, " +
                                 "pinjam_amount, pinjam_status) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setString(1, kodePinjam);
                    ps.setInt(2, memberId);
                    ps.setDate(3, java.sql.Date.valueOf(tanggal));
                    ps.setDouble(4, jumlah);
                    ps.setString(5, status);
                    ps.executeUpdate();
                    
                    showAlert("Sukses", "Data berhasil ditambahkan");
                    muatDataTabel();
                    clearForm();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menambah data: " + e.getMessage());
        }
    }
    
    @FXML
    private void editData(ActionEvent event) {
        try {
            PinjamanModel selected = tablePinjaman.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Pilih data yang akan diedit");
                return;
            }
    
            String kodeMember = cbKodeMember.getValue();
            LocalDate tanggal = dpTanggal.getValue();
            double jumlah = Double.parseDouble(tfJumlah.getText());
            String status = cbStatus.getValue();
    
            try (Connection connection = Connect.connect()) {
                // Get member_id from kode_member
                String getMemberId = "SELECT id FROM member WHERE kode_member = ?";
                PreparedStatement psGetId = connection.prepareStatement(getMemberId);
                psGetId.setString(1, kodeMember);
                ResultSet rs = psGetId.executeQuery();
                
                if (rs.next()) {
                    int memberId = rs.getInt("id");
                    String query = "UPDATE pinjam SET member_id=?, tgl_pinjam=?, " +
                                 "pinjam_amount=?, pinjam_status=? WHERE kode_pinjam=?";
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, memberId);
                    ps.setDate(2, java.sql.Date.valueOf(tanggal));
                    ps.setDouble(3, jumlah);
                    ps.setString(4, status);
                    ps.setString(5, selected.getKodePinjam());
                    ps.executeUpdate();
    
                    showAlert("Sukses", "Data berhasil diupdate");
                    muatDataTabel();
                    clearForm();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal mengupdate data: " + e.getMessage());
        }
    }
    
    @FXML
    private void hapusData(ActionEvent event) {
        try {
            PinjamanModel selected = tablePinjaman.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Pilih data yang akan dihapus");
                return;
            }
    
            try (Connection connection = Connect.connect()) {
                String query = "DELETE FROM pinjam WHERE kode_pinjam=?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, selected.getKodePinjam());
                ps.executeUpdate();
    
                showAlert("Sukses", "Data berhasil dihapus");
                muatDataTabel();
                clearForm();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menghapus data: " + e.getMessage());
        }
    }

    @FXML
    private void cariMember(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AnggotaSearch.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cari Member");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(cbKodeMember.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AnggotaSearchController controller = loader.getController();
            dialogStage.showAndWait();

            if (controller.isPilihClicked()) {
                AnggotaModel selected = controller.getSelectedMember();
                cbKodeMember.setValue(selected.getKodeMember());
                tfNamaAnggota.setText(selected.getNama());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void isiComboBoxKodeMember() {
        try (Connection connection = Connect.connect()) {
            String query = "SELECT kode_member, nama FROM member";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                idNamaMap.put(rs.getString("kode_member"), rs.getString("nama"));
                cbKodeMember.getItems().add(rs.getString("kode_member"));
            }
    
            cbKodeMember.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    tfNamaAnggota.setText(idNamaMap.get(newVal));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterData(String keyword) {
        ObservableList<PinjamanModel> filteredData = FXCollections.observableArrayList();
        for (PinjamanModel pinjaman : dataPinjaman) {
            if (pinjaman.getNamaAnggota().toLowerCase().contains(keyword.toLowerCase()) ||
                    pinjaman.getKodeMember().toLowerCase().contains(keyword.toLowerCase()) ||
                    pinjaman.getKodePinjam().toLowerCase().contains(keyword.toLowerCase())) {
                filteredData.add(pinjaman);
            }
        }
        tablePinjaman.setItems(filteredData);
    }

    private void clearForm() {
        cbKodeMember.setValue(null);
        tfNamaAnggota.clear();
        dpTanggal.setValue(null);
        tfJumlah.clear();
        cbStatus.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void kembaliDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onTableClicked() {
        PinjamanModel selected = tablePinjaman.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cbKodeMember.setValue(selected.getKodeMember());
            tfNamaAnggota.setText(selected.getNamaAnggota());
            dpTanggal.setValue(selected.getTanggal());
            tfJumlah.setText(String.valueOf(selected.getJumlah()));
            cbStatus.setValue(selected.getStatus());
        }
    }
}
