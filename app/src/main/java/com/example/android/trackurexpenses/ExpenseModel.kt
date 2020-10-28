package com.example.android.trackurexpenses

data class ExpenseModel(
    val Item: String,
    val price: String,
    val Quantity: String,
    val Storename: String,
    val Purchasedate: String
) {

    override fun toString(): String {
        return "ExpenseModel(Item='$Item', price='$price', Quantity='$Quantity')"
    }
}
