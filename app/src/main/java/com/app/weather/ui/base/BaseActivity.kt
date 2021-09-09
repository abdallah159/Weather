package com.app.weather.ui.base

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.weather.R
import com.google.android.material.snackbar.Snackbar
import com.app.weather.utils.NetworkUtils
import kotlinx.android.synthetic.main.layout_base.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.progress_dialog.*

abstract class BaseActivity : AppCompatActivity(), BaseViewCallBack, BaseFragment.Callback {

    private var activityView: ViewGroup? = null


    abstract fun buildIntent(context: Context, data: Any?): Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_base)
        activityView = LayoutInflater.from(this)
            .inflate(getActivityView(), layout_base_view, false) as ViewGroup
        layout_base_view.addView(activityView)

        afterInflation(savedInstanceState)

    }

    protected abstract fun getActivityView(): Int

    protected abstract fun afterInflation(savedInstance: Bundle?)

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED


    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(
            findViewById(R.id.layout_base_view),
            message, Snackbar.LENGTH_LONG
        )
        val sbView = snackbar.view
        val textView = sbView
            .findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    override fun onError(message: String?) {
        if (message != null) {
            showSnackBar(message)
        } else {
            showSnackBar(getString(R.string.general_error))
        }
    }

    override fun onError(@StringRes resId: Int) {
        onError(getString(resId))
    }

    override fun showMessage(message: String?) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun showMessage(@StringRes resId: Int) {
        showMessage(getString(resId))
    }

    override fun onError(
        textString: String, actionString: String?, icon: Drawable?,
        actionListener: View.OnClickListener?
    ) {
        progressL.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        activityView!!.visibility = View.GONE
        if (actionString != null) {
            errorActionB.setOnClickListener(actionListener)
            errorActionB.visibility = View.VISIBLE
        }
        if (icon != null) {
            errorIcon!!.setImageDrawable(icon)
        }
        errorText.text = textString
        errorActionB.text = actionString
    }

    override fun onError(
        @StringRes errorTextRes: Int,
        @StringRes errorActionRes: Int,
        @DrawableRes errorIcon: Int,
        errorActionListener: View.OnClickListener?
    ) {
        val errorActionString =
            if (errorActionRes == 0) null else resources.getString(errorActionRes)
        val errorActionIcon =
            if (errorIcon == 0) null else ContextCompat.getDrawable(this, errorIcon)
        onError(
            resources.getString(errorTextRes),
            errorActionString,
            errorActionIcon,
            errorActionListener
        )
    }

    override fun isNetworkConnected(): Boolean = NetworkUtils.isNetworkConnected(applicationContext)


    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {

    }

    override fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun hideLoading() {
        errorView.visibility = View.GONE
        progressL.visibility = View.GONE
        activityView!!.visibility = View.VISIBLE
    }

    override fun showLoading() {
        errorView.visibility = View.GONE
        progressL.visibility = View.VISIBLE
        activityView!!.visibility = View.GONE
    }


    override fun openActivityOnTokenExpire() {
        finish()
    }
}
