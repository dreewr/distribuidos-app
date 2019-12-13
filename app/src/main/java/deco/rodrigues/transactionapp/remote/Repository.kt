package deco.rodrigues.worker.remote

import deco.rodrigues.transactionapp.model.Transaction
import io.reactivex.Observable

/**
 *  Classe de repositório das chamadas de API feitas para o serviço do Anestesista e médico
 */

class Repository {


    private val doctorService: DoctorService =
        RetrofitServiceFactory.makeDoctorService()

    private val anesthetistService: AnesthetistService =
        RetrofitServiceFactory.makeAnesthetistService()

    fun initAnesthetistTransaction(transaction: Transaction): Observable<String?>{
        return anesthetistService.initTransaction(transaction)
    }

    fun commitAnesthetistTransaction(id: String):Observable<String?>{
        return anesthetistService.commitTransaction(id)
    }

    fun abortAnesthetistTransaction(id: String):Observable<String?>{
        return anesthetistService.abortTransaction(id)
    }

    fun initDoctorTransaction(transaction: Transaction): Observable<String?>{
        return doctorService.initTransaction(transaction)
    }

    fun commitDoctorTransaction(id: String):Observable<String?>{
        return doctorService.commitTransaction(id)
    }

    fun abortDoctorTransaction(id: String):Observable<String?>{
        return doctorService.abortTransaction(id)
    }

    fun resetDoctor(): Observable<Int?>{
        return doctorService.reset()
    }

    fun resetAnesthetist(): Observable<Int?>{
        return anesthetistService.reset()
    }

}