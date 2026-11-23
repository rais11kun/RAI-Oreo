/**
 * Penerapan praktikum: Modul 12 - pemetaan data REST (DTO) ke model yang sudah dipakai UI.
 * Dengan mapper ini, layar tidak perlu diubah: data Supabase diubah menjadi Service/Order
 * lalu dimasukkan ke SimperRepository sebagai cache lokal.
 */
package com.example.rai_oreo.data.remote

import com.example.rai_oreo.R
import com.example.rai_oreo.data.Order
import com.example.rai_oreo.data.OrderEvent
import com.example.rai_oreo.data.OrderStatus
import com.example.rai_oreo.data.Service

object RemoteMapper {

    /** id lokal (Service.id) -> id asli di tabel layanan. */
    private val idLayanan = mutableMapOf<Int, Long>()

    /** nomor pesanan (#ORD...) -> id asli di tabel pesanan. */
    private val idPesanan = mutableMapOf<String, Long>()

    fun idLayananRemote(idLokal: Int): Long? = idLayanan[idLokal]

    fun idPesananRemote(nomor: String): Long? = idPesanan[nomor]

    /** Dicatat saat pesanan baru dibuat di Supabase agar langsung bisa diubah/dihapus. */
    fun catatPesanan(nomor: String?, id: Long?) {
        if (!nomor.isNullOrBlank() && id != null) idPesanan[nomor] = id
    }

    // --------------------------------------------------------------- layanan
    fun keService(dto: LayananDto): Service = Service(
        id = (dto.id ?: 0L).toInt(),
        nama = dto.nama,
        kategori = dto.kategori,
        hargaMulai = dto.hargaMulai,
        satuan = dto.satuan,
        deskripsi = dto.deskripsi.orEmpty(),
        rating = 4.8,
        jumlahPesanan = 0,
        estimasi = dto.estimasi.orEmpty().ifBlank { "1 - 2 hari" },
        bahan = dto.bahan.orEmpty().ifEmpty { listOf("Flexi 440 gr") },
        finishing = dto.finishing.orEmpty().ifEmpty { listOf("Mata Ayam") },
        gambar = gambarUntuk(dto.kategori, dto.nama),
        contoh = listOf(
            R.drawable.contoh_banner_1,
            R.drawable.contoh_banner_2,
            R.drawable.contoh_banner_3,
            R.drawable.contoh_banner_4,
        ),
        populer = false,
        aktif = dto.aktif,
    )

    fun keDaftarService(daftar: List<LayananDto>): List<Service> {
        idLayanan.clear()
        return daftar.map { dto ->
            val service = keService(dto)
            dto.id?.let { idLayanan[service.id] = it }
            service
        }
    }

    fun dtoBaru(
        nama: String,
        kategori: String,
        harga: Int,
        satuan: String,
        deskripsi: String,
    ): LayananDto = LayananDto(
        nama = nama,
        kategori = kategori,
        hargaMulai = harga,
        satuan = satuan,
        deskripsi = deskripsi,
    )

    fun dtoUbah(
        idLokal: Int,
        nama: String,
        kategori: String,
        harga: Int,
        satuan: String,
        deskripsi: String,
    ): LayananDto = LayananDto(
        id = idLayananRemote(idLokal),
        nama = nama,
        kategori = kategori,
        hargaMulai = harga,
        satuan = satuan,
        deskripsi = deskripsi,
    )

    // --------------------------------------------------------------- pesanan
    fun keOrder(dto: PesananDto, daftarLayanan: List<Service>, namaPelanggan: String): Order {
        val layanan = daftarLayanan.firstOrNull { it.id == (dto.layananId ?: 0L).toInt() }
        val status = OrderStatus.entries.firstOrNull { it.name == dto.status } ?: OrderStatus.BARU
        val tanggal = tanggalIndo(dto.tanggal ?: dto.createdAt?.take(10))

        return Order(
            nomor = dto.nomor.orEmpty(),
            serviceId = (dto.layananId ?: 0L).toInt(),
            serviceNama = layanan?.nama ?: "Layanan",
            gambar = layanan?.gambar ?: R.drawable.produk_banner,
            ukuran = dto.ukuran.orEmpty().ifBlank { "-" },
            jumlah = dto.jumlah,
            bahan = dto.bahan.orEmpty().ifBlank { "-" },
            finishing = dto.finishing.orEmpty().ifBlank { "-" },
            catatan = dto.catatan.orEmpty(),
            total = dto.total,
            tanggal = tanggal,
            status = status,
            pelanggan = namaPelanggan,
            desainNama = dto.desainNama.orEmpty(),
            desainUkuran = dto.desainUkuran.orEmpty(),
            timeline = timeline(status, tanggal),
        )
    }

    fun keDaftarOrder(
        daftar: List<PesananDto>,
        daftarLayanan: List<Service>,
        namaPelanggan: String,
    ): List<Order> {
        idPesanan.clear()
        return daftar.map { dto ->
            val order = keOrder(dto, daftarLayanan, namaPelanggan)
            dto.id?.let { idPesanan[order.nomor] = it }
            order
        }
    }

    // ------------------------------------------------------------------ util
    private fun timeline(status: OrderStatus, tanggal: String): List<OrderEvent> {
        val langkah = status.step
        return listOf(
            OrderEvent("Pesanan Masuk", tanggal, "Pesanan berhasil dibuat", langkah >= 0),
            OrderEvent("Sedang Design", if (langkah >= 1) tanggal else "-", "Menunggu tim desain", langkah >= 1),
            OrderEvent("Dalam Produksi", if (langkah >= 2) tanggal else "-", "Menunggu proses produksi", langkah >= 2),
            OrderEvent("Pesanan Selesai", if (langkah >= 3) tanggal else "-", "Pesanan siap diambil", langkah >= 3),
        )
    }

    /** "2026-09-12" -> "12 Sep 2026" (tanpa java.time agar aman di minSdk 24). */
    private fun tanggalIndo(iso: String?): String {
        if (iso.isNullOrBlank()) return "-"
        val bersih = iso.take(10)
        val bagian = bersih.split("-")
        if (bagian.size != 3) return bersih
        val bulan = listOf(
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Agu", "Sep", "Okt", "Nov", "Des",
        )
        val b = bagian[1].toIntOrNull() ?: return bersih
        val hari = bagian[2].trimStart('0').ifBlank { "0" }
        return "$hari ${bulan.getOrElse(b - 1) { bagian[1] }} ${bagian[0]}"
    }

    private fun gambarUntuk(kategori: String, nama: String): Int {
        val teks = "$kategori $nama".lowercase()
        return when {
            teks.contains("spanduk") -> R.drawable.produk_spanduk
            teks.contains("stiker") -> R.drawable.produk_stiker
            teks.contains("kartu") || teks.contains("nama") -> R.drawable.produk_idcard
            teks.contains("nota") -> R.drawable.produk_nota
            teks.contains("undangan") -> R.drawable.produk_undangan
            teks.contains("neon") -> R.drawable.produk_neonbox
            teks.contains("roll") -> R.drawable.produk_rollbanner
            teks.contains("gantungan") -> R.drawable.produk_gantungan
            teks.contains("kemasan") -> R.drawable.produk_kemasan
            teks.contains("stempel") -> R.drawable.produk_stempel
            teks.contains("merch") -> R.drawable.produk_merchandise
            else -> R.drawable.produk_banner
        }
    }
}
