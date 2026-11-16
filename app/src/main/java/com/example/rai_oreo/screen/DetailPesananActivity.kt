/**
 * Penerapan praktikum: Modul 3 (Intent data), Modul 4 (MaterialCardView), Modul 5 (Option Menu).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.TimelineAdapter
import com.example.rai_oreo.data.OrderStatus
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityDetailPesananBinding
import com.example.rai_oreo.util.Badge

class DetailPesananActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOMOR = "nomor"
    }

    private lateinit var binding: ActivityDetailPesananBinding
    private var nomor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nomor = intent.getStringExtra(EXTRA_NOMOR).orEmpty()
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_share -> {
                    val kirim = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Pesanan $nomor di SIMPER-NRA")
                    }
                    startActivity(Intent.createChooser(kirim, "Bagikan pesanan"))
                    true
                }
                R.id.menu_help -> {
                    startActivity(Intent(this, BantuanActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tampilkan()
    }

    private fun tampilkan() {
        val order = SimperRepository.orderByNumber(nomor)
        if (order == null) {
            finish()
            return
        }

        binding.tvNomor.text = order.nomor
        binding.tvTanggal.text = "Dibuat ${order.tanggal}"
        Badge.status(binding.tvStatus, order.status)
        Badge.progres(
            steps = listOf(binding.step1, binding.step2, binding.step3, binding.step4),
            lines = listOf(binding.line1, binding.line2, binding.line3),
            langkah = order.status.step.coerceAtLeast(0),
        )

        binding.tvDetail.text = buildString {
            appendLine("Layanan: ${order.serviceNama}")
            appendLine("Ukuran: ${order.ukuran}")
            appendLine("Jumlah: ${order.jumlah} pcs")
            appendLine("Bahan: ${order.bahan}")
            append("Finishing: ${order.finishing}")
        }
        binding.tvCatatan.text = "Catatan: ${order.catatan.ifBlank { "-" }}"

        binding.tvDesainNama.text = order.desainNama.ifBlank { "belum ada file" }
        binding.tvDesainUkuran.text = order.desainUkuran.ifBlank { "-" }
        binding.cardDesain.visibility = View.VISIBLE

        binding.tvTotal.text = order.total.toRupiah()

        binding.rvTimeline.layoutManager = LinearLayoutManager(this)
        binding.rvTimeline.adapter = TimelineAdapter(order.timeline)

        val aktif = order.status.isActive
        binding.btnBatal.visibility = if (aktif) View.VISIBLE else View.GONE
        binding.btnBatal.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Batalkan pesanan?")
                .setMessage("Pesanan yang dibatalkan tidak dapat dikembalikan.")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Batalkan") { _, _ ->
                    SimperRepository.updateStatus(order.nomor, OrderStatus.DIBATALKAN)
                    tampilkan()
                }
                .show()
        }
        binding.btnPesanLagi.setOnClickListener {
            startActivity(
                Intent(this, FormPemesananActivity::class.java)
                    .putExtra("serviceId", order.serviceId)
            )
        }
    }
}
