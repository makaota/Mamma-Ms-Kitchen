package com.makaota.mammamskitchen.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.makaota.mammamskitchen.R
import com.makaota.mammamskitchen.databinding.ActivityUserProfileBinding
import com.makaota.mammamskitchen.firestore.FirestoreClass
import com.makaota.mammamskitchen.models.User
import com.makaota.mammamskitchen.utils.Constants
import com.makaota.mammamskitchen.utils.GlideLoader
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.IOException
import java.util.concurrent.TimeUnit

const val TAG = "UserProfileActivity"

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User
    private  var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationID : String
    private var otpCode: String = ""
    private lateinit var resendingToken: PhoneAuthProvider.ForceResendingToken



    // Declare a variable to store the countdown timer
    private var countdownTimer: CountDownTimer? = null

    // Declare a variable to store the remaining time in milliseconds
    private var remainingTimeMillis: Long = 0



    private lateinit var mobileNumber : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()


        // Retrieve the User details from intent extra.
        // START
        // Create a instance of the User model class.

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.

            @Suppress("DEPRECATION")
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        // Set the existing values to the UI and allow user to edit except the Email ID.
        binding.etFirstName.setText(mUserDetails.firstName)
        binding.etLastName.setText(mUserDetails.lastName)
        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUserDetails.email)


        // If the profile is incomplete then user is from login screen and wants to complete the profile.
        if (mUserDetails.profileCompleted == 0) {
            // Update the title of the screen to complete profile.
            binding.tvTitle.text = resources.getString(R.string.title_complete_profile)

            // Here, the some of the edittext components are disabled because it is added at a time of Registration.
            binding.etFirstName.isEnabled = false
            binding.etLastName.isEnabled = false

        } else {

            // Call the setup action bar function.
            setupUserProfileActionBar()

            // Update the title of the screen to edit profile.
            binding.tvTitle.text = resources.getString(R.string.title_edit_profile)

            // Load the image using the GlideLoader class with the use of Glide Library.
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image,binding.ivUserPhoto)

            if (mUserDetails.mobile.isNotEmpty()) {
                binding.etMobileNumber.setText(mUserDetails.mobile.toString())

            }

        }

        binding.ivUserPhoto.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.btnSendOtp.setOnClickListener(this)
        // binding.btnResendOtp.setOnClickListener(this)
        binding.btnSubmitOtp.setOnClickListener(this)
        binding.tvResendOtp.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {

                    // Here we will check if the permission is already allowed or we need to request for it.
                    // First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed we will request for the same.
                    if (ContextCompat.checkSelfPermission
                            (
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        Constants.showImageChooser(this)

                    } else {

                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                // Add the click event to the SAVE button.
                // START
                R.id.btn_submit ->{
                    if(validateUserEnteredPhoneNumber()){
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if(mSelectedImageFileUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                        }else{
                            updateUserProfileDetails()
                        }
                    }//END IF
                } // END

                R.id.btn_send_otp ->{

                    if (validateUserEnteredPhoneNumber()) {
                        showProgressDialog("sending OTP...")
                        val etPhoneNumber = binding.etMobileNumber.text
                        if (etPhoneNumber != null) {
                            if (etPhoneNumber.length != 10 || etPhoneNumber.length > 10){
                                showErrorSnackBar("Invalid Phone Number",true)
                                hideProgressDialog()
                            }else{
                                val phoneNumber = etPhoneNumber.toString()
                                sendVerificationCode(phoneNumber)
                                binding.tilOptNumber.visibility = View.VISIBLE
                                binding.btnSubmitOtp.visibility = View.VISIBLE
                            }
                        }

                    }

                }
                R.id.tv_resend_otp->{

                    if (validateUserEnteredPhoneNumber()) {
                        showProgressDialog("resending OTP...")
                        resendVerificationCode()
                    }
                }

                R.id.btn_submit_otp->{

                    if (validateOtpNumber()){
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (binding.etOtpNumber.length() != 6 || binding.etOtpNumber.length() > 6){
                            showErrorSnackBar("Invalid OTP Number",true)
                            hideProgressDialog()
                        }else{
                            verifySmsCode()

                        }

                    }


                }
            }
        }
    } // END

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){

        auth.signInWithCredential(credential).addOnCompleteListener(this) {task->

            if (task.isSuccessful){
                hideProgressDialog()
                // Sign-in successful, the user is now authenticated with their phone number.
                val user = task.result?.user

                val currentUserId = mUserDetails.id
                // You can proceed with the authenticated user here.
                Log.i(TAG,"sms code = ${credential.smsCode}")
                if (user != null) {

                    user.delete().addOnCompleteListener{ task->
                        if (task.isSuccessful){
                            Log.i(TAG,"GET THE CURRENT USER ID FROM FIRESTORE CLASS = ${FirestoreClass().getCurrentUserId()}")
                            Log.i(TAG,"user account deleted")
                        }
                    }
                    Log.i(TAG,"user id is = ${user.uid}")
                }
                binding.btnSubmitOtp.visibility = View.GONE
                binding.btnSubmit.visibility = View.VISIBLE
                binding.tvResendOtp.visibility = View.GONE
                FancyToast.makeText(
                    this,
                    "OTP Submitted Successfully",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.SUCCESS,
                    true).show()
            }else{
                showErrorSnackBar("Invalid OTP ",true)
                hideProgressDialog()
            }
        }
    }

    private fun verifySmsCode() {
        val userEnteredOtp = binding.etOtpNumber.text.toString().trim{it<=' '}

        if (userEnteredOtp.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(verificationID, userEnteredOtp)
            signInWithPhoneAuthCredential(credential)
        } else {
            // Show an error message indicating that the SMS code is empty
            showErrorSnackBar("Please Enter the Sms Code ",true)
        }
    }

    private fun updateUserProfileDetails(){

        val userHashMap = HashMap<String, Any>()

        // Update the code if user is about to Edit Profile details instead of Complete Profile.
        // Get the FirstName from editText and trim the space
        val firstName = binding.etFirstName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        // Get the LastName from editText and trim the space
        val lastName = binding.etLastName.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        mobileNumber = binding.etMobileNumber.text.toString().trim{it<=' '}

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()){


            userHashMap[Constants.MOBILE] = mobileNumber
        }

        if (mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1


        FirestoreClass().updateUserProfileData(this, userHashMap,mUserDetails)

    }


    fun userProfileUpdateSuccess() {
        // Hide the progress dialog
        hideProgressDialog()

        FancyToast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,true
        ).show()

        auth.signOut()
        startActivity(Intent(this@UserProfileActivity, LoginActivity::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Constants.showImageChooser(this)

            } else {
                //Displaying another toast if permission is not granted
                FancyToast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    FancyToast.LENGTH_LONG, FancyToast.WARNING,true
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivUserPhoto)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        FancyToast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR,true
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    // Create a function to notify the success result of image upload to the Cloud Storage.
    // START
    /**
     * A function to notify the success result of image upload to the Cloud Storage.
     *
     * @param imageURL After successful upload the Firebase Cloud returns the URL.
     */
    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
    // END

    // Create a function to validate the input entries for profile details.
    // START
    /**
     * A function to validate the input entries for profile details.
     */
    private fun validateUserEnteredPhoneNumber(): Boolean {
        return when {

            // We have kept the user profile picture is optional.
            // The FirstName, LastName, and Email Id are not editable when they come from the login screen.
            // The Radio button for Gender always has the default selected value.

            // Check if the mobile number is not empty as it is mandatory to enter.
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun validateOtpNumber(): Boolean {
        return when {

            // We have kept the user profile picture is optional.
            // The FirstName, LastName, and Email Id are not editable when they come from the login screen.
            // The Radio button for Gender always has the default selected value.

            // Check if the mobile number is not empty as it is mandatory to enter.
            TextUtils.isEmpty(binding.etOtpNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_otp_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun sendVerificationCode(phoneNumber : String){

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+27$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this) // Replace with your activity instance
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationFailed(exception: FirebaseException) {
            // Verification failed
            // Handle the error here
            FancyToast.makeText(
                this@UserProfileActivity, "Cannot complete profile -> " +exception.message,
                FancyToast.LENGTH_SHORT, FancyToast.ERROR,true
            ).show()
            hideProgressDialog()
        }

        override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
            // Called when the verification code auto-retrieval times out
            // Handle the timeout here
            showErrorSnackBar("Code retrieval timed out. Please try again.",true)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // Called when verification code is successfully sent to the user's phone number
            // Store the verificationId and use it to verify the code later
            verificationID = verificationId
            Log.d(TAG, "received verification Id $verificationID")
            resendingToken = token
            Log.d(TAG,"resending token $resendingToken")
            hideProgressDialog()

            binding.btnSendOtp.visibility = View.GONE
            binding.tvResendOtp.visibility = View.VISIBLE





            // Start the countdown timer
            startCountdownTimer()

        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Verification is automatically done on some devices
//            // You can use credential to sign in the user
            signInWithPhoneAuthCredential(credential)

        }
    }


    // Resend the verification code
    private fun resendVerificationCode() {

        if (resendingToken != null) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+27"+binding.etMobileNumber.text.toString().trim(){it <=' '})
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .setForceResendingToken(resendingToken)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)

            // Cancel the existing countdown timer, if any
            cancelCountdownTimer()
        } else {
            // Handle the case when the resend token is not available
            // You can display an error message or disable the resend button
            // ...
        }
    }

    // Start the countdown timer
    private fun startCountdownTimer() {
        // Set the duration of the countdown timer in milliseconds
        val durationMillis = 60_000L

        countdownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the remaining time
                remainingTimeMillis = millisUntilFinished

                // Update UI, e.g., display remaining time or disable the resend button
                // ...
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                val timeString = secondsRemaining.toString()
                binding.tvResendOtp.text = timeString
                binding.tvResendOtp.isEnabled = false
            }

            override fun onFinish() {
                // Countdown timer finished, enable the resend button
                remainingTimeMillis = 0

                // Enable the resend button here
                // ...
                binding.tvResendOtp.isEnabled = true
                binding.tvResendOtp.text = "Resend OTP?"
            }
        }

        countdownTimer?.start()
    }

    // Cancel the countdown timer if needed
    private fun cancelCountdownTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
    }

    private fun setupUserProfileActionBar() {

        val toolbarRegisterActivity = findViewById<Toolbar>(R.id.toolbar_user_profile_activity)
        setSupportActionBar(toolbarRegisterActivity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbarRegisterActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    } // END
}