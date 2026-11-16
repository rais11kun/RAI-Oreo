package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rai_oreo.R
import com.example.rai_oreo.data.OrderEvent
import com.example.rai_oreo.databinding.ItemTimelineBinding

class TimelineAdapter(private val daftar: List<OrderEvent>) :
    RecyclerView.Adapter<TimelineAdapter.VH>() {

    inner class VH(val binding: ItemTimelineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val event = daftar[position]
        val b = holder.binding

        b.tvJudul.text = event.title
        b.tvWaktu.text = event.waktu
        b.tvCatatan.text = event.catatan
        b.ivTitik.setBackgroundResource(
            if (event.done && event.waktu != "-") R.drawable.bg_step_done else R.drawable.bg_step_idle
        )
        b.garis.setBackgroundResource(
            if (event.done && event.waktu != "-") R.drawable.bg_line_done else R.drawable.bg_line_idle
        )
        b.garis.visibility = if (position == daftar.lastIndex) View.INVISIBLE else View.VISIBLE
    }

    override fun getItemCount(): Int = daftar.size
}
