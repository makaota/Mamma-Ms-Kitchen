package com.makaota.mammamskitchen.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityCheckoutBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.Address
import com.makaota.mammamskitchen.models.CartItem
import com.makaota.mammamskitchen.models.NotificationData
import com.makaota.mammamskitchen.models.Notifications
import com.makaota.mammamskitchen.models.OpenCloseStore
import com.makaota.mammamskitchen.models.Order
import com.makaota.mammamskitchen.models.Product
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



class CheckoutActivity : BaseActivity() {

    lateinit var binding: ActivityCheckoutBinding

    // A global variable for the selected address details.
    private var mAddressDetails: Address? = null


    // Global variable for all product list.
    // START
    private lateinit var mProductsList: ArrayList<Product>
    // END

    private var userManagerToken = ""

    private var mUserDeviceToken= ""

    // Global variable for cart items list.
    // START
    private lateinit var mCartItemsList: ArrayList<CartItem>
    // END

    private lateinit var mOrderDetails: Order

    // Create a global variables for SubTotal and Total Amount.
    // START
    // A global variable for the SubTotal Amount.
    private var mSubTotal: Double = 0.0

    // A global variable for the Total Amount.
    private var mTotalAmount: Double = 0.0

    private var userProfileComplete: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        // Get the selected address details through intent.
        // START
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }
        // END

        // Set the selected address details to UI that is received through intent.
        // START
        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote


            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }else{
            binding.llCheckoutAddressDetails.visibility = View.GONE
            binding.tvSelectedAddress.visibility = View.GONE
        }
        // END

        getProductList()


        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)

        binding.btnPlaceOrder.setOnClickListener {

            val sharedPreferences = getSharedPreferences("CheckoutPrefs", Context.MODE_PRIVATE)
             userProfileComplete = sharedPreferences.getInt("userProfileComplete", 0)

            if (userProfileComplete == 0) {
                FirestoreClass().getUserDetails(this)
            }else{
               getOpenCloseStoreInfo()
            }
        }
    }

    private fun getOpenCloseStoreInfo() {


        showProgressDialog(resources.getString(R.string.please_wait))

        val mFirestore = FirebaseFirestore.getInstance()
        val docRef = "KUadjV036C6fvZrjmIWn"
        // The collection name for OPEN CLOSE STORE
        mFirestore.collection(Constants.OPEN_CLOSE_STORE)
            .document(docRef)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of open close store data model class.
                val openCloseStore = document.toObject(OpenCloseStore::class.java)!!


                if (!openCloseStore.isStoreOpen){

                    hideProgressDialog()
                }
                else{

                    hideProgressDialog()
                    placeAnOrder()

                }

            }
            .addOnFailureListener { e ->

                hideProgressDialog()
            }
    }

    // Create a function to get product list to compare it with the cart items stock.
    // START
    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }
    // END

    // Create a function to get the list of cart items in the activity.
    /**
     * A function to get the list of cart items in the activity.
     */
    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
    }


    // Create a function to get the success result of product list.
    // START
    /**
     * A function to get the success result of product list.
     *
     * @param productsList
     */
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // Initialize the global variable of all product list.
        // START
        mProductsList = productsList
        // END

        // Call the function to get the latest cart items.
        // START
        getCartItemsList()
        // END
    }
    // END

    // Create a function to notify the success result of the cart items list from cloud firestore.
    // START
    /**
     * A function to notify the success result of the cart items list from cloud firestore.
     *
     * @param cartList
     */
    fun successCartItemsList(cartList: ArrayList<CartItem>) {

        // Hide progress dialog.
        hideProgressDialog()


        // Update the stock quantity in the cart list from the product list.
        // START
        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }
        // END
        // Initialize the cart list.
        // START
        mCartItemsList = cartList
        // END

        // Populate the cart items in the UI.
        // START
        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)

        // Pass the required param.
        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter
        // END

        // Calculate the subtotal and Total Amount.
        // START
        var subTotal: Double = 0.0

        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }


        binding.tvCheckoutSubTotal.text = "R$mSubTotal"


        if (mAddressDetails != null){
            // Here we have kept Delivery Charge is fixed as R10 but in your case it may cary. Also, it depends on the location and total amount.
            binding.tvCheckoutDeliveryCharge.text = "R10.0"

            if (mSubTotal > 0) {
                binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

                mTotalAmount = mSubTotal + 10
                binding.tvCheckoutTotalAmount.text = "R$mTotalAmount"
            } else {
                binding.llCheckoutPlaceOrder.visibility = View.GONE
            }
            // END
        }
        else{

            binding.tvCheckoutDeliveryCharge.text = "R0.0"

            if (mSubTotal > 0) {
                binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

                mTotalAmount = mSubTotal

                binding.tvCheckoutTotalAmount.text = "R$mTotalAmount"

            } else {
                binding.llCheckoutPlaceOrder.visibility = View.GONE
            }

        }




    }
    // END

    // Create a function to prepare the Order details to place an order.
    // START
    /**
     * A function to prepare the Order details to place an order.
     */
    private fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        val sharedPreferences =
            getSharedPreferences(Constants.RESTAURANT_USER_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")
        val userMobile = sharedPreferences.getString(Constants.LOGGED_IN_USER_MOBILE, "")
        editor.apply()

        val currentTimeMillis = System.currentTimeMillis()

        // Convert the current time to a string
        val currentTimeString = currentTimeMillis.toString()

        var lastFiveDigits = ""
        // Extract the last five digits
        if (currentTimeString.length >= 5) {
            lastFiveDigits = currentTimeString.takeLast(5)
            println("Last five digits: $lastFiveDigits")
        }



        // Now prepare the order details based on all the required details.
        // START
        if (mAddressDetails != null) {
            mOrderDetails = Order(
                user_id = FirestoreClass().getCurrentUserId(),
                mCartItemsList,
                mAddressDetails!!,
                title = "$username $lastFiveDigits",
                orderStatus = resources.getString(R.string.order_status_pending),
                userName = username!!,
                userMobile = userMobile!!,
                image = mCartItemsList[0].image,
                sub_total_amount = mSubTotal.toString(),
                delivery_charge = "10.0", // The Shipping Charge is fixed as R10 for now in our case.
                total_amount = mTotalAmount.toString(),
                order_datetime = currentTimeMillis
            )
            // END


            // Call the function to place the order in the cloud firestore.
            // START
            FirestoreClass().placeOrder(this@CheckoutActivity, mOrderDetails)
            // END

            sendOrderNotificationToUserManager()

        }else{ // Customer to Pickup Items address not required

            mOrderDetails = Order(
                user_id = FirestoreClass().getCurrentUserId(),
                mCartItemsList,
                title = "$username $lastFiveDigits",
                orderStatus = resources.getString(R.string.order_status_pending),
                userName = username!!,
                userMobile = userMobile!!,
                image = mCartItemsList[0].image,
                sub_total_amount = mSubTotal.toString(),
                delivery_charge = "0.0", // The Shipping Charge is fixed as R0 for now in our case.
                total_amount = mTotalAmount.toString(),
                order_datetime = System.currentTimeMillis()
            )
            // END


            // Call the function to place the order in the cloud firestore.
            // START
            FirestoreClass().placeOrder(this@CheckoutActivity, mOrderDetails)
            // END

            sendOrderNotificationToUserManager()

        }
        // END
    }

    /**
     * This Function provide a question that needs to be answered
     * If the userProfile is completed the user can place an order else
     * The user will need to complete the profile before placing an order*/
    fun isUserProfileComplete(user: User) {

        // Redirect the user to the UserProfile screen if it is incomplete otherwise to the Main screen.
        // START
        if (user.profileCompleted == 0) {
            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this, UserProfileActivity::class.java)

            val sharedPreferences = getSharedPreferences("CheckoutPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            userProfileComplete = 1 // Set your value here (0 or 1)
            editor.putInt("userProfileComplete", userProfileComplete)
            //editor.clear()
            editor.apply()
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)

        }else{

            /***
             * / This part here will only happen when the user uninstall the app and re-installing it
             *  This part re-write the shared-preference instance only if the app was uninstalled and
             *  the user has previously completed the user profile
             */

            // If the user profile is complete then launch the CartListActivity.
            val intent = Intent(this, CartListActivity::class.java)

            val sharedPreferences = getSharedPreferences("CheckoutPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            userProfileComplete = 1 // Set your value here (0 or 1)
            editor.putInt("userProfileComplete", userProfileComplete)
            //editor.clear()
            editor.apply()
            startActivity(intent)

        }
    }


    private fun sendOrderNotificationToUser() {

        val userCollection = FirebaseFirestore.getInstance().collection(Constants.USER)
        userCollection.document(FirestoreClass().getCurrentUserId()).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    // Extract user information here
                    mUserDeviceToken = user?.userToken.toString()


                    val title = mOrderDetails.title
                    val message = "${mOrderDetails.userName} has placed an order of R${mOrderDetails.total_amount}"

                    PushNotification(NotificationData(title,message),mUserDeviceToken).also {
                        sendOrderNotification(it)
                        Log.i("MyOrderDetailsActivity", "${title}, $message user Token sent notification = $mUserDeviceToken")

                    }

                    Log.i("TAG","user Token = $mUserDeviceToken")
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

        val userManagerId = getUserMangerId()
        userCollection.document(userManagerId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userManager = documentSnapshot.toObject(UserManager::class.java)
                    // Extract user information here
                    userManagerToken = userManager?.userToken.toString()


                    val title = mOrderDetails.title
                    val message = "${mOrderDetails.userName} has placed an order of R${mOrderDetails.total_amount}"


                    PushNotification(NotificationData(title,message),userManagerToken).also {
                        sendOrderNotification(it)
                        Log.i("TAG","userManager Token sent notification = $userManagerToken")
                    }

                    sendOrderNotificationToUser()


                    val notifications = Notifications(
                        title = title,
                        orderDateTime = mOrderDetails.order_datetime,
                        orderStatus = mOrderDetails.orderStatus,
                        orderMessage = message,
                        orderConfirmed = mOrderDetails.order_confirmation,
                        orderNumber = mOrderDetails.orderNumber,
                        user_id = FirestoreClass().getCurrentUserId()
                    )

                    FirestoreClass().uploadNotificationsDetails(this, notifications)

                    Log.i("TAG","userManager Token = $userManagerToken")
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

    // This Function get the id of user manager via product
    // meaning the manager who created the product has an Id
    private fun getUserMangerId(): String {

        var userManagerId = ""

        for (product in mProductsList) {
            userManagerId = product.user_id
            Log.i("TAG","userManagerID = $userManagerId")
        }
        Log.i("TAG","returned User Manager id = $userManagerId")
        return userManagerId

    }

    // Create a function to notify the success result of the order placed.
    // START
    /**
     * A function to notify the success result of the order placed.
     */
    fun orderPlacedSuccess() {

        hideProgressDialog()

        FirestoreClass().updateAllDetails(this, mCartItemsList)
    }
    // END

    // Create a function to notify the success result after updating all the required details.
    // START
    /**
     * A function to notify the success result after updating all the required details.
     */
    fun allDetailsUpdatedSuccessfully() {

        // Move the piece of code from OrderPlaceSuccess to here.
        // START
        // Hide the progress dialog.
        hideProgressDialog()

        FancyToast.makeText(
            this@CheckoutActivity,
            "Your order was placed successfully.",
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true
        )
            .show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        // END
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

        }


    // Create a function to setup the action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCheckoutActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
    }


}