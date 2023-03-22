package cn.cqautotest.sunnybeach.util

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.blankj.utilcode.util.VibrateUtils
import com.dylanc.longan.toast
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import java.lang.ref.WeakReference

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/01/29
 * desc   : 多功能操作助手
 */
class MultiOperationHelper constructor(activity: Activity) {

    private val mActivity = WeakReference(activity)

    /**
     * 摸鱼动态点赞
     */
    fun dynamicLikes(
        lifecycleOwner: LifecycleOwner,
        viewModel: FishPondViewModel,
        adapter: RecyclerView.Adapter<*>,
        item: Fish.FishItem,
        position: Int
    ) {
        dynamicLikes(lifecycleOwner, viewModel, item)
        adapter.notifyItemChanged(position)
    }

    /**
     * 摸鱼动态点赞
     */
    fun dynamicLikes(
        lifecycleOwner: LifecycleOwner,
        viewModel: FishPondViewModel,
        item: Fish.FishItem,
    ) {
        val thumbUpList = item.thumbUpList
        val currUserId = UserManager.loadCurrUserId()
        takeIf { currUserId in thumbUpList }?.let { mActivity.get()?.toast("请不要重复点赞") }?.also { return }
        thumbUpList.add(currUserId)
        VibrateUtils.vibrate(80)
        viewModel.dynamicLikes(item.id).observe(lifecycleOwner) {}
    }

    /**
     * 分享摸鱼动态
     */
    fun shareFish(momentId: String) {
        val ctx = mActivity.get() ?: return
        val content = UMWeb(SUNNY_BEACH_FISH_URL_PRE + momentId)
        content.title = "我分享了一条摸鱼动态，快来看看吧~"
        content.setThumb(UMImage(ctx, R.mipmap.launcher_ic))
        content.description = ctx.getString(R.string.app_name)
        share(content)
    }

    /**
     * 使用友盟分享
     */
    fun share(content: UMWeb) {
        val activity = mActivity.get() ?: return
        ShareDialog.Builder(activity)
            .setShareLink(content)
            .setListener(object : UmengShare.OnShareListener {
                override fun onSucceed(platform: Platform?) {
                    mActivity.get()?.toast("分享成功")
                }

                override fun onError(platform: Platform?, t: Throwable) {
                    mActivity.get()?.toast(t.message)
                }

                override fun onCancel(platform: Platform?) {
                    mActivity.get()?.toast("分享取消")
                }
            })
            .show()
    }
}