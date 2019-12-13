package deco.rodrigues.transactionapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import deco.rodrigues.transactionapp.model.Transaction
import deco.rodrigues.worker.remote.Repository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import kotlin.math.absoluteValue
import kotlin.random.Random


class MainViewModel : ViewModel() {

    private val repository = Repository()

    val currentTransaction: MutableLiveData<Transaction> = MutableLiveData()


    override fun onCleared() {
        super.onCleared()
        compositeDisposable?.dispose()
    }

    private var compositeDisposable: CompositeDisposable? = null

    /**
     * Inicializa uma transação, combinando os resultados das chamadas ao servidor do anestesita
     * e do médico
     */
    fun initTransaction(startTime: String, endTime: String) {
        currentTransaction.value = Transaction(Random.nextInt().absoluteValue, startTime, endTime)
        val initSubscriber = Observable.zip(
            repository.initAnesthetistTransaction(currentTransaction.value!!),
            repository.initDoctorTransaction(currentTransaction.value!!),
            BiFunction<String?, String?, String> { t1, t2 ->

                if (t1.equals("SUCCESS") && t2.equals("SUCCESS")) return@BiFunction "SUCCESS"
                else return@BiFunction "ERROR"
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(InitSubscriber())
        compositeDisposable?.add(initSubscriber)
    }

    /**
     * Aborta uma transação
     */
    fun abortTransaction() {
        val abortSubscriber = Observable.zip(
            repository.abortAnesthetistTransaction(currentTransaction.value!!.id.toString()),
            repository.abortDoctorTransaction(currentTransaction.value!!.id.toString()),
            BiFunction<String?, String?, String> { t1, t2 ->
                if (t1.equals("SUCCESS") && t2.equals("SUCCESS")) return@BiFunction "SUCCESS"
                else return@BiFunction "ERROR"
            }
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(AbortSubscriber())
        compositeDisposable?.add(abortSubscriber)
    }

    /**
     * Efetiva a transação, escrevendo os dados permanentemente no banco de dados PostGressSQL
     */
    fun commitTransaction() {
        val commitSubscriber = Observable.zip(
            repository.commitAnesthetistTransaction(currentTransaction.value!!.id.toString()),
            repository.commitDoctorTransaction(currentTransaction.value!!.id.toString()),
            BiFunction<String?, String?, String> { t1, t2 ->
                if (t1.equals("SUCCESS") && t2.equals("SUCCESS")) return@BiFunction "SUCCESS"
                else return@BiFunction "ERROR"
            }
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(CommitSubscriber())
        compositeDisposable?.add(commitSubscriber)

    }

    private inner class InitSubscriber : DisposableObserver<String>() {
        override fun onComplete() {
            Log.d("InitTransaction", "onComplete")
            currentTransaction.value!!.status = "partially_committed"
            currentTransaction.postValue(currentTransaction.value)
        }

        override fun onNext(result: String) {
            Log.d("InitTransaction", result)
            if (result.equals("SUCCESS")) /*Log.d("InitTransaction", "DELAY")*/ commitTransaction()
            else abortTransaction()
        }

        override fun onError(e: Throwable) {
            Log.d("InitSubscriber", e.message)
//          Aborta de forma redundante
            abortTransaction()
        }

    }

    private inner class AbortSubscriber : DisposableObserver<String>() {
        override fun onComplete() {
            Log.d("InitTransaction", "onComplete")
            currentTransaction.value!!.status = "aborted"
            currentTransaction.postValue(currentTransaction.value)
        }

        override fun onNext(result: String) {
            Log.d("InitTransaction", result)
        }

        override fun onError(e: Throwable) {
            Log.d("InitSubscriber", e.message)
        }

    }

    private inner class CommitSubscriber : DisposableObserver<String>() {
        override fun onComplete() {
            Log.d("InitTransaction", "onComplete")
            currentTransaction.value!!.status = "committed"
            currentTransaction.postValue(currentTransaction.value)
        }

        override fun onNext(result: String) {
            Log.d("InitTransaction", result)
        }

        override fun onError(e: Throwable) {
            Log.d("InitSubscriber", e.message)
        }

    }

}
