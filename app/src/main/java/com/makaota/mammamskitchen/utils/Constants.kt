package com.makaota.mammamskitchen.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAA6WuBVzg:APA91bG42BqsBYwZ9UHeROrVNaWFE6A1Bl2b8eMDFpWsE3MI4MaLY1PLbGd7R9YIj6ysR4FoIwMmO8fSXALcKAB78RU4eRVBGGz4zjcr6c8Xm9IVE0wZ0O21KgFE_yk_XALjg5JfQ6db"
    const val CONTENT_TYPE = "application/json"

    // Firebase Constants
    // This is used for the collection in the firestore.
    const val USER: String = "user"
    const val USER_MANAGER: String = "userManager"
    const val PRODUCTS: String = "products"
    const val ORDERS: String = "orders"
    const val SOLD_PRODUCTS: String = "sold_products"
    const val ADDRESSES: String = "addresses"
    const val NOTIFICATIONS: String = "notifications"
    const val FAVORITES: String = "favorites"

    const val ORDER_STATUS: String = "orderStatus"
    const val ORDER_CONFIRMATION: String = "order_confirmation"

    const val TOPIC = "myOrders"

    //menu categories
    const val ADDITIONAL_MEALS: String = "ADDITIONAL MEALS"
    const val SCAMBANE: String = "SCAMBANE"
    const val CHIPS: String = "CHIPS"
    const val RUSSIAN: String= "RUSSIAN"
    const val DRINKS: String = "DRINKS"

    const val RESTAURANT_USER_PREFERENCES: String = "restaurantUserPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val USER_PROFILE_IMAGE: String = "user_profile_image"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_MY_ORDER_DETAILS: String = "extra_my_order_details"

    const val READ_STORAGE_PERMISSION_CODE: Int = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val PICK_PRODUCT_IMAGE_REQUEST_CODE = 1
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    const val EXTRA_PRODUCT_ID: String = "extra_product_id"

    const val DEFAULT_CART_QUANTITY: String = "1"
    const val CART_ITEMS: String = "cart_items"
    const val CART_QUANTITY: String = "cart_quantity"
    const val STOCK_QUANTITY: String = "stock_quantity"

    // Constant variables for Gender
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    // Firebase database field names
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val USER_ID: String = "user_id"
    const val APP_USER_ID: String = "app_user_id"
    const val PRODUCT_ID: String = "product_id"

    const val LOGGED_IN_USER_MOBILE = "user_mobile"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    // START
    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    // END

    // Create a function to get the extension of the selected image file.
    // START
    /**
     * A function to get the image file extension of the selected image.
     *
     * @param activity Activity reference.
     * @param uri Image file uri.
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
    // END
}