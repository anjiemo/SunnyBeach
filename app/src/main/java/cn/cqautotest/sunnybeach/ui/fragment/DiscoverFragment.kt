package cn.cqautotest.sunnybeach.ui.fragment

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.DiscoverFragmentBinding
import cn.cqautotest.sunnybeach.http.response.model.HomeBannerBean
import cn.cqautotest.sunnybeach.ui.activity.GalleryActivity
import cn.cqautotest.sunnybeach.ui.adapter.PhotoAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import cn.cqautotest.sunnybeach.viewmodel.discover.DiscoverViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.blankj.utilcode.util.NetworkUtils
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
 * desc   : 发现 Fragment
 */
class DiscoverFragment : AppFragment<AppActivity>(), StatusAction {

    private val mDiscoverViewModel by viewModels<DiscoverViewModel>()
    private var _binding: DiscoverFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val mBannerList = arrayListOf<HomeBannerBean.Data>()
    private val mPhotoAdapter = PhotoAdapter()

    override fun getLayoutId(): Int = R.layout.discover_fragment

    override fun onBindingView() {
        _binding = DiscoverFragmentBinding.bind(view)
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        if (first) {
            mDiscoverViewModel.loadBannerList()
            refreshPhotoList()
        } else {
            // 如果之前的轮播图没加载上，则重新加载
            if (mBannerList.isNullOrEmpty()) {
                mDiscoverViewModel.loadBannerList()
            }
            // 如果没有图片列表数据，则重新加载图片列表数据
            val data = mPhotoAdapter.data
            if (data.isNullOrEmpty()) {
                refreshPhotoList()
            }
        }
    }

    override fun initObserver() {
        mDiscoverViewModel.bannerList.observe(viewLifecycleOwner) { bannerList ->
            mBinding.banner.setDatas(bannerList)
        }
        val loadMoreModule = mPhotoAdapter.loadMoreModule
        mDiscoverViewModel.verticalPhotoList.observe(viewLifecycleOwner) { verticalPhotoList ->
            logByDebug(msg = "initEvent：===> " + verticalPhotoList.toJson())
            mBinding.slDiscoverRefresh.finishRefresh()
            if (verticalPhotoList.isNullOrEmpty()) {
                showEmpty()
                return@observe
            }
            loadMoreModule.apply {
                mPhotoAdapter.addData(verticalPhotoList)
                isEnableLoadMore = true
                loadMoreComplete()
            }
            showComplete()
        }
    }

    override fun initEvent() {
        mPhotoAdapter.setOnItemClickListener { verticalPhoto, _ ->
            Repository.setLocalPhotoList(mPhotoAdapter.data)
            val photoId = verticalPhoto.id
            logByDebug(msg = "initEvent：===> photoId is $photoId")
            GalleryActivity.start(requireContext(), photoId)
        }
        mBinding.slDiscoverRefresh.setOnRefreshListener {
            refreshPhotoList()
        }
        mPhotoAdapter.loadMoreModule.run {
            setOnLoadMoreListener {
                isEnableLoadMore = false
                mDiscoverViewModel.loadMorePhotoList()
            }
        }
    }

    /**
     * 刷新图片列表数据
     */
    private fun refreshPhotoList() {
        NetworkUtils.isAvailableAsync { isAvailable ->
            if (isAvailable.not()) {
                mBinding.slDiscoverRefresh.finishRefresh()
                showError {
                    mDiscoverViewModel.refreshPhotoList()
                }
            } else {
                showLoading()
                mDiscoverViewModel.refreshPhotoList()
            }
        }
    }

    override fun initData() {}

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
            addBannerLifecycleObserver(viewLifecycleOwner)
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

    override fun getStatusLayout(): StatusLayout = mBinding.hlDiscoverHint

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(): AppFragment<*> {
            return DiscoverFragment()
        }
    }
}