package com.aliyun.svideo.common.utils;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * 限制快速点击多次触发的工具类
 * <p>
 * 注意：如果第一次点击涉及到阻塞主线程/主线程耗时的情况则FastClickUtil的判断并不靠谱
 */
public class FastClickUtil {


    private static final String TAG = FastClickUtil.class.getSimpleName();
    /**
     * 两次点击间隔不能少于300ms
     */
    private static final int MIN_DELAY_TIME = 500;

    /**
     * activity两次点击间隔不能少于800ms
     */
    private static final int MIN_DELAY_TIME_ACTIVITY = 800;
    private static long sLastClickTime;
    private static String sLastActivitySimpleName;

    public static boolean isFastClick() {
        long currentClickTime = System.currentTimeMillis();
        boolean isFastClick = (currentClickTime - sLastClickTime) <= MIN_DELAY_TIME;
        Log.e(TAG, "log_common_FastClickUtil : " + (currentClickTime - sLastClickTime));
        sLastClickTime = currentClickTime;
        return isFastClick;
    }

    /**
     * fix连续点击弹出多个activity
     * 1.设置android:launchMode="singleTop"在这个场景下并不管用
     * 2.部分手机可能因为activity的阻塞耗时不同会导致计算的间隔超出500ms，这里定位800毫秒能处理绝大部分的手机和情况
     * 3.通过记录activitySimpleName，避免出现用户熟练连续进入多个页面的时候需要等待
     *
     * @param activitySimpleName Activity.class.getSimpleName()
     * @return boolean
     */
    public static boolean isFastClickActivity(@NonNull String activitySimpleName) {

        long currentClickTime = System.currentTimeMillis();
        boolean isFastClick = (currentClickTime - sLastClickTime) <= MIN_DELAY_TIME_ACTIVITY;
        sLastClickTime = currentClickTime;
        if (!activitySimpleName.equals(sLastActivitySimpleName)) {
            //如果两次的activity不是同一个，不是快速点击
            isFastClick = false;
            sLastActivitySimpleName = activitySimpleName;
        }
        return isFastClick;
    }

}
