package com.example.rai_oreo.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/** Membuat gambar QR (PNG di memori) dari teks apa pun. */
object Qr {

    private const val INK = 0xFF0B2A45.toInt()

    fun bitmap(teks: String, ukuran: Int = 720): Bitmap {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.CHARACTER_SET to "UTF-8",
        )
        val matrix = QRCodeWriter().encode(teks, BarcodeFormat.QR_CODE, ukuran, ukuran, hints)
        val bitmap = Bitmap.createBitmap(ukuran, ukuran, Bitmap.Config.ARGB_8888)
        for (x in 0 until ukuran) {
            for (y in 0 until ukuran) {
                bitmap.setPixel(x, y, if (matrix[x, y]) INK else Color.WHITE)
            }
        }
        return bitmap
    }
}
