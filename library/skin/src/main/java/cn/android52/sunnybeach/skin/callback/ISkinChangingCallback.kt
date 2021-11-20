package cn.android52.sunnybeach.skin.callback

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 换肤回调
 */
interface ISkinChangingCallback {

    /**
     * 开始更换皮肤
     */
    fun onStartChangeSkin()

    /**
     * 更换皮肤错误
     */
    fun onChangeSkinError(e: Exception)

    /**
     * 更换皮肤完成
     */
    fun onChangeSkinComplete()

    /**
     * 默认的换肤回调
     */
    class DefaultSkinChangingCallbackImpl : ISkinChangingCallback {

        override fun onStartChangeSkin() {}

        override fun onChangeSkinError(e: Exception) {}

        override fun onChangeSkinComplete() {}
    }
}