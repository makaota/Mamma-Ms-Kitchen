package com.makaota.mammamskitchen.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.FragmentOrdersBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.Order
import com.makaota.mammamskitchen.ui.adapters.MyOrdersListAdapter

class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Create a function to get the success result of the my order list from cloud firestore.
    // START
    /**
     * A function to get the success result of the my order list from cloud firestore.
     *
     * @param ordersList List of my orders.
     */
    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {

        // Hide the progress dialog.
        hideProgressDialog()

        // Populate the orders list in the UI.
        // START
        if (ordersList.size > 0) {

            _binding!!.rvMyOrderItems.visibility = View.VISIBLE
            _binding!!.tvNoOrdersFound.visibility = View.GONE

            _binding!!.rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            _binding!!.rvMyOrderItems.setHasFixedSize(true)

            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            _binding!!.rvMyOrderItems.adapter = myOrdersAdapter
        } else {
            _binding!!.rvMyOrderItems.visibility = View.GONE
            _binding!!.tvNoOrdersFound.visibility = View.VISIBLE
        }
        // END
    }
    // END

    // Create a function to call the firestore class function to get the list of my orders.
    // START
    /**
     * A function to get the list of my orders.
     */
    private fun getMyOrdersList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }
    // END
}