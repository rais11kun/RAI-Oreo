/**
 * Penerapan praktikum: Modul 10 (ListView custom adapter), Modul 2 (ChipGroup + onClick).
 */
package com.example.rai_oreo.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.PesananAdapter
import com.example.rai_oreo.data.Order
import com.example.rai_oreo.data.OrderStatus
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.databinding.ActivityAntrianBinding

class AntrianActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAntrianBinding
    private var filter: OrderStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAntrianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.menu_help) {
                startActivity(Intent(this, BantuanActivity::class.java))
                true
            } else {
                false
            }
        }

        val opsi = listOf("Semua" to null, "Baru" to OrderStatus.BARU, "Design" to OrderStatus.DESAIN,
            "Produksi" to OrderStatus.PRODUKSI, "Selesai" to OrderStatus.SELESAI)

        opsi.forEachIndexed { index, (label, status) ->
            val chip = layoutInflater.inflate(R.layout.view_chip, binding.chipFilter, false) as Chip
            chip.id = View.generateViewId()
            chip.text = label
            chip.isChecked = index == 0
            chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.chip_bg_selector)
            chip.setTextColor(ContextCompat.getColorStateList(this, R.color.chip_text))
            chip.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    filter = status
                    muat()
                }
            }
            binding.chipFilter.addView(chip)
        }

        binding.lvAntrian.setOnItemClickListener { _, _, position, _ ->
            val order = binding.lvAntrian.adapter.getItem(position) as Order
            startActivity(
                Intent(this, DetailAntrianActivity::class.java)
                    .putExtra(DetailPesananActivity.EXTRA_NOMOR, order.nomor)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        muat()
    }

    private fun muat() {
        val semua = SimperRepository.orders
        val data = if (filter == null) semua else semua.filter { it.status == filter }
        val aktif = semua.count { it.status.isActive }

        binding.tvRingkas.text = "$aktif pesanan aktif · ${semua.size} total pesanan"
        binding.lvAntrian.adapter = PesananAdapter(
            daftar = data,
            onClick = { order ->
                startActivity(
                    Intent(this, DetailAntrianActivity::class.java)
                        .putExtra(DetailPesananActivity.EXTRA_NOMOR, order.nomor)
                )
            },
        )
        binding.emptyAntrian.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
        binding.lvAntrian.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
    }
}
