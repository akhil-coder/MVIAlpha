package com.example.mvialpha.di.main

import com.example.mvialpha.ui.main.account.AccountFragment
import com.example.mvialpha.ui.main.account.ChangePasswordFragment
import com.example.mvialpha.ui.main.account.UpdateAccountFragment
import com.example.mvialpha.ui.main.blog.BlogFragment
import com.example.mvialpha.ui.main.blog.UpdateBlogFragment
import com.example.mvialpha.ui.main.blog.ViewBlogFragment
import com.example.mvialpha.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}