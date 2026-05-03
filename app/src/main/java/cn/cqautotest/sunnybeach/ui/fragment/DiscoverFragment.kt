package cn.cqautotest.sunnybeach.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.GridLayoutManager
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
        // 查找 ID 在当前列表中的位置以进行滚动
        val index = mPhotoListAdapter.snapshot().items.indexOfFirst { it.id == id }
        if (index != -1) {
            mBinding.pagingRecyclerView.scrollToPosition(index)
        }
        // 无论是否需要滚动，都要在布局后尝试开始动画
        mBinding.pagingRecyclerView.post {
            // 检查目标 View 是否就绪
            var found = false
            for (i in 0 until mBinding.pagingRecyclerView.childCount) {
                val child = mBinding.pagingRecyclerView.getChildAt(i)
                val photoIv = child.findViewById<ImageView>(R.id.photoIv)
                if (photoIv?.transitionName == id) {
                    found = true
                    break
                }
            }
            if (found) {
                requireActivity().supportStartPostponedEnterTransition()
            } else {
                // 如果滚动还未完成，增加最后的缓冲
                mBinding.pagingRecyclerView.postDelayed({
                    requireActivity().supportStartPostponedEnterTransition()
                }, 100)
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