/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * FXML Controller class
 *
 * @author pyuts
 */


public class DashboardController {

    @FXML
    private void tombolPinjamanClick(MouseEvent event) {
        loadPage("/Views/Pinjaman.fxml", event);
    }

    @FXML
    private void tombolSimpananClick(MouseEvent event) {
        loadPage("/Views/Simpanan.fxml", event);
    }

    @FXML
    private void tombolRekapanClick(MouseEvent event) {
        loadPage("/Views/Laporan.fxml", event);
    }

    @FXML
    private void tombolAnggotaClick(MouseEvent event) {
        loadPage("/Views/Anggota.fxml", event);
    }

    @FXML
    private void tombolLogoutClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - SiKoper");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String fxmlPath, MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
