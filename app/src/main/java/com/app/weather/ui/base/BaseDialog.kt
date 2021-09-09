package com.app.weather.ui.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialog : DialogFragment(), DialogViewCallBack {

    var baseActivity: BaseActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            val mActivity = context as BaseActivity?
            this.baseActivity = mActivity
            mActivity!!.onFragmentAttached()
        }
    }

    override fun onError(
        textString: String, actionString: String?, icon: Drawable?,
        actionListener: View.OnClickListener?
    ) {
//        progressBar.visibility = View.GONE
//        errorView.visibility = View.VISIBLE
//        if (actionString != null) {
//            errorActionB.setOnClickListener(actionListener)
//            errorActionB.visibility = View.VISIBLE
//        }
//        if (icon != null) {
//            errorIcon!!.setImageDrawable(icon)
//        }
//        errorText.text = textString
//        errorActionB.text = actionString
    }

    override fun onError(
        @StringRes errorTextRes: Int,
        @StringRes errorActionRes: Int,
        @DrawableRes errorIcon: Int,
        errorActionListener: View.OnClickListener?
    ) {
//        val errorActionString = if (errorActionRes == 0) null else resources.getString(errorActionRes)
//        val errorActionIcon = if (errorIcon == 0) null else ContextCompat.getDrawable(baseActivity, errorIcon)
//        onError(resources.getString(errorTextRes),
//                errorActionString,
//                errorActionIcon,
//                errorActionListener)
    }

    override fun showLoading() {
        baseActivity!!.showLoading()
    }

    override fun hideLoading() {
        baseActivity!!.hideLoading()
    }

    override fun onError(message: String?) {
        baseActivity!!.onError(message)
    }

    override fun onError(@StringRes resId: Int) {
        baseActivity!!.onError(resId)
    }

    override fun showMessage(message: String?) {
        baseActivity!!.showMessage(message)
    }

    override fun showMessage(@StringRes resId: Int) {
        baseActivity!!.showMessage(resId)
    }

    override fun isNetworkConnected(): Boolean = baseActivity!!.isNetworkConnected()


    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun hideKeyboard() {
        baseActivity!!.hideKeyboard()
    }

    override fun openActivityOnTokenExpire() {
        baseActivity!!.openActivityOnTokenExpire()
    }

    protected abstract fun setUp(view: View?)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // creating the fullscreen dialog
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view)
    }

//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view!!, savedInstanceState)
//        setUp(view)
//    }

    override fun show(fragmentManager: FragmentManager, tag: String?) {
        val transaction = fragmentManager.beginTransaction()
        val prevFragment = fragmentManager.findFragmentByTag(tag)
        if (prevFragment != null) {
            transaction.remove(prevFragment)
        }
        transaction.addToBackStack(null)
        show(transaction, tag)
    }

    override fun dismissDialog(tag: String) {
        dialog?.dismiss()
        baseActivity!!.onFragmentDetached(tag)
    }

}