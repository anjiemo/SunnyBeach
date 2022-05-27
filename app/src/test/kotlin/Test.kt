import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.ArticleDetail
import cn.cqautotest.sunnybeach.model.UserArticle
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.junit.Test
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Test {

    private val userId = "1204736502274318336"
    private val sobToken by lazy { File("config", "sob_token.config").readText() }
    private val userArticleListFile by lazy { File("config", "user_article_list.json") }
    private val userArticleDetailUrlTemplate = "https://api.sunofbeaches.com/ct/article/detail/{articleId}"
    private val userArticleUpdateUrlTemplate = "https://api.sunofbeaches.com/ct/ucenter/article/{articleId}"

    @Test
    fun createConfigFile() {
        println("createConfigFile：===> pre create new config file...")
        File("config", "sob_token.config").apply { takeUnless { exists() }?.createNewFile() }
        File("config", "url.config").apply { takeUnless { exists() }?.createNewFile() }
        println("createConfigFile：===> create new config file end...")
    }

    @Test
    fun writeUserArticleListToFile(): Unit = runBlocking(Dispatchers.IO) {
        val userArticleList = arrayListOf<UserArticle.UserArticleItem>()
        var currentPage = 1
        var hasNext = true
        while (hasNext) {
            val result = paging(currentPage, userArticleList)
            hasNext = result.getOrNull()?.hasNext ?: false
            currentPage++
        }
        println("test：===> userArticleList size is ${userArticleList.size}")
        val json = GsonBuilder().setPrettyPrinting().create().toJson(userArticleList)
        userArticleListFile.writeText(json)
        println("test：===> write userArticleList to file end...")
    }

    @Test
    fun downloadUserArticleList(): Unit = runBlocking(Dispatchers.IO) {
        File("article").apply { takeUnless { exists() }?.mkdir() }
        var count = 0
        val userArticleList = loadUserArticleList()
        userArticleList.forEach {
            val url = userArticleDetailUrlTemplate.replace("{articleId}", it.id)
            val result = request<ArticleDetail>(url)
            println("test：===> result is $result")
            result.getOrNull()?.let { articleDetail ->
                File("article", "${articleDetail.id}.md").writeText(articleDetail.content)
                count++
            }
        }
        println("test：===> transform end, userArticleList size is ${userArticleList.size} total $count")
    }

    private fun loadUserArticleList(): List<UserArticle.UserArticleItem> =
        GsonFactory.getSingletonGson()
            .fromJson(userArticleListFile.readText(), object : TypeToken<List<UserArticle.UserArticleItem>>() {}.type)

    private fun listArticleFile(): Array<out File> {
        val articleListDir = File("article")
        return articleListDir.listFiles() ?: emptyArray()
    }

    @Test
    fun modifyImgUrl() {
        val modifyArticleDir = File("revised_article").apply { mkdirs() }
        val linkPre = "https://gitee.com/anjiemo/figure-bed/raw/master/img/"
        val newLinkPre = "http://blog.52android.cn/blog/imgs/"
        listArticleFile().forEach { file ->
            val content = file.readText()
            takeIf { content.contains(linkPre) }?.let {
                val newContent = content.replace(linkPre, newLinkPre)
                    .replace("<p>", "\n")
                    .replace("</p>", "\n")
                File(modifyArticleDir.path, file.name).writeText(newContent)
            }
        }
    }

    @Test
    fun findNeedModifyUserArticleAndPrint(): Unit = runBlocking(Dispatchers.IO) {
        val userArticleList = loadUserArticleList()
        listArticleFile().forEach { file ->
            val content = file.readText()
            val articleId = file.name.removeSuffix(".md")
            userArticleList.find { it.id == articleId }?.let { userArticle ->
                val needModify = content.contains("https://gitee.com/anjiemo/figure-bed/raw/master/img/")
                if (needModify) {
                    takeIf { needModify }?.let {
                        println("test：===> need modify articleId is $articleId, userArticle is ${userArticle.title}")
                    }
                }
            }
        }
    }

    @Test
    fun updateUserArticleList(): Unit = runBlocking(Dispatchers.IO) {
        println("updateUserArticleList：===> update start...")
        val headerArr = File("config", "headers.config")
            .readText()
            .split("\n")
            .map { Pair(it.split(":")[0], it.split(":")[1]) }
        listArticleFile().forEach { file ->
            val content = file.readText()
            val articleId = file.name.removeSuffix(".md")
            val url = userArticleUpdateUrlTemplate.replace("{articleId}", articleId)
            request<HashMap<String, Any>>(userArticleDetailUrlTemplate.replace("{articleId}", articleId)).getOrNull()?.let { jsonMap ->
                val result = request<Any>(url) {
                    jsonMap["content"] = content
                    val newContent = jsonMap.toJson()
                    val mediaType = "application/json".toMediaTypeOrNull()
                    val requestBody = RequestBody.create(mediaType, newContent)
                    println("updateUserArticleList：===> requestBody is ${requestBody.toJson()}")
                    headerArr.forEach { addHeader(it.first, it.second) }
                    this.method("PUT", requestBody)
                    this
                }
                println("test：===> result is ${result.getOrNull()}")
            }
        }
        println("updateUserArticleList：===> update end...")
    }

    private suspend fun paging(
        page: Int,
        totalUserArticleList: MutableList<UserArticle.UserArticleItem>
    ): Result<UserArticle> {
        val urlTemplate = File("config", "url.config").readText()
        val url = urlTemplate.replace("{userId}", userId).replace("{page}", page.toString())
        val result: Result<UserArticle> = request(url)
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

    private suspend inline fun <reified T> request(url: String, crossinline build: (Request.Builder.() -> Request.Builder) = { this }) =
        suspendCoroutine<Result<T>> { con ->
            println("request：===> url is $url")
            val request = Request.Builder()
                .header("sob_token", sobToken)
                .run(build)
                .url(url)
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
                        val apiResponse: ApiResponse<T> =
                            GsonFactory.getSingletonGson().fromJson(result, object : TypeToken<ApiResponse<T>>() {}.type)
                        if (apiResponse.isSuccess()) {
                            con.resume(Result.success(apiResponse.getData()))
                        } else {
                            con.resumeWithException(ServiceException("apiResponse is not success"))
                        }
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