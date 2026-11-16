package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rai_oreo.databinding.ItemContohBinding

class ContohAdapter(private val daftar: List<Int>) :
    RecyclerView.Adapter<ContohAdapter.VH>() {

    inner class VH(val binding: ItemContohBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemContohBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.ivContoh.setImageResource(daftar[position])
    }

    override fun getItemCount(): Int = daftar.size
}
