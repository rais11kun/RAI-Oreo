/**
 * Penerapan praktikum: Modul 3 (Intent data), Modul 2 (komponen), pemindaian QR kamera.
 */
package com.example.rai_oreo.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.BarcodeResult
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityScanQrBinding

class ScanQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrBinding
    private var nomorDitemukan: String? = null
    private var izinSiap = false

    private val mintaKamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) mulaiScan() else binding.tvHasil.text = "Izin kamera diperlukan untuk memindai kode QR."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnLanjut.setOnClickListener {
            val nomor = nomorDitemukan ?: return@setOnClickListener
            startActivity(
                Intent(this, DetailPesananActivity::class.java)
                    .putExtra(DetailPesananActivity.EXTRA_NOMOR, nomor)
            )
        }

        val izin = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (izin == PackageManager.PERMISSION_GRANTED) mulaiScan() else mintaKamera.launch(Manifest.permission.CAMERA)
    }

    private fun mulaiScan() {
        izinSiap = true
        binding.barcodeView.resume()
        binding.barcodeView.decodeSingle { hasil: BarcodeResult ->
            tangani(hasil.text)
        }
    }

    private fun tangani(isi: String) {
        val order = SimperRepository.orders.firstOrNull { isi.contains(it.nomor.replace("#", "")) }

        if (order == null) {
            binding.tvHasil.text = "Kode tidak dikenali: $isi"
            binding.btnLanjut.isEnabled = false
            return
        }

        nomorDitemukan = order.nomor
        binding.tvHasil.text = "Pesanan ${order.nomor} · ${order.status.label}"
        binding.btnLanjut.isEnabled = true
        binding.btnLanjut.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (izinSiap) binding.barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeView.pause()
    }
}
