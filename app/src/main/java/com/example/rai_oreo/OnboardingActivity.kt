/**
 * Penerapan praktikum: Modul 11 - Onboard Screen memakai ViewPager2 dengan FragmentStateAdapter
 * dan indikator titik. Layar ini tampil sekali setelah splash; penandanya disimpan di Prefs.
 */
package com.example.rai_oreo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.rai_oreo.adapter.OnboardingAdapter
import com.example.rai_oreo.data.Prefs
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private val jumlahHalaman = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pagerOnboarding.adapter = OnboardingAdapter(this)
        binding.pagerOnboarding.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tampilkanIndikator(position)
                    perbaruiTombol(position)
                }
            }
        )

        binding.tvLewati.setOnClickListener { selesai() }
        binding.btnLanjut.setOnClickListener {
            val posisi = binding.pagerOnboarding.currentItem
            if (posisi < jumlahHalaman - 1) {
                binding.pagerOnboarding.setCurrentItem(posisi + 1, true)
            } else {
                selesai()
            }
        }

        tampilkanIndikator(0)
        perbaruiTombol(0)
    }

    private fun tampilkanIndikator(aktif: Int) {
        val titik = listOf(binding.indikator1, binding.indikator2, binding.indikator3)
        titik.forEachIndexed { index, view ->
            view.setBackgroundResource(
                if (index == aktif) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
            )
        }
    }

    private fun perbaruiTombol(posisi: Int) {
        val terakhir = posisi == jumlahHalaman - 1
        binding.tvLanjut.text = if (terakhir) "Mulai" else "Berikutnya"
        binding.tvLewati.visibility = if (terakhir) View.INVISIBLE else View.VISIBLE
    }

    private fun selesai() {
        Prefs.setOnboardingSelesai(this)
        val email = Prefs.email(this)
        if (Prefs.ingat(this) && email.isNotEmpty()) SimperRepository.masukSebagai(email)
        val tujuan = if (SimperRepository.currentUser != null) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }
        startActivity(Intent(this, tujuan))
        finish()
    }
}
