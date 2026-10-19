package com.example.rai_oreo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rai_oreo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fragment Beranda ditampilkan saat MainActivity pertama kali dibuka.
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            binding.bottomNavView.selectedItemId = R.id.nav_beranda
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_layanan -> {
                    replaceFragment(LayananFragment())
                    true
                }
                R.id.nav_pesan -> {
                    replaceFragment(PesanFragment())
                    true
                }
                R.id.nav_riwayat -> {
                    replaceFragment(RiwayatFragment())
                    true
                }
                R.id.nav_profil -> {
                    replaceFragment(ProfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Pola mengikuti modul: mengganti isi FrameLayout dengan Fragment terpilih.
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            // Sesuai modul Pertemuan 7, back stack Bottom Navigation tidak ditambahkan.
            .commit()
    }
}
