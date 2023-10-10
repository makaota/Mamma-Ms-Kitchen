package com.makaota.mammamskitchen.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makaota.mammamskitchen.databinding.ItemListLayoutBinding
import com.makaota.mammamskitchen.databinding.NotificationsListLayoutBinding
import com.makaota.mammamskitchen.models.Notifications
import com.makaota.mammamskitchen.models.Order
import com.makaota.mammamskitchen.ui.activities.MyOrderDetailsActivity
import com.makaota.mammamskitchen.ui.fragments.NotificationsFragment
import com.makaota.mammamskitchen.ui.fragments.OrdersFragment
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

open class MyNotificationsListAdapter(
    private val context: Context,
    private var list: ArrayList<Notifications>,
    private var fragment: NotificationsFragment
) : RecyclerView.Adapter<MyNotificationsListAdapter.MyViewHolder>() {


    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            NotificationsListLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
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



                // Set the Date in the UI.
                // START
                // Date Format in which the date will be displayed in the UI.
                val dateFormat = "dd MMM yyyy HH:mm"
                // Create a DateFormatter object for displaying date in specified format.
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

                // Create a calendar object that will convert the date and time value in milliseconds to date.
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = model.orderDateTime

                val orderDateTime = formatter.format(calendar.time)
                // END

                binding.orderDetailsId.text = model.title
                binding.orderDetailsDate.text = orderDateTime
                binding.orderStatus.text = model.orderStatus
                binding.orderMessage.text = model.orderMessage
                binding.orderNumber.text = model.orderNumber

                binding.deleteNotification.setOnClickListener {

                    fragment.deleteNotification(model.documentId)
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
    inner class MyViewHolder(val binding: NotificationsListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
// END
