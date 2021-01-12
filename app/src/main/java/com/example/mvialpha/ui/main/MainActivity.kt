package com.example.mvialpha.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.lifecycle.Observer
import com.example.mvialpha.R
import com.example.mvialpha.ui.BaseActivity
import com.example.mvialpha.ui.auth.AuthActivity

class MainActivity : BaseActivity(){
    private  val TAG = "MainActivity"

    override fun displayProgressBar(bool: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
    }

    fun subscribeObservers(){
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "subscribeObservers: AuthToken: ${authToken} ")
            if(authToken == null || authToken.account_pk == -1 || authToken.token == null){
                navAuthActivity()
                finish()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}