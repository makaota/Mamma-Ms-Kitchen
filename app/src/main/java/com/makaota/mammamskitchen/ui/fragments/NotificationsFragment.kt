package com.makaota.mammamskitchen.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.FragmentNotificationsBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.Notifications
import com.makaota.mammamskitchen.ui.activities.DashboardActivity
import com.makaota.mammamskitchen.ui.adapters.MyNotificationsListAdapter
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.ArrayList


class NotificationsFragment : BaseFragment() {


    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var notificationsSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var notificationsList: ArrayList<Notifications>

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        notificationsSwipeRefreshLayout = root.findViewById(R.id.notifications_swipe_refresh_layout)
        refreshPage()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getMyNotificationsList()
    }

    private fun refreshPage(){


        notificationsSwipeRefreshLayout.setOnRefreshListener {


            getMyNotificationsList()



            _binding!!.notificationsSwipeRefreshLayout.isRefreshing = false

        }
    }

    fun populateNotificationsListInUI(notificationsList: ArrayList<Notifications>) {


        // Hide the progress dialog.
        hideProgressDialog()

        this.notificationsList = notificationsList
        // Populate the orders list in the UI.
        // START
        if (notificationsList.size > 0) {

            _binding!!.rvMyNotificationsItems.visibility = View.VISIBLE
            _binding!!.tvNoNotificationFound.visibility = View.GONE

            _binding!!.rvMyNotificationsItems.layoutManager = LinearLayoutManager(activity)
            _binding!!.rvMyNotificationsItems.setHasFixedSize(true)

            val myNotificationsAdapter = MyNotificationsListAdapter(requireActivity(),
                notificationsList,this)
            _binding!!.rvMyNotificationsItems.adapter = myNotificationsAdapter

        } else {
            _binding!!.rvMyNotificationsItems.visibility = View.GONE
            _binding!!.tvNoNotificationFound.visibility = View.VISIBLE
        }
        // END

        val myAct = activity as DashboardActivity
        myAct.getNotificationsList(notificationsList.size)

    }

    // Create a function to call the firestore class function to get the list of my orders.
    // START
    /**
     * A function to get the list of my orders.
     */
    private fun getMyNotificationsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyNotificationsList(this)
    }
    // END

    fun deleteNotification(notificationId: String){

        showAlertDialogToDeleteNotification(notificationId)

    }

    // Create a function to show the alert dialog for the confirmation of delete notification from cloud firestore.
    // START
    /**
     * A function to show the alert dialog for the confirmation of delete notification from cloud firestore.
     */
    private fun showAlertDialogToDeleteNotification(notificationId: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_notification_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Call the function to delete the notification from cloud firestore.
            // START
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteNotification(this, notificationId)
            // END

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // Create a function to notify the success result of product deleted from cloud firestore.
    // START
    /**
     * A function to notify the success result of product deleted from cloud firestore.
     */
    fun notificationDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.notification_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        // Get the latest notifications list from cloud firestore.
        getMyNotificationsList()



    }
    // END


}