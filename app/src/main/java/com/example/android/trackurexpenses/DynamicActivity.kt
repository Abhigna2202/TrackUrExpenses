package com.example.android.trackurexpenses

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_dynamic.*


class DynamicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic)

        //get scanned details from image activity
        val scannedStoreName = intent.getStringExtra("storeName") ?: ""
        val scannedPurchaseDate = intent.getStringExtra("purchaseDate") ?: ""
        val scannedItemsString = intent.getStringExtra("items") ?: ""

        val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

        val groceries: MutableList<MutableList<EditText>> = ArrayList()
        val tableLayout = TableLayout(this)
        tableLayout.gravity = Gravity.CENTER
        tableLayout.setPadding(10, 10, 10, 10)

        //create parent linearlayout into which we add other layouts
        val linearLayoutParent = LinearLayout(this)
        linearLayoutParent.orientation = LinearLayout.VERTICAL

        val (linearLayoutChild, saveButton) = addButtonsToParent(groceries, tableLayout)

        linearLayoutParent.addView(linearLayoutChild)

        var (storeName, purchaseDate) = addStoreNamePurchaseDateToLayout(
            scannedStoreName,
            scannedPurchaseDate,
            linearLayoutParent
        )

        addItemNameQtyAmountHeadingsToLayout(tableLayout)

        addItemNameQtyAmountDataToLayout(scannedItemsString, groceries, tableLayout)

        //add table to horizontalscrollable view
        val horizontalScrollView = HorizontalScrollView(this)
        horizontalScrollView.addView(tableLayout)

        //add horizontalscrollable view to scrollable view
        val scrollView = ScrollView(this)
        scrollView.addView(horizontalScrollView)

        //add scrollable view to linear layout
        linearLayoutParent.addView(scrollView)
        dynamic_layout.addView(linearLayoutParent)

        saveButton.setOnClickListener {
            insertDataToFirestore(groceries, storeName, purchaseDate, email)
        }

    }

    private fun addItemNameQtyAmountDataToLayout(
        scannedItemsString: String,
        groceries: MutableList<MutableList<EditText>>,
        tableLayout: TableLayout
    ) {
        val items = ArrayList<List<String>>()
        val scannedItemsList = scannedItemsString.split(",")
        val itemNames = ArrayList<String>()
        val itemQtys = ArrayList<String>()
        val itemAmounts = ArrayList<String>()
        scannedItemsList.forEachIndexed { index, element ->
            if (index < scannedItemsList.size / 3) {
                itemNames.add(element)
            } else {
                if (index >= scannedItemsList.size / 3 && index < 2 * scannedItemsList.size / 3) {
                    itemQtys.add(element)
                } else {
                    itemAmounts.add(element)
                }
            }
        }
        // Quantity Hardcoded intentionally as ML kit isn't scanning quantity clearly
        // TODO: To be removed once a good Vision API is used for image scanning
        itemNames.forEachIndexed { index, _ ->
            items.add(listOf(itemNames[index], "1", itemAmounts[index]))
        }

        for (item in items) {
            addRow(item, groceries, tableLayout)
        }
    }

    //Dynamically add Headings to layout
    private fun addItemNameQtyAmountHeadingsToLayout(tableLayout: TableLayout) {
        val headings = listOf("Item Name", "Qty", "Amount")
        val tableRow = TableRow(this)
//        tableRow.orientation = LinearLayout.HORIZONTAL
        for (heading in headings) {
            tableRow.layout(10, 10, 10, 10)
            tableRow.setPadding(10, 20, 10, 10)

            //TextView
            val textValue = TextView(this)
            textValue.textSize = 20f
            textValue.text = heading
            textValue.setTypeface(null, Typeface.BOLD)
            textValue.setPadding(10, 10, 10, 10)

            // add TextView to LinearLayout
            tableRow.addView(textValue)
        }
        tableLayout.addView(tableRow)
    }

    //Dynamically add storename & purchase Date to layout
    private fun addStoreNamePurchaseDateToLayout(
        scannedStoreName: String,
        scannedPurchaseDate: String,
        linearLayoutParent: LinearLayout
    ): Pair<EditText, EditText> {
        var storeName = EditText(this)
        var purchaseDate = EditText(this)
        val headers =
            listOf(
                listOf("Store Name", scannedStoreName),
                listOf("Purchase Date", scannedPurchaseDate)
            )
        for (header in headers) {
            val linearLayoutHeader = LinearLayout(this)
            linearLayoutHeader.orientation = LinearLayout.HORIZONTAL
            linearLayoutHeader.layout(10, 10, 10, 10)
            linearLayoutHeader.setPadding(10, 20, 10, 10)

            val textValue = TextView(this)
            textValue.textSize = 20f
            textValue.text = header[0]
            textValue.setTypeface(null, Typeface.BOLD)
            textValue.setPadding(10, 10, 10, 10)

            // Editable text fields population
            val editText = EditText(this)
            editText.textSize = 20f
            editText.setText(header[1])
            editText.setBackgroundColor(Color.WHITE)
            editText.setPadding(10, 10, 10, 10)
            if (header[0] == "Store Name") {
                storeName = editText
            } else {
                purchaseDate = editText
            }

            linearLayoutHeader.addView(textValue)
            linearLayoutHeader.addView(editText)
            linearLayoutParent.addView(linearLayoutHeader)
        }
        return Pair(storeName, purchaseDate)
    }

    // Writes the bulk transaction to firestore database.
    private fun insertDataToFirestore(
        groceries: MutableList<MutableList<EditText>>,
        storeName: EditText,
        purchaseDate: EditText,
        email: String
    ) {
        var allFieldsExist = true
        for (gr in groceries) {
            if (gr[0].text.toString().isEmpty() || gr[1].text.toString()
                    .isEmpty() || gr[2].text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please enter all fields!",
                    Toast.LENGTH_SHORT
                ).show()
                allFieldsExist = false
            }
        }
        if (allFieldsExist) {
            for (gr in groceries) {
                saveDataToFirebase(
                    this,
                    storeName.text.toString(),
                    purchaseDate.text.toString(),
                    gr[0].text.toString(),
                    gr[1].text.toString(),
                    gr[2].text.toString(),
                    email
                )
            }
            finish()
        }
    }

    // Dynamically add buttons after scanning the image
    private fun addButtonsToParent(
        groceries: MutableList<MutableList<EditText>>,
        tableLayout: TableLayout
    ): Pair<LinearLayout, Button> {
        val linearLayoutChild = LinearLayout(this)

        val saveButton = Button(this)
        saveButton.text = "Save"
        saveButton.setBackgroundColor(Color.WHITE)
        saveButton.setBackgroundResource(R.drawable.round_corners)
        linearLayoutChild.addView(saveButton)

        val addRowButton = Button(this)
        addRowButton.text = "Add Row"
        addRowButton.setBackgroundColor(Color.WHITE)
        addRowButton.setBackgroundResource(R.drawable.round_corners)
        addRowButton.setOnClickListener {
            val item = listOf("", "", "")
            addRow(item, groceries, tableLayout)
        }

        linearLayoutChild.addView(addRowButton)
        return Pair(linearLayoutChild, saveButton)
    }

    //Saves data to firebase
    private fun saveDataToFirebase(
        context: Context,
        storeName: String,
        purchaseDate: String,
        itemName: String,
        qty: String,
        price: String,
        email: String
    ) {
        val db = FirebaseFirestore.getInstance()

        val expense: MutableMap<String, Any> = HashMap()
        expense["Date"] = purchaseDate
        expense["Storename"] = storeName
        expense["Item"] = itemName
        expense["Quantity"] = qty
        expense["price"] = price
        expense["email"] = email
        Log.d("email", email)


        db.collection("expenses")
            .add(expense)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Details succesfully entered",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Failed to add expense details!   ",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    //Logic to add another editable row along with other rows
    private fun addRow(
        item: List<String>,
        groceries: MutableList<MutableList<EditText>>,
        tableLayout: TableLayout
    ) {
        var id = 1
        val row = TableRow(this)
        row.setPadding(10, 10, 10, 10)

        val rowParams: TableRow.LayoutParams =
            TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        rowParams.setMargins(10, 10, 10, 10)
        val etList: MutableList<EditText> = ArrayList()
        for (col in item) {
            val editTextElement = EditText(this)
            editTextElement.setText(col)
            editTextElement.setBackgroundColor(Color.WHITE)
            editTextElement.setPadding(10, 10, 10, 10)
            editTextElement.layoutParams = rowParams
            editTextElement.setBackgroundResource(R.drawable.border)
            editTextElement.id = id
            id++
            etList.add(editTextElement)
            row.addView(editTextElement)
        }
        //Delete button on dynamic page to delete a row
        val deleteButton = Button(this)
        deleteButton.text = "Remove"
        deleteButton.setBackgroundColor(Color.WHITE)
        deleteButton.setBackgroundResource(R.drawable.round_corners)
        deleteButton.id = id
        row.addView(deleteButton)

        groceries.add(etList)

        tableLayout.addView(row)
        deleteButton.setOnClickListener {
            tableLayout.removeView(row)
            groceries.remove(etList)
        }
    }
}
