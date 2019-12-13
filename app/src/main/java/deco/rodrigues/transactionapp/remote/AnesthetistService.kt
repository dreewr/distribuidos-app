package deco.rodrigues.worker.remote

import deco.rodrigues.transactionapp.model.Transaction
import io.reactivex.Observable
import retrofit2.http.*

interface AnesthetistService {

    @POST("anesthetist/iniciar")
    fun initTransaction(@Body transaction: Transaction)
            : Observable<String?>

    @GET("anesthetist/efetivar/{transactionId}")
    fun commitTransaction(@Path("transactionId")transactionId: String)
            : Observable<String?>

    @GET("anesthetist/abortar/{transactionId}")
    fun abortTransaction(@Path("transactionId")transactionId: String)
            : Observable<String?>

    @GET("reset")
    fun reset()
            : Observable<Int?>

}