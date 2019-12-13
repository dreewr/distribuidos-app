package deco.rodrigues.transactionapp.model

data class Transaction(
    val id: Int,
    val start_time: String,
    val end_time: String,
    var status: String = "active"
)