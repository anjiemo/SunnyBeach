package cn.cqautotest.sunnybeach.widget.tag

import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/01
 * desc   : 类型安全的适配器
 */
interface TagViewAdapter<T, VH : TagViewHolder> {

    fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH

    fun bindViewHolder(holder: VH, item: T, position: Int)

    /**
     * 将剩余数量转换为一个虚拟的 T 对象。
     * @param count 剩余未显示的标签数量
     * @param dataList 原始完整数据集
     * @param firstHiddenIndex 第一个被截断（隐藏）的元素索引
     * @return 转换后的虚拟对象，若返回 null 则会触发 [bindMoreViewHolder]
     */
    fun convertRemainingToItem(count: Int, dataList: List<T>, firstHiddenIndex: Int): T? = null

    /**
     * 默认实现，外部可按需重写（若 [convertRemainingToItem] 返回空，则调用此方法）
     */
    fun bindMoreViewHolder(holder: VH, remainingCount: Int) {
        // 可选实现
    }

    fun createMoreViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        // 提供默认实现，外部可按需重写
        return createViewHolder(inflater, parent)
    }
}