import cn.cqautotest.sunnybeach.model.UserArticle
import com.hjq.gson.factory.GsonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import org.junit.Test
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Test {

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {
        val userId = ""
        val page = 1
        val sobToken = ""
        val headersTemplate = File("config", "headers.config").readText()
        val headers = headersTemplate.replace("{sob_token}", sobToken)
            .split("\r\n").associate { Pair(it.split(":")[0], it.split(":")[1]) }
            .toHeaders()

        println("test：===> headers is $headers")
        val userArticleList = arrayListOf<UserArticle.UserArticleItem>()
        var hasNext = true
        while (hasNext) {
            val result = paging(userId, page, headers, userArticleList)
            hasNext = result.getOrNull()?.hasNext ?: false
        }
        println("test：===> userArticleList size is ${userArticleList.size}")
    }

    private suspend fun paging(
        userId: String,
        page: Int,
        headers: Headers,
        totalUserArticleList: MutableList<UserArticle.UserArticleItem>
    ): Result<UserArticle> {
        val urlTemplate = File("config", "url.config").readText()
        val url = urlTemplate.replace("{userId}", userId).replace("{page}", page.toString())
        val result: Result<UserArticle> = request(url, headers, UserArticle::class.java)
        val userArticlePage = result.getOrNull()
        println("test：===> result is $userArticlePage")
        val userArticleList = userArticlePage?.list ?: emptyList()
        totalUserArticleList.addAll(userArticleList)
        val hasNext = userArticlePage?.hasNext ?: false
        val currentPage = userArticlePage?.currentPage ?: 1
        println("test：===> hasNext is $hasNext currentPage is $currentPage")
        return result
    }

    private val client = OkHttpClient()

    private suspend fun request(url: String, headers: Headers, clazz: Class<UserArticle>) = suspendCoroutine<Result<UserArticle>> { con ->
        println("request：===> url is $url")
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                con.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string() ?: ""
                println("request：===> result is $result")
                try {
                    val resultBean = GsonFactory.getSingletonGson().fromJson(result, clazz)
                    con.resume(Result.success(resultBean))
                } catch (e: Exception) {
                    e.printStackTrace()
                    con.resumeWithException(e)
                }
            }
        })
    }

    @Test
    fun listFiles() {
        val file = File("/Users/anjiemo/Pictures/阳光沙滩")
        val fileList = file.listFiles() ?: run {
            println("listFiles：===> 路径无效...")
            return
        }
        println("listFiles：===> fileList size is ${fileList.size}")
        val str = buildString {
            append("\n")
            append("========= 分割线 =========")
            append("\n")
            fileList.asSequence()
                .filterNot { it.name.contains(".DS_Store") }
                .sortedBy { it.lastModified() }
                .forEach {
                    append("\n")
                    append("![](")
                    append("./picture/sunnybeach/")
                    append(it.name)
                    append(")")
                }
            append("\n")
            append("========= 分割线 =========")
        }
        println("listFiles：===> $str")
    }
}