/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package Controller;

import Config.Connect;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * FXML Controller class
 *
 * @author pyuts
 */



public class LaporanKeseluruhanController {
    @FXML private ComboBox<String> cbTahun;
    @FXML private ComboBox<String> cbJenisTransaksi;
    @FXML private PieChart pieChart;

    private List<TransaksiData> currentTransaksiList = new ArrayList<>();
    private int totalTransaksi = 0;
    private double totalAmount = 0;
    private String topDepositMember = "";
    private String topTabunganMember = "";

    @FXML
    public void initialize() {
        isiComboBoxTahun();
        isiComboBoxJenisTransaksi();
    }

    private void isiComboBoxTahun() {
        cbTahun.setItems(FXCollections.observableArrayList(
            "2022", "2023", "2024", "2025"
        ));
    }

    private void isiComboBoxJenisTransaksi() {
        cbJenisTransaksi.setItems(FXCollections.observableArrayList(
            "Semua", "Pinjaman", "Simpanan"
        ));
    }

    @FXML
    private void handleLoadData(ActionEvent event) {
        String tahun = cbTahun.getValue();
        String jenisTransaksi = cbJenisTransaksi.getValue();
        if (tahun == null || jenisTransaksi == null) {
            showAlert("Error", "Pilih tahun dan jenis transaksi terlebih dahulu.");
            return;
        }
        loadDataKeseluruhan(tahun, jenisTransaksi);
    }

    private void loadDataKeseluruhan(String tahun, String jenisTransaksi) {
        pieChart.getData().clear();
        currentTransaksiList.clear();
        totalTransaksi = 0;
        totalAmount = 0;
        
        try (Connection connection = Connect.connect()) {
            String query;
            PreparedStatement ps;
            
            if (jenisTransaksi.equals("Semua")) {
                query = "SELECT t.transaksi_type, COUNT(*) as jumlah, SUM(t.jumlah) as total_amount " +
                       "FROM transaksi t " +
                       "WHERE substr(t.transaksi_date, 1, 4) = ? " +
                       "GROUP BY t.transaksi_type";
                ps = connection.prepareStatement(query);
                ps.setString(1, tahun);
            } else {
                String transactionType = jenisTransaksi.equals("Pinjaman") ? "Deposit" : "Tabungan";
                query = "SELECT t.transaksi_type, COUNT(*) as jumlah, SUM(t.jumlah) as total_amount " +
                       "FROM transaksi t " +
                       "WHERE substr(t.transaksi_date, 1, 4) = ? AND t.transaksi_type = ? " +
                       "GROUP BY t.transaksi_type";
                ps = connection.prepareStatement(query);
                ps.setString(1, tahun);
                ps.setString(2, transactionType);
            }

            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("transaksi_type");
                int jumlah = rs.getInt("jumlah");
                double amount = rs.getDouble("total_amount");
                
                totalTransaksi += jumlah;
                totalAmount += amount;
                currentTransaksiList.add(new TransaksiData(type, jumlah, amount));
            }

            if (currentTransaksiList.isEmpty()) {
                showAlert("Informasi", "Tidak ada data untuk tahun " + tahun);
                return;
            }

            for (TransaksiData data : currentTransaksiList) {
                double persentase = (data.jumlah / (double) totalTransaksi) * 100;
                String label = String.format("%s: %d transaksi (%.2f%%) - Rp%.2f", 
                    getTransactionTypeLabel(data.type), data.jumlah, persentase, data.amount);
                pieChart.getData().add(new PieChart.Data(label, data.jumlah));
            }

            topDepositMember = getTopMemberForYear(connection, "Deposit", tahun);
            topTabunganMember = getTopMemberForYear(connection, "Tabungan", tahun);

            showAlert("Statistik " + tahun,
                String.format("Total Transaksi: %d\nTotal Amount: Rp%.2f\n" +
                            "Member dengan Deposit terbanyak: %s\n" +
                            "Member dengan Tabungan terbanyak: %s",
                            totalTransaksi, totalAmount, topDepositMember, topTabunganMember));

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan saat memuat data: " + e.getMessage());
        }
    }

    private String getTopMemberForYear(Connection connection, String type, String tahun) throws SQLException {
        String query = "SELECT m.nama, COUNT(*) as jumlah, SUM(t.jumlah) as total_amount " +
                      "FROM transaksi t " +
                      "JOIN member m ON t.member_id = m.id " +
                      "WHERE t.transaksi_type = ? AND substr(t.transaksi_date, 1, 4) = ? " +
                      "GROUP BY m.nama " +
                      "ORDER BY jumlah DESC " +
                      "LIMIT 1";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, type);
        ps.setString(2, tahun);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return String.format("%s (%d transaksi - Rp%.2f)", 
                rs.getString("nama"), 
                rs.getInt("jumlah"),
                rs.getDouble("total_amount"));
        }
        return "Tidak ada data";
    }

    private String getTransactionTypeLabel(String type) {
        return type.equals("Deposit") ? "Pinjaman" : "Simpanan";
    }

    public static class TransaksiData {
        String type;
        int jumlah;
        double amount;

        public TransaksiData(String type, int jumlah, double amount) {
            this.type = type;
            this.jumlah = jumlah;
            this.amount = amount;
        }
    }

    @FXML
    private void handlePrint(ActionEvent event) {
        if (pieChart.getData().isEmpty()) {
            showAlert("Error", "Tidak ada data untuk dicetak. Silakan load data terlebih dahulu.");
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
        Paragraph title = new Paragraph("Laporan Statistik Transaksi Tahunan", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addReportContent(Document document) throws DocumentException {
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        BaseColor headerColor = new BaseColor(64, 75, 124);

        PdfPTable infoTable = createInfoTable(infoFont);
        document.add(infoTable);

        PdfPTable transactionTable = createTransactionTable(tableHeaderFont, tableCellFont, headerColor);
        document.add(transactionTable);

        addTopMembersSection(document, headerColor, tableHeaderFont, tableCellFont);
        addNotes(document);
    }

    private PdfPTable createInfoTable(Font infoFont) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10);
        infoTable.setSpacingAfter(10);

        addInfoRow(infoTable, "Tahun", cbTahun.getValue(), infoFont);
        addInfoRow(infoTable, "Jenis Transaksi", cbJenisTransaksi.getValue(), infoFont);
        addInfoRow(infoTable, "Total Transaksi", String.valueOf(totalTransaksi), infoFont);
        addInfoRow(infoTable, "Total Amount", formatCurrency(totalAmount), infoFont);

        return infoTable;
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label + ":", font));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        
        labelCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBorder(Rectangle.NO_BORDER);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private PdfPTable createTransactionTable(Font headerFont, Font cellFont, BaseColor headerColor) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setSpacingAfter(20);
        table.setWidths(new float[]{3f, 2f, 2f, 2f});

        addStyledCell(table, "Jenis Transaksi", headerFont, headerColor, Element.ALIGN_CENTER);
        addStyledCell(table, "Jumlah", headerFont, headerColor, Element.ALIGN_CENTER);
        addStyledCell(table, "Total", headerFont, headerColor, Element.ALIGN_CENTER);
        addStyledCell(table, "Persentase", headerFont, headerColor, Element.ALIGN_CENTER);

        BaseColor altRowColor = new BaseColor(240, 240, 240);
        boolean isAltRow = false;
        
        for (TransaksiData data : currentTransaksiList) {
            BaseColor rowColor = isAltRow ? altRowColor : BaseColor.WHITE;
            double percentage = (data.jumlah / (double) totalTransaksi) * 100;

            addStyledCell(table, getTransactionTypeLabel(data.type), cellFont, rowColor, Element.ALIGN_LEFT);
            addStyledCell(table, String.valueOf(data.jumlah), cellFont, rowColor, Element.ALIGN_CENTER);
            addStyledCell(table, formatCurrency(data.amount), cellFont, rowColor, Element.ALIGN_RIGHT);
            addStyledCell(table, String.format("%.2f%%", percentage), cellFont, rowColor, Element.ALIGN_CENTER);

            isAltRow = !isAltRow;
        }

        return table;
    }

    private void addStyledCell(PdfPTable table, String text, Font font, BaseColor backgroundColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTopMembersSection(Document document, BaseColor headerColor, Font headerFont, Font cellFont) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Paragraph membersSection = new Paragraph("Anggota Terbaik", sectionFont);
        membersSection.setSpacingBefore(20);
        membersSection.setSpacingAfter(10);
        document.add(membersSection);

        PdfPTable membersTable = new PdfPTable(2);
        membersTable.setWidthPercentage(100);
        membersTable.setSpacingBefore(10);
        membersTable.setSpacingAfter(20);

        addTopMemberRow(membersTable, "Deposit Terbanyak", topDepositMember, headerColor, headerFont, cellFont);
        addTopMemberRow(membersTable, "Tabungan Terbanyak", topTabunganMember, headerColor, headerFont, cellFont);
        
        document.add(membersTable);
    }

    private void addTopMemberRow(PdfPTable table, String label, String value, BaseColor headerColor, Font headerFont, Font cellFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, headerFont));
        labelCell.setBackgroundColor(headerColor);
        labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        labelCell.setPadding(5);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, cellFont));
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addNotes(Document document) throws DocumentException {
        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph notes = new Paragraph();
        notes.add(new Chunk("Catatan:\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.GRAY)));
        notes.add(new Chunk("* Persentase dihitung berdasarkan jumlah transaksi\n", noteFont));
        notes.add(new Chunk("* Total amount merupakan akumulasi nilai transaksi", noteFont));
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