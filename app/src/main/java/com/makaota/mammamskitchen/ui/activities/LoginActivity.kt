package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityLoginBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.User
import com.makaota.mammamskitchen.utils.Constants

class LoginActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else
        {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.tvRegister.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v?.id){


            R.id.tv_forgot_password -> {
                val intent = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_login -> {
                logInRegisteredUser()
            }

            R.id.tv_register -> {
                // Launch the register screen when the user clicks on the text.
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)

            }
        }
    }

    // Create a function to validate the login details.
    // START
    /**
     * A function to validate the login entries of a user.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                //  showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }

    /**
     * A function to Log-In. The user will be able to log in using the registered email and password with Firebase Authentication.
     */
    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space
            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                         if (Firebase.auth.currentUser!!.isEmailVerified){
                        FirestoreClass().getUserDetails(this)
                        }else{
                            // Hide the progress dialog
                            hideProgressDialog()
                              showErrorSnackBar("Please verify your email address", true)
                           }
                    } else {
                        // Hide the progress dialog
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }


    fun userDeviceTokenListener(user: User){
        // Retrieving the FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Fetching FCM registration token failed
                return@OnCompleteListener
            }
            // fetching the token
            val token = task.result
            Log.i("TOKEN", "This user token is -> $token")

            FirestoreClass().writeNewDeviceToken(token,user)

        })
    }
    fun userLoggedInSuccess(user: User) {
        // Hide the progress dialog.
        hideProgressDialog()


        // Redirect the user to the UserProfile screen if it is incomplete otherwise to the Main screen.
        // START
//        if (user.profileCompleted == 0) {
//            // If the user profile is incomplete then launch the UserProfileActivity.
//            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
//            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
//            startActivity(intent)
//        } else {
//            // Redirect the user to Main Screen after log in.
//            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
//        }
//        finish()
        // END

        // Redirect the user to Main Screen after log in.
        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        finish()
    }

}