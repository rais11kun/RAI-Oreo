package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Halaman Awal tidak menggunakan Toolbar sesuai instruksi tugas.
        binding.btnMyProject.setOnClickListener {
            startActivity(Intent(this, SnackActivity::class.java))
        }

        binding.btnOpenWeb.setOnClickListener {
            startActivity(Intent(this, WebActivity::class.java))
        }

        binding.btnDeveloperProfile.setOnClickListener {
            startActivity(Intent(this, DeveloperProfileActivity::class.java))
        }

        // Fitur logout mengikuti modul: minta konfirmasi, lalu hapus status login.
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { dialog, _ ->
                    getSharedPreferences("simper_pref", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply()

                    dialog.dismiss()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .show()
        }
    }
}
