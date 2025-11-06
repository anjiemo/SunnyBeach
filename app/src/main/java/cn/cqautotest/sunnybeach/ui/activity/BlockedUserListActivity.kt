package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.BlockedUserListActivityBinding
import cn.cqautotest.sunnybeach.db.dao.UserBlock
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import coil3.compose.AsyncImage
import com.blankj.utilcode.util.TimeUtils
import dev.androidbroadcast.vbpd.viewBinding

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
            BlockedList(modifier = Modifier.fillMaxSize())
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
private fun BlockedList(modifier: Modifier = Modifier) {
    val userViewModel = viewModel<UserViewModel>()
    val currUserId by remember { mutableStateOf(UserManager.loadCurrUserId()) }
    val blockedUserList = remember { mutableStateListOf<UserBlock>() }

    LaunchedEffect(currUserId) {
        val userBlockList = userViewModel.getBlockListDetails(currUserId)
        blockedUserList.clear()
        blockedUserList.addAll(userBlockList)
    }

    LazyColumn(modifier = modifier) {
        itemsIndexed(blockedUserList, key = { _, item -> item.uuid }) { index, userBlock ->
            if (index != 0) {
                Spacer(modifier = Modifier.height(1.dp))
            }
            BlockedListItem(
                userBlock = userBlock,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
private fun BlockedListItem(userBlock: UserBlock, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(66.dp)
            .background(color = Color.White)
    ) {
        AsyncImage(
            model = "https://cdn.sunofbeaches.com/images/default_avatar.png",
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
            SobNickName(text = userBlock.targetUId)
            SobDesc(text = TimeUtils.getFriendlyTimeSpanByNow(userBlock.createdAt))
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