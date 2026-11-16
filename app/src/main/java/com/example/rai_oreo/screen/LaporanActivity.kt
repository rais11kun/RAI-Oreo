/**
 * Penerapan praktikum: Modul 10 (RecyclerView + Spinner), Modul 5 (Option Menu bagikan).
 */
package com.example.rai_oreo.screen

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rai_oreo.R
import com.example.rai_oreo.adapter.LaporanAdapter
import com.example.rai_oreo.data.OrderStatus
import com.example.rai_oreo.data.SimperRepository
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ActivityLaporanBinding

class LaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.menu_share) {
                bagikan()
                true
            } else {
                false
            }
        }

        binding.rvLaporan.layoutManager = LinearLayoutManager(this)
        binding.spPeriode.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                muat()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }

        muat()
    }

    private fun muat() {
        val orders = SimperRepository.orders
        val selesai = orders.filter { it.status == OrderStatus.SELESAI }

        binding.tvTotalPendapatan.text = SimperRepository.revenueOf(orders).toRupiah()
        binding.tvJumlahTransaksi.text = selesai.size.toString()
        binding.rvLaporan.adapter = LaporanAdapter(orders.sortedByDescending { it.tanggal })
    }

    private fun bagikan() {
        val kirim = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                android.content.Intent.EXTRA_TEXT,
                "Laporan SIMPER-NRA: pendapatan ${binding.tvTotalPendapatan.text} dari ${binding.tvJumlahTransaksi.text} transaksi selesai.",
            )
        }
        startActivity(android.content.Intent.createChooser(kirim, "Bagikan laporan"))
    }
}
