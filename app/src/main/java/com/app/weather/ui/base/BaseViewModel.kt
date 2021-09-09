package com.app.weather.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    val snackbarText = MutableLiveData<Pair<Int, Int>>()
    val snackbarMessage: LiveData<Pair<Int, Int>>
        get() = snackbarText

    val loading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> get() = loading


    val mCompositeDisposable: CompositeDisposable = CompositeDisposable()


}
