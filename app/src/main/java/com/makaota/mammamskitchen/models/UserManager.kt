package com.makaota.mammamskitchen.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// START
/**
 * A data model class for User with required fields.
 */
// START
@Parcelize
data class UserManager(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    var userToken: String = "",
    val profileCompleted: Int = 0) : Parcelable