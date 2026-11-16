/**
 * Penerapan praktikum: Adapter RecyclerView dengan dua tipe baris (header dan item) untuk menu Profil.
 */
package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rai_oreo.R
import com.example.rai_oreo.databinding.ItemMenuBinding
import com.example.rai_oreo.databinding.ItemMenuHeaderBinding

data class MenuProfil(
    val judul: String,
    val sub: String,
    val ikon: Int,
    val aksi: String,
    val warnaIkon: Int = R.color.blue,
    val latarIkon: Int = R.drawable.bg_tile_blue,
)

class MenuAdapter(
    private val barang: List<Any>,
    private val onClick: (String) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val TIPE_HEADER = 0
        const val TIPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int =
        if (barang[position] is String) TIPE_HEADER else TIPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TIPE_HEADER) {
            val binding = ItemMenuHeaderBinding.inflate(inflater, parent, false)
            object : RecyclerView.ViewHolder(binding.root) {}
        } else {
            val binding = ItemMenuBinding.inflate(inflater, parent, false)
            object : RecyclerView.ViewHolder(binding.root) {}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = barang[position]
        if (data is String) {
            (holder.itemView as android.widget.TextView).text = data
            return
        }
        val item = data as MenuProfil
        val binding = ItemMenuBinding.bind(holder.itemView)
        binding.tvJudul.text = item.judul
        binding.tvSub.text = item.sub
        binding.ivIcon.setImageResource(item.ikon)
        binding.ivIcon.setBackgroundResource(item.latarIkon)
        binding.ivIcon.imageTintList = ContextCompat.getColorStateList(holder.itemView.context, item.warnaIkon)
        holder.itemView.setOnClickListener { onClick(item.aksi) }
    }

    override fun getItemCount(): Int = barang.size

    fun tampilkan(view: View, aksiAda: Boolean) {
        view.visibility = if (aksiAda) View.VISIBLE else View.GONE
    }
}
