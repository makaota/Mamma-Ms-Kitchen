package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivitySettingsBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.User
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.utils.GlideLoader
import com.shashank.sony.fancytoastlib.FancyToast

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSettingsActionBar()
        binding.btnLogout.setOnClickListener(this)
        binding.tvEdit.setOnClickListener(this)
        binding.llAddress.setOnClickListener(this)
        binding.tvDeleteAccount.setOnClickListener(this)

    }

    private fun setupSettingsActionBar() {

        val toolbarSettingsActivity = findViewById<Toolbar>(R.id.toolbar_settings_activity)
        setSupportActionBar(toolbarSettingsActivity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // Create a function to get the user details from firestore.
    // START
    /**
     * A function to get the user details from firestore.
     */
    private fun getUserDetails() {

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class to get the user details from firestore which is already created.
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {

        mUserDetails = user
        // Set the user details to UI.
        // START
        // Hide the progress dialog
        hideProgressDialog()

        // Load the image using the Glide Loader class.
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, binding.ivUserPhoto)

        binding.tvName.text = "${user.firstName} ${user.lastName}"
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"
        // END
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {

        if (v != null) {
            when (v.id) {

                // Call the User Profile Activity to add the Edit Profile feature to the app. Pass the user details through intent.
                // START
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                // END

                // Add Logout feature when user clicks on logout button.
                // START
                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    Log.i("INFO", "Flag Executed")
                    finish()
                }
                // END

                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }

                R.id.tv_delete_account -> {

                    showAlertDialogToDeleteAccount()
                }
            }
        }

    }

    // Create a function to show the alert dialog for the confirmation of delete account from cloud firestore.
    // START
    /**
     * A function to show the alert dialog for the confirmation of delete account from cloud firestore.
     */
    private fun showAlertDialogToDeleteAccount() {

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_account_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->


            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection(Constants.USER).document(userId!!)

            userDocRef.delete()
                .addOnSuccessListener {
                    // User data deleted successfully

                    // Show the progress dialog.
                    showProgressDialog(resources.getString(R.string.please_wait))

                    val user = FirebaseAuth.getInstance().currentUser
                    user?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // User deleted successfully

                                FancyToast.makeText(
                                    this, "User account deleted successfully",
                                    FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true
                                ).show()

                                val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)

                                hideProgressDialog()

                                finish()


                            } else {
                                // Handle failure
                            }
                        }


                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
            // END

            hideProgressDialog()

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}