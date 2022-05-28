package cn.cqautotest.sunnybeach.ktx

import android.view.View
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.action.StatusAction

fun interface OnItemClickListener {
    fun onItemClick(view: View, position: Int)
}

fun interface OnItemLongClickListener {
    fun onItemLongClick(view: View, position: Int): Boolean
}

/**
 * Take over the action of StatusAction, returns CombinedLoadStates instance,then execute the given lambda expression.
 */
inline fun <T : Any, reified VH : RecyclerView.ViewHolder> StatusAction.loadStateListener(
    pagingAdapter: PagingDataAdapter<T, VH>, crossinline block: () -> Unit
): (CombinedLoadStates) -> Unit = { cls ->
    when (cls.refresh) {
        is LoadState.NotLoading -> {
            block.invoke()
            takeIf { pagingAdapter.isEmpty() }?.let { showEmpty() } ?: showComplete()
        }
        is LoadState.Loading -> showLoading()
        is LoadState.Error -> showError { pagingAdapter.refresh() }
    }
}

/**
 * Returns `true` if this adapter content is not empty.
 */
inline fun <reified VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.isNotEmpty() =
    itemCount != 0

/**
 * Returns `true` if this adapter content is empty.
 */
inline fun <reified VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.isEmpty() =
    itemCount == 0
