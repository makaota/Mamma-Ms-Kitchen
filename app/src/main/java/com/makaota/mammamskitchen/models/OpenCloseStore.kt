package com.makaota.mammamskitchen.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class OpenCloseStore(
    var isStoreOpen: Boolean = false,
    @DocumentId
    val id: String = ""
): Parcelable