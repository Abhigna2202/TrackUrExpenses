package com.example.android.trackurexpenses

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.history_expenses.*

class ExpensesHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_expenses)
        val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

        val expensesLiveSearch = DataSource.createDataSet(email)

        expensesLiveSearch.observe(
            this,
            object : Observer<ArrayList<ExpenseModel>> {
                override fun onChanged(data: ArrayList<ExpenseModel>) {
                    tabl_view.removeAllViews()

                    val row = TableRow(this@ExpensesHistory)

                    row.setBackgroundColor(Color.GREEN)
                    var cell = TextView(this@ExpensesHistory)


                    cell.text = "Date"
                    cell.setTypeface(null, Typeface.BOLD)
                    row.addView(cell)

                    cell = TextView(this@ExpensesHistory)
                    cell.text = "Store"
                    cell.setTypeface(null, Typeface.BOLD)
                    row.addView(cell)

                    cell = TextView(this@ExpensesHistory)
                    cell.text = "Item"
                    cell.setTypeface(null, Typeface.BOLD)
                    row.addView(cell)

                    cell = TextView(this@ExpensesHistory)
                    cell.text = "Qty"
                    cell.setTypeface(null, Typeface.BOLD)
                    row.addView(cell)

                    cell = TextView(this@ExpensesHistory)
                    cell.text = "Price"
                    cell.setTypeface(null, Typeface.BOLD)
                    row.addView(cell)
                    tabl_view.addView(row)

                    for (record in data) {
                        val row = TableRow(this@ExpensesHistory)

                        row.setBackgroundColor(Color.WHITE)
                        var cell = TextView(this@ExpensesHistory)
                        cell.setTypeface(null, Typeface.BOLD)
                        cell.text = record.Purchasedate.toString()
                        row.addView(cell)

                        cell = TextView(this@ExpensesHistory)
                        cell.setTypeface(null, Typeface.BOLD)
                        cell.text = record.Storename
                        row.addView(cell)

                        cell = TextView(this@ExpensesHistory)
                        cell.setTypeface(null, Typeface.BOLD)
                        cell.text = record.Item
                        row.addView(cell)

                        cell = TextView(this@ExpensesHistory)
                        cell.setTypeface(null, Typeface.BOLD)
                        cell.text = record.Quantity
                        row.addView(cell)

                        cell = TextView(this@ExpensesHistory)
                        cell.setTypeface(null, Typeface.BOLD)
                        cell.text = record.price
                        row.addView(cell)

                        tabl_view.addView(row)
                        Log.d("data", data.toString())
                    }
                }
            })


    }
}