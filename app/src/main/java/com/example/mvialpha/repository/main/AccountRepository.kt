package com.example.mvialpha.repository.main

import AccountPropertiesDao
import android.util.Log
import com.example.mvialpha.api.main.OpenApiMainService
import com.example.mvialpha.session.SessionManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class AccountRepository {
    @Inject
    constructor(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ){
        val TAG = "AccountRepository"

        var repositoryJob: Job? = null

        fun cancelActiveJobs(){
            Log.d(TAG, "cancelActiveJobs: ")
        }
    }
}