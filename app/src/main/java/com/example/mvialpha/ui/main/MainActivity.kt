package com.example.mvialpha.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.mvialpha.R
import com.example.mvialpha.ui.BaseActivity
import com.example.mvialpha.ui.auth.AuthActivity
import com.example.mvialpha.ui.main.account.ChangePasswordFragment
import com.example.mvialpha.ui.main.account.UpdateAccountFragment
import com.example.mvialpha.ui.main.blog.UpdateBlogFragment
import com.example.mvialpha.ui.main.blog.ViewBlogFragment
import com.example.mvialpha.util.BottomNavController
import com.example.mvialpha.util.BottomNavController.*
import com.example.mvialpha.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
NavGraphProvider,
OnNavigationGraphChanged,
OnNavigationReselectedListener{
    private  val TAG = "MainActivity"
    private lateinit var bottomNavigationView: BottomNavigationView
    private val bottomNavController: BottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun displayProgressBar(bool: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if(savedInstanceState == null){
            bottomNavController.onNavigationItemSelected()
        }
        subscribeObservers()
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

    override fun getNavGraphId(itemId: Int) = when(itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }
        else -> {
            R.navigation.nav_blog
        }
    }

    override fun onGraphChange() {
        expandAppbar()
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) = when(fragment){
        is ViewBlogFragment -> {
            navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
        }
        is UpdateBlogFragment -> {
            navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
        }
        is UpdateAccountFragment -> {
            navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
        }
        is ChangePasswordFragment -> {
            navController.navigate(R.id.action_changePasswordFragment_to_accountFragment)
        }
        else -> {
            // Do nothing
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(tool_bar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun expandAppbar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()
}