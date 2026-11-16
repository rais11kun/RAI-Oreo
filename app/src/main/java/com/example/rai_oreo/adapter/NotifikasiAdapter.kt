/**
 * Penerapan praktikum: Modul 10 - ListView custom adapter notifikasi.
 */
package com.example.rai_oreo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.example.rai_oreo.R
import com.example.rai_oreo.data.AppNotification
import com.example.rai_oreo.data.NotificationType
import com.example.rai_oreo.databinding.ItemNotifikasiBinding

class NotifikasiAdapter(private val daftar: List<AppNotification>) : BaseAdapter() {

    private class VH(val binding: ItemNotifikasiBinding)

    override fun getCount(): Int = daftar.size

    override fun getItem(position: Int): AppNotification = daftar[position]

    override fun getItemId(position: Int): Long = daftar[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: VH
        val view: View
        if (convertView == null) {
            val binding = ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            holder = VH(binding)
            view = binding.root
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as VH
        }

        val b = holder.binding
        val notif = getItem(position)

        b.tvJudul.text = notif.judul
        b.tvIsi.text = notif.isi
        b.tvWaktu.text = notif.waktu

        val (ikon, warna, latar) = when (notif.tipe) {
            NotificationType.PESANAN -> Triple(R.drawable.ic_receipt, R.color.blue, R.drawable.bg_tile_blue)
            NotificationType.PROMO -> Triple(R.drawable.ic_tag, R.color.orange_dark, R.drawable.bg_tile_orange)
            NotificationType.SISTEM -> Triple(R.drawable.ic_help, R.color.green, R.drawable.bg_tile_green)
        }
        b.ivIcon.setImageResource(ikon)
        b.ivIcon.setBackgroundResource(latar)
        b.ivIcon.imageTintList = ContextCompat.getColorStateList(view.context, warna)

        b.tvJudul.alpha = if (notif.dibaca) 0.75f else 1f
        return view
    }
}
