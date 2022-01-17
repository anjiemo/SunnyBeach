package cn.cqautotest.sunnybeach.ui.activity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.android52.sunnybeach.skin.activity.SupportSkinActivity
import cn.android52.sunnybeach.skin.callback.ISkinChangingCallback
import cn.android52.sunnybeach.skin.manager.SkinManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.aop.Permissions
import cn.cqautotest.sunnybeach.databinding.TestActivityBinding
import cn.cqautotest.sunnybeach.ui.adapter.TimeLineAdapter
import com.hjq.permissions.Permission
import timber.log.Timber

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/16
 *    desc   : 用于测试的界面
 */
class TestActivity : SupportSkinActivity(), Init, ISkinChangingCallback {

    private val mBinding by viewBinding<TestActivityBinding>()
    private val mTimeLineAdapter = TimeLineAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("pre onCreate：===>")
        super.onCreate(savedInstanceState)
        Timber.d("after onCreate：===>")
        setContentView(getLayoutId())
        initView()
        initData()
        initEvent()
        initObserver()
    }

    private fun getLayoutId(): Int {
        return R.layout.test_activity
    }

    private fun initView() {
        val rvTimeLine = mBinding.rvTimeLine
        rvTimeLine.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mTimeLineAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val width = parent.measuredWidth
                    val spaceWidth = width / 17 * 11
                    val position = parent.getChildAdapterPosition(view)
                    outRect.left = if (!position.isLeft) 0 else spaceWidth
                    outRect.right = if (!position.isRight) 0 else spaceWidth
                }

                override fun onDrawOver(
                    canvas: Canvas,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val width = parent.measuredWidth
                    val height = parent.measuredHeight
                    val lineWidth = 2
                    val centerX = (width / 2 - lineWidth).toFloat()
                    val endY = height.toFloat()
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.rgb(0, 126, 255)
                    }
                    val layoutManager = parent.layoutManager ?: return
                    val itemCount = layoutManager.itemCount
                    if (itemCount == 0) {
                        return
                    }
                    val firstView = layoutManager.findViewByPosition(0)
                    val firstY = (firstView?.measuredHeight?.div(4f)) ?: 0f
                    var posY = firstY
                    val lastView = layoutManager.findViewByPosition(layoutManager.itemCount - 1)
                    val lastItemHeight = (lastView?.measuredHeight)?.toFloat() ?: 0f
                    repeat(itemCount) { index ->
                        val itemView = layoutManager.findViewByPosition(index)
                        itemView?.let {
                            canvas.drawOval(
                                if (index.isLeft) centerX + 140 else centerX - 160,
                                posY - 10,
                                if (index.isRight) centerX - 140 else centerX + 160,
                                posY + 10,
                                paint
                            )
                            canvas.drawLine(
                                if (index.isLeft) centerX else centerX - 160,
                                posY,
                                if (index.isRight) centerX else centerX + 160,
                                posY,
                                paint
                            )
                            canvas.drawOval(centerX - 20, posY - 20, centerX + 20, posY + 20, paint)
                            posY += itemView.measuredHeight.toFloat()
                        }
                    }
                    // canvas.drawLine(centerX, firstY, centerX, endY - lastItemHeight + firstY, paint)
                    canvas.drawLine(centerX, 0f, centerX, endY, paint)
                }

                private val Int.isLeft
                    get() = (this + 1) % 2 == 0

                private val Int.isRight
                    get() = !isLeft
            })
        }
    }

    private fun initData() {
        val data = arrayListOf<Pair<String, String>>()
        data.apply {
            add(Pair("395年", "罗马帝国[前27年—395年(乙未年)]，正式名称为元老院与罗马人民，中国史书称为大秦、拂菻，是古罗马文明的一个阶段。"))
            add(Pair("1600年", "卡尔德隆·德·拉·巴尔卡（1600～1681）Caldern de la Barca，Pedro西班牙剧作家，诗人。"))
            add(Pair("1676年", "弗朗切斯科·卡瓦利Francesco Cavalli, （1602年生于克雷马；1676年卒于威尼斯）。"))
            add(Pair("1978年", "中国现代著名西洋文学家、国学大师、诗人。也是清华大学国学院创办人之一，学贯中西，融通古今，被称为中国比较文学之父。"))
            add(Pair("2002年", "非洲中东部维龙加山脉的活火山。在刚果（金） （全称：刚果民主共和国）靠近乌干达边境的维龙加国家公园的火山区内。"))
        }
        mTimeLineAdapter.setData(data)
    }

    override fun initEvent() {
        mBinding.btnChange.setOnClickListener {
            skinChange()
        }
        mBinding.btnReset.setOnClickListener {
            val manager = SkinManager.instance
            manager.resetSkin()
        }
    }

    @Permissions(Permission.MANAGE_EXTERNAL_STORAGE)
    private fun skinChange() {
        val manager = SkinManager.instance
        manager.changeSkin(lifecycle, this)
    }

    override fun onSkinChanged() {
        Timber.d("onSkinChanged：===> skin change...")
    }

    override fun onStartChangeSkin() {
        Timber.d("onStartChangeSkin：===> skin start change...")
    }

    override fun onChangeSkinError(e: Exception) {
        Timber.d("onChangeSkinError：===> skin error...")
    }

    override fun onChangeSkinComplete() {
        Timber.d("onChangeSkinComplete：===> skin complete...")
    }
}