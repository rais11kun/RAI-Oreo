package com.example.rai_oreo

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class RiwayatFragment : Fragment(R.layout.fragment_riwayat) {

    private val qrScanner = registerForActivityResult(ScanContract()) { result ->
        val scanned = result.contents
        if (scanned.isNullOrBlank()) {
            // Pengguna membatalkan scan atau tidak ada QR terbaca.
            return@registerForActivityResult
        }

        // Validasi format nomor pesanan, contoh: NR-20260721-746.
        if (!scanned.matches(Regex("^NR-\\d{8}-\\d{3,}$"))) {
            AlertDialog.Builder(requireContext())
                .setTitle("QR tidak valid")
                .setMessage("QR tidak berisi nomor pesanan SIMPER-NRA yang valid.")
                .setPositiveButton("OK", null).show()
            return@registerForActivityResult
        }

        // Data status dummy lokal sementara; ganti dengan request API saat backend tersedia.
        val localOrders = mapOf(
            "NR-20260721-746" to "Sedang diproses",
            "NR-20260721-747" to "Siap diambil"
        )
        val status = localOrders[scanned] ?: "Nomor pesanan valid, tetapi belum ditemukan pada data lokal."
        AlertDialog.Builder(requireContext())
            .setTitle("Status Pesanan")
            .setMessage("Nomor: $scanned\nStatus: $status")
            .setPositiveButton("OK", null).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnScanOrderQr).setOnClickListener {
            val options = ScanOptions().apply {
                setPrompt("Arahkan kamera ke QR nomor pesanan")
                setBeepEnabled(false)
                setOrientationLocked(false)
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            }
            // ScanContract menangani permintaan izin kamera runtime dan hasil izin ditolak.
            qrScanner.launch(options)
        }
    }
}
