package cn.cqautotest.sunnybeach.paging

import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import androidx.paging.*
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/9/6
 * desc   : PagingAdapter 包装类
 */
class PagingAdapterWrapper<T : Any, VH : RecyclerView.ViewHolder>(
    private val adapter: RecyclerView.Adapter<VH>,
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default,
    callback: (List<T>) -> Unit
) : RecyclerView.Adapter<VH>() {

    private var userSetRestorationPolicy = false

    override fun setStateRestorationPolicy(strategy: StateRestorationPolicy) {
        userSetRestorationPolicy = true
        super.setStateRestorationPolicy(strategy)
    }

    private val differ = AsyncPagingDataDiffer(
        diffCallback = diffCallback,
        updateCallback = AdapterListUpdateCallback(this),
        mainDispatcher = mainDispatcher,
        workerDispatcher = workerDispatcher
    )

    init {
        // Wait on state restoration until the first insert event.
        super.setStateRestorationPolicy(StateRestorationPolicy.PREVENT)

        fun considerAllowingStateRestoration() {
            if (stateRestorationPolicy == StateRestorationPolicy.PREVENT && !userSetRestorationPolicy) {
                this@PagingAdapterWrapper.stateRestorationPolicy = StateRestorationPolicy.ALLOW
            }
        }

        // Watch for adapter insert before triggering state restoration. This is almost redundant
        // with loadState below, but can handle cached case.
        @Suppress("LeakingThis")
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                considerAllowingStateRestoration()
                unregisterAdapterDataObserver(this)
                super.onItemRangeInserted(positionStart, itemCount)
            }
        })

        // Watch for loadState update before triggering state restoration. This is almost
        // redundant with data observer above, but can handle empty page case.
        addLoadStateListener(object : Function1<CombinedLoadStates, Unit> {
            // Ignore the first event we get, which is always the initial state, since we only
            // want to observe for Insert events.
            private var ignoreNextEvent = true

            override fun invoke(loadStates: CombinedLoadStates) {
                if (ignoreNextEvent) {
                    ignoreNextEvent = false
                } else if (loadStates.source.refresh is LoadState.NotLoading) {
                    considerAllowingStateRestoration()
                    removeLoadStateListener(this)
                }
            }
        })
        addLoadStateListener {
            if (it.append is LoadState.NotLoading) {
                val items = differ.snapshot().items
                callback.invoke(items)
            }
        }
    }

    final override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    final override fun setHasStableIds(hasStableIds: Boolean) {
        throw UnsupportedOperationException("Stable ids are unsupported on PagingDataAdapter.")
    }

    suspend fun submitData(pagingData: PagingData<T>) {
        differ.submitData(pagingData)
    }

    fun submitData(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        differ.submitData(lifecycle, pagingData)
    }

    fun retry() {
        differ.retry()
    }

    fun refresh() {
        differ.refresh()
    }

    fun getItem(@IntRange(from = 0) position: Int) = differ.getItem(position)

    fun peek(@IntRange(from = 0) index: Int) = differ.peek(index)

    fun snapshot(): ItemSnapshotList<T> = differ.snapshot()

    val loadStateFlow: Flow<CombinedLoadStates> = differ.loadStateFlow

    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.addLoadStateListener(listener)
    }

    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.removeLoadStateListener(listener)
    }

    fun withLoadStateHeader(
        header: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
        }
        return ConcatAdapter(header, this)
    }

    fun withLoadStateFooter(
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            footer.loadState = loadStates.append
        }
        return ConcatAdapter(this, footer)
    }

    fun withLoadStateHeaderAndFooter(
        header: LoadStateAdapter<*>,
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
            footer.loadState = loadStates.append
        }
        return ConcatAdapter(header, this, footer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return adapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        differ.getItem(position)
        adapter.onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        return adapter.itemCount
    }
}