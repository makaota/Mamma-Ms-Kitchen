package com.makaota.mammamskitchen.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ItemCartLayoutBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.CartItem
import com.makaota.mammamskitchen.ui.activities.CartListActivity
import com.makaota.mammamskitchen.ui.activities.DashboardActivity
import com.makaota.mammamskitchen.ui.fragments.MenuFragment
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.utils.GlideLoader

// Create a adapter class for CartItemsList.
// START
/**
 * A adapter class for dashboard items list.
 */
open class CartItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<CartItem>,
    private val updateCartItems: Boolean,
) : RecyclerView.Adapter<CartItemsListAdapter.MyViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ItemCartLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }

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

        with(holder){
            with(model){

                GlideLoader(context).loadProductPicture(model.image, binding.ivCartItemImage)

                binding.tvCartItemTitle.text = model.title
                binding.tvCartItemPrice.text = "R${model.price}"
                binding.tvCartQuantity.text = model.cart_quantity


                // Show the text Out of Stock when cart quantity is zero.
                // START
                if (model.cart_quantity == "0") {
                    binding.ibRemoveCartItem.visibility = View.GONE
                    binding.ibAddCartItem.visibility = View.GONE


                    // Update the UI components as per the param.
                    // START
                    if (updateCartItems) {
                        binding.ibDeleteCartItem.visibility = View.VISIBLE
                    } else {
                        binding.ibDeleteCartItem.visibility = View.GONE
                    }
                    // END

                    binding.tvCartQuantity.text =
                        context.resources.getString(R.string.lbl_out_of_stock)

                    binding.tvCartQuantity.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorSnackBarError
                        )
                    )
                } else {
                    // Update the UI components as per the param.
                    // START
                    if (updateCartItems) {
                        binding.ibRemoveCartItem.visibility = View.VISIBLE
                        binding.ibAddCartItem.visibility = View.VISIBLE
                        binding.ibDeleteCartItem.visibility = View.VISIBLE
                    } else {

                        binding.ibRemoveCartItem.visibility = View.GONE
                        binding.ibAddCartItem.visibility = View.GONE
                        binding.ibDeleteCartItem.visibility = View.GONE
                    }
                    // END

                    binding.tvCartQuantity.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorSecondaryText
                        )
                    )
                }
                // END

                // Assign the onclick event to the ib_delete_cart_item.
                // START
                binding.ibDeleteCartItem.setOnClickListener {
                    // Call the firestore class function to remove the item from cloud firestore.
                    // START
                    when (context) {
                        is CartListActivity -> {
                            context.showProgressDialog(context.resources.getString(R.string.please_wait))
                        }
                    }
                    FirestoreClass().removeItemFromCart(context, model.id)
                    // END
                }
                // END


                // Assign the click event to the ib_remove_cart_item.
                // START
                binding.ibRemoveCartItem.setOnClickListener {

                    // Call the update or remove function of firestore class based on the cart quantity.
                    // START
                    if (model.cart_quantity == "1") {
                        FirestoreClass().removeItemFromCart(context, model.id)
                    } else {

                        val cartQuantity: Int = model.cart_quantity.toInt()

                        val itemHashMap = HashMap<String, Any>()

                        itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                        // Show the progress dialog.

                        if (context is CartListActivity) {
                            context.showProgressDialog(context.resources.getString(R.string.please_wait))
                        }

                        FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                    }
                    // END
                }
                // END

                // Assign the click event to the ib_add_cart_item.
                // START
                binding.ibAddCartItem.setOnClickListener {

                    // Call the update function of firestore class based on the cart quantity.
                    // START
                    val cartQuantity: Int = model.cart_quantity.toInt()

                    if (cartQuantity < model.stock_quantity.toInt()) {

                        val itemHashMap = HashMap<String, Any>()

                        itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                        // Show the progress dialog.
                        if (context is CartListActivity) {
                            context.showProgressDialog(context.resources.getString(R.string.please_wait))
                        }


                        FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                    } else {
                        if (context is CartListActivity) {
                            context.showErrorSnackBar(
                                context.resources.getString(
                                    R.string.msg_for_available_stock,
                                    model.stock_quantity
                                ),
                                true
                            )
                        }
                    }
                    // END
                }
                // END

            }
        }


    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class MyViewHolder(val binding: ItemCartLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
