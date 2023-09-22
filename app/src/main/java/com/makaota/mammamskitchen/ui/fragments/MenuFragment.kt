package com.makaota.mammamskitchen.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.FragmentMenuBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.ui.activities.CartListActivity
import com.makaota.mammamskitchen.ui.activities.ProductDetailsActivity
import com.makaota.mammamskitchen.ui.activities.SettingsActivity
import com.makaota.mammamskitchen.ui.adapters.MenuItemsListAdapter
import com.makaota.mammamskitchen.utils.Constants
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.ArrayList

class MenuFragment : BaseFragment() {

    private var _binding: FragmentMenuBinding? = null
    private lateinit var menuSwipeRefreshLayout: SwipeRefreshLayout


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
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
        return root
    }

    override fun onResume() {
        super.onResume()
        getMenuItemsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshPage() {

        menuSwipeRefreshLayout.setOnRefreshListener {
            getMenuItemsList() // Reload Menu Items
            FancyToast.makeText(
                requireContext(),
                "Menu Refreshed",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                true
            ).show()

            _binding!!.menuSwipeRefreshLayout.isRefreshing = false
        }
    }

    fun successMenuItemsList(menuItemsList: ArrayList<Product>) {

        hideProgressDialog()

        if (menuItemsList.size > 0) {

            binding.rvMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE

            binding.rvMyProductItems.layoutManager = GridLayoutManager(activity, 2)
            binding.rvMyProductItems.setHasFixedSize(true)

            val adapter = MenuItemsListAdapter(requireActivity(), menuItemsList)
            binding.rvMyProductItems.adapter = adapter


            //Define the onclick listener here that is defined in the adapter class and handle the click on an item in the base class.
            // Earlier we have done is a different way of creating the function and calling it from the adapter class based on the instance of the class.

            // START
            adapter.setOnClickListener(object :
                MenuItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {

                    // Launch the product details screen from the dashboard.
                    // START

                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    startActivity(intent)
                    // END
                }
            })
            // END

        } else {
            binding.rvMyProductItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
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
}