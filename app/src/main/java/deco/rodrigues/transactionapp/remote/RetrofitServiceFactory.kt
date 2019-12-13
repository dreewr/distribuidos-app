package deco.rodrigues.worker.remote

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitServiceFactory {

    val DOCTOR_BASE_URL: String ="https://server-doctor.herokuapp.com/"
    val ANESTHETIST_BASE_URL: String ="https://server-anesthetist.herokuapp.com/"

    //TODO: Avaliar o uso de URL Base dinâmica usando esse método

    fun makeDoctorService(): DoctorService {
        val retrofit = Retrofit.Builder()
            .baseUrl(DOCTOR_BASE_URL)
            .client( makeOkHttpClient(
                makeLoggingInterceptor((true))))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
        return retrofit.create(DoctorService::class.java)
    }

   fun makeAnesthetistService(): AnesthetistService {
        val retrofit = Retrofit.Builder()
            .baseUrl(ANESTHETIST_BASE_URL)
            .client( makeOkHttpClient(
                makeLoggingInterceptor((true))))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
        return retrofit.create(AnesthetistService::class.java)
    }

    private fun makeOkHttpClient(httpLoggingInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(makeLoggingInterceptor(true))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug) {

            HttpLoggingInterceptor.Level.BODY

        } else {

            HttpLoggingInterceptor.Level.NONE

        }

        return logging
    }

}