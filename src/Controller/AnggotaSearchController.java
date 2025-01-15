/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import Config.Connect;
import Model.AnggotaModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * FXML Controller class
 *
 * @author pyuts
 */


public class AnggotaSearchController {
    @FXML private TextField searchField;
    @FXML private TableView<AnggotaModel> tableAnggota;
    @FXML private TableColumn<AnggotaModel, String> colKodeMember;
    @FXML private TableColumn<AnggotaModel, String> colNama;
    @FXML private TableColumn<AnggotaModel, String> colNoTelepon;
    @FXML private TableColumn<AnggotaModel, String> colAlamat;

    private AnggotaModel selectedMember;
    private boolean pilihClicked = false;

    @FXML
    public void initialize() {
        setupTable();
        loadMembers();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterMembers(newValue);
        });
    }

    private void setupTable() {
        colKodeMember.setCellValueFactory(new PropertyValueFactory<>("kodeMember"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNoTelepon.setCellValueFactory(new PropertyValueFactory<>("noTelepon"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
    }

    private void loadMembers() {
        ObservableList<AnggotaModel> members = FXCollections.observableArrayList();
        try (Connection conn = Connect.connect()) {
            String query = "SELECT * FROM member";
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                members.add(new AnggotaModel(
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
        tableAnggota.setItems(members);
    }

    private void filterMembers(String keyword) {
        ObservableList<AnggotaModel> filteredList = FXCollections.observableArrayList();
        tableAnggota.getItems().forEach(member -> {
            if (member.getNama().toLowerCase().contains(keyword.toLowerCase()) ||
                member.getKodeMember().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(member);
            }
        });
        tableAnggota.setItems(filteredList);
    }

    @FXML
    private void handlePilih() {
        selectedMember = tableAnggota.getSelectionModel().getSelectedItem();
        pilihClicked = true;
        closeDialog();
    }

    @FXML
    private void handleBatal() {
        pilihClicked = false;
        closeDialog();
    }

    private void closeDialog() {
        ((Stage) searchField.getScene().getWindow()).close();
    }

    public AnggotaModel getSelectedMember() {
        return selectedMember;
    }

    public boolean isPilihClicked() {
        return pilihClicked;
    }
}