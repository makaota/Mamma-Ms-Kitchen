package com.makaota.mammamskitchen.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makaota.mammamskitchen.databinding.ItemMenuLayoutBinding
import com.makaota.mammamskitchen.models.Favorites
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.ui.fragments.MyFavoritesFragment
import com.makaota.mammamskitchen.utils.GlideLoader
import java.util.ArrayList

open class MyFavoritesListAdapter(
    private val context: Context,
    private var list: ArrayList<Favorites>,
    private var fragment: MyFavoritesFragment
) : RecyclerView.Adapter<MyFavoritesListAdapter.MyViewHolder>() {

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


        val binding = ItemMenuLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
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

        with(holder) {
            with(model) {
                GlideLoader(context).loadProductPicture(
                    model.image, binding.ivDashboardItemImage
                )
                binding.tvDashboardItemTitle.text = model.title
                binding.tvDashboardItemPrice.text = "R${model.price}"

                holder.itemView.setOnClickListener {
                    if (onClickListener != null) {
                        onClickListener!!.onClick(position, model)
                    }
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
    class MyViewHolder(val binding: ItemMenuLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    // Create an interface for OnClickListener.
    /**
     * An interface for onclick items.
     */
    interface OnClickListener {

        // Define a function to get the required params when user clicks on the item view in the interface.
        // START
        fun onClick(position: Int, product: Favorites)
        // END
    }
    // END
}