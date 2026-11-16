/**
 * Penerapan praktikum: Modul 3 (Intent + MaterialCardView), Modul 5 (Option Menu pada Toolbar).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rai_oreo.R
import com.example.rai_oreo.data.OrderDraft
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityDetailLayananBinding
import com.example.rai_oreo.adapter.ContohAdapter

class DetailLayananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailLayananBinding
    private var serviceId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLayananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceId = intent.getIntExtra("serviceId", 1)
        val service = SimperRepository.serviceById(serviceId)
        if (service == null) {
            finish()
            return
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_share -> {
                    val kirim = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${service.nama} di SIMPER-NRA mulai ${service.hargaMulai.toRupiah()}")
                    }
                    startActivity(Intent.createChooser(kirim, "Bagikan layanan"))
                    true
                }
                R.id.menu_help -> {
                    startActivity(Intent(this, BantuanActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.ivGambar.setImageResource(service.gambar)
        binding.tvNama.text = service.nama
        binding.tvKategori.text = service.kategori
        binding.tvPopuler.visibility = if (service.populer) android.view.View.VISIBLE else android.view.View.GONE
        binding.tvRating.text = service.rating.toString()
        binding.tvJumlahPesanan.text = "(${service.jumlahPesanan}+ pesanan)"
        binding.tvDeskripsi.text = service.deskripsi
        binding.tvHarga.text = "${service.hargaMulai.toRupiah()} / ${service.satuan}"
        binding.tvEstimasi.text = service.estimasi
        binding.tvBahanUtama.text = service.bahan.firstOrNull().orEmpty()
        binding.tvBahanList.text = service.bahan.joinToString(" · ")
        binding.tvFinishingList.text = service.finishing.joinToString(" · ")

        binding.rvContoh.adapter = ContohAdapter(service.contoh)
        binding.tvLihatSemua.setOnClickListener {
            startActivity(Intent(this, BantuanActivity::class.java))
        }

        perbaruiFavorit()
        binding.ivFavorit.setOnClickListener {
            SimperRepository.toggleFavorite(service.id)
            perbaruiFavorit()
        }

        binding.btnPesan.setOnClickListener {
            OrderDraft.mulai(service)
            startActivity(
                Intent(this, FormPemesananActivity::class.java)
                    .putExtra("serviceId", service.id)
            )
        }
    }

    private fun perbaruiFavorit() {
        val favorit = SimperRepository.favorites.contains(serviceId)
        binding.ivFavorit.setImageResource(if (favorit) R.drawable.ic_heart else R.drawable.ic_heart_outline)
        binding.ivFavorit.imageTintList = ContextCompat.getColorStateList(
            this,
            if (favorit) R.color.red else R.color.primary,
        )
    }
}
