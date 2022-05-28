package cn.cqautotest.sunnybeach.ktx

import androidx.paging.ItemSnapshotList
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Returns a new [ItemSnapshotList] representing the currently presented items, including any
 * placeholders if they are enabled.
 */
inline val <T : Any, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.snapshotList: ItemSnapshotList<T>
    get() = snapshot()
