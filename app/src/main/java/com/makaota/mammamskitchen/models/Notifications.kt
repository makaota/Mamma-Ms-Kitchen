package com.makaota.mammamskitchen.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Notifications(
    val title: String = "",
    val orderDateTime: Long = 0L,
    val orderStatus: String ="",
    val orderMessage: String ="",
    val orderConfirmed: String="",
    val orderNumber: String ="",
    val user_id: String = "",
    @DocumentId
    var documentId: String = ""

): Parcelable