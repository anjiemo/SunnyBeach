package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater

fun Context.asInflater(): LayoutInflater = LayoutInflater.from(this)

/**
 * simple：
 *
 * startActivity(HomeActivity::class.java)
 *
 * or
 *
 * startActivity(HomeActivity::class.java) {
 *
 * }
 */
fun <T : Activity> Context.startActivity(clazz: Class<T>, block: (Intent.() -> Unit)? = null) {
    startActivity(createIntent(this, clazz).apply { block?.invoke(this) })
}

/**
 * simple：
 *
 * startActivity<HomeActivity>()
 *
 * or
 *
 * startActivity<HomeActivity> {
 *
 * }
 */
inline fun <reified T : Activity> Context.startActivity(noinline block: (Intent.() -> Unit)? = null) {
    startActivity(createIntent(this, T::class.java).apply { block?.invoke(this) })
}

/**
 * simple：
 *
 * startActivity(createIntent(this, clazz))
 */
fun Context.createIntent(ctx: Context, clazz: Class<*>) = Intent(ctx, clazz).also {
    if (this !is Activity) it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
