package com.example.android.trackurexpenses

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_manualentry.*
import java.util.*
import kotlin.collections.HashMap


class ManualentryActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manualentry)
        val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

        submit_btn.setOnClickListener {
            val db = FirebaseFirestore.getInstance();

            val expense: MutableMap<String, Any> = HashMap()
            expense["Date"] = date_input.text.toString();
            expense["Storename"] = store_name.text.toString()
            expense["Item"] = Item_name.text.toString()
            expense["Quantity"] = quantity_input.text.toString()
            expense["price"] = price_input.text.toString()
            expense["email"] = FirebaseAuth.getInstance().currentUser?.email ?: ""

            if (date_input.text.toString().isEmpty() || date_input.text.toString().isEmpty() ||
                store_name.text.toString().isEmpty() || Item_name.text.toString().isEmpty() || price_input.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please enter all fields!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                db.collection("expenses")
                    .add(expense)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Details succesfully entered",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Failed to add expense details!   ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        back_home_textview.setOnClickListener {
            finish()
        }

    }
}


