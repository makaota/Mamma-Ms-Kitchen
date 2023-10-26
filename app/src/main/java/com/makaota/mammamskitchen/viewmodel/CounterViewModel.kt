package com.makaota.mammamskitchen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel(){

    var number = 0

    val currentCartNumber: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentNotificationNumber: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
}