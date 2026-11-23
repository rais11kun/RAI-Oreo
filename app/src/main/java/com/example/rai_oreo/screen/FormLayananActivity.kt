
/*
 * Penerapan praktikum:
 * Modul 2  : AutoCompleteTextView
 * Modul 3  : Intent data layanan
 * Modul 12 : Create & Update layanan ke Supabase
 */

package com.example.rai_oreo.screen

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rai_oreo.R
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.remote.RemoteMapper
import com.example.rai_oreo.data.remote.RemoteRepository
import com.example.rai_oreo.databinding.ActivityFormLayananBinding
import kotlinx.coroutines.launch

class FormLayananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormLayananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormLayananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Modul 2: AutoCompleteTextView untuk kategori layanan.
        val kategoriAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.kategori_layanan)
        )

        binding.etKategori.apply {
            setAdapter(kategoriAdapter)

            // Dropdown dapat muncul tanpa harus mengetik karakter.
            threshold = 0

            // Menampilkan pilihan ketika kolom ditekan.
            setOnClickListener {
                showDropDown()
            }

            // Menampilkan pilihan ketika kolom mendapatkan fokus.
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    post {
                        showDropDown()
                    }
                }
            }
        }

        // Modul 3: Mengambil data layanan melalui Intent.
        val serviceId = intent.getIntExtra("serviceId", 0)

        val existing = if (serviceId != 0) {
            SimperRepository.serviceById(serviceId)
        } else {
            null
        }

        // Jika sedang mengedit, tampilkan data layanan sebelumnya.
        if (existing != null) {
            binding.toolbar.title = "Ubah Layanan"

            binding.etNama.setText(existing.nama)
            binding.etKategori.setText(existing.kategori, false)
            binding.etHarga.setText(existing.hargaMulai.toString())
            binding.etSatuan.setText(existing.satuan)
            binding.etDeskripsi.setText(existing.deskripsi)
        }

        // Modul 12: Create dan Update layanan.
        binding.btnSimpan.setOnClickListener {

            val nama = binding.etNama.text
                ?.toString()
                .orEmpty()
                .trim()

            val kategori = binding.etKategori.text
                ?.toString()
                .orEmpty()
                .ifBlank { "Lainnya" }

            val harga = binding.etHarga.text
                ?.toString()
                .orEmpty()
                .toIntOrNull() ?: 0

            val satuan = binding.etSatuan.text
                ?.toString()
                .orEmpty()
                .ifBlank { "pcs" }

            val deskripsi = binding.etDeskripsi.text
                ?.toString()
                .orEmpty()

            binding.tilNama.error = null
            binding.tilHarga.error = null

            // Validasi nama layanan.
            if (nama.isEmpty()) {
                binding.tilNama.error = "Nama layanan wajib diisi"
                return@setOnClickListener
            }

            // Validasi harga layanan.
            if (harga <= 0) {
                binding.tilHarga.error = "Harga harus lebih dari 0"
                return@setOnClickListener
            }

            // Fallback jika Supabase belum dikonfigurasi.
            if (!RemoteRepository.siap()) {
                simpanLokal(
                    existing,
                    nama,
                    kategori,
                    harga,
                    satuan,
                    deskripsi
                )
                return@setOnClickListener
            }

            // Simpan atau perbarui data ke Supabase.
            binding.btnSimpan.isEnabled = false

            lifecycleScope.launch {

                val idRemote = existing?.let {
                    RemoteMapper.idLayananRemote(it.id)
                }

                val hasil = if (idRemote == null) {

                    // CREATE: menambahkan layanan baru.
                    RemoteRepository.tambahLayanan(
                        RemoteMapper.dtoBaru(
                            nama,
                            kategori,
                            harga,
                            satuan,
                            deskripsi
                        )
                    )

                } else {

                    // UPDATE: memperbarui layanan yang sudah ada.
                    RemoteRepository.ubahLayanan(
                        idRemote,
                        RemoteMapper.dtoUbah(
                            existing.id,
                            nama,
                            kategori,
                            harga,
                            satuan,
                            deskripsi
                        )
                    )
                }

                hasil
                    .onSuccess {

                        Toast.makeText(
                            this@FormLayananActivity,
                            if (idRemote == null) {
                                "Layanan ditambahkan ke server"
                            } else {
                                "Layanan diperbarui di server"
                            },
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
                    .onFailure { error ->

                        binding.btnSimpan.isEnabled = true

                        Toast.makeText(
                            this@FormLayananActivity,
                            "Gagal menyimpan ke server: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }

    /*
     * Dipakai bila Supabase belum dikonfigurasi,
     * agar aplikasi tetap bisa digunakan secara lokal.
     */
    private fun simpanLokal(
        existing: com.example.rai_oreo.data.Service?,
        nama: String,
        kategori: String,
        harga: Int,
        satuan: String,
        deskripsi: String
    ) {

        if (existing == null) {

            // CREATE lokal.
            SimperRepository.addService(
                nama,
                kategori,
                harga,
                satuan,
                deskripsi
            )

            Toast.makeText(
                this,
                "Layanan ditambahkan",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            // UPDATE lokal.
            SimperRepository.updateService(
                existing.copy(
                    nama = nama,
                    kategori = kategori,
                    hargaMulai = harga,
                    satuan = satuan,
                    deskripsi = deskripsi
                )
            )

            Toast.makeText(
                this,
                "Layanan diperbarui",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }
}
