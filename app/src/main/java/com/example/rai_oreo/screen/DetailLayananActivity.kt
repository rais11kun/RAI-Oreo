/**
 * Penerapan praktikum: Modul 11 (galeri ViewPager2 + indikator titik), Modul 3 (Intent),
 * Modul 5 (Option Menu), Modul 4 (MaterialCardView spesifikasi).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.ContohAdapter
import com.example.rai_oreo.adapter.GaleriAdapter
import com.example.rai_oreo.data.OrderDraft
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityDetailLayananBinding

class DetailLayananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailLayananBinding
    private var serviceId = 1
    private var jumlahGaleri = 0
    private val titik = mutableListOf<View>()

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
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${service.nama} di SIMPER-NRA mulai ${service.hargaMulai.toRupiah()}"
                        )
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

        binding.tvNama.text = service.nama
        binding.tvKategori.text = service.kategori
        binding.tvPopuler.visibility = if (service.populer) View.VISIBLE else View.GONE
        binding.tvRating.text = service.rating.toString()
        binding.tvJumlahPesanan.text = "(${service.jumlahPesanan}+ pesanan)"
        binding.tvDeskripsi.text = service.deskripsi
        binding.tvHarga.text = service.hargaMulai.toRupiah()
        binding.tvSatuan.text = "/ ${service.satuan}"
        binding.tvEstimasi.text = service.estimasi
        binding.tvBahanUtama.text = service.bahan.joinToString(", ")
        binding.tvFinishingList.text = service.finishing.joinToString(" · ")
        binding.tvSpesifikasi.text = buildString {
            appendLine("Bahan: ${service.bahan.joinToString(", ")}")
            appendLine("Finishing: ${service.finishing.joinToString(", ")}")
            append("Pengerjaan: ${service.estimasi} setelah desain disetujui")
        }

        siapkanGaleri(listOf(service.gambar) + service.contoh)

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

    /** Galeri foto layanan memakai ViewPager2 + deretan titik indikator. */
    private fun siapkanGaleri(gambar: List<Int>) {
        jumlahGaleri = gambar.size
        binding.pagerGaleri.adapter = GaleriAdapter(gambar)

        binding.indikatorGaleri.removeAllViews()
        titik.clear()
        val ukuranAktif = (9 * resources.displayMetrics.density).toInt()
        val ukuranPasif = (7 * resources.displayMetrics.density).toInt()
        val jarak = (3 * resources.displayMetrics.density).toInt()

        repeat(jumlahGaleri) {
            val dot = View(this)
            val lp = LinearLayout.LayoutParams(ukuranPasif, ukuranPasif)
            lp.marginStart = jarak
            lp.marginEnd = jarak
            dot.layoutParams = lp
            dot.setBackgroundResource(R.drawable.bg_dot_inactive)
            binding.indikatorGaleri.addView(dot)
            titik.add(dot)
        }
        tandaiTitik(0, ukuranAktif, ukuranPasif)

        binding.pagerGaleri.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tandaiTitik(position, ukuranAktif, ukuranPasif)
                }
            }
        )
    }

    private fun tandaiTitik(aktif: Int, ukuranAktif: Int, ukuranPasif: Int) {
        titik.forEachIndexed { index, dot ->
            val lp = dot.layoutParams as LinearLayout.LayoutParams
            val ukuran = if (index == aktif) ukuranAktif else ukuranPasif
            lp.width = ukuran
            lp.height = ukuran
            dot.layoutParams = lp
            dot.setBackgroundResource(
                if (index == aktif) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
            )
        }
    }

    private fun perbaruiFavorit() {
        val favorit = SimperRepository.favorites.contains(serviceId)
        binding.iconFavorit.setImageResource(
            if (favorit) R.drawable.ic_heart else R.drawable.ic_heart_outline
        )
        binding.iconFavorit.imageTintList = ContextCompat.getColorStateList(
            this,
            if (favorit) R.color.red else R.color.muted,
        )
    }
}
