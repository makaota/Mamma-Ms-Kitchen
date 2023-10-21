package com.makaota.mammamskitchen.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makaota.mammamskitchen.databinding.MenuByCategoryListLayoutBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.CartItem
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.ui.activities.MenuByCategoryActivity
import com.makaota.mammamskitchen.utils.GlideLoader
import com.shashank.sony.fancytoastlib.FancyToast


open class MenuByCategoryListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private var activity: MenuByCategoryActivity,
    private var binding: MenuByCategoryListLayoutBinding?,
    private val cartItems: Set<String>, // store the product IDs of items in the cart
    private val favorites: Set<String>
) : RecyclerView.Adapter<MenuByCategoryListAdapter.MyViewHolder>() {

    // Create a global variable for OnClickListener interface.
    // START
    // A global variable for OnClickListener interface.
    private var onClickListener: OnClickListener? = null
    // END

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {


        binding = MenuByCategoryListLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding!!)
    }


    // Create A function for OnClickListener where the Interface is the expected parameter and assigned to the global variable.
    // START
    /**
     * A function for OnClickListener where the Interface is the expected parameter and assigned to the global variable.
     *
     * @param onClickListener
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    // END

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        val productID = model.product_id

        // Check if the item is in the cart
        val isItemInCart = cartItems.contains(productID)

        val isItemInFavorites = favorites.contains(productID)


        with(holder) {
            with(model) {


                holder.setIsRecyclable(true)
                GlideLoader(context).loadProductPicture(
                    model.image, binding.ivProductItemImage
                )
                binding.tvItemTitle.text = model.title
                binding.tvItemDescription.text = model.description
                binding.tvItemPrice.text = "R${model.price}"


                // Check if the item is in the cart and adjust button visibility
                if (isItemInCart) {
                    binding.ibGoToCart.visibility = View.VISIBLE
                    binding.ibAddToCart.visibility = View.GONE
                } else {
                    binding.ibGoToCart.visibility = View.GONE
                    binding.ibAddToCart.visibility = View.VISIBLE
                }

                // Check if the item is in the favorites and adjust button visibility
                if (isItemInFavorites) {
                    binding.ibRemoveToFavorite.visibility = View.VISIBLE
                    binding.ibAddToFavorite.visibility = View.GONE
                } else {
                    binding.ibRemoveToFavorite.visibility = View.GONE
                    binding.ibAddToFavorite.visibility = View.VISIBLE
                }


                holder.itemView.setOnClickListener {
                    if (onClickListener != null) {
                        onClickListener!!.onClick(position, model)
                    }
                }

                binding.ibAddToCart.setOnClickListener{

                    activity.addToCart(model)

                    FancyToast.makeText(context,"${model.title} added to cart",
                        FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show()

                    binding.ibGoToCart.visibility = View.VISIBLE
                    binding.ibAddToCart.visibility = View.GONE
                }

                binding.ibGoToCart.setOnClickListener{

                    activity.goToCart()
                }

                binding.ibAddToFavorite.setOnClickListener{

                    activity.addToFavorites(model)

                    FancyToast.makeText(context,"${model.title} added to favorites",
                        FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show()

                    binding.ibRemoveToFavorite.visibility = View.VISIBLE
                    binding.ibAddToFavorite.visibility = View.GONE
                }

                binding.ibRemoveToFavorite.setOnClickListener{

                    activity.removeFavorites(productID)

                    FancyToast.makeText(context,"${model.title} removed from favorites",
                        FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show()

                    binding.ibRemoveToFavorite.visibility = View.GONE
                    binding.ibAddToFavorite.visibility = View.VISIBLE
                }
            }


        }


    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    // Create a function to notify the success result of item exists in the cart.
    // START
    /**
     * A function to notify the success result of item exists in the cart.
     */
    fun productExistsInCart() {

        // Hide the progress dialog.
        hideProgressDialog()

        binding!!.ibGoToCart.visibility = View.VISIBLE
        binding!!.ibAddToCart.visibility = View.GONE
    }
    // END

    fun hideProgressDialog() {

    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(val binding: MenuByCategoryListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


    // Create an interface for OnClickListener.
    /**
     * An interface for onclick items.
     */
    interface OnClickListener {

        // Define a function to get the required params when user clicks on the item view in the interface.
        // START
        fun onClick(position: Int, product: Product)
        // END
    }
    // END
}