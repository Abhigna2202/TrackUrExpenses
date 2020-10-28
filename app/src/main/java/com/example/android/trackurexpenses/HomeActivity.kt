package com.example.android.trackurexpenses

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home)

        upload_receipt_button_home.setOnClickListener {
            val imageIntent = Intent(this, ImageActivity::class.java)
            startActivity(imageIntent)
        }

        //navigates to expense manual entry screen.
        manual_entry_button_home2.setOnClickListener {
            val manualIntent = Intent(this, ManualentryActivity::class.java)
            startActivity(manualIntent)
        }

        //navigate to know your best store screen
        best_store_button_home.setOnClickListener {
            val searchIntent = Intent(this, SearchstoreActivity::class.java)
            startActivity(searchIntent)
        }

        //navigate to expenses history screen
        expense_history_button_home.setOnClickListener {
            val historyIntent = Intent(this, ExpensesHistory::class.java)
            startActivity(historyIntent)
        }

        //Signout from the app
        signout_button_home.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

    }
}
