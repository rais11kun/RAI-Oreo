/**
 * Penerapan praktikum: Modul 6 (SharedPreferences), Modul 2 (SwitchMaterial).
 */
package com.example.rai_oreo.screen

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.data.Prefs
import com.example.rai_oreo.databinding.ActivityPengaturanBinding
import com.example.rai_oreo.util.Biometric

class PengaturanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val sidikTersedia = Biometric.tersedia(this)
        binding.tvStatusSidik.text = if (sidikTersedia) {
            "Tersedia di perangkat ini"
        } else {
            "Perangkat tidak mendukung sidik jari"
        }

        binding.swNotif.isChecked = with(Prefs) { notifPesanan }
        binding.swPromo.isChecked = with(Prefs) { notifPromo }
        binding.swSidik.isChecked = with(Prefs) { sidikJari } && sidikTersedia
        binding.swSidik.isEnabled = sidikTersedia

        binding.swNotif.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            with(Prefs) { notifPesanan = checked }
        }
        binding.swPromo.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            with(Prefs) { notifPromo = checked }
        }
        binding.swSidik.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            with(Prefs) { sidikJari = checked }
        }

        binding.tvVersi.text = "SIMPER-NRA Mobile v1.0.0 · ${Prefs.tambahPeluncuran(this)} peluncuran"
    }
}
