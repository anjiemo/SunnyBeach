package cn.cqautotest.sunnybeach.ui.popup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import timber.log.Timber

@Composable
fun UserMoreActionPopup(
    currUserId: String,
    targetUId: String,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    onClick: (clickType: ActionClickType) -> Unit
) {
    var isBlocked by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currUserId, targetUId) {
        isLoading = true
        isBlocked = runCatching {
            userViewModel.isUserBlocked(uId = currUserId, targetUId = targetUId)
        }.fold(
            onSuccess = { it },
            onFailure = {
                Timber.e(it)
                false
            }
        )
        isLoading = false
    }

    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currUserId != targetUId) {
            ActionMenuItem(text = "举报", modifier = Modifier.fillMaxWidth()) {
                onClick.invoke(ActionClickType.REPORT)
            }
            HorizontalDivider(color = colorResource(R.color.common_line_color))
            if (isLoading) {
                ActionMenuItem(text = "加载中...", modifier = Modifier.fillMaxWidth())
            } else {
                ActionMenuItem(text = if (isBlocked) "取消屏蔽" else "屏蔽", modifier = Modifier.fillMaxWidth()) {
                    onClick.invoke(ActionClickType.BLOCK)
                }
            }
            HorizontalDivider(color = colorResource(R.color.common_line_color))
        }
        ActionMenuItem(text = "分享", modifier = Modifier.fillMaxWidth()) {
            onClick.invoke(ActionClickType.SHARE)
        }
    }
}

@Composable
private fun ActionMenuItem(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = ButtonDefaults.shape,
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        Text(
            text = text,
            color = colorResource(R.color.default_font_color),
            fontSize = 14.sp
        )
    }
}

enum class ActionClickType {
    REPORT,
    BLOCK,
    SHARE
}