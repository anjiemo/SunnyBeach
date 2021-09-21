package cn.cqautotest.sunnybeach.other

import android.webkit.JavascriptInterface
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.util.logByDebug
import java.io.*

class DemoJsApi {

    companion object {
        private const val TAG = "DemoJsApi"
    }

    @JavascriptInterface
    fun printPageSource(html: String?) {
        logByDebug(msg = "printPageSource：===> pageSource is $html")
        saveHtml(html!!)
    }

    private fun saveHtml(html: String) {
        val f = File("data/data/${AppApplication.getInstance().packageName}/result.html")
        if (!f.exists()) {
            f.createNewFile()
        }
        val fos = FileOutputStream(f)
        val input = OutputStreamWriter(BufferedOutputStream(fos))
        val bfReader = BufferedWriter(input)
        bfReader.write(html)
        bfReader.flush()
        bfReader.close()
    }
}