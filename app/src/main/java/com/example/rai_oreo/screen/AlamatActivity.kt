/**
 * Penerapan praktikum: Modul 3 (Dialog & PopUp), Modul 10 (RecyclerView adapter).
 */
package com.example.rai_oreo.screen

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.rai_oreo.adapter.AlamatAdapter
import com.example.rai_oreo.data.Address
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityAlamatBinding

class AlamatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlamatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.rvAlamat.layoutManager = LinearLayoutManager(this)
        binding.btnTambah.setOnClickListener { dialogTambah() }
    }

    override fun onResume() {
        super.onResume()
        muat()
    }

    private fun muat() {
        val daftar = SimperRepository.addresses
        binding.rvAlamat.adapter = AlamatAdapter(
            daftar = daftar,
            onUtama = { alamat: Address ->
                SimperRepository.setPrimaryAddress(alamat.id)
                muat()
            },
            onHapus = { alamat: Address ->
                SimperRepository.removeAddress(alamat.id)
                muat()
            },
        )
        binding.emptyAlamat.visibility = if (daftar.isEmpty()) View.VISIBLE else View.GONE
        binding.rvAlamat.visibility = if (daftar.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun dialogTambah() {
        val isian = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        fun tambahField(hint: String, tipe: Int = InputType.TYPE_CLASS_TEXT): EditText {
            val field = EditText(this).apply {
                this.hint = hint
                inputType = tipe
                setSingleLine(true)
            }
            isian.addView(field)
            return field
        }

        val label = tambahField("Label (Rumah / Kantor)")
        val nama = tambahField("Nama penerima")
        val telepon = tambahField("Nomor telepon", InputType.TYPE_CLASS_PHONE)
        val detail = tambahField("Alamat lengkap")

        MaterialAlertDialogBuilder(this)
            .setTitle("Tambah alamat")
            .setView(isian)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan") { _, _ ->
                val isiDetail = detail.text?.toString().orEmpty().trim()
                if (isiDetail.isEmpty()) {
                    Toast.makeText(this, "Alamat lengkap wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                SimperRepository.addAddress(
                    label = label.text?.toString().orEmpty().ifBlank { "Alamat" },
                    nama = nama.text?.toString().orEmpty().ifBlank { "Pelanggan" },
                    telepon = telepon.text?.toString().orEmpty(),
                    detail = isiDetail,
                )
                muat()
            }
            .show()
    }
}
