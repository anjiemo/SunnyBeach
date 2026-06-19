package cn.cqautotest.sunnybeach.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingTitleBarFragment
import cn.cqautotest.sunnybeach.databinding.DiscoverFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBannerBean
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.repository.Repository
import cn.cqautotest.sunnybeach.ui.activity.GalleryActivity
import cn.cqautotest.sunnybeach.ui.adapter.PhotoListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.CustomAnimation
import cn.cqautotest.sunnybeach.viewmodel.PhotoViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.UniversalSpaceDecoration
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 发现 Fragment
 */
class DiscoverFragment : PagingTitleBarFragment<AppActivity>() {

    private val mBinding by viewBinding(DiscoverFragmentBinding::bind)
    private val mPhotoViewModel by activityViewModels<PhotoViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mWallpaperBannerAdapter = BannerAdapter()
    private val mAdapterDelegate = AdapterDelegate().apply {
        adapterAnimation = CustomAnimation()
    }
    private val mPhotoListAdapter = PhotoListAdapter(mAdapterDelegate)
    private var mExitId: String? = null
    private var mIsLoadingDetail = false

    override fun getPagingAdapter() = mPhotoListAdapter

    override fun getLayoutId(): Int = R.layout.discover_fragment

    override fun onResume() {
        super.onResume()
        mIsLoadingDetail = false
    }

    override fun initView() {
        super.initView()
        mBinding.banner.apply {
            addBannerLifecycleObserver(viewLifecycleOwner)
            setAdapter(mWallpaperBannerAdapter)
            indicator = CircleIndicator(requireContext())
            setIndicatorSelectedColor(Color.WHITE)
        }
        mBinding.pagingRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(UniversalSpaceDecoration(8.dp))
        }
    }

    /**
     * 由 Activity 委托调用的共享元素映射
     */
    fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
        // 如果 names 为空，说明没有共享元素
        if (names.isEmpty()) return
        // 关键：从 names 中获取目标 ID（由详情页传回）
        val targetId = names[0]
        mExitId = targetId

        // 清除默认映射，建立基于 ID 的精准映射
        sharedElements.clear()

        // 动态搜索 View（不依赖索引，适配 ConcatAdapter 和分页偏移）
        for (i in 0 until mBinding.pagingRecyclerView.childCount) {
            val child = mBinding.pagingRecyclerView.getChildAt(i)
            val photoIv = child.findViewById<ImageView>(R.id.photoIv)
            if (photoIv?.transitionName == targetId) {
                sharedElements[targetId] = photoIv
                break
            }
        }
    }

    override suspend fun loadListData() {
        mPhotoViewModel.wallpaperListFlow
            .flowWithLifecycle(lifecycle)
            .collectLatest {
                mPhotoListAdapter.submitData(it)
            }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { view, position ->
            if (mIsLoadingDetail) return@setOnItemClickListener
            mIsLoadingDetail = true
            Repository.setPhotoIdList(mPhotoListAdapter.snapshot().items.toList())
            mPhotoListAdapter.snapshotList.getOrNull(position)?.let {
                val photoIv = view.findViewById<ImageView>(R.id.photoIv)
                GalleryActivity.smoothEntry(requireActivity(), it.id, photoIv)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 共享元素返回逻辑已移至 handleActivityReenter
    }

    /**
     * 处理共享元素返回逻辑
     */
    fun handleActivityReenter(resultCode: Int, data: Intent?) {
        // 尝试从 Intent 中获取 ID，如果拿不到，我们也会在 onMapSharedElements 中尝试获取
        val id = data?.getStringExtra(IntentKey.ID) ?: mExitId ?: return
        mExitId = id

        // 1. 强制展开 AppBarLayout 并强制请求布局。
        // 这确保了后续滚动定位逻辑运行在“已知且稳定”的视图状态（完全展开）之上。
        mBinding.appBar.setExpanded(true, false)
        mBinding.root.requestLayout()

        // 2. 延迟执行定位逻辑，让 CoordinatorLayout 的 Behavior 有时间处理布局位移，
        // 从而彻底解决 AppBar 展开导致的 RecyclerView 顶部被裁切或重叠的问题。
        mBinding.pagingRecyclerView.post {
            val index = mPhotoListAdapter.snapshot().items.indexOfFirst { it.id == id }
            if (index == RecyclerView.NO_POSITION) {
                requireActivity().supportStartPostponedEnterTransition()
                return@post
            }

            val layoutManager = mBinding.pagingRecyclerView.layoutManager as? GridLayoutManager ?: run {
                requireActivity().supportStartPostponedEnterTransition()
                return@post
            }

            val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()
            val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()

            // 如果目标 item 已经在完全可见范围内，直接开始动画
            if (index in firstVisible..lastVisible) {
                mBinding.pagingRecyclerView.doOnPreDraw {
                    requireActivity().supportStartPostponedEnterTransition()
                }
                return@post
            }

            // 如果不在完全可见范围内，需要滚动对齐
            // 防止由于 CustomAnimation 导致转场动画漂移：提前跳过目标项及其周围项的入场动画
            mAdapterDelegate.skipAnimationTo(index + 10)
            
            // 第一步：先粗略滚动到目标 index 让 View 被创建
            layoutManager.scrollToPosition(index)
            
            // 第二步：嵌套 post 确保 ViewHolder 已就绪，进行像素级对齐
            mBinding.pagingRecyclerView.post {
                val targetView = layoutManager.findViewByPosition(index)
                if (targetView != null) {
                    val spanCount = layoutManager.spanCount
                    val row = index / spanCount
                    val totalRows = (layoutManager.itemCount + spanCount - 1) / spanCount
                    val isLastRow = row == totalRows - 1
                    
                    val decoratedHeight = layoutManager.getDecoratedMeasuredHeight(targetView)
                    val offset = if (isLastRow) {
                        mBinding.pagingRecyclerView.height - decoratedHeight - mBinding.pagingRecyclerView.paddingBottom
                    } else if (row == 0) {
                        mBinding.pagingRecyclerView.paddingTop
                    } else {
                        (mBinding.pagingRecyclerView.height - decoratedHeight) / 2
                    }
                    layoutManager.scrollToPositionWithOffset(index, offset)
                }
                
                // 最终定位后的绘制前开启过渡
                mBinding.pagingRecyclerView.doOnPreDraw {
                    requireActivity().supportStartPostponedEnterTransition()
                }
            }
        }
    }

    override fun initObserver() {
        loadWallpaperBannerList()
    }

    private fun loadWallpaperBannerList() {
        mPhotoViewModel.getWallpaperBannerList().observe(viewLifecycleOwner) {
            val slBannerHint = mBinding.slBannerHint
            val wallpaperBannerList = it.getOrElse {
                slBannerHint.showError {
                    loadWallpaperBannerList()
                }
                return@observe
            }
            mWallpaperBannerAdapter.setDatas(wallpaperBannerList)
            if (wallpaperBannerList.isEmpty()) slBannerHint.showEmpty() else slBannerHint.showComplete()
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    companion object {

        private class BannerAdapter : BannerImageAdapter<WallpaperBannerBean.Data>(null) {

            override fun onBindView(
                holder: BannerImageHolder,
                data: WallpaperBannerBean.Data,
                position: Int,
                size: Int
            ) {
                Glide.with(holder.itemView)
                    .load(data.urlThumb)
                    .into(holder.imageView)
            }
        }

        @JvmStatic
        fun newInstance() = DiscoverFragment()
    }
}