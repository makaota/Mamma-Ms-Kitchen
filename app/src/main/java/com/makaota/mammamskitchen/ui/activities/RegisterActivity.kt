package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityRegisterBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.User
import com.shashank.sony.fancytoastlib.FancyToast

class RegisterActivity : BaseActivity(), View.OnClickListener {


    lateinit var token: String

    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        // Retrieving the FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Fetching FCM registration token failed
                return@OnCompleteListener
            }
            // fetching the token
            token = task.result
            Log.i("TOKEN", "This user token is -> $token")

        })


        binding.tvLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_login -> {
                // Launch the register screen when the user clicks on the text.
                onBackPressedDispatcher.onBackPressed()

            }

            R.id.btn_register -> {
                registerUser()
            }
        }
    }

    // Create a function to set up an action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        val toolbarRegisterActivity = findViewById<Toolbar>(R.id.toolbar_register_activity)
        setSupportActionBar(toolbarRegisterActivity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbarRegisterActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    } // END

    // Create an function to validate the register account fields.
    // START
    /**
     * A function to validate the entries of a new user.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(binding.etLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }

            else -> {
                true
            }
        }
    } // END

    // Create a function to register the user with email and password using FirebaseAuth.
    // START
    /**
     * A function to register the user with email and password using FirebaseAuth.
     */
    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            val newUser = Firebase.auth.currentUser

                            newUser!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        FancyToast.makeText(
                                            this, "Your are registered successfully. \n" +
                                                    "Please check your email address for verification",
                                            FancyToast.LENGTH_LONG, FancyToast.INFO, true
                                        ).show()
                                    } else {
                                        showErrorSnackBar(task.exception!!.message.toString(), true)
                                    }
                                }

                            //  Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!


                            val user = User(
                                id = firebaseUser.uid,
                                firstName = binding.etFirstName.text.toString().trim { it <= ' ' },
                                lastName = binding.etLastName.text.toString().trim { it <= ' ' },
                                email = binding.etEmail.text.toString().trim { it <= ' ' },
                                userToken = token
                            )

                            FirestoreClass().registerUser(this@RegisterActivity, user)

                        } else {

                            hideProgressDialog()
                            // If the registering is not successful then show error message.
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })

        }
    }
    // END

    fun userRegistrationSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        FancyToast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true
        ).show()

        FirebaseAuth.getInstance().signOut()
        onBackPressedDispatcher.onBackPressed()

    }

}