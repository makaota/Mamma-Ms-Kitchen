package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAboutUsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        binding.tvContactNumber.setOnClickListener{

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:0744386460")
            startActivity(intent)
        }

        binding.tvAddress.setOnClickListener{


            val longitude = -27.85465846644504
            val latitude = 26.756570099646055

            val uri = "geo:$latitude,$longitude?z=15"; // z=15 sets the zoom level

            // Create an Intent to launch Google Maps
            // Create an Intent to launch Google Maps
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

            // Check if Google Maps is installed on the device

            // Check if Google Maps is installed on the device
            if (mapIntent.resolveActivity(packageManager) != null) {
                // Start the Google Maps activity
                startActivity(mapIntent)
            } else {
                // If Google Maps is not installed, you can handle this case appropriately (e.g., show a message).
                // You can redirect the user to the Google Play Store to download Google Maps if desired.

                Toast.makeText(this,"Install Google Maps on your device",Toast.LENGTH_SHORT).show()
            }
        }

    }


    // Create a function to setup action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAboutUsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAboutUsActivity.setNavigationOnClickListener { onBackPressed() }
    }
    // END
}