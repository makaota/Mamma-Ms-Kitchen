package com.makaota.mammamskitchen.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.DialogProgressBinding
import com.shashank.sony.fancytoastlib.FancyToast

open class BaseActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    private var doubleBackToExitPressedOnce = false

    // Create a function to load and show the progress dialog.
    // START
    /**
     * This function is used to show the progress dialog with the title and message to user.
     */
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        var binding: DialogProgressBinding = DialogProgressBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(binding.root)

        binding.tvProgressText.text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }
    // END

    // Create a function to hide progress dialog.
    // START
    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
    // END

    // Create a function to show the success and error messages in snack bar component.
    // START
    /**
     * A function to show the success and error messages in snack bar component.
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackBar.show()
    }
    // END

    // Create a function to implement the double back press to exit the app.
    // START
    /**
     * A function to implement the double back press feature to exit the app.
     */
    fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            onBackPressedDispatcher.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true

        FancyToast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            FancyToast.LENGTH_SHORT, FancyToast.INFO,true
        ).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
    // END
}