package com.example.mvialpha.di.auth

import androidx.lifecycle.ViewModel
import com.example.mvialpha.di.ViewModelKey
import com.example.mvialpha.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule{

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}