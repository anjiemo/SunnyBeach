package cn.cqautotest.sunnybeach.di

import android.content.Context
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    fun provideGson(): Gson = GsonUtils.getGson()

    @Provides
    fun provideApplication(@ApplicationContext context: Context): AppApplication = context.applicationContext as AppApplication

    @Provides
    @Singleton
    fun provideAppViewModel(): AppViewModel = AppViewModel.getAppViewModel()
}