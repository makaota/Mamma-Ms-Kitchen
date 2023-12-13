package com.makaota.mammamskitchen.ui.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityMyOrderDetailsBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.NotificationData
import com.makaota.mammamskitchen.models.Notifications
import com.makaota.mammamskitchen.models.Order
import com.makaota.mammamskitchen.models.PushNotification
import com.makaota.mammamskitchen.models.User
import com.makaota.mammamskitchen.models.UserManager
import com.makaota.mammamskitchen.ui.adapters.CartItemsListAdapter
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.utils.RetrofitInstance
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit



class MyOrderDetailsActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityMyOrderDetailsBinding
    var username: String? = null
    private var userMobile: String? = null
    lateinit var myOrderDetails: Order
    lateinit var mOrderStatus: String
    lateinit var mOrderNumber: String
    private var userManagerToken = ""
    private var mUserDeviceToken= ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        // Get the order details through intent.
        // START
        myOrderDetails = Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }
        // END

        mOrderStatus = myOrderDetails.orderStatus
        mOrderNumber = myOrderDetails.orderNumber

        if (mOrderNumber.isNotEmpty()){
            binding.llOrderNumber.visibility = View.VISIBLE
            binding.tvOrderNumber.text = mOrderNumber
        }


        val sharedPreferences =
            getSharedPreferences(Constants.RESTAURANT_USER_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")
        userMobile = sharedPreferences.getString(Constants.LOGGED_IN_USER_MOBILE,"")
        editor.apply()

        setupUI(myOrderDetails)

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)

        binding.btnSendCancelOrder.setOnClickListener(this)
        binding.btnUserConfirmOrder.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btn_user_confirm_order -> {

                showProgressDialog(resources.getString(R.string.please_wait))

                val db = Firebase.firestore
                val batch = db.batch()

                val documentRef = db.collection(Constants.ORDERS)
                    .document(myOrderDetails.id)
                // Update the value of the order status
                batch.update(documentRef, Constants.ORDER_CONFIRMATION, "Yes")
                Log.i(TAG, "orders status is = $mOrderStatus")
                Log.i(TAG, "document ID is = ${myOrderDetails.id}")
                batch.commit()
                    .addOnSuccessListener {
                        // Update successful
                        //hideProgressDialog()

                    }
                    .addOnFailureListener { e ->
                        // Handle error
                        //hideProgressDialog()
                    }

                sendOrderNotificationToUserManager()
                hideProgressDialog()
                onBackPressed()

            }
            R.id.btn_send_cancel_order -> {

                cancelOrder(myOrderDetails.id)
            }
        }
    }
    // END

    private fun sendOrderNotificationToUser() {

        val userCollection = FirebaseFirestore.getInstance().collection(Constants.USER)
        userCollection.document(FirestoreClass().getCurrentUserId()).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    // Extract user information here
                    mUserDeviceToken = user?.userToken.toString()


                    val title = myOrderDetails.title
                    val message = "${myOrderDetails.userName} has confirmed the order please proceed"

                    PushNotification(NotificationData(title,message),mUserDeviceToken).also {
                        sendOrderNotification(it)
                        Log.i("MyOrderDetailsActivity", "${title}, $message user Token sent notification = $mUserDeviceToken")

                    }

                    Log.i("TAG","userManager Token = $mUserDeviceToken")
                } else {
                    // User document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Error occurred while fetching user data
            }
    }
    // END

    private fun sendOrderNotificationToUserManager(){


        val userCollection = FirebaseFirestore.getInstance().collection(Constants.USER_MANAGER)
        userCollection.document(myOrderDetails.user_manager_id).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userManager = documentSnapshot.toObject(UserManager::class.java)
                    // Extract user information here
                    userManagerToken = userManager?.userToken.toString()


                    val title = myOrderDetails.title
                    val message = "${myOrderDetails.userName} has confirmed the order please proceed"


                    PushNotification(NotificationData(title,message),userManagerToken).also {
                        sendOrderNotification(it)
                        Log.i("TAG","userManager Token sent notification = $userManagerToken")
                    }
                    Log.i("TAG","userManager Token = $userManagerToken")
                    sendOrderNotificationToUser()


                    val notifications = Notifications(
                        title = title,
                        orderDateTime = myOrderDetails.order_datetime,
                        orderStatus = myOrderDetails.orderStatus,
                        orderMessage = message,
                        orderConfirmed = myOrderDetails.order_confirmation,
                        orderNumber = myOrderDetails.orderNumber,
                        user_id = FirestoreClass().getCurrentUserId()
                    )

                    FirestoreClass().uploadNotificationsDetails(this, notifications)


                } else {
                    // User document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Error occurred while fetching user data
            }
    }

    fun notificationsUploadSuccess(){

        //  hideProgressDialog()

    }

    private fun sendOrderNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.i("CheckoutActivity", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.i("CheckoutActivity","not successful ${response.errorBody().toString()}")

                }

            } catch (e: Exception) {
                Log.i("CheckoutActivity","Exception found $e")
            }

            finish()

        }

    private fun cancelOrder(orderID: String){

        showAlertDialogToDeleteDeliveredOrder(orderID)

    }

    // Create a function to show the alert dialog for the confirmation of delete product from cloud firestore.
    // START
    /**
     * A function to show the alert dialog for the confirmation of delete product from cloud firestore.
     */
    private fun showAlertDialogToDeleteDeliveredOrder(orderID: String) {

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_order_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Call the function to delete the product from cloud firestore.
            // START
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().cancelOrder(this,orderID)
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

    fun orderCancelSuccess(){

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this,
            "Your order was canceled successfully",
            Toast.LENGTH_SHORT
        ).show()

        finish()

    }
    // END

    // Create a function to setup UI.
    // START
    /**
     * A function to setup UI.
     *
     * @param orderDetails Order details received through intent.
     */
    private fun setupUI(orderDetails: Order) {

        binding.tvOrderDetailsId.text = orderDetails.title

        // Set the Date in the UI.
        // START
        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)


        binding.tvOrderDetailsDate.text = orderDateTime
        // END

        // Set the order status based on the time for now.
        // START
        // Get the difference between the order date time and current date time in hours.
        // If the difference in hours is 1 or less then the order status will be PENDING.
        // If the difference in hours is 2 or greater then 1 then the order status will be PROCESSING.
        // And, if the difference in hours is 3 or greater then the order status will be DELIVERED.

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")


        if (orderDetails.order_confirmation == ""){
            binding.tvOrderConfirmationText.text = "No"
        }else{
            binding.tvOrderConfirmationText.text = orderDetails.order_confirmation
        }


        when {
            mOrderStatus == resources.getString(R.string.order_status_pending) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorAccent
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_pending)
                binding.btnSendCancelOrder.visibility = View.VISIBLE
            }


            mOrderStatus == resources.getString(R.string.order_status_in_process) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_in_process)

                if(orderDetails.order_confirmation == "Yes"){
                    binding.btnSendCancelOrder.visibility = View.GONE
                    binding.btnUserConfirmOrder.visibility = View.GONE
                }else{
                    binding.btnSendCancelOrder.visibility = View.VISIBLE
                    binding.btnUserConfirmOrder.visibility = View.VISIBLE
                }


            }

            mOrderStatus == resources.getString(R.string.order_status_preparing) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusPreparing
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_preparing)

                binding.btnSendCancelOrder.visibility = View.GONE
                binding.btnUserConfirmOrder.visibility = View.GONE
            }

            mOrderStatus == resources.getString(R.string.order_status_ready_for_collection) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusReadyForCollection
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_ready_for_collection)
                binding.btnSendCancelOrder.visibility = View.GONE
                binding.btnUserConfirmOrder.visibility = View.GONE
            }

            mOrderStatus == resources.getString(R.string.order_status_delivered) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
                binding.btnSendCancelOrder.visibility = View.GONE
                binding.btnUserConfirmOrder.visibility = View.GONE
            }
        }
        // END

        binding.rvMyOrderItemsList.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
        binding.rvMyOrderItemsList.setHasFixedSize(true)

        val cartListAdapter =
            CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
        binding.rvMyOrderItemsList.adapter = cartListAdapter


        binding.tvMyOrderDetailsFullName.text = username
        binding.tvMyOrderDetailsMobileNumber.text = userMobile

        binding.tvOrderDetailsSubTotal.text = orderDetails.sub_total_amount
        binding.tvOrderDetailsDeliveryCharge.text = orderDetails.delivery_charge
        binding.tvOrderDetailsTotalAmount.text = orderDetails.total_amount


        // Set the selected address details to UI that is received through intent.
        // START
        if (myOrderDetails.address.type.isNotEmpty()) {
            binding.tvCheckoutAddressType.text = myOrderDetails.address.type
            binding.tvCheckoutFullName.text = myOrderDetails.address.name
            binding.tvCheckoutAddress.text = "${myOrderDetails.address.address} ${myOrderDetails.address.zipCode}"
            binding.tvCheckoutAdditionalNote.text = myOrderDetails.address.additionalNote
            binding.tvCheckoutMobileNumber.text = myOrderDetails.address.mobileNumber

            if (myOrderDetails.address.otherDetails.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = myOrderDetails.address.otherDetails
            }

        }else{
          //  binding.llCheckoutAddressDetails.visibility = View.GONE
          //  binding.tvSelectedAddress.visibility = View.GONE

            binding.tvCheckoutAddressType.text = "Address not selected"
            binding.tvCheckoutFullName.text = "Customer to pickup"
        }
        // END


    }
    // END



    // Create a function to setup action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }


}