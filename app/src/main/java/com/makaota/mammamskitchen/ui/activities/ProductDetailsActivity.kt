package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityProductDetailsBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.CartItem
import com.makaota.mammamskitchen.models.Favorites
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.ui.fragments.MenuFragment
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.utils.GlideLoader
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.ArrayList

const val PRODUCT_DETAILS_TAG = "ProductDetails"
class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityProductDetailsBinding

    private lateinit var mProductDetails: Product
    private var mProductId: String = ""

    private lateinit var favorites: Favorites
    private lateinit var favDishDocId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMenuDetailsActivityActionBar()

        // Get the product id through the intent extra and print it in the log.
        // START
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }
        // END

        getProductDetails()


        FirestoreClass().getMyFavoritesList(this)


        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
        binding.ibAddToFavoriteFromMenu.setOnClickListener(this)
        binding.ibRemoveToFavoriteFromMenu.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        FirestoreClass().getMyFavoritesList(this)
    }

    override fun onClick(v: View?) {
        // Handle the click event of the Add to cart button and call the addToCart function.
        // START
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }

                R.id.ib_add_to_favorite_from_menu -> {


                    showProgressDialog(resources.getString(R.string.please_wait))

                    FirestoreClass().addToFavorites(this, favorites)

                    FirestoreClass().getMyFavoritesList(this) // update the list


                }

                R.id.ib_remove_to_favorite_from_menu -> {


                    showProgressDialog(resources.getString(R.string.please_wait))

                    FirestoreClass().getMyFavoritesList(this)

                    removeFavorites(favDishDocId)

                    Log.i(PRODUCT_DETAILS_TAG,"TAG Ke $favDishDocId")


                }
            }
        }
        // END
    }

    private fun removeFavorites(documentId: String){

        FirestoreClass().deleteMyFavorites(this, documentId)

    }

    // Create a function to prepare the cart item to add it to the cart.
    // START
    /**
     * A function to prepare the cart item to add it to the cart.
     */
    private fun addToCart() {

        val cartItem = CartItem(
            FirestoreClass().getCurrentUserId(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        // Call the function of Firestore class to add the cart item to the cloud firestore along with the required params.
        // START
        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addCartItems(this@ProductDetailsActivity, cartItem)
        // END
    }
    // END

    // Create a function to notify the success result of item added to the to cart.\
    // START
    fun addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        FancyToast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true
        ).show()

        // Change the buttons visibility once the item is added to the cart.
        // Hide the AddToCart button if the item is already in the cart.
        binding.btnAddToCart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        binding.btnGoToCart.visibility = View.VISIBLE
    }
    // END

    // Create a function to notify the success result of item exists in the cart.
    // START
    /**
     * A function to notify the success result of item exists in the cart.
     */
    fun productExistsInCart() {

        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        binding.btnAddToCart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        binding.btnGoToCart.visibility = View.VISIBLE
    }
    // END

    // Create a function to notify the success result of the product details based on the product id.
    // START
    /**
     * A function to notify the success result of the product details based on the product id.
     *
     * @param product A model class with product details.
     */
    fun productDetailsSuccess(product: Product) {

        mProductDetails = product

        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )

        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "R${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity


        // Update the UI if the stock quantity is 0.
        // START
        if (product.stock_quantity.toInt() == 0) {

            // Hide Progress dialog.
            hideProgressDialog()

            // Hide the AddToCart button if the item is already in the cart.
            binding.btnAddToCart.visibility = View.GONE

            binding.tvProductDetailsStockQuantity.text =
                resources.getString(R.string.lbl_out_of_stock)

            binding.tvProductDetailsStockQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        } else {
            // There is no need to check the cart list if the product owner himself is seeing the product details.
            if (FirestoreClass().getCurrentUserId() == product.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }

        }

        favorites = Favorites(
            user_id = mProductDetails.user_id,
            title = mProductDetails.title,
            category = mProductDetails.category,
            app_user_id = FirestoreClass().getCurrentUserId(),
            description = mProductDetails.description,
            price = mProductDetails.price,
            image = mProductDetails.image,
            product_id = mProductId)


    }

    fun addToFavoritesSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        FancyToast.makeText(
            this, "${mProductDetails.title} added to favorites",
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true
        ).show()

        binding.ibRemoveToFavoriteFromMenu.visibility = View.VISIBLE
        binding.ibAddToFavoriteFromMenu.visibility = View.GONE
    }

    fun favoritesDeleteSuccess() {

        // Hide the progress dialog.
        hideProgressDialog()

        FancyToast.makeText(
            this, "${mProductDetails.title} removed from favorites",
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true
        ).show()

        binding.ibRemoveToFavoriteFromMenu.visibility = View.GONE
        binding.ibAddToFavoriteFromMenu.visibility = View.VISIBLE
    }

    fun populateFavoritesListInUI(favList: ArrayList<Favorites>) {

        var productIdFromFavorites = ""

        for (favorites in favList){
            productIdFromFavorites = favorites.product_id
            if (productIdFromFavorites == mProductId){

                binding.ibRemoveToFavoriteFromMenu.visibility = View.VISIBLE
                binding.ibAddToFavoriteFromMenu.visibility = View.GONE

                favDishDocId = favorites.documentId
                Log.i(PRODUCT_DETAILS_TAG,"fav dish id $favDishDocId")
            }

        }
        Log.i(PRODUCT_DETAILS_TAG,"from Fav $productIdFromFavorites to Product $mProductId")


    }




    // Create a function to call the firestore class function that will get the product details from cloud firestore based on the product id.
    // START
    /**
     * A function to call the firestore class function that will get the product details from cloud firestore based on the product id.
     */
    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)


    }
    // END

    private fun setupMenuDetailsActivityActionBar() {

        val toolbarSettingsActivity = findViewById<Toolbar>(R.id.toolbar_product_details_activity)
        setSupportActionBar(toolbarSettingsActivity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }




}