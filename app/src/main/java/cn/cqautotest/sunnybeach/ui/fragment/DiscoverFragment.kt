package cn.cqautotest.sunnybeach.ui.fragment

import android.graphics.Color
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingTitleBarFragment
import cn.cqautotest.sunnybeach.databinding.DiscoverFragmentBinding
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBannerBean
import cn.cqautotest.sunnybeach.ui.activity.GalleryActivity
import cn.cqautotest.sunnybeach.ui.adapter.PhotoListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.CustomAnimation
import cn.cqautotest.sunnybeach.viewmodel.PhotoViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.GridSpaceDecoration
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 发现 Fragment
 */
class DiscoverFragment : PagingTitleBarFragment<AppActivity>() {

    private val mBinding: DiscoverFragmentBinding by viewBinding()
    private val mPhotoViewModel by activityViewModels<PhotoViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mWallpaperBannerAdapter = BannerAdapter()
    private val mAdapterDelegate = AdapterDelegate().apply {
        adapterAnimation = CustomAnimation()
    }
    private val mPhotoListAdapter = PhotoListAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mPhotoListAdapter

    override fun getLayoutId(): Int = R.layout.discover_fragment

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
            addItemDecoration(GridSpaceDecoration(8.dp))
        }
    }

    override suspend fun loadListData() {
        mPhotoViewModel.wallpaperListFlow.collectLatest {
            mPhotoListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { view, position ->
            Repository.setPhotoIdList(mPhotoListAdapter.snapshot().items.toList())
            mPhotoListAdapter.snapshotList.getOrNull(position)?.let { GalleryActivity.smoothEntry(requireActivity(), it.id, view) }
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