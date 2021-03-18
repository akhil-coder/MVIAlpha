package com.example.mvialpha.di.main

import androidx.lifecycle.ViewModel
import com.example.mvialpha.di.ViewModelKey
import com.example.mvialpha.ui.auth.AuthViewModel
import com.example.mvialpha.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(accountViewModel: AccountViewModel): ViewModel
}