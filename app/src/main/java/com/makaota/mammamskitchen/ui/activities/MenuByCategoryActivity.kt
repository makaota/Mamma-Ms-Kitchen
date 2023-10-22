package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityMenuByCategoryBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.CartItem
import com.makaota.mammamskitchen.models.Favorites
import com.makaota.mammamskitchen.models.Order
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.ui.adapters.MenuByCategoryListAdapter
import com.makaota.mammamskitchen.utils.Constants
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val MENU_BY_CATEGORY_TAG = "MenuByCategoryActivity"

class MenuByCategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityMenuByCategoryBinding

    private lateinit var menuByCategoryList: ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupMenuByCategoryActivityActionBar()

        if (intent.hasExtra(Constants.SCAMBANE)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.SCAMBANE)!!
            binding.tvMenuByCategoryTitle.text = Constants.SCAMBANE

        }


        if (intent.hasExtra(Constants.CHIPS)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.CHIPS)!!
            binding.tvMenuByCategoryTitle.text = Constants.CHIPS

        }

        if (intent.hasExtra(Constants.RUSSIAN)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.RUSSIAN)!!
            binding.tvMenuByCategoryTitle.text = Constants.RUSSIAN

        }

        if (intent.hasExtra(Constants.ADDITIONAL_MEALS)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.ADDITIONAL_MEALS)!!
            binding.tvMenuByCategoryTitle.text = Constants.ADDITIONAL_MEALS

        }

        if (intent.hasExtra(Constants.DRINKS)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.DRINKS)!!
            binding.tvMenuByCategoryTitle.text = Constants.DRINKS
        }

        getMenuByCategory()
    }

    override fun onResume() {
        super.onResume()

        getMenuByCategory()
    }

    /** Making use of Kotlin's suspend and CoroutineScope
     * Inside the activity method that retrieves the product IDs in the cart from Firestore database
     * In the code above, we fetch cart items for a specific user by querying the Firestore collection
     * named "CART_ITEMS" where the "USER_ID" matches the user's ID.
     * The product IDs are extracted from the retrieved documents and added to the cartItems HashSet
     * In this code, we're using Kotlin Coroutines to fetch the cart items
     * asynchronously and suspend the function execution until the data is available
     */
    private suspend fun fetchCartItems(userId: String): Set<String> {
        return withContext(Dispatchers.IO) {
            val cartItems = HashSet<String>()
            val firestore = FirebaseFirestore.getInstance()

            val documents = firestore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, userId)
                .get()
                .await() // This is an extension function for Firestore queries

            for (document in documents) {
                val productId = document.getString(Constants.PRODUCT_ID)
                if (!productId.isNullOrBlank()) {
                    cartItems.add(productId)
                }
            }

            cartItems
        }
    }

    private suspend fun fetchFavoritesItems(userId: String): Set<String> {
        return withContext(Dispatchers.IO) {
            val cartItems = HashSet<String>()
            val firestore = FirebaseFirestore.getInstance()

            val documents = firestore.collection(Constants.FAVORITES)
                .whereEqualTo(Constants.APP_USER_ID, userId)
                .get()
                .await() // This is an extension function for Firestore queries

            for (document in documents) {
                val productId = document.getString(Constants.PRODUCT_ID)
                if (!productId.isNullOrBlank()) {
                    cartItems.add(productId)
                }
            }

            cartItems
        }
    }

    private fun getMenuByCategory(){

        val userId = FirestoreClass().getCurrentUserId()

        val cartItems: Set<String> = runBlocking {
            fetchCartItems(userId)
        }

        val favorites: Set<String> = runBlocking {
            fetchFavoritesItems(userId)
        }

        binding.rvMenuByCategory.layoutManager = LinearLayoutManager(this)
        val adapter = MenuByCategoryListAdapter(this, menuByCategoryList, this,null,cartItems,favorites)
        binding.rvMenuByCategory.adapter = adapter


        //Define the onclick listener here that is defined in the adapter class and handle the click on an item in the base class.
        // Earlier we have done is a different way of creating the function and calling it from the adapter class based on the instance of the class.

        // START
        adapter.setOnClickListener(object :
            MenuByCategoryListAdapter.OnClickListener {
            override fun onClick(position: Int, product: Product) {

                // Launch the product details screen from the dashboard.
                // START

                val intent =
                    Intent(this@MenuByCategoryActivity, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                startActivity(intent)
                // END
            }
        })
        // END
    }

    fun removeFavorites(productId: String){

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().deleteMyFavorites(this, productId)

    }

    fun favoritesDeleteSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        FancyToast.makeText(
            this, "removed from favorites successfully",
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true
        ).show()

    }


    // Create a function to prepare the cart item to add it to the cart.
    // START
    /**
     * A function to prepare the cart item to add it to the cart.
     */
     fun addToCart(mProductDetails: Product) {

        val cartItem = CartItem(
            FirestoreClass().getCurrentUserId(),
            mProductDetails.product_id,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        // Call the function of Firestore class to add the cart item to the cloud firestore along with the required params.
        // START
        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addCartItems(this@MenuByCategoryActivity, cartItem)
        // END
    }
    // END

    // Create a function to notify the success result of item added to the to cart.\
    // START
    fun addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        FancyToast.makeText(
            this@MenuByCategoryActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true
        ).show()


    }
    // END

    fun goToCart() {
        startActivity(
            Intent(
                this@MenuByCategoryActivity,
                CartListActivity::class.java
            )
        )
    }

    // Create a function to prepare the cart item to add it to the cart.
    // START
    /**
     * A function to prepare the favorites item to add it to the favorites.
     */
    fun addToFavorites(mProductDetails: Product) {

        val favorites = Favorites(
            user_id = mProductDetails.user_id,
            title = mProductDetails.title,
            category = mProductDetails.category,
            app_user_id = FirestoreClass().getCurrentUserId(),
            description = mProductDetails.description,
            price = mProductDetails.price,
            image = mProductDetails.image,
            product_id = mProductDetails.product_id
        )

        // Call the function of Firestore class to add the cart item to the cloud firestore along with the required params.
        // START
        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addToFavorites(this@MenuByCategoryActivity, favorites)
        // END
    }
    // END

    fun addToFavoritesSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        FancyToast.makeText(
            this, "added to favorites successfully",
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true
        ).show()

    }

    private fun setupMenuByCategoryActivityActionBar() {
        val toolbarSettingsActivity = findViewById<Toolbar>(R.id.toolbar_menu_category_activity)
        setSupportActionBar(toolbarSettingsActivity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }




}