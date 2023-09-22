package com.makaota.mammamskitchen.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityMyOrderDetailsBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.Order
import com.makaota.mammamskitchen.ui.adapters.CartItemsListAdapter
import com.makaota.mammamskitchen.utils.Constants
import com.shashank.sony.fancytoastlib.FancyToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


const val ORDER_TAG = "MyOrderDetailsActivity"
class MyOrderDetailsActivity : BaseActivity() {

    lateinit var binding: ActivityMyOrderDetailsBinding
    private lateinit var orderDetailsSwipeRefreshLayout: SwipeRefreshLayout
    var username: String? = null
    private var userMobile: String? = null
    lateinit var myOrderDetails: Order
    lateinit var mOrderStatus: String
    lateinit var mOrderNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        orderDetailsSwipeRefreshLayout = findViewById(R.id.order_details_swipe_refresh_layout)
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

        refreshOrdersPage()

    }

    private fun refreshOrdersPage() {

        orderDetailsSwipeRefreshLayout.setOnRefreshListener {
            onBackPressed()
            FancyToast.makeText(
                this,
                "Order Details Refreshed",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                true
            ).show()

            orderDetailsSwipeRefreshLayout.isRefreshing = false
        }
    }

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

        when {
            mOrderStatus == resources.getString(R.string.order_status_pending) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorAccent
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_pending)
            }


            mOrderStatus == resources.getString(R.string.order_status_in_process) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
            }

            mOrderStatus == resources.getString(R.string.order_status_preparing) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusPreparing
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_preparing)
            }

            mOrderStatus == resources.getString(R.string.order_status_ready_for_collection) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusReadyForCollection
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_ready_for_collection)
            }

            mOrderStatus == resources.getString(R.string.order_status_delivered) -> {
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
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
        binding.tvOrderDetailsShippingCharge.text = orderDetails.shipping_charge
        binding.tvOrderDetailsTotalAmount.text = orderDetails.total_amount
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
    // END
}