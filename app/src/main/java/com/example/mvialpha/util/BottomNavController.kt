package com.example.mvialpha.util

import android.app.Activity
import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.mvialpha.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int,
    @IdRes val appStartDestinationId: Int,
    val graphChangeListener: OnNavigationGraphChanged?,
    val navGraphProvider: NavGraphProvider
) {
    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager
    lateinit var navItemChangeListener: OnNavigationItemChanged
    private val navigationBackStack: BackStack = BackStack.of(appStartDestinationId)

    init{
        if(context is Activity){
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    fun onNavigationItemSelected(itemId: Int = navigationBackStack.last()): Boolean{
        // Replace fragment representing a navigation item
        val fragment = fragmentManager.findFragmentByTag(itemId.toString())
            ?: NavHostFragment.create(navGraphProvider.getNavGraphId(itemId))
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, fragment, itemId.toString())
            .addToBackStack(null)
            .commit()
        // Add to backstack
        navigationBackStack.moveLast(itemId)

        // Update checked icon
        navItemChangeListener.onItemChanged(itemId)

        // Communicate with activity
        graphChangeListener?.onGraphChange()

        return true
    }

    fun onBackPressed(){
        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!
            .childFragmentManager
        when{
            childFragmentManager.popBackStackImmediate() -> {

            }
            navigationBackStack.size > 1 -> {
                navigationBackStack.removeLast()

                // Update the container with the new fragment
                onNavigationItemSelected()
            }
            // If the stack has only one and it's not the navigation home we should ensure that the application always
            // leave from startDestination
            navigationBackStack.last() != appStartDestinationId ->{
                navigationBackStack.removeLast()
                navigationBackStack.add(0, appStartDestinationId)
                onNavigationItemSelected()
            }
            else -> activity.finish()
        }
    }

    private class BackStack: ArrayList<Int>(){
        companion object{
            fun of(vararg elements: Int): BackStack{
                val b = BackStack()
                b.addAll(elements.toTypedArray())
                return b
            }
        }

        fun removeLast() = removeAt(size - 1)

        fun moveLast(item: Int){
            remove(item)
            add(item)
        }
    }

    // For setting the checked icon in the bottom nav
    interface OnNavigationItemChanged{
        fun onItemChanged(itemId: Int)
    }

    fun setOnItemNavigationChanged(listener: (itemId: Int) -> Unit){
        this.navItemChangeListener = object: OnNavigationItemChanged{
            override fun onItemChanged(itemId: Int) {
                listener.invoke(itemId)
            }
        }
    }
    // Get id of each graph
    // ex: R.navigation.nav_blog
    // ex: R.navigation.nav_create_blog
    interface NavGraphProvider{
        @NavigationRes
        fun getNavGraphId(itemId: Int): Int
    }

    // Execute when navigation graph changes
    // ex: Select a new item on the bottom nav
    // ex: Home -> Account
    interface OnNavigationGraphChanged{
        fun onGraphChange()
    }

    interface OnNavigationReselectedListener{
        fun onReselectNavItem(navController: NavController, fragment: Fragment)
    }

    companion object {
        private const val TAG = "BottomNavController"
    }
}

fun BottomNavigationView.setUpNavigation(
    bottomNavController: BottomNavController,
    onReselectListener: BottomNavController.OnNavigationReselectedListener
){
    setOnNavigationItemSelectedListener {
        bottomNavController.onNavigationItemSelected()
    }

    setOnNavigationItemReselectedListener {
        bottomNavController
            .fragmentManager
            .findFragmentById(bottomNavController.containerId)!!
            .childFragmentManager
            .fragments[0]?.let { fragment ->
            onReselectListener.onReselectNavItem(
                bottomNavController.activity.findNavController(bottomNavController.containerId),
                fragment
            )
        }
    }
    bottomNavController.setOnItemNavigationChanged { itemId ->
        menu.findItem(itemId).isChecked = true
    }
}