package com.makaota.mammamskitchen.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.FragmentMenuBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.CartItem
import com.makaota.mammamskitchen.models.OpenCloseStore
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.models.User
import com.makaota.mammamskitchen.ui.activities.AboutUsActivity
import com.makaota.mammamskitchen.ui.activities.CartListActivity
import com.makaota.mammamskitchen.ui.activities.MenuByCategoryActivity
import com.makaota.mammamskitchen.ui.activities.ProductDetailsActivity
import com.makaota.mammamskitchen.ui.activities.SettingsActivity
import com.makaota.mammamskitchen.ui.adapters.MenuItemsListAdapter
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.viewmodel.CounterViewModel
import com.shashank.sony.fancytoastlib.FancyToast
import kotlin.concurrent.timerTask

const val MENU_FRAGMENT_TAG = "MenuFragment"

class MenuFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentMenuBinding? = null
    private lateinit var menuSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mMenuItemsList: ArrayList<Product>

    private lateinit var viewModel: CounterViewModel

    private lateinit var adapter: MenuItemsListAdapter

    private lateinit var menu: Menu

    private lateinit var popupWindow: PopupWindow

    private var cartQuantity = 0
    private lateinit var cartListener: ListenerRegistration // To listen for changes in the cart
    private var isMenuInitialized: Boolean = false
    private var isCartQuantityInitialized: Boolean = false

    private val mFirestore = FirebaseFirestore.getInstance()

    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo : ReviewInfo


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)



        Log.i(MENU_FRAGMENT_TAG, " in onCreate")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)


        viewModel = ViewModelProvider(requireActivity()).get(CounterViewModel::class.java)

        this.menu = menu
        isMenuInitialized = true
        // The collection name for Cart Items
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, FirestoreClass().getCurrentUserId())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                //   Log.e(javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {

                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(0, cartItem)
                }

                // Notify the success result.
                // START
                var quantity = 0

                for (cartItems in list) {
                    quantity += cartItems.cart_quantity.toInt()
                }

                cartQuantity = quantity
                isCartQuantityInitialized = true

                val menuItem = menu.findItem(R.id.action_cart)
                val actionView = menuItem.actionView
                val cartBadgeTextView =
                    actionView!!.findViewById<TextView>(R.id.cart_badge_text_view)

                updateCountInUI()

                actionView.setOnClickListener {
                    onOptionsItemSelected(menuItem)

                }

                Log.i(MENU_FRAGMENT_TAG, " in onCreateOptionsMenu")

            }
            .addOnFailureListener { e ->

                Log.e(javaClass.simpleName, "Error while getting the cart list items.", e)
            }


        invalidateOptionsMenu(requireActivity())
        super.onCreateOptionsMenu(menu, inflater)

    }

    private fun updateCountInUI() {


        viewModel.currentCartNumber.value = cartQuantity

        val menuItem = menu.findItem(R.id.action_cart)
        val actionView = menuItem.actionView
        val cartBadgeTextView = actionView!!.findViewById<TextView>(R.id.cart_badge_text_view)


        viewModel.currentCartNumber.observe(this, Observer {
            cartBadgeTextView.text = it.toString()
        })
//            cartBadgeTextView.text = cartQuantity.toString()

        Log.i(MENU_FRAGMENT_TAG, " in updateCountInUI ${viewModel.currentCartNumber.value}")


    }

    private fun registerCartListener() {
        val userId = FirestoreClass().getCurrentUserId()

        if (userId.isNotEmpty()) {
            val cartItemsRef = mFirestore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, userId)

            cartListener = cartItemsRef.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e(javaClass.simpleName, "Error while listening for cart items", e)
                    return@addSnapshotListener
                }

                // Handle cart item changes here
                val cartItems = snapshots?.documents ?: emptyList()
                val quantity = cartItems.sumBy { (it[Constants.CART_QUANTITY] as String).toInt() }
                cartQuantity = quantity
                isCartQuantityInitialized = true

                updateCountInUI()

                Log.i(MENU_FRAGMENT_TAG, " in registerCartListener after updateCountInUI()")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }

            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }

            R.id.action_about_us -> {
                startActivity(Intent(activity, AboutUsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root
        menuSwipeRefreshLayout = root.findViewById(R.id.menu_swipe_refresh_layout)
        refreshPage()


        _binding!!.tvScambane.setOnClickListener(this)

        _binding!!.tvChips.setOnClickListener(this)
        _binding!!.tvRussian.setOnClickListener(this)
        _binding!!.tvAdditionalMeals.setOnClickListener(this)
        _binding!!.tvDrinks.setOnClickListener(this)

        return root
    }

    override fun onResume() {
        super.onResume()
        binding.tvScambane.text = "Scambane"
        binding.tvScambane.isEnabled = true
        binding.tvScambane.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
        )

        binding.tvChips.text = "Chips"
        binding.tvChips.isEnabled = true
        binding.tvChips.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
        )

        binding.tvRussian.text = "Russian"
        binding.tvRussian.isEnabled = true
        binding.tvRussian.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
        )

        binding.tvAdditionalMeals.text = "Additionals"
        binding.tvAdditionalMeals.isEnabled = true
        binding.tvAdditionalMeals.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
        )

        binding.tvDrinks.text = "Drinks"
        binding.tvDrinks.isEnabled = true
        binding.tvDrinks.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
        )

        // Register a listener to listen for changes to the cart items
        if (isMenuInitialized) {
            registerCartListener()
            Log.i(MENU_FRAGMENT_TAG, " in onResume After registerCartListener()")
        }
        if (isCartQuantityInitialized || isMenuInitialized) {
            updateCountInUI()
            Log.i(MENU_FRAGMENT_TAG, " in onResume After updateCountInUI()")
        }

        getMenuItemsList()
        checkIfUserHasCompleteOrder()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the cart listener when the fragment is destroyed
//        cartListener.remove()
        _binding = null
    }

    override fun onClick(v: View?) {

        if (v != null) {
            when (v.id) {
                R.id.tv_scambane -> {

                    binding.tvScambane.text = resources.getString(R.string.please_wait)
                    binding.tvScambane.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOffWhite
                        )
                    )
                    binding.tvChips.isEnabled = false
                    binding.tvRussian.isEnabled = false
                    binding.tvAdditionalMeals.isEnabled = false
                    binding.tvDrinks.isEnabled = false


                    getOpenCloseStoreInfo { isStoreOpen ->
                        if (isStoreOpen) {
                            // Store is open, you can place an order here
                            val scambaneList = ArrayList<Product>()
                            for (product in mMenuItemsList) {
                                if (product.category == Constants.SCAMBANE) {
                                    scambaneList.add(product)
                                }
                            }
                            Log.i(MENU_FRAGMENT_TAG, "List Of Scambanes $scambaneList")
                            val intent = Intent(context, MenuByCategoryActivity::class.java)
                            intent.putParcelableArrayListExtra(Constants.SCAMBANE, scambaneList)
                            startActivity(intent)
                        } else {
                            // Store is closed
                            showPopup()
                        }
                    }


                }

                R.id.tv_chips -> {

                    binding.tvScambane.isEnabled = false
                    binding.tvChips.text = resources.getString(R.string.please_wait)
                    binding.tvChips.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOffWhite
                        )
                    )
                    binding.tvRussian.isEnabled = false
                    binding.tvAdditionalMeals.isEnabled = false
                    binding.tvDrinks.isEnabled = false


                    getOpenCloseStoreInfo { isStoreOpen ->
                        if (isStoreOpen) {
                            // Store is open, you can place an order here
                            val chipsList = ArrayList<Product>()
                            for (product in mMenuItemsList) {
                                if (product.category == Constants.CHIPS) {
                                    chipsList.add(product)
                                }
                            }
                            val intent = Intent(context, MenuByCategoryActivity::class.java)
                            intent.putParcelableArrayListExtra(Constants.CHIPS, chipsList)
                            startActivity(intent)
                        } else {
                            // Store is closed
                            showPopup()
                        }
                    }



                }

                R.id.tv_russian -> {

                    binding.tvScambane.isEnabled = false
                    binding.tvChips.isEnabled = false
                    binding.tvRussian.text = resources.getString(R.string.please_wait)
                    binding.tvRussian.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOffWhite
                        )
                    )
                    binding.tvAdditionalMeals.isEnabled = false
                    binding.tvDrinks.isEnabled = false

                    getOpenCloseStoreInfo { isStoreOpen ->
                        if (isStoreOpen) {
                            // Store is open, you can place an order here
                            val russianList = ArrayList<Product>()
                            for (product in mMenuItemsList) {
                                if (product.category == Constants.RUSSIAN) {
                                    russianList.add(product)
                                }
                            }
                            val intent = Intent(context, MenuByCategoryActivity::class.java)
                            intent.putParcelableArrayListExtra(Constants.RUSSIAN, russianList)
                            startActivity(intent)
                        } else {
                            // Store is closed
                           showPopup()
                        }
                    }

                }

                R.id.tv_additional_meals -> {

                    binding.tvScambane.isEnabled = false
                    binding.tvChips.isEnabled = false
                    binding.tvRussian.isEnabled = false
                    binding.tvAdditionalMeals.text = resources.getString(R.string.please_wait)
                    binding.tvAdditionalMeals.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOffWhite
                        )
                    )
                    binding.tvDrinks.isEnabled = false


                    getOpenCloseStoreInfo { isStoreOpen ->
                        if (isStoreOpen) {
                            // Store is open, you can place an order here
                            val additionalList = ArrayList<Product>()
                            for (product in mMenuItemsList) {
                                if (product.category == Constants.ADDITIONAL_MEALS) {
                                    additionalList.add(product)
                                }
                            }
                            val intent = Intent(context, MenuByCategoryActivity::class.java)
                            intent.putParcelableArrayListExtra(Constants.ADDITIONAL_MEALS, additionalList)
                            startActivity(intent)
                        } else {
                            // Store is closed
                           showPopup()
                        }
                    }
                }

                R.id.tv_drinks -> {

                    binding.tvScambane.isEnabled = false
                    binding.tvChips.isEnabled = false
                    binding.tvRussian.isEnabled = false
                    binding.tvAdditionalMeals.isEnabled = false
                    binding.tvDrinks.text = resources.getString(R.string.please_wait)
                    binding.tvDrinks.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOffWhite
                        )
                    )


                    getOpenCloseStoreInfo { isStoreOpen ->
                        if (isStoreOpen) {
                            // Store is open, you can place an order here
                            val drinksList = ArrayList<Product>()
                            for (product in mMenuItemsList) {
                                if (product.category == Constants.DRINKS) {
                                    drinksList.add(product)
                                }
                            }
                            val intent = Intent(context, MenuByCategoryActivity::class.java)
                            intent.putParcelableArrayListExtra(Constants.DRINKS, drinksList)
                            startActivity(intent)
                        } else {
                            // Store is closed
                           showPopup()
                        }
                    }
                }
            }
        }
    }

    private fun refreshPage() {

        menuSwipeRefreshLayout.setOnRefreshListener {
            getMenuItemsList() // Reload Menu Items


            _binding!!.menuSwipeRefreshLayout.isRefreshing = false
        }
    }

    fun successMenuItemsList(productsList: ArrayList<Product>) {

        hideProgressDialog()


        mMenuItemsList = productsList

        val scambaneList = ArrayList<Product>()
        val chipsList = ArrayList<Product>()
        val russianList = ArrayList<Product>()
        val additionalMealList = ArrayList<Product>()
        val drinksList = ArrayList<Product>()


        for (product in productsList) {

            //Scambane List
            if (product.category == Constants.SCAMBANE) {
                scambaneList.add(product)
            }

            //Chips List
            if (product.category == Constants.CHIPS){
                chipsList.add(product)
            }

            //Russian List
            if (product.category == Constants.RUSSIAN){
                russianList.add(product)
            }

            //Additional List
            if (product.category == Constants.ADDITIONAL_MEALS){
                additionalMealList.add(product)
            }

            //Drinks List
            if (product.category == Constants.DRINKS){
                drinksList.add(product)
            }

        }

        //Scambane List RecycleView
        if (scambaneList.size > 0) {
            binding.rvMyScambaneItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
            binding.tvScambaneText.visibility = View.VISIBLE

            binding.rvMyScambaneItems.layoutManager = LinearLayoutManager(requireActivity(),
                LinearLayoutManager.HORIZONTAL,false)
            binding.rvMyScambaneItems.setHasFixedSize(true)

            adapter = MenuItemsListAdapter(requireActivity(), scambaneList, this)
            binding.rvMyScambaneItems.adapter = adapter


            myClickListener()


        } else {
            binding.rvMyScambaneItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }

        //Chips List RecycleView
        if (chipsList.size > 0) {
            binding.rvMyChipsItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
            binding.tvChipsText.visibility = View.VISIBLE

            binding.rvMyChipsItems.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL,false)
            binding.rvMyChipsItems.setHasFixedSize(true)

            adapter = MenuItemsListAdapter(requireActivity(), chipsList, this)
            binding.rvMyChipsItems.adapter = adapter


            myClickListener()


        } else {
            binding.rvMyChipsItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }

        //Russian List RecycleView
        if (russianList.size > 0) {
            binding.rvMyRussianItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
            binding.tvRussianText.visibility = View.VISIBLE

            binding.rvMyRussianItems.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL,false)
            binding.rvMyRussianItems.setHasFixedSize(true)

            adapter = MenuItemsListAdapter(requireActivity(), russianList, this)
            binding.rvMyRussianItems.adapter = adapter


            myClickListener()

        } else {
            binding.rvMyRussianItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }

        //Additional Meals List RecycleView
        if (additionalMealList.size > 0) {
            binding.rvMyAddtionalMealsItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
            binding.tvAdditionalMealsText.visibility = View.VISIBLE

            binding.rvMyAddtionalMealsItems.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL,false)
            binding.rvMyAddtionalMealsItems.setHasFixedSize(true)

            adapter = MenuItemsListAdapter(requireActivity(), additionalMealList, this)
            binding.rvMyAddtionalMealsItems.adapter = adapter


            myClickListener()

        } else {
            binding.rvMyAddtionalMealsItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }

        //Drinks List RecycleView
        if (drinksList.size > 0) {
            binding.rvMyDrinksItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
            binding.tvDrinksText.visibility = View.VISIBLE

            binding.rvMyDrinksItems.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL,false)
            binding.rvMyDrinksItems.setHasFixedSize(true)
            adapter = MenuItemsListAdapter(requireActivity(), drinksList, this)
            binding.rvMyDrinksItems.adapter = adapter


            myClickListener()

        } else {
            binding.rvMyDrinksItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }



    }


    private fun myClickListener(){


        //Define the onclick listener here that is defined in the adapter class and handle the click on an item in the base class.
        // Earlier we have done is a different way of creating the function and calling it from the adapter class based on the instance of the class.

        // START
        adapter.setOnClickListener(object :
            MenuItemsListAdapter.OnClickListener {
            override fun onClick(position: Int, product: Product) {


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

                        //  val mOpenCloseStore = openCloseStore

                        if (!openCloseStore.isStoreOpen){

                            hideProgressDialog()

                            showPopup()

                        }
                        else{

                            hideProgressDialog()

                            // Launch the product details screen from the dashboard.
                            // START

                            val intent = Intent(context, ProductDetailsActivity::class.java)
                            intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                            startActivity(intent)
                            // END
                        }

                    }
                    .addOnFailureListener { e ->

                        hideProgressDialog()
                    }
            }
        })
        // END


    }

    private fun getOpenCloseStoreInfo(callback: (Boolean) -> Unit) {

        showProgressDialog(resources.getString(R.string.please_wait))

        val mFirestore = FirebaseFirestore.getInstance()
        val docRef = "KUadjV036C6fvZrjmIWn"

        mFirestore.collection(Constants.OPEN_CLOSE_STORE)
            .document(docRef)
            .get()
            .addOnSuccessListener { document ->

                val openCloseStore = document.toObject(OpenCloseStore::class.java)

                if (openCloseStore != null) {
                    val isStoreOpen = openCloseStore.isStoreOpen
                    hideProgressDialog()
                    callback(isStoreOpen)
                } else {
                    hideProgressDialog()
                    callback(false) // Handle the case when the document is not found or cannot be converted to the expected type.
                }
            }
            .addOnFailureListener { e ->
                hideProgressDialog()
                callback(false) // Handle the failure case.
            }
    }


    /**
     * A function to get the dashboard items list from cloud firestore.
     */
    private fun getMenuItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMenuItemsList(this@MenuFragment)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPopup() {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_window, null)

        // Initialize the PopupWindow
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val focusable = true
        popupWindow = PopupWindow(popupView, width, height, focusable)

        // Dismiss the popup window when touched outside
        popupView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupWindow.dismiss()
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }

        val aboutUsTextView = popupView.findViewById<TextView>(R.id.about_us_popup)

        aboutUsTextView.setOnClickListener {
            startActivity(Intent(activity, AboutUsActivity::class.java))
        }


        // Display the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }


    private fun requestReviewInfo() {

        reviewManager = ReviewManagerFactory.create(requireContext())
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                reviewInfo = task.result
                showReviewFlow()
            } else {
                // There was some problem, log or handle the error code.
                @ReviewErrorCode val reviewErrorCode =
                    (task.getException() as ReviewException).errorCode
            }

        }

    }

    private fun showReviewFlow() {

        val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
        flow.addOnCompleteListener { _ ->
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        }
    }


    // This function checks if the user has completed an order if true then the app asks for a review
    private fun checkIfUserHasCompleteOrder(){
        // Here we pass the collection name from which we wants the data.
        mFirestore.collection(Constants.USER)
            // The document id to get the Fields of user.

            .document(FirestoreClass().getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                if (user.hasCompleteOrder == true) {
                    requestReviewInfo()
                }

            }.addOnFailureListener { e->
                e.message
            }
    }


}