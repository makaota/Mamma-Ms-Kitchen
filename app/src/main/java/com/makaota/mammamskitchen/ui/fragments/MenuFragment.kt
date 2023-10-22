package com.makaota.mammamskitchen.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.FragmentMenuBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.Favorites
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.ui.activities.CartListActivity
import com.makaota.mammamskitchen.ui.activities.MenuByCategoryActivity
import com.makaota.mammamskitchen.ui.activities.ProductDetailsActivity
import com.makaota.mammamskitchen.ui.activities.SettingsActivity
import com.makaota.mammamskitchen.ui.adapters.MenuItemsListAdapter
import com.makaota.mammamskitchen.utils.Constants
import com.shashank.sony.fancytoastlib.FancyToast

const val MENU_FRAGMENT_TAG = "MenuFragment"
class MenuFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentMenuBinding? = null
    private lateinit var menuSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mMenuItemsList: ArrayList<Product>


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
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
        binding.tvScambane.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.colorPrimaryText))

        binding.tvChips.text = "Chips"
        binding.tvChips.isEnabled = true
        binding.tvChips.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.colorPrimaryText))

        binding.tvRussian.text = "Russian"
        binding.tvRussian.isEnabled = true
        binding.tvRussian.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.colorPrimaryText))

        binding.tvAdditionalMeals.text = "Additionals"
        binding.tvAdditionalMeals.isEnabled = true
        binding.tvAdditionalMeals.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.colorPrimaryText))

        binding.tvDrinks.text = "Drinks"
        binding.tvDrinks.isEnabled = true
        binding.tvDrinks.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.colorPrimaryText))

        getMenuItemsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {

        if (v != null) {
            when (v.id) {
                R.id.tv_scambane -> {

                    binding.tvScambane.text = resources.getString(R.string.please_wait)
                    binding.tvScambane.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.colorOffWhite))
                    binding.tvChips.isEnabled = false
                    binding.tvRussian.isEnabled = false
                    binding.tvAdditionalMeals.isEnabled = false
                    binding.tvDrinks.isEnabled = false


                    val scambaneList = ArrayList<Product>()

                    for (product in mMenuItemsList){

                        if (product.category == Constants.SCAMBANE){

                            scambaneList.add(product)

                        }
                    }

                    Log.i(MENU_FRAGMENT_TAG,"List Of Scambanes $scambaneList")

                    val intent = Intent(context, MenuByCategoryActivity::class.java)
                    intent.putParcelableArrayListExtra(Constants.SCAMBANE, scambaneList)

                    startActivity(intent)

                }

                R.id.tv_chips -> {

                    binding.tvScambane.isEnabled = false
                    binding.tvChips.text = resources.getString(R.string.please_wait)
                    binding.tvChips.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.colorOffWhite))
                    binding.tvRussian.isEnabled = false
                    binding.tvAdditionalMeals.isEnabled = false
                    binding.tvDrinks.isEnabled = false


                    val chipsList = ArrayList<Product>()

                    for (product in mMenuItemsList){

                        if (product.category == Constants.CHIPS){

                            chipsList.add(product)

                        }
                    }
                    val intent = Intent(context, MenuByCategoryActivity::class.java)
                    intent.putParcelableArrayListExtra(Constants.CHIPS, chipsList)
                    startActivity(intent)
                }

                R.id.tv_russian -> {

                    binding.tvScambane.isEnabled = false
                    binding.tvChips.isEnabled = false
                    binding.tvRussian.text = resources.getString(R.string.please_wait)
                    binding.tvRussian.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.colorOffWhite))
                    binding.tvAdditionalMeals.isEnabled = false
                    binding.tvDrinks.isEnabled = false

                    val russianList = ArrayList<Product>()

                    for (product in mMenuItemsList){

                        if (product.category == Constants.RUSSIAN){

                            russianList.add(product)

                        }
                    }
                    val intent = Intent(context, MenuByCategoryActivity::class.java)
                    intent.putParcelableArrayListExtra(Constants.RUSSIAN, russianList)
                    startActivity(intent)
                }

                R.id.tv_additional_meals -> {

                    binding.tvScambane.isEnabled = false
                    binding.tvChips.isEnabled = false
                    binding.tvRussian.isEnabled = false
                    binding.tvAdditionalMeals.text = resources.getString(R.string.please_wait)
                    binding.tvAdditionalMeals.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.colorOffWhite))
                    binding.tvDrinks.isEnabled = false

                    val additionalList = ArrayList<Product>()

                    for (product in mMenuItemsList){

                        if (product.category == Constants.ADDITIONAL_MEALS){

                            additionalList.add(product)

                        }
                    }
                    val intent = Intent(context, MenuByCategoryActivity::class.java)
                    intent.putParcelableArrayListExtra(Constants.ADDITIONAL_MEALS, additionalList)
                    startActivity(intent)
                }

                R.id.tv_drinks -> {

                    binding.tvScambane.isEnabled = false
                    binding.tvChips.isEnabled = false
                    binding.tvRussian.isEnabled = false
                    binding.tvAdditionalMeals.isEnabled = false
                    binding.tvDrinks.text = resources.getString(R.string.please_wait)
                    binding.tvDrinks.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.colorOffWhite))

                    val drinksList = ArrayList<Product>()

                    for (product in mMenuItemsList){

                        if (product.category == Constants.DRINKS){

                            drinksList.add(product)

                        }
                    }
                    val intent = Intent(context, MenuByCategoryActivity::class.java)
                    intent.putParcelableArrayListExtra(Constants.DRINKS, drinksList)
                    startActivity(intent)
                }
            }
        }
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

            mMenuItemsList = menuItemsList

            binding.rvMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE

            binding.rvMyProductItems.layoutManager = GridLayoutManager(activity, 2)
            binding.rvMyProductItems.setHasFixedSize(true)

            val adapter = MenuItemsListAdapter(requireActivity(), menuItemsList, this)
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