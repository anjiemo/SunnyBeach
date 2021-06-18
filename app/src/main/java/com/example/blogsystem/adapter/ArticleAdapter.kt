package com.example.blogsystem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogsystem.R
import com.example.blogsystem.databinding.ItemArticleBinding
import com.example.blogsystem.model.ArticleInfo

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.Holder>() {

    private val mData = arrayListOf<ArticleInfo.ArticleItem>()

    fun setData(data: List<ArticleInfo.ArticleItem>) {
        mData.run {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemArticleBinding.inflate(layoutInflater)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val binding = holder.binding
        val data = mData[position]
        binding.root.run {
            Glide.with(this).load(data.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(binding.ivAvatar)
            binding.tvNickName.text = data.nickName
            binding.tvNickName.setTextColor(
                ContextCompat.getColor(
                    context, if (data.vip) {
                        R.color.pink
                    } else {
                        R.color.default_font_color
                    }
                )
            )
            binding.tvArticleTitle.text = data.title
            binding.tvCreateTime.text = data.createTime
            binding.tvViewCount.text = data.viewCount.toString()
        }
    }

    override fun getItemCount(): Int = mData.size
}

