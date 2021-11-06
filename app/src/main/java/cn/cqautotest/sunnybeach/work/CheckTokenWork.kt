package cn.cqautotest.sunnybeach.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/29
 * desc   : Token 解析 Worker
 */
class CheckTokenWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Timber.d("checkToken...")
        try {
            val userBasicInfo = Repository.checkToken()
            if (userBasicInfo == null) {
                onError()
            } else {
                onSuccess()
            }
        } catch (t: Throwable) {
            if (t is ServiceException) {
                Result.retry()
            } else {
                onError()
            }
        }
    }

    private fun onSuccess(): Result {
        return Result.success()
    }

    private fun onError(): Result {
        return Result.failure()
    }
}