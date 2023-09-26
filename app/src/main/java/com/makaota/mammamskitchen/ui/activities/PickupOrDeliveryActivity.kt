package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityPickupOrDeliveryBinding
import com.makaota.mammamskitchen.utils.Constants

class PickupOrDeliveryActivity : BaseActivity() {

    lateinit var binding: ActivityPickupOrDeliveryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickupOrDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        binding.tvPickup.setOnClickListener{

            val intent = Intent(this, CheckoutActivity::class.java)
            //intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }

        binding.tvDelivery.setOnClickListener{

            val intent = Intent(this, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }

    }

    // Create a function to setup action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarPickupDeliveryActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarPickupDeliveryActivity.setNavigationOnClickListener { onBackPressed() }
    }
    // END
}