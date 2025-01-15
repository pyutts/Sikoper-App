/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import Config.Connect;
import Config.UniqueCodeGenerator;
import Model.AnggotaModel;
import Model.SimpananModel;
import Utils.CurrencyUtils;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
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

public class SimpananController {

    @FXML private ComboBox<String> cbKodeMember;
    @FXML private TextField tfNamaAnggota;
    @FXML private TextField tfJumlah;
    @FXML private DatePicker dpTanggal;
    @FXML private ComboBox<String> cbJenisTransaksi;
    @FXML private TextField searchField;
    @FXML private TableView<SimpananModel> tableSimpanan;
    @FXML private TableColumn<SimpananModel, Integer> colNo;
    @FXML private TableColumn<SimpananModel, String> colKodeTransaksi;
    @FXML private TableColumn<SimpananModel, String> colKodeMember;
    @FXML private TableColumn<SimpananModel, String> colNamaAnggota;
    @FXML private TableColumn<SimpananModel, LocalDate> colTanggal;
    @FXML private TableColumn<SimpananModel, Double> colJumlah;
    @FXML private TableColumn<SimpananModel, String> colTipe;

    private ObservableList<SimpananModel> dataSimpanan;
    private HashMap<String, String> idNamaMap = new HashMap<>();

    @FXML
    public void initialize() {
        dataSimpanan = FXCollections.observableArrayList();
        setupTable();
        muatDataTabel();
        isiComboBoxKodeMember();
        cbJenisTransaksi.setItems(FXCollections.observableArrayList(
            "Deposit",
            "Tabungan"
        ));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue));
    }

    private void setupTable() {
        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colKodeTransaksi.setCellValueFactory(new PropertyValueFactory<>("kodeTransaksi"));
        colKodeMember.setCellValueFactory(new PropertyValueFactory<>("kodeMember"));
        colNamaAnggota.setCellValueFactory(new PropertyValueFactory<>("namaAnggota"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
         colJumlah.setCellFactory(column -> new TableCell<SimpananModel, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(CurrencyUtils.formatRupiah(item));
                }
            }
        });
        colTipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        tableSimpanan.setItems(dataSimpanan);
    }

    private void isiComboBoxKodeMember() {
        try (Connection connection = Connect.connect()) {
            String query = "SELECT kode_member, nama FROM member";
            ResultSet rs = connection.createStatement().executeQuery(query);
            while (rs.next()) {
                String kodeMember = rs.getString("kode_member");
                String nama = rs.getString("nama");
                cbKodeMember.getItems().add(kodeMember);
                idNamaMap.put(kodeMember, nama);
            }

            cbKodeMember.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    tfNamaAnggota.setText(idNamaMap.get(newVal));
                }
            });
        } catch (SQLException e) {
            tampilkanPeringatan("Kesalahan", "Gagal memuat data anggota: " + e.getMessage());
        }
    }

    private void muatDataTabel() {
        dataSimpanan.clear();
        try (Connection connection = Connect.connect()) {
            String query = "SELECT t.id, t.kode_transaksi, m.kode_member, m.nama, " +
                          "t.transaksi_date, t.jumlah, t.transaksi_type " +
                          "FROM transaksi t " +
                          "JOIN member m ON t.member_id = m.id";
            ResultSet rs = connection.createStatement().executeQuery(query);
            int no = 1;
            while (rs.next()) {
                dataSimpanan.add(new SimpananModel(
                    no++,
                    rs.getInt("id"),
                    rs.getString("kode_transaksi"),
                    rs.getString("kode_member"),
                    rs.getString("nama"),
                    rs.getDate("transaksi_date").toLocalDate(),
                    rs.getDouble("jumlah"),
                    rs.getString("transaksi_type")
                ));
            }
        } catch (SQLException e) {
            tampilkanPeringatan("Kesalahan", "Gagal memuat data simpanan: " + e.getMessage());
        }
    }

    private void filterData(String keyword) {
        ObservableList<SimpananModel> filteredList = FXCollections.observableArrayList();
        for (SimpananModel simpanan : dataSimpanan) {
            if (simpanan.getNamaAnggota().toLowerCase().contains(keyword.toLowerCase()) ||
                simpanan.getKodeMember().toLowerCase().contains(keyword.toLowerCase()) ||
                simpanan.getKodeTransaksi().toLowerCase().contains(keyword.toLowerCase()) ||
                simpanan.getTanggal().toString().contains(keyword) ||
                String.valueOf(simpanan.getJumlah()).contains(keyword) ||
                simpanan.getTipe().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(simpanan);
            }
        }
        tableSimpanan.setItems(filteredList);
    }

    @FXML
    private void tambahData() {
        if (!validasiInput()) return;
        try (Connection connection = Connect.connect()) {
            String memberId = getMemberId(cbKodeMember.getValue());
            if (memberId == null) {
                tampilkanPeringatan("Error", "Member tidak ditemukan");
                return;
            }

            String kodeTransaksi = UniqueCodeGenerator.generateUniqueCode("TRX", 6);
            String query = "INSERT INTO transaksi (kode_transaksi, member_id, transaksi_date, jumlah, transaksi_type) " +
                          "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, kodeTransaksi);
            ps.setString(2, memberId);
            ps.setDate(3, Date.valueOf(dpTanggal.getValue()));
            ps.setDouble(4, Double.parseDouble(tfJumlah.getText()));
            ps.setString(5, cbJenisTransaksi.getValue());
            ps.executeUpdate();

            tampilkanPeringatan("Sukses", "Data berhasil ditambahkan!");
            muatDataTabel();
            bersihkanField();
        } catch (SQLException e) {
            tampilkanPeringatan("Kesalahan", "Gagal menambah data: " + e.getMessage());
        }
    }

    @FXML
    private void editData() {
        SimpananModel selected = tableSimpanan.getSelectionModel().getSelectedItem();
        if (selected == null || !validasiInput()) {
            tampilkanPeringatan("Validasi Gagal", "Pilih data yang akan diubah!");
            return;
        }
        try (Connection connection = Connect.connect()) {
            String memberId = getMemberId(cbKodeMember.getValue());
            if (memberId == null) {
                tampilkanPeringatan("Error", "Member tidak ditemukan");
                return;
            }

            String query = "UPDATE transaksi SET member_id = ?, transaksi_date = ?, jumlah = ?, transaksi_type = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, memberId);
            ps.setDate(2, Date.valueOf(dpTanggal.getValue()));
            ps.setDouble(3, Double.parseDouble(tfJumlah.getText()));
            ps.setString(4, cbJenisTransaksi.getValue());
            ps.setInt(5, selected.getId());
            ps.executeUpdate();

            tampilkanPeringatan("Sukses", "Data berhasil diperbarui!");
            muatDataTabel();
            bersihkanField();
        } catch (SQLException e) {
            tampilkanPeringatan("Kesalahan", "Gagal memperbarui data: " + e.getMessage());
        }
    }

    @FXML
    private void hapusData() {
        SimpananModel selected = tableSimpanan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            tampilkanPeringatan("Validasi Gagal", "Pilih data yang akan dihapus!");
            return;
        }
        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus data ini?", ButtonType.OK, ButtonType.CANCEL);
        konfirmasi.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection connection = Connect.connect()) {
                    String query = "DELETE FROM transaksi WHERE id = ?";
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, selected.getId());
                    ps.executeUpdate();
                    tampilkanPeringatan("Sukses", "Data berhasil dihapus!");
                    muatDataTabel();
                    bersihkanField();
                } catch (SQLException e) {
                    tampilkanPeringatan("Kesalahan", "Gagal menghapus data: " + e.getMessage());
                }
            }
        });
    }

    private boolean validasiInput() {
        return cbKodeMember.getValue() != null && dpTanggal.getValue() != null && !tfJumlah.getText().isEmpty() && cbJenisTransaksi.getValue() != null;
    }

    private void tampilkanPeringatan(String judul, String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void bersihkanField() {
        cbKodeMember.setValue(null);
        tfNamaAnggota.clear();
        dpTanggal.setValue(null);
        tfJumlah.clear();
        cbJenisTransaksi.setValue(null);
    }

    private String getMemberId(String kodeMember) {
        try (Connection connection = Connect.connect()) {
            String query = "SELECT id FROM member WHERE kode_member = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, kodeMember);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void onTableClicked(MouseEvent event) {
        SimpananModel selected = tableSimpanan.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cbKodeMember.setValue(selected.getKodeMember());
            tfNamaAnggota.setText(selected.getNamaAnggota());
            dpTanggal.setValue(selected.getTanggal());
            tfJumlah.setText(String.valueOf(selected.getJumlah()));
            cbJenisTransaksi.setValue(selected.getTipe());
        }
    }

    @FXML
    private void cariMember() {
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
}
