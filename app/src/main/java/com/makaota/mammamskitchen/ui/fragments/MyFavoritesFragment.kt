package com.makaota.mammamskitchen.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.FragmentMenuBinding
import com.makaota.mammamskitchen.databinding.FragmentMyFavoritesBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.Favorites
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.ui.activities.ProductDetailsActivity
import com.makaota.mammamskitchen.ui.adapters.MenuItemsListAdapter
import com.makaota.mammamskitchen.ui.adapters.MyFavoritesListAdapter
import com.makaota.mammamskitchen.ui.adapters.MyNotificationsListAdapter
import com.makaota.mammamskitchen.utils.Constants
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.ArrayList


class MyFavoritesFragment : BaseFragment() {

    private var _binding: FragmentMyFavoritesBinding? = null
    private lateinit var favoritesSwipeRefreshLayout: SwipeRefreshLayout
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyFavoritesBinding.inflate(inflater,container,false)
      val root: View = binding.root
        favoritesSwipeRefreshLayout = root.findViewById(R.id.favorites_swipe_refresh_layout)
        refreshPage()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getMyFavoritesList()
    }

    private fun refreshPage(){

        favoritesSwipeRefreshLayout.setOnRefreshListener {

            getMyFavoritesList()

            FancyToast.makeText(requireContext(),
                "Favorites Refreshed",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                true).show()

            _binding!!.favoritesSwipeRefreshLayout.isRefreshing = false

        }
    }

    private fun getMyFavoritesList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyFavoritesList(this)
    }

    fun populateFavoritesListInUI(list: ArrayList<Favorites>) {
        // Hide the progress dialog.
        hideProgressDialog()

        // Populate the orders list in the UI.
        // START
        if (list.size > 0) {

            _binding!!.rvMyFavoritesItems.visibility = View.VISIBLE
            _binding!!.tvNoFavoritesFound.visibility = View.GONE

            _binding!!.rvMyFavoritesItems.layoutManager = GridLayoutManager(activity, 2)
            _binding!!.rvMyFavoritesItems.setHasFixedSize(true)

            val myFavoritesListAdapter = MyFavoritesListAdapter(requireContext(),list,this)
            _binding!!.rvMyFavoritesItems.adapter = myFavoritesListAdapter


            // START
            myFavoritesListAdapter.setOnClickListener(object :
                MyFavoritesListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Favorites) {

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
            _binding!!.rvMyFavoritesItems.visibility = View.GONE
            _binding!!.tvNoFavoritesFound.visibility = View.VISIBLE
        }



    }
    // END

}