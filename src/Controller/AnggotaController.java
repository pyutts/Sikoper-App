/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import Config.Connect;
import Config.UniqueCodeGenerator;
import Model.AnggotaModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pyuts
 */
public class AnggotaController {

    @FXML
    private TableView<AnggotaModel> tableView;
    @FXML
    private TableColumn<AnggotaModel, Integer> colNo;
    @FXML
    private TableColumn<AnggotaModel, String> colNama;
    @FXML
    private TableColumn<AnggotaModel, String> colNoTelepon;
    @FXML
    private TableColumn<AnggotaModel, String> colAlamat;
    @FXML
    private TableColumn<AnggotaModel, String> colKodeMember;
    @FXML
    private TextField txtNama;
    @FXML
    private TextField txtNoTelepon;
    @FXML
    private TextArea txtAlamat;
    @FXML
    private ComboBox<String> comboKodeMember;
    @FXML
    private TextField searchField;
    @FXML
    private Button btnTambah;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnHapus;
    @FXML
    private Button btnKembali;

    private ObservableList<AnggotaModel> anggotaList;

    @FXML
    public void initialize() {
        anggotaList = FXCollections.observableArrayList();

        colNo.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(anggotaList.indexOf(data.getValue()) + 1));
        colNama.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNama()));
        colNoTelepon.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNoTelepon()));
        colAlamat.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAlamat()));
        colKodeMember.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getKodeMember()));

        tableView.setItems(anggotaList);

        loadDataFromDatabase();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue));
    }

    private void loadDataFromDatabase() {
        anggotaList.clear();
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM member")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                anggotaList.add(new AnggotaModel(
                        rs.getInt("id"),
                        rs.getString("kode_member"),
                        rs.getString("nama"),
                        rs.getString("no_telepon"),
                        rs.getString("alamat")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.refresh();
        updateComboBoxKodeMember();
    }

    private void filterData(String keyword) {
        ObservableList<AnggotaModel> filteredList = FXCollections.observableArrayList();
        for (AnggotaModel anggota : anggotaList) {
            if (anggota.getNama().toLowerCase().contains(keyword.toLowerCase()) ||
                anggota.getNoTelepon().toLowerCase().contains(keyword.toLowerCase()) ||
                anggota.getAlamat().toLowerCase().contains(keyword.toLowerCase()) ||
                anggota.getKodeMember().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(anggota);
            }
        }
        tableView.setItems(filteredList);
    }

    @FXML
    private void tambahAnggota(ActionEvent event) {
        String nama = txtNama.getText().trim();
        String noTelepon = txtNoTelepon.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String kodeMember = UniqueCodeGenerator.generateUniqueCode("MEM", 6);

        if (nama.isEmpty()) {
            showAlert("Field tidak boleh kosong!");
            return;
        }

        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO member (kode_member, nama, no_telepon, alamat) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, kodeMember);
            stmt.setString(2, nama);
            stmt.setString(3, noTelepon);
            stmt.setString(4, alamat);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal menambahkan data!");
            return;
        }

        showAlert("Data berhasil ditambahkan!");
        loadDataFromDatabase();
        clearFields();
    }

    @FXML
    private void editAnggota(ActionEvent event) {
        String selectedKodeMember = comboKodeMember.getValue();

        if (selectedKodeMember == null) {
            showAlert("Pilih Kode Member untuk Edit!");
            return;
        }

        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE member SET nama = ?, no_telepon = ?, alamat = ? WHERE kode_member = ?")) {
            stmt.setString(1, txtNama.getText().trim());
            stmt.setString(2, txtNoTelepon.getText().trim());
            stmt.setString(3, txtAlamat.getText().trim());
            stmt.setString(4, selectedKodeMember);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal mengedit data!");
            return;
        }

        showAlert("Data berhasil diperbarui!");
        loadDataFromDatabase();
        clearFields();
    }

    @FXML
    private void hapusAnggota(ActionEvent event) {
        String selectedKodeMember = comboKodeMember.getValue();

        if (selectedKodeMember == null) {
            showAlert("Pilih Kode Member untuk Hapus!");
            return;
        }

        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM member WHERE kode_member = ?")) {
            stmt.setString(1, selectedKodeMember);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal menghapus data!");
            return;
        }

        showAlert("Data berhasil dihapus!");
        loadDataFromDatabase();
        clearFields();
    }

    private void updateComboBoxKodeMember() {
        comboKodeMember.getItems().clear();
        for (AnggotaModel anggota : anggotaList) {
            comboKodeMember.getItems().add(anggota.getKodeMember());
        }
    }

    private void clearFields() {
        txtNama.clear();
        txtNoTelepon.clear();
        txtAlamat.clear();
        comboKodeMember.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onTableClicked(MouseEvent event) {
        AnggotaModel selectedAnggota = tableView.getSelectionModel().getSelectedItem();
        if (selectedAnggota != null) {
            comboKodeMember.setValue(selectedAnggota.getKodeMember());
            txtNama.setText(selectedAnggota.getNama());
            txtNoTelepon.setText(selectedAnggota.getNoTelepon());
            txtAlamat.setText(selectedAnggota.getAlamat());
        }
    }

    @FXML
    private void kembaliDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
