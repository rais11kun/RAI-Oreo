package com.example.rai_oreo

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class QROrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_order)

        val orderNumber = intent.getStringExtra(EXTRA_ORDER_NUMBER) ?: "NR-20260721-746"
        findViewById<TextView>(R.id.tvOrderNumber).text = "Nomor Pesanan: $orderNumber"

        try {
            // QR berisi teks nomor pesanan agar dapat dicetak pada nota.
            val matrix: BitMatrix = MultiFormatWriter().encode(orderNumber, BarcodeFormat.QR_CODE, 720, 720)
            val bitmap = Bitmap.createBitmap(720, 720, Bitmap.Config.ARGB_8888)
            for (x in 0 until 720) {
                for (y in 0 until 720) {
                    bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            findViewById<ImageView>(R.id.ivOrderQr).setImageBitmap(bitmap)
        } catch (e: Exception) {
            findViewById<TextView>(R.id.tvQrError).text = "QR tidak dapat dibuat: ${e.message}"
        }
    }

    companion object {
        const val EXTRA_ORDER_NUMBER = "extra_order_number"
    }
}
