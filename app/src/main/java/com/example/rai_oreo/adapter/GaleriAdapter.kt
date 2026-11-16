/**
 * Penerapan praktikum: Modul 11 - adapter RecyclerView yang dipakai sebagai halaman ViewPager2
 * pada galeri foto layanan.
 */
package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rai_oreo.databinding.ItemGaleriBinding

class GaleriAdapter(private val daftar: List<Int>) : RecyclerView.Adapter<GaleriAdapter.VH>() {

    inner class VH(val binding: ItemGaleriBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemGaleriBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.ivGaleri.setImageResource(daftar[position])
    }

    override fun getItemCount(): Int = daftar.size
}
