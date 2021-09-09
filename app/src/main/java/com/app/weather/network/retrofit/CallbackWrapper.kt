package  com.app.weather.network.retrofit

import com.google.gson.Gson
import com.app.weather.model.GenericResponse
import io.reactivex.observers.DisposableObserver
import okhttp3.ResponseBody
import retrofit2.HttpException

abstract class CallbackWrapper<T : Any> : DisposableObserver<T>() {

    protected abstract fun onSuccess(t: T)
    protected abstract fun onFail(t: String?)

    override fun onNext(t: T) {
        try {
            onSuccess(t)
        } catch (e: Exception) {
            onFail(e.message)
        }
    }

    override fun onError(e: Throwable) {
        if (e is HttpException) {
            val responseBody = e.response()?.errorBody()
            onFail(getErrorMessage(responseBody!!))
        } else {
            onFail(e.message)
        }
    }

    override fun onComplete() {

    }

    private fun getErrorMessage(responseBody: ResponseBody): String? {
        var response = String(responseBody.string().toByteArray())

        var message: String?
        try {
            val error = Gson().fromJson(response, GenericResponse::class.java)
            message = error.message
        } catch (e: Exception) {
            try {
                val error = Gson().fromJson(response, GenericResponse::class.java)
                message = error.message
            } catch (e: Exception) {
                return null
            }
        }
        return message
    }
}