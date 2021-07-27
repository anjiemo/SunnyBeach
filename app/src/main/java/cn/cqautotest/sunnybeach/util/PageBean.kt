package cn.cqautotest.sunnybeach.util

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/27
 * desc   : 分页 bean 类
 */
class PageBean {

    // 上一页的页码
    private var prePage: Int = -1

    // 当前页的页码
    var currentPage: Int = 0

    // 下一页的页码
    private var nextPage: Int = currentPage + 1

    // 每页的大小，每页显示多少条
    var pageSize: Int = DEFAULT_PAGE_NUMBER

    // 总条数
    var count: Int = 0

    // 首页页码
    private val firstPage: Int = 0

    // 总页数，或最后一页
    var totalPage: Int = currentPage

    private var isLoading: Boolean = false

    /**
     * @Description: 获取首页页码
     * @author: anjiemo
     */
    fun getFirstPage() = firstPage

    /**
     * @Description: 上一页
     * @author: anjiemo
     */
    fun prePage() {
        prePage = currentPage - 1
        if (prePage < 0) {
            prePage = 0
        }
    }

    /**
     * @Description: 下一页
     * @author: anjiemo
     */
    fun nextPage() {
        currentPage += 1
    }

    /**
     * @Description: 重置分页数据
     * @author: anjiemo
     */
    fun resetPage() {
        prePage = -1
        currentPage = 0
        nextPage = currentPage + 1
        pageSize = DEFAULT_PAGE_NUMBER
        count = 0
        totalPage = currentPage
    }

    /**
     * @Description: 是否在加载中（true：是，false：否）
     * @author: anjiemo
     */
    fun isLoading(): Boolean = isLoading

    /**
     * @Description: 加载完成
     * @author: anjiemo
     */
    fun finishLoading() {
        isLoading = false
    }

    /**
     * @Description: 开始加载
     * @author: anjiemo
     */
    fun startLoading() {
        isLoading = true
    }

    companion object {
        const val DEFAULT_PAGE_NUMBER = 15
    }
}