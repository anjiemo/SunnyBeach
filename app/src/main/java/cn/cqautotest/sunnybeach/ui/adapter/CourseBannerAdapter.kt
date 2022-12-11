package cn.cqautotest.sunnybeach.ui.adapter

import cn.cqautotest.sunnybeach.model.course.Course
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/11
 * desc   : 课程 Banner 适配器
 */
class CourseBannerAdapter(private val mData: List<Course.CourseItem> = arrayListOf()) : BannerImageAdapter<Course.CourseItem>(mData) {

    override fun onBindView(holder: BannerImageHolder?, data: Course.CourseItem?, position: Int, size: Int) {
        listOf(holder ?: return, data ?: return)
        holder.apply {
            Glide.with(itemView)
                .load(data.cover)
                .into(imageView)
        }
    }

    fun getData(): List<Course.CourseItem> = mData
}