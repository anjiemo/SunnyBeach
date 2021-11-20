package cn.cqautotest.sunnybeach.other

import android.webkit.WebSettings
import android.webkit.WebView

/**
 * 自适应阳光沙滩网页的文章显示效果
 */
class FitScreen(private val browserView: WebView) : Runnable {

    override fun run() {
        browserView.apply {
            doFit()
        }
    }

    private fun WebView.doFit() {
        settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            textZoom = 125
        }
        setInitialScale(144)
        evaluateJavascript(
            "var child=document.getElementById(\"header-container\");\n" +
                    "child.style.display=\"none\";\n", null
        )
        evaluateJavascript(
            "var child=document.getElementById(\"article-detail-left-part\");\n" +
                    "child.style.display=\"none\";\n", null
        )
        evaluateJavascript(
            "var child=document.getElementById(\"article-detail-right-part\");\n" +
                    "child.style.display=\"none\";\n", null
        )
        evaluateJavascript(
            "var child=document.getElementById(\"footer-container\");\n" +
                    "child.style.display=\"none\";\n", null
        )
        evaluateJavascript(
            "var child=document.getElementById('article-detail-center-part');\n" +
                    "child.style.margin='-20px 0 0 0';", null
        )
        evaluateJavascript(
            "var child=document.getElementById('main-content');\n" +
                    "child.style.width='750px';", null
        )
    }
}