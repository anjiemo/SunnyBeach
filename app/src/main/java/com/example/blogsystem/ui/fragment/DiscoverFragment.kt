package com.example.blogsystem.ui.fragment

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogsystem.R
import com.example.blogsystem.adapter.PhotoAdapter
import com.example.blogsystem.base.BaseFragment
import com.example.blogsystem.databinding.FragmentDiscoverBinding
import com.example.blogsystem.network.Repository
import com.example.blogsystem.network.model.HomeBannerBean
import com.example.blogsystem.network.model.HomePhotoBean
import com.example.blogsystem.ui.activity.GalleryActivity
import com.example.blogsystem.utils.*
import com.example.blogsystem.viewmodel.SingletonManager
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

class DiscoverFragment : BaseFragment(R.layout.fragment_discover) {

    private var _binding: FragmentDiscoverBinding? = null
    private val mBinding get() = _binding!!
    private val mBannerList = arrayListOf<HomeBannerBean.Data>()
    private val mPhotoAdapter by lazy { PhotoAdapter() }
    private val mPhotoList = arrayListOf<HomePhotoBean.Res.Vertical>()
    private val discoverViewModel by lazy { SingletonManager.discoverViewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDiscoverBinding.bind(view)
        callAllInit()
    }

    override fun initEvent() {
        mPhotoAdapter.setOnItemClickListener { verticalPhoto, _ ->
            Repository.setLocalPhotoList(mPhotoList)
            GalleryActivity.startActivity(requireContext(), verticalPhoto.id)
        }
        discoverViewModel.bannerList.observe(this) { bannerList ->
            mBinding.banner.setDatas(bannerList)
        }
        val loadMoreModule = mPhotoAdapter.loadMoreModule
        discoverViewModel.verticalPhotoList.observe(this) { verticalPhotoList ->
            logByDebug(msg = "initEvent：===> " + verticalPhotoList.toJson())
            loadMoreModule.apply {
                mPhotoAdapter.addData(verticalPhotoList)
                isEnableLoadMore = true
                loadMoreComplete()
            }
        }
        mPhotoAdapter.loadMoreModule.run {
            setOnLoadMoreListener {
                isEnableLoadMore = false
                discoverViewModel.loadMorePhotoList()
            }
        }
    }

    override fun initData() {
        discoverViewModel.loadBannerList()
        discoverViewModel.loadMorePhotoList()
    }

    override fun initView() {
        mBinding.banner.apply {
            adapter = object : BannerImageAdapter<HomeBannerBean.Data>(mBannerList) {

                override fun setDatas(datas: MutableList<HomeBannerBean.Data>?) {
                    super.setDatas(datas)
                    mBinding.bannerNoDataIv.visibility =
                        if (datas.isNullOrEmpty()) View.VISIBLE else View.GONE
                }

                override fun onBindView(
                    holder: BannerImageHolder,
                    data: HomeBannerBean.Data?,
                    position: Int,
                    size: Int
                ) {
                    if (data != null) {
                        Glide.with(holder.itemView)
                            .load(data.urlThumb)
                            .into(holder.imageView)
                    }
                    mBinding.bannerNoDataIv.visibility =
                        if (data == null) View.VISIBLE else View.GONE
                }
            }
            addBannerLifecycleObserver(this@DiscoverFragment)
            indicator = CircleIndicator(context)
            setIndicatorSelectedColor(Color.WHITE)
        }
        mBinding.photoListRv.apply {
            mPhotoAdapter.adapterAnimation = CustomAnimation()
            mPhotoAdapter.isAnimationFirstOnly = false
            layoutManager = GridLayoutManager(context, 2)
            adapter = mPhotoAdapter
            setupSpacing(this)
        }
    }

    private fun setupSpacing(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {

            // 单位间距（实际间距的一半）
            private val unit = 4.dp

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                equilibriumAssignmentOfGrid(unit, outRect, view, parent)
            }
        })
    }
}