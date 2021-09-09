package com.app.weather

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.app.weather.db.Database
import com.app.weather.di.appModule
import com.app.weather.network.retrofit.APInterceptor
import com.app.weather.network.retrofit.Service
import com.google.android.gms.security.ProviderInstaller
import com.app.weather.utils.Singleton
import okhttp3.*
import org.koin.android.ext.android.startKoin
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        createApi(APInterceptor(), this)
        startKoin(this, listOf(appModule))
        MultiDex.install(this)
        Singleton.instance?.appDatabase = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "weatherApp"
        ).fallbackToDestructiveMigration().build()
    }


    companion object {
        lateinit var getService: Service
        lateinit var getGoogleService: Service


        internal fun createApi(
            apiInterceptur: APInterceptor?,
            context: Context
        ) {
            val clientBuilder: OkHttpClient.Builder
            val client: OkHttpClient
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            clientBuilder = OkHttpClient.Builder()
                .addInterceptor(interceptor)

            apiInterceptur?.let {
                clientBuilder.addInterceptor(
                    apiInterceptur
                )
            }



            ProviderInstaller.installIfNeeded(context)
            client = clientBuilder.build()


            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val retrofitGoogle = Retrofit.Builder()
                .baseUrl(BuildConfig.GOOGLE_BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            getService = retrofit.create(Service::class.java)
            getGoogleService = retrofitGoogle.create(Service::class.java)

        }
    }
}
