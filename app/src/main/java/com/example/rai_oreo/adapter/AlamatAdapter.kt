package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rai_oreo.data.Address
import com.example.rai_oreo.databinding.ItemAlamatBinding

class AlamatAdapter(
    private val daftar: List<Address>,
    private val onUtama: (Address) -> Unit,
    private val onHapus: (Address) -> Unit,
) : RecyclerView.Adapter<AlamatAdapter.VH>() {

    inner class VH(val binding: ItemAlamatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemAlamatBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val alamat = daftar[position]
        val b = holder.binding

        b.tvLabel.text = alamat.label
        b.tvDetail.text = alamat.detail
        b.tvNama.text = "${alamat.nama} · ${alamat.telepon}"
        b.tvUtama.visibility = if (alamat.utama) View.VISIBLE else View.GONE
        b.root.setOnClickListener { onUtama(alamat) }
        b.ivHapus.setOnClickListener { onHapus(alamat) }
    }

    override fun getItemCount(): Int = daftar.size
}
