package com.app.weather.utils.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.SupportMapFragment

class WorkaroundMapFragment : SupportMapFragment() {

    private var mListener: OnTouchListener? = null

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstance: Bundle?
    ): View? {
        val layout = super.onCreateView(layoutInflater, viewGroup, savedInstance)

        val frameLayout = activity?.let { TouchableWrapper(it) }

        activity?.let { ContextCompat.getColor(it, android.R.color.transparent) }
            ?.let { frameLayout?.setBackgroundColor(it) }

        (layout as ViewGroup).addView(
            frameLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        return layout
    }

    fun setListener(listener: OnTouchListener) {
        mListener = listener
    }

    interface OnTouchListener {
        fun onTouch()
    }

    inner class TouchableWrapper(context: Context) : FrameLayout(context) {

        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            try {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> mListener?.onTouch()
                    MotionEvent.ACTION_UP -> mListener?.onTouch()
                }
            } catch (e: Exception) {
            }
            return super.dispatchTouchEvent(event)
        }
    }
}