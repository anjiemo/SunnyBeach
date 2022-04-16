package cn.cqautotest.sunnybeach.di

import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object AppModule {

    @Provides
    fun provideAppViewModel(): AppViewModel = AppViewModel.getAppViewModel()
}