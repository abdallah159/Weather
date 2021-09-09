package com.app.weather.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.app.weather.R
import com.app.weather.ui.base.BaseActivity
import com.app.weather.ui.main.presntation.MainActivity

class SplashActivity : BaseActivity() {

    override fun buildIntent(context: Context, data: Any?): Intent =
        Intent(context, SplashActivity::class.java)

    override fun getActivityView(): Int = R.layout.activity_splash

    override fun afterInflation(savedInstance: Bundle?) {
        Handler().postDelayed({
            startActivity(
                MainActivity()
                    .buildIntent(this, null)
            )
            finish()
        }, 2000)
    }

}