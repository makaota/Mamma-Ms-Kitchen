package com.makaota.mammamskitchen.models

data class PushNotification(
    val data: NotificationData,
    val to: String
)