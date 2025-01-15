package Config;

import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {
    private static final String DB_FOLDER = "data"; // Folder untuk menyimpan database
    private static final String DB_NAME = "Sikoper.db";

    public static Connection connect() {
        Connection conn = null;

        try {
            // Tentukan lokasi penyimpanan database
            String userDir = System.getProperty("user.dir"); // Folder aplikasi
            File dbFolder = new File(userDir, DB_FOLDER);
            if (!dbFolder.exists()) {
                dbFolder.mkdirs(); // Buat folder jika belum ada
            }

            File dbFile = new File(dbFolder, DB_NAME);
            if (!dbFile.exists()) {
                // Jika file database belum ada, ekstrak dari JAR
                try (InputStream dbStream = Connect.class.getResourceAsStream("/Config/Sikoper.db")) {
                    if (dbStream == null) {
                        System.err.println("File database tidak ditemukan di dalam JAR.");
                        return null;
                    }
                    Files.copy(dbStream, dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Koneksi ke file database tetap
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            System.out.println("Koneksi Berhasil ke database di lokasi: " + dbFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
        }

        return conn;
    }
}
