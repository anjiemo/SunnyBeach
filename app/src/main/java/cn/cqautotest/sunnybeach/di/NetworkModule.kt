package cn.cqautotest.sunnybeach.di

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.sob.AliyunVodApi
import cn.cqautotest.sunnybeach.http.api.sob.CourseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/24
 * desc   : 网络模块
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCourseApi(): CourseApi = ServiceCreator.create()

    @Provides
    @Singleton
    fun provideAliyunVodApi(): AliyunVodApi = ServiceCreator.create()
}
