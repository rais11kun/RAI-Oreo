package com.example.rai_oreo.screen

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityEditProfilBinding

class EditProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val user = SimperRepository.currentUser
        binding.etNama.setText(user?.nama.orEmpty())
        binding.etEmail.setText(user?.email.orEmpty())
        binding.etTelepon.setText(user?.telepon.orEmpty())

        binding.tvGantiFoto.setOnClickListener {
            Toast.makeText(this, "Foto profil demo memakai avatar bawaan.", Toast.LENGTH_SHORT).show()
        }

        binding.btnSimpan.setOnClickListener {
            val nama = binding.etNama.text?.toString().orEmpty().trim()
            val email = binding.etEmail.text?.toString().orEmpty().trim()
            val telepon = binding.etTelepon.text?.toString().orEmpty().trim()

            binding.tilNama.error = null
            binding.tilEmail.error = null

            if (nama.isEmpty()) {
                binding.tilNama.error = "Nama wajib diisi"
                return@setOnClickListener
            }
            if (!email.contains("@")) {
                binding.tilEmail.error = "Format email tidak valid"
                return@setOnClickListener
            }

            SimperRepository.updateProfile(nama, email, telepon)
            Toast.makeText(this, "Profil diperbarui", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
