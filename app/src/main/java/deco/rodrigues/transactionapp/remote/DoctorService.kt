package deco.rodrigues.worker.remote


import deco.rodrigues.transactionapp.model.Transaction
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

/**
 *  Serviços do médico
 */

interface DoctorService {

    @POST("doctor/iniciar")
    fun initTransaction(@Body transaction: Transaction)
            : Observable<String?>

    @GET("doctor/efetivar/{transactionId}")
    fun commitTransaction(@Path("transactionId")transactionId: String)
            : Observable<String?>

    @GET("doctor/abortar/{transactionId}")
    fun abortTransaction(@Path("transactionId")transactionId: String)
            : Observable<String?>

    @GET("reset")
    fun reset()
            : Observable<Int?>
}