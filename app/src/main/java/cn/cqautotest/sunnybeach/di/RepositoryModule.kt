package cn.cqautotest.sunnybeach.di

import cn.cqautotest.sunnybeach.repository.VideoRepository
import cn.cqautotest.sunnybeach.repository.VideoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/23
 * desc   : Repository 模块
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVideoRepository(repositoryImpl: VideoRepositoryImpl): VideoRepository
}
