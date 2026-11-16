/**
 * Penerapan praktikum: Modul 7 - Activity & Fragment serta Bottom Navigation dari res/menu/menu_bottom.xml.
 */
package com.example.rai_oreo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rai_oreo.databinding.ActivityMainBinding
import com.example.rai_oreo.fragment.HomeFragment
import com.example.rai_oreo.fragment.KatalogFragment
import com.example.rai_oreo.fragment.PesananFragment
import com.example.rai_oreo.fragment.ProfilFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_home -> tampilkan(HomeFragment())
                R.id.tab_katalog -> tampilkan(KatalogFragment())
                R.id.tab_pesanan -> tampilkan(PesananFragment())
                R.id.tab_profil -> tampilkan(ProfilFragment())
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.tab_home
        }
    }

    private fun tampilkan(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragContainer, fragment)
            .commit()
    }

    /** Dipakai fragment lain untuk berpindah tab (misal dari Home ke Katalog). */
    fun bukaTab(menuId: Int) {
        binding.bottomNav.selectedItemId = menuId
    }
}
