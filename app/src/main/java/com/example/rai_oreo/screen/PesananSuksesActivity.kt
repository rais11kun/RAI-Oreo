/**
 * Penerapan praktikum: Modul 2 (ConstraintLayout: guideline + chain), Modul 3 (Intent data nomor pesanan).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.MainActivity
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityPesananSuksesBinding

class PesananSuksesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesananSuksesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananSuksesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nomor = intent.getStringExtra("nomor").orEmpty()
        val order = SimperRepository.orderByNumber(nomor)

        binding.tvNomor.text = nomor
        binding.tvLayanan.text = order?.serviceNama ?: "-"
        binding.tvTotal.text = (order?.total ?: 0).toRupiah()
        binding.tvEstimasi.text = SimperRepository.serviceById(order?.serviceId ?: 0)?.estimasi ?: "1 - 2 hari"

        binding.btnLihat.setOnClickListener {
            startActivity(
                Intent(this, DetailPesananActivity::class.java)
                    .putExtra(DetailPesananActivity.EXTRA_NOMOR, nomor)
            )
            finish()
        }

        binding.btnBeranda.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
            finish()
        }
    }
}
