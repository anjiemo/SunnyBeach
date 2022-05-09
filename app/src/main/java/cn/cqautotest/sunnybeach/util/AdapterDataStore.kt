package cn.cqautotest.sunnybeach.util

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/10
 * desc   : 适配器数据存储
 */
class AdapterDataStore<E> {

    private val mData = arrayListOf<E>()

    fun submitData(data: List<E>, action: () -> Unit) {
        mData.clear()
        mData.addAll(data)
        action.invoke()
    }

    fun getItem(index: Int) = mData[index]

    fun getItemOrNull(index: Int) = mData.getOrNull(index)

    fun getItemCount() = mData.size
}