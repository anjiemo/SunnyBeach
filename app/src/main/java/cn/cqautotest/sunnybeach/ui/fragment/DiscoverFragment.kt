package cn.cqautotest.sunnybeach.ui.fragment

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.FragmentDiscoverBinding
import cn.cqautotest.sunnybeach.http.response.model.HomeBannerBean
import cn.cqautotest.sunnybeach.http.response.model.HomePhotoBean
import cn.cqautotest.sunnybeach.ui.activity.GalleryActivity
import cn.cqautotest.sunnybeach.ui.adapter.PhotoAdapter
import cn.cqautotest.sunnybeach.utils.*
import cn.cqautotest.sunnybeach.viewmodel.SingletonManager
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

class DiscoverFragment : AppFragment<AppActivity>() {

    private var _binding: FragmentDiscoverBinding? = null
    private val mBinding get() = _binding!!
    private val mBannerList = arrayListOf<HomeBannerBean.Data>()
    private val mPhotoAdapter by lazy { PhotoAdapter() }
    private val mPhotoList = arrayListOf<HomePhotoBean.Res.Vertical>()
    private val discoverViewModel by lazy { SingletonManager.discoverViewModel }

    override fun getLayoutId(): Int = R.layout.fragment_discover

    override fun onBindingView() {
        _binding = FragmentDiscoverBinding.bind(view)
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

    companion object {
        @JvmStatic
        fun newInstance(): DiscoverFragment {
            return DiscoverFragment()
        }
    }
}