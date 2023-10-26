package com.makaota.mammamskitchen.ui.activities

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityDashboardBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.Notifications
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.viewmodel.CounterViewModel
import kotlin.collections.ArrayList

class DashboardActivity : BaseActivity() {

    private val DASHBOARD_TAG = "DashboardActivity"

    private lateinit var binding: ActivityDashboardBinding

    private lateinit var navView: BottomNavigationView

    private val mFirestore = FirebaseFirestore.getInstance()

    private lateinit var viewModel: CounterViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProvider(this).get(CounterViewModel::class.java)

        supportActionBar!!.setBackgroundDrawable(
            ContextCompat.getDrawable(this, R.drawable.app_gradient_color_background)
        )

        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_menu,
                R.id.navigation_orders,
                R.id.navigation_favorites,
                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

       registerNotificationsListener()

    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
    override fun onResume() {
        super.onResume()

       updateCountInUI(viewModel.number)

    }
    private fun registerNotificationsListener(){


        // getting an updated list

        mFirestore.collection(Constants.NOTIFICATIONS)
            .whereEqualTo(Constants.USER_ID, FirestoreClass().getCurrentUserId())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                val list: ArrayList<Notifications> = ArrayList()

                for (i in document.documents) {
                    val notifications = i.toObject(Notifications::class.java)!!
                    list.add(0, notifications)
                }

                viewModel.number = list.size

                updateCountInUI(viewModel.number)

            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                Log.e(javaClass.simpleName, "Error while getting the orders list.", e)

            }
    }

    fun getNotificationsList(listSize: Int){

        viewModel.number = listSize

        updateCountInUI(viewModel.number)

    }
    private fun updateCountInUI(count: Int) {

        viewModel.currentNotificationNumber.value = count
        val badge = navView.getOrCreateBadge(R.id.navigation_notifications)

        viewModel.currentNotificationNumber.observe(this, Observer {
            badge.isVisible = true
            badge.number = it

        })

        Log.i(DASHBOARD_TAG, " in updateCountInUI ${viewModel.currentNotificationNumber.value}")


    }


}