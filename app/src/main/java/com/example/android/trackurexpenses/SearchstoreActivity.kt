package com.example.android.trackurexpenses


import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_searchstore.*
import com.example.android.trackurexpenses.MyAdapter.*
import com.google.firebase.auth.FirebaseAuth


class SearchstoreActivity : AppCompatActivity() {

    var transactions = ArrayList<ExpenseModel>()
    private lateinit var expensAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchstore)
        addDataSet()

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val expensesLiveSearch = DataSource.searchDataSet(
                    query,
                    FirebaseAuth.getInstance().currentUser?.email ?: ""
                )

                expensesLiveSearch.observe(
                    this@SearchstoreActivity,
                    object : Observer<ArrayList<ExpenseModel>> {
                        override fun onChanged(data: ArrayList<ExpenseModel>) {
                            initRecyclerView()
                            expensAdapter.submitList(data)
                            transactions = data
                            Log.d("data", data.toString())
                        }
                    })
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }


    private fun addDataSet() {
        val expensesLiveData =
            DataSource.createDataSet(FirebaseAuth.getInstance().currentUser?.email ?: "")

        expensesLiveData.observe(this, object : Observer<ArrayList<ExpenseModel>> {
            override fun onChanged(data: ArrayList<ExpenseModel>) {
                initRecyclerView()
                expensAdapter.submitList(data)
                transactions = data
//                Log.d("data", data.toString())
            }
        })
    }

    private fun initRecyclerView() {

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@SearchstoreActivity)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecorator)
            expensAdapter = MyAdapter()
            adapter = expensAdapter
        }
    }
}







