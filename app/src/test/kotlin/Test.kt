import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File

class Test {

    @Test
    fun test(): Unit = runBlocking(Dispatchers.IO) {

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