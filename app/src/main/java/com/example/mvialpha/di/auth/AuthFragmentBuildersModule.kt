package com.example.mvialpha.di.auth

import com.example.mvialpha.ui.auth.ForgotPasswordFragment
import com.example.mvialpha.ui.auth.LauncherFragment
import com.example.mvialpha.ui.auth.LoginFragment
import com.example.mvialpha.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {
    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment
}