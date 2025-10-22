package cn.cqautotest.sunnybeach.model

import java.util.UUID

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/07/09
 * desc   : 搜索过滤条件Item
 */
data class SearchFilterItem(val id: String = UUID.randomUUID().toString(), val text: String, var isChecked: Boolean = false)