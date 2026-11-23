package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rai_oreo.data.Service
import com.example.rai_oreo.data.toRupiah
import com.example.rai_oreo.databinding.ItemLayananBinding

class LayananAdapter(
    private val daftar: List<Service>,
    private val onToggle: (Service, Boolean) -> Unit,
    private val onClick: (Service) -> Unit,
    private val onLongClick: ((Service) -> Unit)? = null,
) : RecyclerView.Adapter<LayananAdapter.VH>() {

    inner class VH(val binding: ItemLayananBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemLayananBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val service = daftar[position]
        val b = holder.binding

        b.ivGambar.setImageResource(service.gambar)
        b.tvNama.text = service.nama
        b.tvDetail.text = "${service.kategori} · ${service.hargaMulai.toRupiah()} / ${service.satuan}"

        b.swAktif.setOnCheckedChangeListener(null)
        b.swAktif.isChecked = service.aktif
        b.swAktif.setOnCheckedChangeListener { _, checked -> onToggle(service, checked) }
        b.root.setOnClickListener { onClick(service) }
        // Tekan lama = hapus layanan (CRUD Delete, tanpa mengubah layout).
        b.root.setOnLongClickListener {
            onLongClick?.invoke(service)
            onLongClick != null
        }
    }

    override fun getItemCount(): Int = daftar.size
}
