package com.example.rai_oreo.data

enum class Role(val label: String) {
    PELANGGAN("Pelanggan"),
    KARYAWAN("Karyawan"),
    PEMILIK("Pemilik"),
}

data class User(
    val id: Int,
    val nama: String,
    val email: String,
    val telepon: String,
    val role: Role,
    val alamat: String = "",
    val bergabung: String = "Jan 2026",
)

enum class OrderStatus(val label: String) {
    BARU("Pesanan Baru"),
    DESAIN("Design"),
    DIPROSES("Diproses"),
    PRODUKSI("Produksi"),
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan");

    val step: Int
        get() = when (this) {
            BARU -> 0
            DESAIN -> 1
            DIPROSES -> 1
            PRODUKSI -> 2
            SELESAI -> 3
            DIBATALKAN -> -1
        }

    val isActive: Boolean
        get() = this == BARU || this == DESAIN || this == DIPROSES || this == PRODUKSI
}

data class OrderEvent(
    val title: String,
    val waktu: String,
    val catatan: String,
    val done: Boolean,
)

data class Service(
    val id: Int,
    val nama: String,
    val kategori: String,
    val hargaMulai: Int,
    val satuan: String,
    val deskripsi: String,
    val rating: Double,
    val jumlahPesanan: Int,
    val estimasi: String,
    val bahan: List<String>,
    val finishing: List<String>,
    val gambar: Int,
    val contoh: List<Int>,
    val populer: Boolean = false,
    val aktif: Boolean = true,
)

data class Order(
    val nomor: String,
    val serviceId: Int,
    val serviceNama: String,
    val gambar: Int,
    val ukuran: String,
    val jumlah: Int,
    val bahan: String,
    val finishing: String,
    val catatan: String,
    val total: Int,
    val tanggal: String,
    val status: OrderStatus,
    val pelanggan: String,
    val desainNama: String = "",
    val desainUkuran: String = "",
    val timeline: List<OrderEvent> = emptyList(),
)

data class AppNotification(
    val id: Int,
    val judul: String,
    val isi: String,
    val waktu: String,
    val dibaca: Boolean = false,
    val tipe: NotificationType = NotificationType.PESANAN,
)

enum class NotificationType { PESANAN, PROMO, SISTEM }

data class Address(
    val id: Int,
    val label: String,
    val nama: String,
    val telepon: String,
    val detail: String,
    val utama: Boolean = false,
)

fun Int.toRupiah(): String {
    val s = this.toString()
    val sb = StringBuilder()
    var count = 0
    for (i in s.length - 1 downTo 0) {
        sb.append(s[i])
        count++
        if (count % 3 == 0 && i != 0) sb.append('.')
    }
    return "Rp " + sb.reverse().toString()
}
