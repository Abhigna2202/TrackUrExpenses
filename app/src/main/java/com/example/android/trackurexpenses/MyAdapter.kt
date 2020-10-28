package com.example.android.trackurexpenses

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_layout.view.*

public class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var expens: List<ExpenseModel> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder -> {
                holder.bind(expens.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return expens.size
    }

    fun submitList(expensList: List<ExpenseModel>) {
        expens = expensList
        Log.d("expens", expens.toString())
    }

    class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeName = itemView.store_name
        val itemName = itemView.item_name
        val itemPrice = itemView.item_price
        val itemQuantity = itemView.item_quantity
        val datePurchased = itemView.date_purchased

        fun bind(expenseModel: ExpenseModel) {
            storeName.setText(expenseModel.Storename)
            itemPrice.setText(expenseModel.price)
            itemQuantity.setText(expenseModel.Quantity)
            itemName.setText(expenseModel.Item)
            datePurchased.setText(expenseModel.Purchasedate)
        }
    }

}