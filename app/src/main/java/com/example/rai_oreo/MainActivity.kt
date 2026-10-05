package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
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
    }
}
