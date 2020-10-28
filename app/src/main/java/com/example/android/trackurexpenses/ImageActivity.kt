package com.example.android.trackurexpenses

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_image.*
import java.io.IOException


class ImageActivity : AppCompatActivity() {
    private lateinit var uri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        img_pick_btn.setOnClickListener {
            //check runtime permission
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //permission already granted
                    pickImageFromGallery()
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery()
            }
        }
        back_to_home_textview.setOnClickListener {
            finish()
        }

        scan_btn.setOnClickListener {
            var storeName = ""
            var purchaseDate = ""
            val items: MutableList<String> = ArrayList()
            val image: FirebaseVisionImage

            try {
                image = uri.let { FirebaseVisionImage.fromFilePath(this, it) }
                val detector = FirebaseVision.getInstance()
                    .onDeviceTextRecognizer
                detector.processImage(image)
                    .addOnSuccessListener { firebaseVisionText ->
                        var blockNum = 0
                        for (block in firebaseVisionText.textBlocks) {
                            blockNum++
//                            Log.d("ImageProcessResultBlk", "===============================================")
//                            Log.d("ImageProcessResultBlk", blockNum.toString() + ' ' + block.text)
                            var lineNum = 0
                            for (line in block.lines) {
                                lineNum++
                                if (blockNum == 1 && lineNum == 1) {
                                    storeName = line.text
                                }
//                                Log.d("ImageProcessResultline", "------------------------------------------")
//                                Log.d("ImageProcessResultline", lineNum.toString() + ' ' + line.text)
                                if (blockNum >= 7 && blockNum <= firebaseVisionText.textBlocks.size - 8) {
                                    items.add(line.text)
                                }
                                var elementNum = 0
                                for (element in line.elements) {
                                    elementNum++
                                    if (blockNum == 3 && lineNum == 1 && elementNum >= 3 && elementNum <= 5) {
                                        purchaseDate += element.text + " "
                                    }

//                                    Log.d(
//                                        "ImageProcessResultEle",
//                                        elementNum.toString() + ' ' + element.text
//                                    )
                                }
                            }
                        }
//                        Log.d("ImageProcessingResult", firebaseVisionText.text)
                        Toast.makeText(this, "Image scanned successfully", Toast.LENGTH_SHORT)
                            .show()
                        val dynamicIntent = Intent(this, DynamicActivity::class.java)
                        dynamicIntent.putExtra("storeName", storeName)
                        dynamicIntent.putExtra("purchaseDate", purchaseDate)
                        dynamicIntent.putExtra("items", items.toString())
                        dynamicIntent.putExtra(
                            "email",
                            FirebaseAuth.getInstance().currentUser?.email ?: ""
                        )
                        startActivity(dynamicIntent)
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        Toast.makeText(
                            this,
                            "Task failed with an exception: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            image_view.setImageURI(data?.data)
            uri = data?.data!!
        }
    }

}
