package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rai_oreo.data.Order
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ItemLaporanBinding
import com.example.rai_oreo.util.Badge

class LaporanAdapter(private var daftar: List<Order>) :
    RecyclerView.Adapter<LaporanAdapter.VH>() {

    inner class VH(val binding: ItemLaporanBinding) : RecyclerView.ViewHolder(binding.root)

    fun perbarui(data: List<Order>) {
        daftar = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemLaporanBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val order = daftar[position]
        val b = holder.binding
        b.tvNomor.text = order.nomor
        b.tvKeterangan.text = "${order.serviceNama} · ${order.tanggal}"
        b.tvTotal.text = order.total.toRupiah()
        Badge.status(b.tvStatus, order.status)
    }

    override fun getItemCount(): Int = daftar.size
}
