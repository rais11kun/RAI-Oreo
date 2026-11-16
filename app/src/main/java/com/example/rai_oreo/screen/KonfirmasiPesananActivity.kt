/**
 * Penerapan praktikum: Modul 3 (Dialog & PopUp), Modul 3 (Intent kirim data).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.rai_oreo.data.OrderDraft
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityKonfirmasiPesananBinding

class KonfirmasiPesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonfirmasiPesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonfirmasiPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val service = SimperRepository.serviceById(OrderDraft.serviceId)
        if (service == null) {
            finish()
            return
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.ivLayanan.setImageResource(service.gambar)
        binding.tvLayanan.text = service.nama
        binding.tvRingkas.text = "${OrderDraft.ukuran} · ${OrderDraft.jumlah} pcs"
        binding.tvDetailBaris.text = buildString {
            appendLine("Bahan: ${OrderDraft.bahan.ifBlank { "-" }}")
            appendLine("Finishing: ${OrderDraft.finishing.ifBlank { "-" }}")
            append("Jumlah: ${OrderDraft.jumlah} pcs")
        }
        binding.tvCatatan.text = "Catatan: ${OrderDraft.catatan.ifBlank { "-" }}"
        binding.tvSubtotal.text = OrderDraft.total.toRupiah()
        binding.tvBiaya.text = "Rp 0"
        binding.tvTotal.text = OrderDraft.total.toRupiah()

        val alamat = SimperRepository.addresses.firstOrNull { it.utama } ?: SimperRepository.addresses.firstOrNull()
        binding.tvAlamat.text = alamat?.detail ?: "Ambil di toko New Reklame Advertising"
        binding.tvGantiAlamat.setOnClickListener {
            startActivity(Intent(this, AlamatActivity::class.java))
        }

        binding.btnBuat.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Buat pesanan sekarang?")
                .setMessage("Pesanan akan masuk antrian produksi dan status dapat dipantau di tab Pesanan.")
                .setNegativeButton("Periksa lagi", null)
                .setPositiveButton("Buat Pesanan") { _, _ -> buatPesanan(service.id) }
                .show()
        }
    }

    private fun buatPesanan(serviceId: Int) {
        val service = SimperRepository.serviceById(serviceId) ?: return
        val order = SimperRepository.createOrder(
            service = service,
            ukuran = OrderDraft.ukuran,
            jumlah = OrderDraft.jumlah,
            bahan = OrderDraft.bahan,
            finishing = OrderDraft.finishing,
            catatan = OrderDraft.catatan,
            desainNama = OrderDraft.desainNama.ifBlank { "belum ada file" },
            desainUkuran = OrderDraft.desainUkuran.ifBlank { "-" },
            total = OrderDraft.total,
        )

        startActivity(
            Intent(this, PesananSuksesActivity::class.java)
                .putExtra("nomor", order.nomor)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        OrderDraft.reset()
        finish()
    }
}
