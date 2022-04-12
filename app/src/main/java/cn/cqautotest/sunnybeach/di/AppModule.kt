package cn.cqautotest.sunnybeach.di

import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object AppModule {

    @Provides
    fun provideAppViewModel(): AppViewModel = AppApplication.getAppViewModel()

    @Provides
    fun provideGson(): Gson = GsonUtils.getGson()
}