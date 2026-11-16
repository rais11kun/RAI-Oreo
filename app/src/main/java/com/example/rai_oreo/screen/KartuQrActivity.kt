/**
 * Penerapan praktikum: Modul 5 (Option Menu), kode QR dibuat sebagai bitmap PNG dari ZXing.
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.R
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityKartuQrBinding
import com.example.rai_oreo.util.Qr

class KartuQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKartuQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKartuQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = SimperRepository.currentUser
        val nama = user?.nama ?: "Pelanggan"
        val id = "NRA-%04d".format(user?.id ?: 1)
        val isiQr = "SIMPER-NRA|$id|${user?.email.orEmpty()}"

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_share -> {
                    bagikan(nama, id)
                    true
                }
                R.id.menu_help -> {
                    startActivity(Intent(this, BantuanActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.tvNama.text = nama
        binding.tvId.text = id
        binding.tvRole.text = user?.role?.label ?: "Pelanggan"
        binding.ivQr.setImageBitmap(Qr.bitmap(isiQr))
        binding.btnBagikan.setOnClickListener { bagikan(nama, id) }
    }

    private fun bagikan(nama: String, id: String) {
        val kirim = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Kartu pelanggan SIMPER-NRA\n$nama · $id")
        }
        startActivity(Intent.createChooser(kirim, "Bagikan kartu"))
    }
}
