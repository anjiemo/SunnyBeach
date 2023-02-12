import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlin.system.measureTimeMillis

suspend fun loadFromCache(): Int {
    delay(1000)
    return 1
}

suspend fun loadFromNetwork(): Int {
    delay(2000L)
    return 2
}

fun main() = runBlocking {
    val deferredFromCache = async {
        loadFromCache()
    }
    val deferredFromNetwork = async {
        loadFromNetwork()
    }

    val result: String
    val timeConsume = measureTimeMillis {
        result = select {
            deferredFromCache.onAwait {
                "来自本地缓存的响应：$it"
            }
            deferredFromNetwork.onAwait {
                "来自服务端的响应：$it"
            }
        }
    }
    println("result is $result timeConsume is $timeConsume")
}