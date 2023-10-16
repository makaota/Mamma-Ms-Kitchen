package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityMenuByCategoryBinding
import com.makaota.mammamskitchen.models.Order
import com.makaota.mammamskitchen.models.Product
import com.makaota.mammamskitchen.ui.adapters.MenuByCategoryListAdapter
import com.makaota.mammamskitchen.utils.Constants

const val MENU_BY_CATEGORY_TAG = "MenuByCategoryActivity"

class MenuByCategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityMenuByCategoryBinding

    private lateinit var menuByCategoryList: ArrayList<Product>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupMenuByCategoryActivityActionBar()

        if (intent.hasExtra(Constants.SCAMBANE)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.SCAMBANE)!!
            binding.tvMenuByCategoryTitle.text = Constants.SCAMBANE
        }


        if (intent.hasExtra(Constants.CHIPS)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.CHIPS)!!
            binding.tvMenuByCategoryTitle.text = Constants.CHIPS


        }

        if (intent.hasExtra(Constants.RUSSIAN)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.RUSSIAN)!!
            binding.tvMenuByCategoryTitle.text = Constants.RUSSIAN

        }

        if (intent.hasExtra(Constants.ADDITIONAL_MEALS)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.ADDITIONAL_MEALS)!!
            binding.tvMenuByCategoryTitle.text = Constants.ADDITIONAL_MEALS

        }

        if (intent.hasExtra(Constants.DRINKS)) {
            menuByCategoryList = intent.getParcelableArrayListExtra(Constants.DRINKS)!!
            binding.tvMenuByCategoryTitle.text = Constants.DRINKS
        }
        getMenuByCategory()

    }

    private fun getMenuByCategory(){

        binding.rvMenuByCategory.layoutManager = LinearLayoutManager(this)


        val adapter = MenuByCategoryListAdapter(this, menuByCategoryList)
        binding.rvMenuByCategory.adapter = adapter


        //Define the onclick listener here that is defined in the adapter class and handle the click on an item in the base class.
        // Earlier we have done is a different way of creating the function and calling it from the adapter class based on the instance of the class.

        // START
        adapter.setOnClickListener(object :
            MenuByCategoryListAdapter.OnClickListener {
            override fun onClick(position: Int, product: Product) {

                // Launch the product details screen from the dashboard.
                // START

                val intent =
                    Intent(this@MenuByCategoryActivity, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                startActivity(intent)
                // END
            }
        })
        // END
    }

    private fun setupMenuByCategoryActivityActionBar() {
        val toolbarSettingsActivity = findViewById<Toolbar>(R.id.toolbar_menu_category_activity)
        setSupportActionBar(toolbarSettingsActivity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }
}