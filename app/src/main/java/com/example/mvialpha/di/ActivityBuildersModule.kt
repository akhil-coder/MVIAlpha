package com.example.mvialpha.di

import com.example.mvialpha.di.auth.AuthFragmentBuildersModule
import com.example.mvialpha.di.auth.AuthModule
import com.example.mvialpha.di.auth.AuthScope
import com.example.mvialpha.di.auth.AuthViewModelModule
import com.example.mvialpha.di.main.MainFragmentBuildersModule
import com.example.mvialpha.di.main.MainModule
import com.example.mvialpha.di.main.MainScope
import com.example.mvialpha.di.main.MainViewModelModule
import com.example.mvialpha.ui.auth.AuthActivity
import com.example.mvialpha.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}