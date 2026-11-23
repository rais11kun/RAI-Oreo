package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.rai_oreo.data.Order
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ItemPesananBinding
import com.example.rai_oreo.util.Badge

/**
 * Penerapan praktikum: Modul 10 - ListView custom adapter dengan ViewHolder dan ViewBinding.
 * Ditambah dukungan tekan-lama untuk menghapus pesanan (Modul 12 - CRUD Delete).
 */
class PesananAdapter(
    private var daftar: List<Order>,
    private val onClick: (Order) -> Unit,
    private val onLongClick: ((Order) -> Unit)? = null,
) : BaseAdapter() {

    private class VH(val binding: ItemPesananBinding)

    fun perbarui(data: List<Order>) {
        daftar = data
        notifyDataSetChanged()
    }

    override fun getCount(): Int = daftar.size

    override fun getItem(position: Int): Order = daftar[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: VH
        val view: View

        if (convertView == null) {
            val binding = ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            holder = VH(binding)
            view = binding.root
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as VH
        }

        val b = holder.binding
        val order = getItem(position)

        b.ivGambar.setImageResource(order.gambar)
        b.tvNomor.text = order.nomor
        b.tvLayanan.text = "${order.serviceNama} · ${order.ukuran} · ${order.jumlah} pcs"
        b.tvTanggal.text = order.tanggal
        b.tvTotal.text = order.total.toRupiah()
        Badge.status(b.tvStatus, order.status)

        b.root.setOnClickListener { onClick(order) }
        b.root.setOnLongClickListener {
            onLongClick?.invoke(order)
            onLongClick != null
        }
        return view
    }
}
