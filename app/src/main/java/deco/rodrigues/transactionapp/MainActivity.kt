package deco.rodrigues.transactionapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import deco.rodrigues.transactionapp.model.Transaction
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPrefs = this.getSharedPreferences(
            "MAIN_ACTIVITY", Context.MODE_PRIVATE
        )

        mainViewModel = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)

        initViews()

        mainViewModel.currentTransaction.observe(this, Observer { handleTransaction(it) })

        checkPendingTransactions()
    }

    /**
     *  Inicializa elementos visuais e callbacks
     */
    fun initViews() {

        btn_confirmar.setOnClickListener {
            if (
                et_hora_final.text.toString().isNullOrEmpty() ||
                et_hora_inicial.text.toString().isNullOrEmpty() ||
                et_dia.text.toString().isNullOrEmpty() ||
                et_mes.text.toString().isNullOrEmpty() ||
                et_minuto_final.text.toString().isNullOrEmpty() ||
                et_minuto_inicial.text.toString().isNullOrEmpty()

            ) Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            else if (
                et_hora_final.text.toString().toInt() > 12 ||
                et_hora_inicial.text.toString().toInt() > 12
            )
                Toast.makeText(
                    this,
                    "Hora menor que 12 (formato timestamp)",
                    Toast.LENGTH_SHORT
                ).show()
            else if (Timestamp.valueOf("2020-${et_mes.text}-${et_dia.text} ${et_hora_final.text}:${et_minuto_final.text}:00")
                < Timestamp.valueOf("2020-${et_mes.text}-${et_dia.text} ${et_hora_inicial.text}:${et_minuto_inicial.text}:00")
            ) Toast.makeText(
                this,
                "Horário final precede horário inicial!",
                Toast.LENGTH_SHORT
            ).show()
            else mainViewModel.initTransaction(
                "2020-${et_mes.text}-${et_dia.text} ${et_hora_inicial.text}:${et_minuto_inicial.text}:00",
                "2020-${et_mes.text}-${et_dia.text} ${et_hora_final.text}:${et_minuto_final.text}:00"
            )
        }

    }

    /**
     * Callback de atualizações no estado da transação
     */
    fun handleTransaction(transaction: Transaction) {
        Log.d("TransactionStatus", transaction.status)
        when (transaction.status) {
            "aborted" -> {

                btn_confirmar.isEnabled = true
                tv_mensagem.text = transaction.status
            }
            "partially_committed" -> {
                btn_confirmar.isEnabled = false
                tv_mensagem.text = transaction.status
            }
            "committed" -> {
                btn_confirmar.isEnabled = true
                tv_mensagem.text = transaction.status
            }
            "active" -> {
                btn_confirmar.isEnabled = false
                tv_mensagem.text = transaction.status
            }
        }
    }

    /**
     * Callback do sistema operacional, acionado quando o aplicativo é desligado
     */
    override fun onPause() {
        if (mainViewModel.currentTransaction.value!!.status.equals("partially_committed")) {
            writePreference("HAS_PARTIAL_TRANSACTION", true)
            writePreference("TRANSACTION_ID", mainViewModel.currentTransaction.value!!.id)
            writePreference("START_TIME", mainViewModel.currentTransaction.value!!.start_time)
            writePreference("END_TIME", mainViewModel.currentTransaction.value!!.end_time)
            Log.d("TransactionStatus", "Saving Pending Transaction")
        } else {
            writePreference("HAS_PARTIAL_TRANSACTION", false)
            Log.d("TransactionStatus", "Exited app without pending transactions")
        }
        super.onPause()
    }

    /**
     * Checa se alguma transação que deveria ter sido efetivada está pendente
     */
    fun checkPendingTransactions() {
        if (sharedPrefs.getBoolean("HAS_PARTIAL_TRANSACTION", false)) {
            mainViewModel.currentTransaction
                .value = Transaction(
                sharedPrefs.getInt("TRANSACTION_ID", 0),
                sharedPrefs.getString("START_TIME", null)!!,
                sharedPrefs.getString("END_TIME", null)!!,
                "partially_committed"
            )
            //Vai fazer o commit da transação
            mainViewModel.commitTransaction()
        }
    }

    /**
     * Função de escrita para o log da transação pendente
     */
    private fun writePreference(prefKey: String, prefValue: Any?) {

        sharedPrefs.edit().let {

            when (prefValue) {
                is String -> it.putString(prefKey, prefValue)
                is Int -> it.putInt(prefKey, prefValue)
                is Boolean -> it.putBoolean(prefKey, prefValue)
            }
            it.commit()
        }
    }

}
