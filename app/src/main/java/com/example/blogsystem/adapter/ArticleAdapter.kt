package com.example.blogsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blogsystem.R

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.Holder>() {

    private val mData = arrayListOf<String>()

    open fun setData(data: List<String>) {
        mData.run {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

    }

    override fun getItemCount(): Int = mData.size
}

