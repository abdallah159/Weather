package com.app.weather.ui.base

import com.app.weather.ui.base.BaseViewCallBack


interface DialogViewCallBack : BaseViewCallBack {

    fun dismissDialog(tag: String)
}
