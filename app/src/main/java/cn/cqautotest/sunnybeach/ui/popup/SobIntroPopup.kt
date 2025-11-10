package cn.cqautotest.sunnybeach.ui.popup

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import cn.cqautotest.sunnybeach.R

@Composable
fun SobIntroPopup(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = colorResource(R.color.white),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
    ) {
        // 绘制顶部指示器
        Canvas(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .width(50.dp)
                .height(5.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            drawRoundRect(
                color = Color("#F8F5F5".toColorInt()),
                cornerRadius = CornerRadius(10f, 10f)
            )
        }

        // 绘制内容区域（标题+内容列表）
        val sections = remember {
            listOf(
                IntroSection(
                    titleRes = R.string.sob_intro_consume_method,
                    contentArrayRes = R.array.sob_intro_consume_channels
                ),
                IntroSection(
                    titleRes = R.string.sob_intro_obtain_method,
                    contentArrayRes = R.array.sob_intro_obtain_channels
                )
            )
        }
        val nestedScrollInterop = rememberNestedScrollInteropConnection()
        LazyColumn(modifier = Modifier.nestedScroll(nestedScrollInterop)) {
            itemsIndexed(sections, key = { index, _ -> "section_${index}" }) { index, item ->
                IntroSectionContent(
                    section = item,
                    isLastSection = index == sections.lastIndex,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun IntroSectionContent(section: IntroSection, isLastSection: Boolean, modifier: Modifier = Modifier) {
    val titleText = stringResource(section.titleRes)
    val contentItems = stringArrayResource(section.contentArrayRes)

    Column(modifier = modifier) {
        SobIntroTitle(text = titleText)
        Spacer(modifier = Modifier.height(10.dp))

        contentItems.forEachIndexed { index, text ->
            SobIntroContent(
                text = text,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            if (index != contentItems.lastIndex) {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        if (!isLastSection) {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SobIntroTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = colorResource(R.color.black),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SobIntroContent(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = colorResource(R.color.default_font_color),
        fontSize = 14.sp
    )
}

private data class IntroSection(
    @field:StringRes val titleRes: Int,
    @field:ArrayRes val contentArrayRes: Int
)