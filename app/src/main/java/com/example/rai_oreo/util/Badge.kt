package com.example.rai_oreo.util

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.rai_oreo.R
import com.example.rai_oreo.data.OrderStatus

/** Mewarnai label status pesanan supaya konsisten di seluruh layar. */
object Badge {

    fun status(view: TextView, status: OrderStatus) {
        val latar = when (status) {
            OrderStatus.BARU -> R.drawable.bg_pill_blue
            OrderStatus.DESAIN -> R.drawable.bg_pill_blue
            OrderStatus.DIPROSES -> R.drawable.bg_pill_orange
            OrderStatus.PRODUKSI -> R.drawable.bg_pill_orange
            OrderStatus.SELESAI -> R.drawable.bg_pill_green
            OrderStatus.DIBATALKAN -> R.drawable.bg_pill_red
        }
        val warna = when (status) {
            OrderStatus.BARU -> R.color.blue
            OrderStatus.DESAIN -> R.color.primary
            OrderStatus.DIPROSES -> R.color.orange_dark
            OrderStatus.PRODUKSI -> R.color.orange_dark
            OrderStatus.SELESAI -> R.color.green
            OrderStatus.DIBATALKAN -> R.color.red
        }
        view.setBackgroundResource(latar)
        view.setTextColor(ContextCompat.getColor(view.context, warna))
        view.text = status.label
    }

    fun progres(
        steps: List<android.widget.ImageView>,
        lines: List<android.view.View>,
        langkah: Int,
    ) {
        steps.forEachIndexed { index, step ->
            step.setBackgroundResource(
                if (index <= langkah) R.drawable.bg_step_done else R.drawable.bg_step_idle
            )
        }
        lines.forEachIndexed { index, line ->
            line.setBackgroundResource(
                if (index < langkah) R.drawable.bg_line_done else R.drawable.bg_line_idle
            )
        }
    }
}
