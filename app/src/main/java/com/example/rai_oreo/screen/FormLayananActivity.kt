/**
 * Penerapan praktikum: Modul 2 (AutoCompleteTextView), Modul 3 (Intent data layanan).
 */
package com.example.rai_oreo.screen

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.R
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityFormLayananBinding

class FormLayananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormLayananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLayananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.etKategori.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.kategori_layanan))
        )

        val serviceId = intent.getIntExtra("serviceId", 0)
        val existing = if (serviceId != 0) SimperRepository.serviceById(serviceId) else null

        if (existing != null) {
            binding.toolbar.title = "Ubah Layanan"
            binding.etNama.setText(existing.nama)
            binding.etKategori.setText(existing.kategori)
            binding.etHarga.setText(existing.hargaMulai.toString())
            binding.etSatuan.setText(existing.satuan)
            binding.etDeskripsi.setText(existing.deskripsi)
        }

        binding.btnSimpan.setOnClickListener {
            val nama = binding.etNama.text?.toString().orEmpty().trim()
            val kategori = binding.etKategori.text?.toString().orEmpty().ifBlank { "Lainnya" }
            val harga = binding.etHarga.text?.toString().orEmpty().toIntOrNull() ?: 0
            val satuan = binding.etSatuan.text?.toString().orEmpty().ifBlank { "pcs" }
            val deskripsi = binding.etDeskripsi.text?.toString().orEmpty()

            binding.tilNama.error = null
            binding.tilHarga.error = null

            if (nama.isEmpty()) {
                binding.tilNama.error = "Nama layanan wajib diisi"
                return@setOnClickListener
            }
            if (harga <= 0) {
                binding.tilHarga.error = "Harga harus lebih dari 0"
                return@setOnClickListener
            }

            if (existing == null) {
                SimperRepository.addService(nama, kategori, harga, satuan, deskripsi)
                Toast.makeText(this, "Layanan ditambahkan", Toast.LENGTH_SHORT).show()
            } else {
                SimperRepository.updateService(
                    existing.copy(
                        nama = nama,
                        kategori = kategori,
                        hargaMulai = harga,
                        satuan = satuan,
                        deskripsi = deskripsi,
                    )
                )
                Toast.makeText(this, "Layanan diperbarui", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
