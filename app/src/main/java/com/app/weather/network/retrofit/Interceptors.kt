package  com.app.weather.network.retrofit

import com.app.weather.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class APInterceptor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        var url: HttpUrl? = original.url().newBuilder()
            .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
            .addQueryParameter("units", "metric")
            .addQueryParameter("exclude", "hourly")
            .build()

        val request = original.newBuilder()
            .header("Accept", "application/json")
            .method(original.method(), original.body())
            .url(url)
            .build()


        return chain.proceed(request)
    }
}
