package cn.cqautotest.sunnybeach.ui.fragment

import android.graphics.Color
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.DiscoverFragmentBinding
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBannerBean
import cn.cqautotest.sunnybeach.ui.activity.GalleryActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.WallpaperListAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.PhotoViewModel
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import kotlinx.coroutines.flow.collectLatest


/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
 * desc   : 发现 Fragment
 */
class DiscoverFragment : TitleBarFragment<AppActivity>(), StatusAction {

    private val mBinding: DiscoverFragmentBinding by viewBinding()
    private val mPhotoViewModel by activityViewModels<PhotoViewModel>()
    private val mWallpaperBannerAdapter = BannerAdapter()
    private val mWallpaperListAdapter = WallpaperListAdapter(AdapterDelegate().apply {
        adapterAnimation = CustomAnimation()
    })
    private val loadStateListener = loadStateListener(mWallpaperListAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.discover_fragment

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

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            mWallpaperListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mWallpaperListAdapter.addLoadStateListener(loadStateListener)
        mWallpaperListAdapter.setOnItemClickListener { view, verticalPhoto, _ ->
            Repository.setPhotoIdList(mWallpaperListAdapter.snapshot().items.toList())
            GalleryActivity.smoothEntry(requireActivity(), verticalPhoto.id, view)
        }
    }

    override fun initData() {
        loadPhotoList()
    }

    private fun loadPhotoList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mPhotoViewModel.getWallpaperList().collectLatest {
                mWallpaperListAdapter.submitData(it)
            }
        }
    }

    override fun initView() {
        mBinding.banner.apply {
            addBannerLifecycleObserver(viewLifecycleOwner)
            setAdapter(mWallpaperBannerAdapter)
            indicator = CircleIndicator(requireContext())
            setIndicatorSelectedColor(Color.WHITE)
        }
        mBinding.rvPhotoList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = mWallpaperListAdapter
            addItemDecoration(GridSpaceItemDecoration(4.dp))
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.slDiscoverHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mWallpaperListAdapter.removeLoadStateListener(loadStateListener)
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