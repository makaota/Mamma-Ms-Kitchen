package com.makaota.mammamskitchen.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// START
/**
 * A data model class for User with required fields.
 */
// START
@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: String = "",
    var userToken: String = "",
    var hasCompleteOrder: Boolean = false,
    val profileCompleted: Int = 0) : Parcelable