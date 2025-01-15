/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import Config.Connect;
import Model.AnggotaModel;
import Model.LaporanModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * FXML Controller class
 *
 * @author pyuts
 */

public class LaporanPerseoranganController {
    @FXML private TableView<LaporanModel> tableLaporan;
    @FXML private TableColumn<LaporanModel, String> colNama;
    @FXML private TableColumn<LaporanModel, String> colTanggalPinjam;
    @FXML private TableColumn<LaporanModel, Double> colJumlahPinjam;
    @FXML private TableColumn<LaporanModel, String> colStatusPinjam;
    @FXML private TableColumn<LaporanModel, String> colTanggalSimpan;
    @FXML private TableColumn<LaporanModel, Double> colJumlahSimpan;

    private ObservableList<LaporanModel> dataLaporan;

    @FXML
    public void initialize() {
        dataLaporan = FXCollections.observableArrayList();
        setupTable();
    }

    private void setupTable() {
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colTanggalPinjam.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        colJumlahPinjam.setCellValueFactory(new PropertyValueFactory<>("jumlahPinjam"));
        colStatusPinjam.setCellValueFactory(new PropertyValueFactory<>("statusPinjam"));
        colTanggalSimpan.setCellValueFactory(new PropertyValueFactory<>("tanggalSimpan"));
        colJumlahSimpan.setCellValueFactory(new PropertyValueFactory<>("jumlahSimpan"));
        tableLaporan.setItems(dataLaporan);
    }

    @FXML
    private void handleCariAnggota(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AnggotaSearch.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cari Member");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableLaporan.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AnggotaSearchController controller = loader.getController();
            dialogStage.showAndWait();

            if (controller.isPilihClicked()) {
                AnggotaModel selected = controller.getSelectedMember();
                loadDataPerseorangan(selected.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDataPerseorangan(int memberId) {
        dataLaporan.clear();
        try (Connection connection = Connect.connect()) {
            String query = "SELECT m.nama, p.tgl_pinjam, p.pinjam_amount, p.pinjam_status, t.transaksi_date, t.jumlah, t.transaksi_type " +
                           "FROM member m " +
                           "LEFT JOIN pinjam p ON m.id = p.member_id " +
                           "LEFT JOIN transaksi t ON m.id = t.member_id " +
                           "WHERE m.id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dataLaporan.add(new LaporanModel(
                    rs.getString("nama"),
                    rs.getDate("tgl_pinjam") != null ? rs.getDate("tgl_pinjam").toString() : "",
                    rs.getDouble("pinjam_amount"),
                    rs.getString("pinjam_status"),
                    rs.getDate("transaksi_date") != null ? rs.getDate("transaksi_date").toString() : "",
                    rs.getDouble("jumlah")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrint(ActionEvent event) {
        if (dataLaporan.isEmpty()) {
            showAlert("Error", "Tidak ada data untuk dicetak. Silakan pilih anggota terlebih dahulu.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                Document document = new Document(PageSize.A4);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                addDocumentHeader(document);
                addReportContent(document);
                addDocumentFooter(document);

                document.close();
                showAlert("Sukses", "Laporan berhasil diekspor ke PDF.");

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                showAlert("Error", "Gagal mengekspor laporan ke PDF: " + e.getMessage());
            }
        }
    }

    private void addDocumentHeader(Document document) throws DocumentException, IOException {
        Image logo = Image.getInstance(getClass().getResource("/Images/logo.png"));
        logo.scaleToFit(70, 70);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph title = new Paragraph("Laporan Transaksi Perseorangan", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addReportContent(Document document) throws DocumentException {
        // Add member info
        if (!dataLaporan.isEmpty()) {
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Paragraph memberInfo = new Paragraph("Data Anggota: " + dataLaporan.get(0).getNama(), infoFont);
            memberInfo.setSpacingAfter(10);
            document.add(memberInfo);
        }

        // Create and add the main table
        PdfPTable table = createMainTable();
        document.add(table);

        // Add notes
        addNotes(document);
    }

    private PdfPTable createMainTable() throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);

        // Set column widths
        float[] columnWidths = {2f, 2f, 2f, 2f, 2f, 2f};
        table.setWidths(columnWidths);

        // Add headers with white text
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        BaseColor headerColor = new BaseColor(64, 75, 124);

        addTableHeader(table, "Nama", headerFont, headerColor);
        addTableHeader(table, "Tanggal Pinjam", headerFont, headerColor);
        addTableHeader(table, "Jumlah Pinjam", headerFont, headerColor);
        addTableHeader(table, "Status Pinjam", headerFont, headerColor);
        addTableHeader(table, "Tanggal Simpan", headerFont, headerColor);
        addTableHeader(table, "Jumlah Simpan", headerFont, headerColor);

        // Add data rows with alternating colors
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        BaseColor altRowColor = new BaseColor(240, 240, 240);
        boolean isAltRow = false;

        for (LaporanModel laporan : dataLaporan) {
            BaseColor rowColor = isAltRow ? altRowColor : BaseColor.WHITE;
            
            addTableCell(table, laporan.getNama(), cellFont, rowColor, Element.ALIGN_LEFT);
            addTableCell(table, laporan.getTanggalPinjam(), cellFont, rowColor, Element.ALIGN_CENTER);
            addTableCell(table, formatCurrency(laporan.getJumlahPinjam()), cellFont, rowColor, Element.ALIGN_RIGHT);
            addTableCell(table, laporan.getStatusPinjam(), cellFont, rowColor, Element.ALIGN_CENTER);
            addTableCell(table, laporan.getTanggalSimpan(), cellFont, rowColor, Element.ALIGN_CENTER);
            addTableCell(table, formatCurrency(laporan.getJumlahSimpan()), cellFont, rowColor, Element.ALIGN_RIGHT);

            isAltRow = !isAltRow;
        }

        return table;
    }

    private void addTableHeader(PdfPTable table, String text, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, BaseColor backgroundColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addNotes(Document document) throws DocumentException {
        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph notes = new Paragraph();
        notes.add(new Chunk("Catatan:\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.GRAY)));
        notes.add(new Chunk("* Status pinjaman: pending (menunggu persetujuan), diterima, atau ditolak\n", noteFont));
        notes.add(new Chunk("* Jumlah dalam Rupiah (Rp)", noteFont));
        notes.setSpacingBefore(20);
        document.add(notes);
    }

    private void addDocumentFooter(Document document) throws DocumentException {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        Paragraph footer = new Paragraph(
            "Dokumen ini dihasilkan secara otomatis pada " + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")), 
            footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }

    private String formatCurrency(double amount) {
        return String.format("Rp%.2f", amount);
    }

    @FXML
    private void kembaliDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}