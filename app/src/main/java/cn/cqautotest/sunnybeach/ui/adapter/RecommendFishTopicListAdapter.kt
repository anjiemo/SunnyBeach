package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.FishTopicListIncludeBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.widget.recyclerview.LinearSpaceItemDecoration

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/17
 * desc   : 推荐话题列表的适配器
 */
class RecommendFishTopicListAdapter(private val mFishCategoryAdapter: FishCategoryAdapter) :
    RecyclerView.Adapter<RecommendFishTopicListAdapter.InnerHolder>() {

    private val mData = FishPondTopicList()

    fun setData(data: List<FishPondTopicList.TopicItem>) {
        mData.clear()
        mData += data
        notifyDataSetChanged()
    }

    inner class InnerHolder(binding: FishTopicListIncludeBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.rvFishCategory.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = mFishCategoryAdapter
                addItemDecoration(LinearSpaceItemDecoration(5.dp))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        return InnerHolder(parent.asViewBinding())
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        // Default empty.
    }

    override fun getItemCount(): Int = 1
}