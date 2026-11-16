/**
 * Penerapan praktikum: Modul 5 (Toolbar + Option Menu), ProgressBar sebagai grafik sederhana.
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.R
import com.example.rai_oreo.data.OrderStatus
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityDashboardPemilikBinding

class DashboardPemilikActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardPemilikBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardPemilikBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.menu_share) {
                bagikan()
                true
            } else {
                false
            }
        }

        val orders = SimperRepository.orders
        val selesai = orders.filter { it.status == OrderStatus.SELESAI }
        val pendapatan = SimperRepository.revenueOf(orders)

        binding.tvPendapatan.text = pendapatan.toRupiah()
        binding.tvTotalPesanan.text = orders.size.toString()
        binding.tvPesananProses.text = orders.count { it.status.isActive }.toString()
        binding.tvPesananSelesai.text = selesai.size.toString()
        binding.tvPelanggan.text = orders.map { it.pelanggan }.distinct().size.toString()

        val perLayanan = selesai.groupBy { it.serviceNama.substringBefore(" ") }
            .mapValues { (_, daftar) -> daftar.sumOf { it.total } }
            .toList()
            .sortedByDescending { it.second }
            .take(3)

        val tertinggi = (perLayanan.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

        val nama = listOf(binding.tvNamaLayanan1, binding.tvNamaLayanan2, binding.tvNamaLayanan3)
        val nilai = listOf(binding.tvNilai1, binding.tvNilai2, binding.tvNilai3)
        val bar = listOf(binding.bar1, binding.bar2, binding.bar3)

        nama.forEachIndexed { index, teks ->
            val data = perLayanan.getOrNull(index)
            teks.text = data?.first ?: "-"
            nilai[index].text = (data?.second ?: 0).toRupiah()
            bar[index].progress = ((data?.second ?: 0) * 100) / tertinggi
        }

        binding.btnLaporan.setOnClickListener { startActivity(Intent(this, LaporanActivity::class.java)) }
        binding.btnAntrian.setOnClickListener { startActivity(Intent(this, AntrianActivity::class.java)) }
    }

    private fun bagikan() {
        val kirim = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Ringkasan SIMPER-NRA: ${binding.tvPendapatan.text} dari ${binding.tvTotalPesanan.text} pesanan.")
        }
        startActivity(Intent.createChooser(kirim, "Bagikan ringkasan"))
    }
}
