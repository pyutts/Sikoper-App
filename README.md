# Sikoper - Sistem Informasi Koperasi

**Sikoper** adalah aplikasi berbasis Java yang dirancang untuk membantu koperasi dalam mengelola data anggota, transaksi, simpanan, pinjaman, dan laporan keuangan secara efisien. Aplikasi ini menggunakan antarmuka grafis untuk mempermudah interaksi dengan pengguna, dan dapat digunakan oleh pengelola koperasi untuk melakukan berbagai pengelolaan keuangan dan administrasi anggota koperasi.

**Jika Hanya ingin Mencoba Aplikasi ini, Aplikasi ini sudah release di github silahkan lihat di link ini**
- [Installer SIKOPER](https://github.com/pyutts/Sikoper-App/releases/tag/v1.0.1)

## Fitur Utama

### 1. **Manajemen Anggota**
   - Menambah, mengedit, dan menghapus data anggota koperasi.
   - Menampilkan informasi anggota seperti nama, alamat, nomor anggota, dan status keanggotaan.
   - Memantau riwayat transaksi yang dilakukan oleh anggota.

### 2. **Manajemen Transaksi**
   - Mencatat transaksi keuangan, baik untuk simpanan maupun pinjaman anggota.
   - Memantau histori transaksi dan saldo yang dimiliki oleh anggota.

### 3. **Manajemen Simpanan & Pinjaman**
   - Mencatat simpanan yang dilakukan oleh anggota koperasi.
   - Mengelola pinjaman yang diberikan kepada anggota dan memantau pembayaran cicilan.

### 4. **Laporan Keuangan**
   - Menyediakan laporan keuangan yang dapat disesuaikan, seperti laporan tahunan, dan transaksi anggota.
   - Menampilkan laporan terkait simpanan, pinjaman, dan transaksi lainnya dalam bentuk tabel dan grafik.

### 5. **Antarmuka Pengguna (UI)**
   - Antarmuka pengguna yang mudah dipahami dan sederhana, menggunakan **JavaFX** 
   - Pengguna dapat dengan mudah melakukan navigasi untuk mengakses berbagai fitur aplikasi.

## Syarat yang dibutuhkan (WAJIB)

Agar aplikasi **Sikoper** dapat berjalan dengan baik, Anda perlu memenuhi beberapa prasyarat berikut:

- **Java Development Kit (JDK)** versi 8 (Java 11 atau lebih tinggi tidak mendukung nashron engine)
- **Integrated Development Environment (IDE)** seperti IntelliJ IDEA, Eclipse, atau NetBeans.
- **Database** untuk menyimpan data anggota dan transaksi, seperti **MySQL** atau disini saya memakai **SQLite**.
- **JavaFX** SDK 17 sebagai pustaka antarmuka pengguna.

## Instalasi

### 1. **Unduh dan Instal JDK**
   - Pastikan Anda memiliki **Java Development Kit (JDK)** versi 8 atau lebih tinggi. Jika belum, unduh dari situs resmi:
     - [Zulu JDK FX 8](https://www.azul.com/core-post-download/?endpoint=zulu&uuid=46a772a0-10e1-433c-8678-c1869ed3deb2)
     - [Gluon SDK FX 17](https://download2.gluonhq.com/openjfx/17.0.13/openjfx-17.0.13_windows-x64_bin-sdk.zip)

### 2. **Kloning atau Mengunduh Proyek**
   - Anda dapat mengunduh atau mengkloning proyek ini dengan menjalankan perintah Git berikut:
     ```bash
     git clone https://github.com/pyutts/Sikoper-App.git
     ```
   - Atau Anda bisa mengunduh proyek dalam bentuk ZIP dari halaman repositori GitHub ini.

### 3. **Buka Proyek di IDE**
   - Setelah mengunduh atau mengkloning proyek, buka proyek tersebut di IDE pilihan Anda:
     - IntelliJ IDEA
     - Eclipse
     - NetBeans
   - Jika menggunakan **Eclipse**, pastikan untuk mengimpor proyek sebagai proyek Java biasa.

### 4. **Pengaturan Database**
   Tidak diperlukan konfigurasi database lagi jika menggunakan SQLite, installer aplikasi sudah menyertakan file database yang dapat langsung digunakan.

### 5. **Users Admin**
   **Masukkan Users Admin ini jika ingin login ke dalam aplikasi**:
   | No | Username | Password |
   |----------|----------|----------|
   | 1 | admin1 | admin234 |
   | 2 | admin2 | admin456 |


### Object Oriented Programing (Final Exams MI)
 
