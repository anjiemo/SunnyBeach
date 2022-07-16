import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.ktx.fromJsonByTypeToken
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.ArticleDetail
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.other.AppConfig
import com.blankj.utilcode.util.FileUtils
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

    private val userId = ""
    private val sobToken by lazy { File("config", "sob_token.config").readText() }
    private val userArticleListFile by lazy { File("config", "user_article_list.json") }
    private val userArticleDetailUrlTemplate = "https://api.sunofbeaches.com/ct/ucenter/article/{articleId}"
    private val userArticleUpdateUrlTemplate = "https://api.sunofbeaches.com/ct/ucenter/article/{articleId}"

    /**
     * 查找项目生成的 apk 文件或返回 null.
     */
    private fun findApkFileOrNull(): File? {
        val methodTag = "findApkFileOrNull"
        val projectDirPath = System.getProperty("user.dir")
        println("$methodTag：===> projectDirPath is $projectDirPath")
        val releaseDir = File(projectDirPath, "/release")
        val apks = releaseDir.listFiles { _, name -> name.endsWith(".apk") } ?: return null
        return apks.maxByOrNull { it.lastModified() }
    }

    /**
     * 打印 apk 文件的 md5 值（小写）
     */
    @Test
    fun printApkMd5() {
        val methodTag = "printApkMd5"
        val apkFile = findApkFileOrNull() ?: return println("未在项目目录中获取到 apk 文件，请先生成")
        val fileMd5 = FileUtils.getFileMD5ToString(apkFile).lowercase()
        println("$methodTag：===> fileMd5 is $fileMd5")
    }

    /**
     * 打印 apk 文件的大小
     */
    @Test
    fun printApkSize() {
        val methodTag = "printApkMd5"
        val apkFile = findApkFileOrNull() ?: return println("未在项目目录中获取到 apk 文件，请先生成")
        val apkSize = apkFile.length()
        println("$methodTag：===> apkSize is $apkSize")
    }

    /**
     * 打印 APP 配置信息
     */
    @Test
    fun printAppConfig() {
        val methodTag = "printAppConfig"
        val apkFile = findApkFileOrNull() ?: return
        val appVersionName = AppConfig.getVersionName()
        val appVersionCode = AppConfig.getVersionCode()
        val apkSize = apkFile.length()
        val apkHash = FileUtils.getFileMD5ToString(apkFile).lowercase()
        val appConfig = mapOf("versionName" to appVersionName, "versionCode" to appVersionCode, "apkSize" to apkSize, "apkHash" to apkHash)
        println("$methodTag：===> appConfig is ${appConfig.toJson()}")
    }

    /**
     * 批量替换文章图片链接
     * 一个简化版本的例子
     */
    @Test
    fun batchReplaceArticleImageLinks(): Unit = runBlocking(Dispatchers.IO) {
        // 找到所有的文章
        // 过滤出需要替换链接的文章
        // 替换文章里的链接
        // 保存文章到本地
        // 更新文章到服务器
        val keywords = loadKeywords()
        loadArticleList()
            .filter { it.contains("http://") }
            .map { it.replaceKeyword(keywords) }
            .onEach { saveArticle(it) }
            .forEach { updateArticle(it) }
    }

    private suspend fun updateArticle(article: String) {
        println("updateArticle：===> article $article is update...")
    }

    private fun saveArticle(article: String) {
        println("saveArticle：===> article $article is save...")
    }

    private fun String.replaceKeyword(keywordsMap: List<Pair<String, String>>): String {
        return keywordsMap.fold(this) { acc, (keyword, replace) ->
            acc.replace(keyword, replace)
        }
    }

    private fun loadKeywords() = arrayListOf<Pair<String, String>>().apply {
        // oldKeyword, newKeyword
        add(Pair("baidu.com", "52android.cn"))
    }.toList()

    private fun loadArticleList() = arrayListOf<String>().apply {
        add("张三")
        add("李四")
        add("王五")
        add("赵六")
        repeat(10) {
            add("http://www.baidu.com?id=$it")
        }
    }.toList()

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

    private fun listRevisedArticleFile(): Array<out File> {
        val articleListDir = File("revised_article")
        return articleListDir.listFiles() ?: emptyArray()
    }

    @Test
    fun modifyImgUrl() {
        val modifyArticleDir = File("revised_article").apply {
            delete()
            mkdirs()
        }
        val linkPre = ""
        val imageList: List<Pair<String, String>> = fromJsonByTypeToken(File("img", "imageMap.json").readText())
        val listArticleFile = listArticleFile()
        for (file in listArticleFile) {
            val content = file.readText()
            if (content.contains(linkPre)) {
                var newContent = content
                for ((fileName, url) in imageList) {
                    val oldValue = linkPre + fileName
                    println("modifyImgUrl：===> oldValue is $oldValue newValue is $url")
                    newContent = newContent.replace(oldValue, url)
                }
                // println("modifyImgUrl：===> newContent is $newContent")
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
                val needModify = content.contains("")
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
        listRevisedArticleFile().forEach { file ->
            val content = file.readText()
            val articleId = file.name.removeSuffix(".md")
            println("updateUserArticleList：===> articleId is $articleId")
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

    @Test
    fun uploadLocalImage(): Unit = runBlocking(Dispatchers.IO) {
        val imageDirPath = ""
        val imageServiceUrl = ""
        val imageMapFile = File(File("img").apply { mkdir() }.path, "imageMap.json")
        val imageList = arrayListOf<Pair<String, String>>()

        val copyFileDir = File("copyFile").apply {
            mkdir()
            listFiles()?.forEach { it.delete() }
        }

        File(imageDirPath).listFiles()?.forEach { file ->
            val targetFile = File(
                copyFileDir.path, file.name
                    .replace(".jpg", ".png")
                    .replace(".gif", ".png")
                    .replace(".jpeg", ".png")
            )

            println("uploadLocalImage：===> target image file name is ${targetFile.name})")

            val newFile = file.copyTo(targetFile)
            request<String>(imageServiceUrl) {
                val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", newFile.path, RequestBody.create("image/png".toMediaTypeOrNull(), newFile))
                    .build()
                this.method("POST", requestBody)
                this
            }.getOrNull()?.let {
                imageList.add(Pair(file.name, it))
                println("test：===> result is $it")
            }
        }
        imageMapFile.writeText(imageList.toJson())
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
                    val result = response.body?.string().orEmpty()
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