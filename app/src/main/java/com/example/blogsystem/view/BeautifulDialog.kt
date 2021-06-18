package com.example.blogsystem.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.PaintDrawable
import androidx.appcompat.app.AppCompatDialog
import com.example.blogsystem.utils.dp

class BeautifulDialog(val context: Context) {

    private val dialog = AppCompatDialog(context)

    fun create() = dialog.apply {
        window?.let {
            val paintDrawable = PaintDrawable(Color.BLACK)
            val radii = floatArrayOf(
                10.dp.toFloat(),
                10.dp.toFloat(),
                10.dp.toFloat(),
                10.dp.toFloat(),
                10.dp.toFloat(),
                10.dp.toFloat(),
                10.dp.toFloat(),
                10.dp.toFloat()
            )
            paintDrawable.setCornerRadii(radii)
            it.setBackgroundDrawable(paintDrawable)
        }
        dialog.create()
    }
}