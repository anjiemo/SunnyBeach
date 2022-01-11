package cn.cqautotest.sunnybeach.util

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.action.StatusAction

/**
 * Take over the action of StatusAction, returns CombinedLoadStates instance,then execute the given lambda expression.
 */
inline fun <T : Any, reified VH : RecyclerView.ViewHolder> StatusAction.loadStateListener(
    pagingAdapter: PagingDataAdapter<T, VH>, crossinline block: () -> Unit
): (CombinedLoadStates) -> Unit = { cls ->
    when (cls.refresh) {
        is LoadState.NotLoading -> {
            block.invoke()
            if (pagingAdapter.isEmpty()) {
                showEmpty()
            } else {
                showComplete()
            }
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
