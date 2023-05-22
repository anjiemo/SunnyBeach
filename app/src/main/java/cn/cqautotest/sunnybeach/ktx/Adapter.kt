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
fun <T : Any, VH : RecyclerView.ViewHolder> StatusAction.loadStateListener(
    pagingAdapter: PagingDataAdapter<T, VH>, block: () -> Unit
): (CombinedLoadStates) -> Unit = { cls ->
    when (cls.refresh) {
        is LoadState.NotLoading -> {
            block.invoke()
            if (pagingAdapter.isEmpty()) showEmpty() else showComplete()
        }
        is LoadState.Loading -> showLoading()
        is LoadState.Error -> showError { pagingAdapter.refresh() }
    }
}

/**
 * Returns the index of the last item in the list or -1 if the list is empty.
 */
val <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.lastIndex
    get() = itemCount - 1

/**
 * Returns `true` if this adapter content is not empty.
 */
fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.isNotEmpty() =
    itemCount != 0

/**
 * Returns `true` if this adapter content is empty.
 */
fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.isEmpty() =
    itemCount == 0
