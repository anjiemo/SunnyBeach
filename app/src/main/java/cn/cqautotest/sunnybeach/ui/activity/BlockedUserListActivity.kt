package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.BlockedUserListActivityBinding
import cn.cqautotest.sunnybeach.db.dao.UserBlock
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserInfoStatus
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import coil3.compose.AsyncImage
import com.blankj.utilcode.util.TimeUtils
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.cancel

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/11/06
 * desc   : 被屏蔽的用户列表界面
 */
class BlockedUserListActivity : AppActivity() {

    private val mBinding by viewBinding(BlockedUserListActivityBinding::bind)

    override fun getLayoutId(): Int = R.layout.blocked_user_list_activity

    override fun initView() {
        mBinding.composeView.setContent {
            val context = LocalContext.current
            BlockedList(onItemClick = { userBlock ->
                ViewUserActivity.start(context, userId = userBlock.targetUId)
            }, modifier = Modifier.fillMaxSize())
        }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    companion object {

        fun start(context: Context) {
            context.startActivity<BlockedUserListActivity>()
        }
    }
}

@Composable
private fun BlockedList(onItemClick: (userBlock: UserBlock) -> Unit, modifier: Modifier = Modifier) {
    val userViewModel = viewModel<UserViewModel>()
    val currUserId by remember { mutableStateOf(UserManager.loadCurrUserId()) }

    // 收集黑名单 Flow（自动响应数据变化，包括取消拉黑后）
    val blockedUserList by userViewModel
        .getBlockListDetailsByFlow(currUserId) // 直接监听 Flow
        .collectAsState(initial = emptyList())

    // 缓存用户信息：key=userId，value=用户信息状态
    val userInfoCache = remember { mutableStateMapOf<String, UserInfoStatus>() }

    // 当黑名单变化时，清理已移除用户的缓存（优化：避免缓存无效数据）
    LaunchedEffect(currUserId) {
        val currentUserIds = blockedUserList.map { it.targetUId }.toSet()
        userInfoCache.keys.retainAll(currentUserIds) // 只保留当前列表中的用户缓存
    }

    // 按需加载用户信息（已缓存则跳过，未缓存则请求）
    LazyColumn(modifier = modifier) {
        itemsIndexed(blockedUserList, key = { _, item -> item.uuid }) { index, userBlock ->
            if (index != 0) {
                Spacer(modifier = Modifier.height(1.dp))
            }

            // 获取当前被拉黑的用户ID
            val blockedUserId = userBlock.targetUId

            LaunchedEffect(blockedUserId) {
                if (!userInfoCache.containsKey(blockedUserId)) {
                    // 初始标记为加载中
                    userInfoCache[blockedUserId] = UserInfoStatus.Loading

                    userViewModel.getUserInfo(blockedUserId).asFlow()
                        .collect { result ->
                            val status = when {
                                result.isSuccess -> {
                                    val info = result.getOrNull()
                                    if (info != null) UserInfoStatus.Success(info)
                                    else UserInfoStatus.Error(NullPointerException("User info is null"))
                                }

                                result.isFailure -> UserInfoStatus.Error(result.exceptionOrNull())
                                else -> return@collect // 忽略非终态（如中间状态）
                            }
                            // 更新缓存
                            userInfoCache[blockedUserId] = status

                            // 已拿到终态，可取消收集（避免后续不必要的更新）
                            this.coroutineContext.cancel()
                        }
                }
            }

            // 获取当前用户的信息状态
            val userInfoStatus = userInfoCache[blockedUserId]

            BlockedListItem(
                userBlock = userBlock,
                userInfoStatus = userInfoStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable(onClick = { onItemClick.invoke(userBlock) })
            )
        }
    }
}

@Composable
private fun BlockedListItem(
    userBlock: UserBlock,
    userInfoStatus: UserInfoStatus?,
    modifier: Modifier = Modifier
) {
    when (userInfoStatus) {
        is UserInfoStatus.Loading, null -> {
            Row(
                modifier = modifier
                    .height(66.dp)
                    .background(color = Color.White)
            ) {
                AsyncImage(
                    model = R.mipmap.ic_default_avatar,
                    contentDescription = "用户头像",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    SobNickName(text = "加载中...")
                    SobDesc(text = "添加时间：${TimeUtils.millis2String(userBlock.createdAt)}")
                }
            }
        }

        is UserInfoStatus.Success -> {
            val userInfo = userInfoStatus.info
            Row(
                modifier = modifier
                    .height(66.dp)
                    .background(color = Color.White)
            ) {
                AsyncImage(
                    model = userInfo.avatar,
                    contentDescription = "用户头像",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    SobNickName(text = userInfo.nickname)
                    SobDesc(text = "添加时间：${TimeUtils.millis2String(userBlock.createdAt)}")
                }
            }
        }

        is UserInfoStatus.Error -> {
            Row(
                modifier = modifier
                    .height(66.dp)
                    .background(color = Color.White)
            ) {
                AsyncImage(
                    model = R.mipmap.ic_default_avatar,
                    contentDescription = "用户头像",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    SobNickName(text = "加载错误")
                    SobDesc(text = "添加时间：${TimeUtils.millis2String(userBlock.createdAt)}")
                }
            }
        }
    }
}

@Composable
private fun SobNickName(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = colorResource(R.color.black),
        fontSize = 16.sp
    )
}

@Composable
private fun SobDesc(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = colorResource(R.color.default_font_color),
        fontSize = 12.sp
    )
}