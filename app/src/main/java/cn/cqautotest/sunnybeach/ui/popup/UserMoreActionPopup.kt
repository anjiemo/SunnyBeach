package cn.cqautotest.sunnybeach.ui.popup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.cqautotest.sunnybeach.R

@Composable
fun UserMoreActionPopup(modifier: Modifier = Modifier, onClick: (clickType: ActionClickType) -> Unit) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionMenuItem(text = "举报") {
            onClick.invoke(ActionClickType.REPORT)
        }
        HorizontalDivider(color = colorResource(R.color.common_line_color))
        ActionMenuItem(text = "分享") {
            onClick.invoke(ActionClickType.SHARE)
        }
    }
}

@Composable
private fun ActionMenuItem(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, shape = ButtonDefaults.shape, colors = ButtonDefaults.buttonColors(Color.Transparent)) {
        Text(
            text = text,
            modifier = modifier,
            color = colorResource(R.color.default_font_color),
            fontSize = 14.sp
        )
    }
}

enum class ActionClickType {
    REPORT,
    SHARE
}