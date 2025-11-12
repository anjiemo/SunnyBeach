package cn.cqautotest.sunnybeach.http.glide

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.RequestManager
import java.io.File

/**
 * The entry point for interacting with Glide for Applications
 *
 *
 * Includes all generated APIs from all
 * [com.bumptech.glide.annotation.GlideExtension]s in source and dependent libraries.
 *
 *
 * This class is generated and should not be modified
 * @see Glide
 */
object GlideApp {
    /**
     * @see Glide.getPhotoCacheDir
     */
    fun getPhotoCacheDir(context: Context): File? {
        return Glide.getPhotoCacheDir(context)
    }

    /**
     * @see Glide.getPhotoCacheDir
     */
    fun getPhotoCacheDir(context: Context, string: String): File? {
        return Glide.getPhotoCacheDir(context, string)
    }

    /**
     * @see Glide.get
     */
    operator fun get(context: Context): Glide {
        return Glide.get(context)
    }

    /**
     * @see Glide.init
     */
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    fun init(context: Context, builder: GlideBuilder) {
        Glide.init(context, builder)
    }

    /**
     * @see Glide.enableHardwareBitmaps
     */
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    fun enableHardwareBitmaps() {
        Glide.enableHardwareBitmaps()
    }

    /**
     * @see Glide.tearDown
     */
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    fun tearDown() {
        Glide.tearDown()
    }

    /**
     * @see Glide.with
     */
    fun with(context: Context): RequestManager {
        return Glide.with(context)
    }

    /**
     * @see Glide.with
     */
    fun with(activity: FragmentActivity): RequestManager {
        return Glide.with(activity)
    }

    /**
     * @see Glide.with
     */
    fun with(fragment: Fragment): RequestManager {
        return Glide.with(fragment)
    }

    /**
     * @see Glide.with
     */
    fun with(view: View): RequestManager {
        return Glide.with(view)
    }
}
