package com.app.weather.ui.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.weather.R
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.progress_dialog.*


abstract class BaseFragment : Fragment(), BaseViewCallBack {

    var baseActivity: BaseActivity? = null
    var fragmentView: ViewGroup? = null
    lateinit var title: String


//    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_base, container, false)
        try {
            fragmentView = LayoutInflater.from(activity).inflate(
                getFragmentView(),
                container,
                false
            ) as ViewGroup
            view?.findViewById<ViewGroup>(R.id.layout_base_view)?.addView(fragmentView)
        } catch (e: Exception) {
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(fragmentView)
    }

    protected abstract fun getFragmentView(): Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            val activity = context as BaseActivity?
            this.baseActivity = activity
            activity!!.onFragmentAttached()
        }
    }

    override fun showLoading() {
        errorView?.visibility = View.GONE
        progressL?.visibility = View.VISIBLE
        fragmentView?.visibility = View.GONE
    }

    override fun hideLoading() {
        errorView?.visibility = View.GONE
        progressL?.visibility = View.GONE
        fragmentView?.visibility = View.VISIBLE
    }


    override fun onError(message: String?) {
        baseActivity?.onError(message)
    }

    override fun onError(@StringRes resId: Int) {
        baseActivity?.onError(resId)
    }

    override fun onError(
        textString: String, actionString: String?, icon: Drawable?,
        actionListener: View.OnClickListener?
    ) {
        progressL.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        fragmentView?.visibility = View.GONE
        if (actionString != null) {
            errorActionB.setOnClickListener(actionListener)
            errorActionB.visibility = View.VISIBLE
        }
        if (icon != null) {
            errorIcon?.setImageDrawable(icon)
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
        val errorActionIcon = if (errorIcon == 0) null else ContextCompat.getDrawable(
            this.baseActivity!!,
            errorIcon
        )
        onError(
            resources.getString(errorTextRes),
            errorActionString,
            errorActionIcon,
            errorActionListener
        )
    }

    override fun showMessage(message: String?) {
        baseActivity?.showMessage(message)
    }

    override fun showMessage(@StringRes resId: Int) {
        baseActivity?.showMessage(resId)
    }

    override fun isNetworkConnected(): Boolean = baseActivity!!.isNetworkConnected()

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun hideKeyboard() {
        baseActivity?.hideKeyboard()
    }

    override fun openActivityOnTokenExpire() {
        baseActivity?.openActivityOnTokenExpire()
    }

    protected abstract fun setUp(view: View?)

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentView = null
        System.gc()
    }


    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }
}