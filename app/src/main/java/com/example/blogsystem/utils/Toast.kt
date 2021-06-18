package com.example.blogsystem.utils

import android.widget.Toast
import com.example.blogsystem.base.App

fun simpleToast(text: Int) {
    Toast.makeText(App.get(), text, Toast.LENGTH_SHORT).show()
}

fun simpleToast(text: CharSequence) {
    Toast.makeText(App.get(), text, Toast.LENGTH_SHORT).show()
}