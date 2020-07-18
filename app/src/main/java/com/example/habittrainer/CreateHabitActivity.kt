package com.example.habittrainer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import kotlinx.android.synthetic.main.activity_create_habit.*
import java.io.IOException

class CreateHabitActivity : AppCompatActivity() {

    private val TAG = CreateHabitActivity::class.java.simpleName
    private val CHOOSE_IMAGE_REQUEST = 1
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)
    }

    fun chooseImage(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        val chooser = Intent.createChooser(intent, "Choose image for habit")
        startActivityForResult(chooser, CHOOSE_IMAGE_REQUEST)

        Log.d(TAG, "Intent to choose image sent...")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null) {

            Log.d(TAG, "An image was chosen by the user.")

            val bitmap = tryReadBitmap(data.data!!)
            bitmap?.let {
                iv_image.setImageBitmap(it)
                imageBitmap = it

                Log.d(TAG, "Image view updated with selected image.")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun tryReadBitmap(data: Uri): Bitmap? {
        return try {
            val source = ImageDecoder.createSource(data.toFile())
            ImageDecoder.decodeBitmap(source)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun storeHabit(view: View) {
        if (et_title.text.toString().isBlank() || et_description.text.toString().isBlank()) {

            Log.d(TAG, "No habit stored: title and/or description are blank.")
            displayErrorMessage("Your habit needs engaging title and description.")
            return
        } else if (imageBitmap == null) {

            Log.d(TAG, "No habit stored: image not selected")
            displayErrorMessage("Your habit needs a motivating picture.")
            return
        }
    }

    private fun displayErrorMessage(msg: String) {
        tv_error.text = msg
        tv_error.visibility = View.VISIBLE
    }
}