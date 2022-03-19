package cn.cqautotest.sunnybeach.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.CreationCenterActivityBinding
import cn.cqautotest.sunnybeach.ui.adapter.AchievementAdapter
import cn.cqautotest.sunnybeach.util.GridSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/09
 * desc   : 摸鱼评论列表页
 */
class CreationCenterActivity : AppActivity() {

    private val mBinding by viewBinding(CreationCenterActivityBinding::bind)
    private val mAchievementAdapter = AchievementAdapter()

    override fun getLayoutId(): Int = R.layout.creation_center_activity

    override fun initView() {
        mBinding.rvAchievementList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mAchievementAdapter
            addItemDecoration(GridSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        val data = arrayListOf<Pair<Int, Int>>().apply {
            add(Pair(127551, 451))
            add(Pair(222, 6))
            add(Pair(35, 2))
            add(Pair(3870, 8))
        }
        mAchievementAdapter.setData(data)
    }

    override fun initEvent() {

    }
}