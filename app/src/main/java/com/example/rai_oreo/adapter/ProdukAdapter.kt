/**
 * Penerapan praktikum: Adapter RecyclerView untuk kartu layanan (grid Katalog dan baris Home).
 */
package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rai_oreo.data.Service
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ItemProdukBinding

/**
 * Adapter kartu layanan. Dipakai grid 2 kolom di Katalog dan baris horizontal di Home.
 */
class ProdukAdapter(
    private val daftar: List<Service>,
    private val horizontal: Boolean = false,
    private val onClick: (Service) -> Unit,
) : RecyclerView.Adapter<ProdukAdapter.VH>() {

    inner class VH(val binding: ItemProdukBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemProdukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val service = daftar[position]
        val b = holder.binding

        b.ivGambar.setImageResource(service.gambar)
        b.tvNama.text = service.nama
        b.tvHarga.text = "Mulai ${service.hargaMulai.toRupiah()} / ${service.satuan}"
        b.tvPopuler.visibility = if (service.populer) android.view.View.VISIBLE else android.view.View.GONE

        if (horizontal) {
            val density = b.root.resources.displayMetrics.density
            b.root.layoutParams = b.root.layoutParams.apply { width = (140 * density).toInt() }
        }

        b.root.setOnClickListener { onClick(service) }
    }

    override fun getItemCount(): Int = daftar.size
}
