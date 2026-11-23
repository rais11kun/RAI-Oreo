package com.example.rai_oreo.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rai_oreo.R

/**
 * Single in-memory data source for the demo build. Every screen reads from the
 * same observable state, so an action such as confirming an order immediately
 * appears in Home, Riwayat and the staff queue.
 */
object SimperRepository {

    var currentUser by mutableStateOf<User?>(null)
        private set
    var rememberMe by mutableStateOf(false)
    var biometricEnabled by mutableStateOf(true)
    var promoSeen by mutableStateOf(false)

    val services = mutableStateListOf<Service>().apply { addAll(mockServices()) }
    val orders = mutableStateListOf<Order>().apply { addAll(mockOrders()) }
    val notifications = mutableStateListOf<AppNotification>().apply { addAll(mockNotifications()) }
    val addresses = mutableStateListOf<Address>().apply { addAll(mockAddresses()) }
    val favorites = mutableStateListOf<Int>()

    val activeOrders: List<Order>
        get() = orders.filter { it.status.isActive }

    val unreadNotifications: Int
        get() = notifications.count { !it.dibaca }

    fun login(email: String, password: String): Result<User> {
        val user = demoUsers.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
            ?: return Result.failure(IllegalArgumentException("Email belum terdaftar"))
        if (password.length < 4) return Result.failure(IllegalArgumentException("Password minimal 4 karakter"))
        currentUser = user
        return Result.success(user)
    }

    fun masukSebagai(email: String): User? {
        val user = demoUsers.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) } ?: return null
        currentUser = user
        return user
    }

    fun loginWithBiometric(): Result<User> {
        val user = currentUser ?: demoUsers.first()
        currentUser = user
        return Result.success(user)
    }

    fun register(nama: String, email: String, telepon: String, password: String): Result<User> {
        if (nama.isBlank() || email.isBlank() || telepon.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Semua data wajib diisi"))
        }
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Format email tidak valid"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password minimal 6 karakter"))
        val user = User(
            id = demoUsers.size + 1,
            nama = nama.trim(),
            email = email.trim(),
            telepon = telepon.trim(),
            role = Role.PELANGGAN,
            alamat = "Pekanbaru",
        )
        currentUser = user
        return Result.success(user)
    }

    fun logout() {
        currentUser = null
    }

    fun updateProfile(nama: String, email: String, telepon: String): User? {
        val user = currentUser ?: return null
        val updated = user.copy(nama = nama, email = email, telepon = telepon)
        currentUser = updated
        return updated
    }

    fun toggleFavorite(serviceId: Int) {
        if (favorites.contains(serviceId)) favorites.remove(serviceId) else favorites.add(serviceId)
    }

    fun serviceById(id: Int): Service? = services.firstOrNull { it.id == id }

    fun orderByNumber(nomor: String): Order? = orders.firstOrNull { it.nomor == nomor }

    fun ordersOf(user: User?): List<Order> {
        if (user == null) return orders.toList()
        if (user.role == Role.PELANGGAN) return orders.filter { it.pelanggan == user.nama }
        return orders.toList()
    }

    fun createOrder(
        service: Service,
        ukuran: String,
        jumlah: Int,
        bahan: String,
        finishing: String,
        catatan: String,
        desainNama: String,
        desainUkuran: String,
        total: Int,
        nomorOverride: String? = null,
    ): Order {
        val next = 123 + orders.size
        // Modul 12: bila pesanan sudah dibuat di Supabase, pakai nomor dari server.
        val nomor = nomorOverride ?: "#ORD%05d".format(next)
        val order = Order(
            nomor = nomor,
            serviceId = service.id,
            serviceNama = service.nama,
            gambar = service.gambar,
            ukuran = ukuran,
            jumlah = jumlah,
            bahan = bahan,
            finishing = finishing,
            catatan = catatan,
            total = total,
            tanggal = "12 Sep 2026",
            status = OrderStatus.BARU,
            pelanggan = currentUser?.nama ?: "Rais Wirdiyan",
            desainNama = desainNama,
            desainUkuran = desainUkuran,
            timeline = listOf(
                OrderEvent("Pesanan Masuk", "12 Sep 2026 • 10:30", "Pesanan berhasil dibuat", true),
                OrderEvent("Sedang Design", "-", "Menunggu tim desain", false),
                OrderEvent("Dalam Produksi", "-", "Menunggu proses produksi", false),
                OrderEvent("Pesanan Selesai", "-", "Pesanan siap diambil", false),
            ),
        )
        orders.add(0, order)
        notifications.add(
            0,
            AppNotification(
                id = notifications.size + 1,
                judul = "Pesanan $nomor dibuat",
                isi = "${service.nama} • $jumlah pcs berhasil masuk antrian",
                waktu = "Baru saja",
                tipe = NotificationType.PESANAN,
            ),
        )
        return order
    }

    fun updateStatus(nomor: String, status: OrderStatus) {
        val index = orders.indexOfFirst { it.nomor == nomor }
        if (index < 0) return
        val order = orders[index]
        val timeline = order.timeline.mapIndexed { i, event ->
            when {
                event.done -> event
                i <= status.step -> event.copy(done = true, waktu = "12 Sep 2026 • 11:00")
                else -> event
            }
        }
        orders[index] = order.copy(status = status, timeline = timeline)
        notifications.add(
            0,
            AppNotification(
                id = notifications.size + 1,
                judul = "Status $nomor diperbarui",
                isi = "Pesanan sekarang berstatus ${status.label}",
                waktu = "Baru saja",
                tipe = NotificationType.PESANAN,
            ),
        )
    }

    fun markNotificationsRead() {
        for (i in notifications.indices) notifications[i] = notifications[i].copy(dibaca = true)
    }

    fun addAddress(label: String, nama: String, telepon: String, detail: String): Address {
        val address = Address(
            id = addresses.size + 1,
            label = label,
            nama = nama,
            telepon = telepon,
            detail = detail,
            utama = addresses.isEmpty(),
        )
        addresses.add(address)
        return address
    }

    fun setPrimaryAddress(id: Int) {
        for (i in addresses.indices) addresses[i] = addresses[i].copy(utama = addresses[i].id == id)
    }

    fun removeAddress(id: Int) {
        addresses.removeAll { it.id == id }
    }

    fun addService(nama: String, kategori: String, harga: Int, satuan: String, deskripsi: String): Service {
        val service = Service(
            id = (services.maxOfOrNull { it.id } ?: 0) + 1,
            nama = nama,
            kategori = kategori,
            hargaMulai = harga,
            satuan = satuan,
            deskripsi = deskripsi,
            rating = 5.0,
            jumlahPesanan = 0,
            estimasi = "1 - 2 hari",
            bahan = listOf("Flexi 440 gr", "Vinyl", "Backlite"),
            finishing = listOf("Mata Ayam", "Tanpa Finishing"),
            gambar = R.drawable.produk_banner,
            contoh = listOf(R.drawable.contoh_banner_1, R.drawable.contoh_banner_2),
        )
        services.add(service)
        return service
    }

    fun updateService(updated: Service) {
        val index = services.indexOfFirst { it.id == updated.id }
        if (index >= 0) services[index] = updated
    }

    fun toggleServiceActive(id: Int) {
        val index = services.indexOfFirst { it.id == id }
        if (index >= 0) services[index] = services[index].copy(aktif = !services[index].aktif)
    }

    fun revenueOf(list: List<Order>): Int = list.filter { it.status == OrderStatus.SELESAI }.sumOf { it.total }

    // -------------------------------------------------- sinkronisasi Supabase
    // Modul 12: data REST dimasukkan ke cache lokal agar seluruh layar tetap
    // memakai SimperRepository tanpa perlu diubah.

    fun gantiLayanan(daftar: List<Service>) {
        services.clear()
        services.addAll(daftar)
    }

    fun gantiPesanan(daftar: List<Order>) {
        orders.clear()
        orders.addAll(daftar)
    }

    fun hapusPesanan(nomor: String) {
        orders.removeAll { it.nomor == nomor }
    }

    fun hapusLayanan(id: Int) {
        services.removeAll { it.id == id }
    }
}

val demoUsers = listOf(
    User(
        id = 1,
        nama = "Rais Wirdiyan",
        email = "rais@simpernra.id",
        telepon = "0812 3456 7890",
        role = Role.PELANGGAN,
        alamat = "Jl. Sekolah Ujung No. 106, Pekanbaru",
        bergabung = "Sep 2026",
    ),
    User(
        id = 2,
        nama = "Anugrah Ghani",
        email = "karyawan@simpernra.id",
        telepon = "0813 4455 6677",
        role = Role.KARYAWAN,
        alamat = "Pekanbaru",
    ),
    User(
        id = 3,
        nama = "Cinta Dwi Arianti",
        email = "pemilik@simpernra.id",
        telepon = "0811 2233 4455",
        role = Role.PEMILIK,
        alamat = "Pekanbaru",
    ),
)

private fun mockServices(): List<Service> {
    val bannerContoh = listOf(
        R.drawable.contoh_banner_1,
        R.drawable.contoh_banner_2,
        R.drawable.contoh_banner_3,
        R.drawable.contoh_banner_4,
    )
    return listOf(
        Service(
            id = 1,
            nama = "Banner",
            kategori = "Banner",
            hargaMulai = 20000,
            satuan = "m²",
            deskripsi = "Media promosi yang cocok untuk kebutuhan indoor dan outdoor dengan hasil cetak berkualitas tinggi dan tahan lama.",
            rating = 4.8,
            jumlahPesanan = 120,
            estimasi = "1 - 2 hari",
            bahan = listOf("Flexi 440 gr", "Vinyl", "Backlite"),
            finishing = listOf("Mata Ayam", "Tanpa Finishing", "Jahit Keliling"),
            gambar = R.drawable.produk_banner,
            contoh = bannerContoh,
            populer = true,
        ),
        Service(
            id = 2,
            nama = "Spanduk",
            kategori = "Spanduk",
            hargaMulai = 15000,
            satuan = "m²",
            deskripsi = "Spanduk flexi untuk promosi toko, event, dan kegiatan organisasi dengan warna tajam.",
            rating = 4.7,
            jumlahPesanan = 98,
            estimasi = "1 - 2 hari",
            bahan = listOf("Flexi 280 gr", "Flexi 340 gr", "Flexi 440 gr"),
            finishing = listOf("Mata Ayam", "Tanpa Finishing", "Jahit Keliling"),
            gambar = R.drawable.produk_spanduk,
            contoh = listOf(bannerContoh[1], bannerContoh[2], bannerContoh[0], bannerContoh[3]),
            populer = true,
        ),
        Service(
            id = 3,
            nama = "Stiker",
            kategori = "Stiker",
            hargaMulai = 5000,
            satuan = "pcs",
            deskripsi = "Stiker vinyl, chromo, dan transparan untuk label produk, packaging, dan branding.",
            rating = 4.9,
            jumlahPesanan = 210,
            estimasi = "1 hari",
            bahan = listOf("Vinyl", "Chromo", "Transparan"),
            finishing = listOf("Cutting", "Tanpa Cutting", "Laminasi Doff"),
            gambar = R.drawable.produk_stiker,
            contoh = listOf(bannerContoh[3], bannerContoh[0], bannerContoh[2], bannerContoh[1]),
            populer = true,
        ),
        Service(
            id = 4,
            nama = "Undangan",
            kategori = "Lainnya",
            hargaMulai = 1000,
            satuan = "pcs",
            deskripsi = "Undangan pernikahan, khitanan, dan acara resmi dengan pilihan kertas premium.",
            rating = 4.8,
            jumlahPesanan = 175,
            estimasi = "2 - 3 hari",
            bahan = listOf("Art Paper 250 gr", "Concorde", "Ivory"),
            finishing = listOf("Hot Print", "Tanpa Finishing", "Emboss"),
            gambar = R.drawable.produk_undangan,
            contoh = listOf(bannerContoh[3], bannerContoh[1], bannerContoh[2], bannerContoh[0]),
            populer = true,
        ),
        Service(
            id = 5,
            nama = "ID Card",
            kategori = "ID Card",
            hargaMulai = 10000,
            satuan = "pcs",
            deskripsi = "ID card karyawan, pelajar, dan member lengkap dengan tali lanyard.",
            rating = 4.7,
            jumlahPesanan = 88,
            estimasi = "1 - 2 hari",
            bahan = listOf("PVC 0.8 mm", "PVC 1 mm", "Art Carton"),
            finishing = listOf("Laminasi Glossy", "Tanpa Finishing", "Lubang Lanyard"),
            gambar = R.drawable.produk_idcard,
            contoh = listOf(bannerContoh[0], bannerContoh[2], bannerContoh[3], bannerContoh[1]),
        ),
        Service(
            id = 6,
            nama = "Nota",
            kategori = "Lainnya",
            hargaMulai = 2000,
            satuan = "pcs",
            deskripsi = "Nota penjualan, invoice, dan form custom dengan nomor urut otomatis.",
            rating = 4.6,
            jumlahPesanan = 64,
            estimasi = "2 hari",
            bahan = listOf("NCR 2 ply", "NCR 3 ply", "HVS 70 gr"),
            finishing = listOf("Lem", "Tanpa Finishing", "Nomor Urut"),
            gambar = R.drawable.produk_nota,
            contoh = listOf(bannerContoh[2], bannerContoh[3], bannerContoh[1], bannerContoh[0]),
        ),
        Service(
            id = 7,
            nama = "Kemasan Custom",
            kategori = "Lainnya",
            hargaMulai = 5000,
            satuan = "pcs",
            deskripsi = "Box makanan, paper bag, dan kemasan produk dengan cetak full colour.",
            rating = 4.8,
            jumlahPesanan = 142,
            estimasi = "3 - 4 hari",
            bahan = listOf("Ivory 250 gr", "Kraft", "Duplex"),
            finishing = listOf("Laminasi Doff", "Tanpa Finishing", "Spot UV"),
            gambar = R.drawable.produk_kemasan,
            contoh = listOf(bannerContoh[3], bannerContoh[0], bannerContoh[1], bannerContoh[2]),
        ),
        Service(
            id = 8,
            nama = "Merchandise",
            kategori = "Lainnya",
            hargaMulai = 10000,
            satuan = "pcs",
            deskripsi = "Kaos, mug, topi, dan gantungan kunci dengan sablon atau DTF sesuai desain Anda.",
            rating = 4.7,
            jumlahPesanan = 76,
            estimasi = "2 - 3 hari",
            bahan = listOf("Cotton Combed 30s", "Cotton Combed 24s", "Polyester"),
            finishing = listOf("Sablon", "DTF", "Bordir"),
            gambar = R.drawable.produk_merchandise,
            contoh = listOf(bannerContoh[1], bannerContoh[2], bannerContoh[0], bannerContoh[3]),
        ),
        Service(
            id = 9,
            nama = "Roll Banner",
            kategori = "Banner",
            hargaMulai = 85000,
            satuan = "pcs",
            deskripsi = "Roll up banner dengan stand aluminium praktis untuk pameran dan event.",
            rating = 4.8,
            jumlahPesanan = 52,
            estimasi = "2 hari",
            bahan = listOf("Flexi China", "Flexi Korea", "Backlite"),
            finishing = listOf("Stand Aluminium", "Tanpa Stand"),
            gambar = R.drawable.produk_rollbanner,
            contoh = listOf(bannerContoh[0], bannerContoh[3], bannerContoh[2], bannerContoh[1]),
        ),
        Service(
            id = 10,
            nama = "Neon Box",
            kategori = "Lainnya",
            hargaMulai = 350000,
            satuan = "m²",
            deskripsi = "Neon box acrylic dengan lampu LED untuk papan nama toko yang menyala.",
            rating = 4.9,
            jumlahPesanan = 31,
            estimasi = "4 - 6 hari",
            bahan = listOf("Acrylic 3 mm", "Acrylic 5 mm", "Galvalum"),
            finishing = listOf("LED Module", "Tanpa Lampu", "Frame Aluminium"),
            gambar = R.drawable.produk_neonbox,
            contoh = listOf(bannerContoh[2], bannerContoh[0], bannerContoh[1], bannerContoh[3]),
        ),
        Service(
            id = 11,
            nama = "Stempel",
            kategori = "Lainnya",
            hargaMulai = 45000,
            satuan = "pcs",
            deskripsi = "Stempel flash otomatis dan manual untuk keperluan administrasi usaha.",
            rating = 4.6,
            jumlahPesanan = 58,
            estimasi = "1 hari",
            bahan = listOf("Flash Stamp", "Wooden", "Self Inking"),
            finishing = listOf("Tinta Hitam", "Tinta Biru", "Tinta Merah"),
            gambar = R.drawable.produk_stempel,
            contoh = listOf(bannerContoh[3], bannerContoh[1], bannerContoh[0], bannerContoh[2]),
        ),
        Service(
            id = 12,
            nama = "Gantungan Kunci",
            kategori = "Lainnya",
            hargaMulai = 7000,
            satuan = "pcs",
            deskripsi = "Gantungan kunci akrilik dan resin cetak foto untuk souvenir acara.",
            rating = 4.7,
            jumlahPesanan = 96,
            estimasi = "2 - 3 hari",
            bahan = listOf("Akrilik 3 mm", "Resin", "Karet"),
            finishing = listOf("Rantai", "Tanpa Rantai", "Ring Besi"),
            gambar = R.drawable.produk_gantungan,
            contoh = listOf(bannerContoh[0], bannerContoh[2], bannerContoh[3], bannerContoh[1]),
        ),
    )
}

private fun mockOrders(): List<Order> = listOf(
    Order(
        nomor = "#ORD00123",
        serviceId = 1,
        serviceNama = "Banner",
        gambar = R.drawable.produk_banner,
        ukuran = "2 x 1 m",
        jumlah = 1,
        bahan = "Flexi 440 gr",
        finishing = "Mata Ayam",
        catatan = "Tolong gunakan warna sesuai desain yang dikirim ya.",
        total = 40000,
        tanggal = "12 Sep 2026",
        status = OrderStatus.DIPROSES,
        pelanggan = "Rais Wirdiyan",
        desainNama = "desain_banner.pdf",
        desainUkuran = "2,4 MB",
        timeline = listOf(
            OrderEvent("Pesanan Masuk", "12 Sep 2026 • 10:30", "Pesanan berhasil dibuat", true),
            OrderEvent("Sedang Design", "12 Sep 2026 • 14:20", "Tim design sedang memproses desain", true),
            OrderEvent("Dalam Produksi", "13 Sep 2026 • -", "Menunggu proses produksi", false),
            OrderEvent("Pesanan Selesai", "-", "-", false),
        ),
    ),
    Order(
        nomor = "#ORD00122",
        serviceId = 3,
        serviceNama = "Stiker A3",
        gambar = R.drawable.produk_stiker,
        ukuran = "A3",
        jumlah = 5,
        bahan = "Vinyl",
        finishing = "Cutting",
        catatan = "",
        total = 25000,
        tanggal = "10 Sep 2026",
        status = OrderStatus.SELESAI,
        pelanggan = "Rais Wirdiyan",
        desainNama = "stiker_a3.png",
        desainUkuran = "1,1 MB",
        timeline = listOf(
            OrderEvent("Pesanan Masuk", "10 Sep 2026 • 09:10", "Pesanan berhasil dibuat", true),
            OrderEvent("Sedang Design", "10 Sep 2026 • 10:00", "Desain disetujui", true),
            OrderEvent("Dalam Produksi", "10 Sep 2026 • 13:00", "Produksi selesai", true),
            OrderEvent("Pesanan Selesai", "10 Sep 2026 • 16:40", "Pesanan siap diambil", true),
        ),
    ),
    Order(
        nomor = "#ORD00121",
        serviceId = 4,
        serviceNama = "Undangan 100 pcs",
        gambar = R.drawable.produk_undangan,
        ukuran = "10 x 20 cm",
        jumlah = 100,
        bahan = "Art Paper 250 gr",
        finishing = "Hot Print",
        catatan = "Motif bunga warna dusty pink.",
        total = 150000,
        tanggal = "5 Sep 2026",
        status = OrderStatus.SELESAI,
        pelanggan = "Rais Wirdiyan",
        desainNama = "undangan_final.pdf",
        desainUkuran = "6,8 MB",
        timeline = listOf(
            OrderEvent("Pesanan Masuk", "5 Sep 2026 • 08:30", "Pesanan berhasil dibuat", true),
            OrderEvent("Sedang Design", "5 Sep 2026 • 11:00", "Desain disetujui", true),
            OrderEvent("Dalam Produksi", "6 Sep 2026 • 09:00", "Produksi selesai", true),
            OrderEvent("Pesanan Selesai", "7 Sep 2026 • 15:20", "Pesanan siap diambil", true),
        ),
    ),
    Order(
        nomor = "#ORD00120",
        serviceId = 5,
        serviceNama = "ID Card 10 pcs",
        gambar = R.drawable.produk_idcard,
        ukuran = "8,5 x 5,4 cm",
        jumlah = 10,
        bahan = "PVC 0.8 mm",
        finishing = "Laminasi Glossy",
        catatan = "",
        total = 100000,
        tanggal = "1 Sep 2026",
        status = OrderStatus.DIBATALKAN,
        pelanggan = "Rais Wirdiyan",
        desainNama = "idcard.pdf",
        desainUkuran = "920 KB",
        timeline = listOf(
            OrderEvent("Pesanan Masuk", "1 Sep 2026 • 13:00", "Pesanan berhasil dibuat", true),
            OrderEvent("Dibatalkan", "1 Sep 2026 • 17:30", "Dibatalkan oleh pelanggan", true),
        ),
    ),
    Order(
        nomor = "#ORD00119",
        serviceId = 2,
        serviceNama = "Spanduk 3x1 m",
        gambar = R.drawable.produk_spanduk,
        ukuran = "3 x 1 m",
        jumlah = 1,
        bahan = "Flexi 340 gr",
        finishing = "Mata Ayam",
        catatan = "",
        total = 75000,
        tanggal = "28 Agu 2026",
        status = OrderStatus.SELESAI,
        pelanggan = "Rais Wirdiyan",
        desainNama = "spanduk.pdf",
        desainUkuran = "1,4 MB",
        timeline = listOf(
            OrderEvent("Pesanan Masuk", "28 Agu 2026 • 10:00", "Pesanan berhasil dibuat", true),
            OrderEvent("Sedang Design", "28 Agu 2026 • 13:00", "Desain disetujui", true),
            OrderEvent("Dalam Produksi", "29 Agu 2026 • 09:00", "Produksi selesai", true),
            OrderEvent("Pesanan Selesai", "29 Agu 2026 • 16:00", "Pesanan siap diambil", true),
        ),
    ),
    Order(
        nomor = "#ORD00118",
        serviceId = 8,
        serviceNama = "Merchandise Kaos",
        gambar = R.drawable.produk_merchandise,
        ukuran = "L",
        jumlah = 6,
        bahan = "Cotton Combed 30s",
        finishing = "Sablon",
        catatan = "Sablon depan kiri dada.",
        total = 60000,
        tanggal = "20 Agu 2026",
        status = OrderStatus.DESAIN,
        pelanggan = "Rais Wirdiyan",
        desainNama = "kaos.pdf",
        desainUkuran = "3,2 MB",
        timeline = listOf(
            OrderEvent("Pesanan Masuk", "20 Agu 2026 • 09:15", "Pesanan berhasil dibuat", true),
            OrderEvent("Sedang Design", "20 Agu 2026 • 15:00", "Tim design sedang memproses desain", true),
            OrderEvent("Dalam Produksi", "-", "Menunggu proses produksi", false),
            OrderEvent("Pesanan Selesai", "-", "Pesanan siap diambil", false),
        ),
    ),
)

private fun mockNotifications(): List<AppNotification> = listOf(
    AppNotification(
        id = 1,
        judul = "Pesanan #ORD00123 sedang diproses",
        isi = "Tim produksi mulai mengerjakan banner 2 x 1 m Anda.",
        waktu = "5 menit lalu",
        tipe = NotificationType.PESANAN,
    ),
    AppNotification(
        id = 2,
        judul = "Promo diskon 30%",
        isi = "Berlaku untuk semua layanan cetak sampai akhir bulan.",
        waktu = "2 jam lalu",
        tipe = NotificationType.PROMO,
    ),
    AppNotification(
        id = 3,
        judul = "Pesanan #ORD00122 selesai",
        isi = "Pesanan stiker A3 sudah siap diambil di toko.",
        waktu = "Kemarin",
        dibaca = true,
        tipe = NotificationType.PESANAN,
    ),
    AppNotification(
        id = 4,
        judul = "Selamat datang di SIMPER-NRA Mobile",
        isi = "Cek layanan percetakan dan pantau pesanan Anda dari sini.",
        waktu = "2 hari lalu",
        dibaca = true,
        tipe = NotificationType.SISTEM,
    ),
)

private fun mockAddresses(): List<Address> = listOf(
    Address(
        id = 1,
        label = "Rumah",
        nama = "Rais Wirdiyan",
        telepon = "0812 3456 7890",
        detail = "Jl. Sekolah Ujung No. 106, Pekanbaru, Riau 28265",
        utama = true,
    ),
    Address(
        id = 2,
        label = "Kantor",
        nama = "Rais Wirdiyan",
        telepon = "0812 3456 7890",
        detail = "Jl. Umbansari No. 1, Rumbai, Pekanbaru",
    ),
)
