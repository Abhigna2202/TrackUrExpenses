package com.example.android.trackurexpenses

//import com.example.android.trackurexpenses.ExpenseModel
import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_searchstore.*


class DataSource {

    companion object {
        private val expensesLiveData =
            MutableLiveData<ArrayList<ExpenseModel>>()

        private val expensesLiveSearch =
            MutableLiveData<ArrayList<ExpenseModel>>()

        //Get data from firestore data base
        fun createDataSet(email: String): MutableLiveData<ArrayList<ExpenseModel>> {
            val expens = ArrayList<ExpenseModel>()
            val db = FirebaseFirestore.getInstance();
            Log.d("email", email)
            db.collection("expenses")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("document", document.data.toString())

                        expens.add(
                            ExpenseModel(
                                document.data["Item"].toString() + ' ',
                                document.data["price"].toString() + "$  ",
                                document.data["Quantity"].toString() + ' ',
                                document.data["Storename"].toString() + ' ',
                                document.data["Date"].toString() + ' '
                            )
                        )
                    }
                    expensesLiveData.value = expens
                    Log.d("expensList", expens.size.toString())

                }
                .addOnFailureListener { exception ->
                    Log.w("test", "Error getting documents: ", exception)

                }

            Log.d("expensList", expens.size.toString())
            return expensesLiveData
        }

        //Search required Item from firestore database
        fun searchDataSet(query: String, email: String): MutableLiveData<ArrayList<ExpenseModel>> {
            var expen = ArrayList<ExpenseModel>()
            val db = FirebaseFirestore.getInstance();
            val upperQuery = query.toUpperCase()
            db.collection("expenses")
                .whereEqualTo("email", email)
                .whereEqualTo("Item", upperQuery)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("document", document.data.toString())

                        expen.add(
                            ExpenseModel(
                                document.data["Item"].toString() + ' ',
                                document.data["price"].toString() + "$  ",
                                document.data["Quantity"].toString() + ' ',
                                document.data["Storename"].toString() + ' ',
                                document.data["Date"].toString() + ' '
                            )
                        )
                    }
                    expensesLiveSearch.value = expen
                    Log.d("expensList", expen.size.toString())

                }
                .addOnFailureListener { exception ->
                    Log.w("test", "Error getting documents: ", exception)

                }

            Log.d("expensList", expen.size.toString())
            return expensesLiveSearch

        }
    }
}