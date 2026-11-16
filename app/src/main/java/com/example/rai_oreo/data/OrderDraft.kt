package com.example.rai_oreo.data

/** Data sementara saat pelanggan mengisi form pemesanan. */
object OrderDraft {

    var serviceId: Int = 0
    var ukuran: String = ""
    var jumlah: Int = 1
    var bahan: String = ""
    var finishing: String = ""
    var catatan: String = ""
    var desainNama: String = ""
    var desainUkuran: String = ""
    var total: Int = 0
    var tanggalAmbil: String = ""
    var pengiriman: String = "Ambil di toko"

    fun mulai(service: Service) {
        serviceId = service.id
        ukuran = ""
        jumlah = 1
        bahan = service.bahan.firstOrNull().orEmpty()
        finishing = service.finishing.firstOrNull().orEmpty()
        catatan = ""
        desainNama = ""
        desainUkuran = ""
        total = service.hargaMulai
        tanggalAmbil = ""
        pengiriman = "Ambil di toko"
    }

    fun reset() {
        serviceId = 0
        ukuran = ""
        jumlah = 1
        bahan = ""
        finishing = ""
        catatan = ""
        desainNama = ""
        desainUkuran = ""
        total = 0
        tanggalAmbil = ""
        pengiriman = "Ambil di toko"
    }

    fun layanan(): Service? = SimperRepository.serviceById(serviceId)
}
