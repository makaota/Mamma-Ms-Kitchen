package com.makaota.mammamskitchen.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ItemListLayoutBinding
import com.makaota.mammamskitchen.models.Order
import com.makaota.mammamskitchen.ui.activities.MyOrderDetailsActivity
import com.makaota.mammamskitchen.ui.fragments.OrdersFragment
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.utils.GlideLoader
import java.util.ArrayList

// Create an adapter class for my list of orders.
// START
open class MyOrdersListAdapter(
    private val context: Context,
    private var list: ArrayList<Order>,
    private var fragment: OrdersFragment
) : RecyclerView.Adapter<MyOrdersListAdapter.MyViewHolder>() {


    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
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
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        with(holder) {
            with(model) {
                GlideLoader(context).loadProductPicture(
                    model.image,
                    binding.ivItemImage
                )

                binding.tvItemName.text = model.title
                binding.tvItemPrice.text = "R${model.total_amount}"
                binding.ibDeleteProduct.visibility = View.GONE

                if (model.orderStatus == "Pending") {
                    binding.tvUpdateStatus.text = "Pending"
                    binding.tvUpdateStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent
                        )
                    )

                } else if (model.orderStatus == "In Process") {
                    binding.tvUpdateStatus.text = "In Process"
                    binding.tvUpdateStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorOrderStatusInProcess
                        )
                    )

                } else if (model.orderStatus == "Preparing") {
                    binding.tvUpdateStatus.text = "Preparing"
                    binding.tvUpdateStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorOrderStatusPreparing
                        )
                    )

                } else if (model.orderStatus == "Ready For Collection") {
                    binding.tvUpdateStatus.text = "Ready For Collection"
                    binding.tvUpdateStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorOrderStatusReadyForCollection
                        )
                    )

                } else if (model.orderStatus == "Delivered") {
                    binding.tvUpdateStatus.text = "Delivered"
                    binding.tvUpdateStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorOrderStatusDelivered
                        )
                    )

                    binding.ibDeleteProduct.visibility = View.VISIBLE

                    binding.ibDeleteProduct.setOnClickListener {

                        fragment.deleteDeliveredOrder(model.id)
                    }
                }

                // Assign the click event to my order item and launch and pass the details to the detail page through intent.
                // START
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, MyOrderDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS, model)
                    context.startActivity(intent)
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

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class MyViewHolder(val binding: ItemListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
// END
