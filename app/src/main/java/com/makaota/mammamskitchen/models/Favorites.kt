package com.makaota.mammamskitchen.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Favorites(
    val user_id: String = "",
    val title: String = "",
    var app_user_id: String = "",
    val category: String = "",
    val price: String = "",
    val description: String = "",
    val image: String = "",
    var product_id: String = "",
    @DocumentId
    var documentId: String = ""
) : Parcelable