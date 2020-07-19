package com.example.habittrainer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.habittrainer.dao.HabitDbTable
import kotlinx.android.synthetic.main.activity_create_habit.*
import kotlinx.android.synthetic.main.single_card.*
import java.io.IOException

class CreateHabitActivity : AppCompatActivity() {

    private val tag = CreateHabitActivity::class.java.simpleName
    private val chooseImageRequest = 1
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
        startActivityForResult(chooser, chooseImageRequest)

        Log.d(tag, "Intent to choose image sent...")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == chooseImageRequest && resultCode == Activity.RESULT_OK &&
            data != null
        ) {

            Log.d(tag, "An image was chosen by the user.")

            tryReadBitmap(data.data)?.let {
                iv_image.setImageBitmap(it)
                imageBitmap = it

                Log.d(tag, "Image view updated with selected image.")
            }
        }
    }

    private fun tryReadBitmap(data: Uri?): Bitmap? {

        return try {
            data?.let {
                val source = ImageDecoder.createSource(contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun storeHabit(view: View) {

        if (et_title.isBlank() || et_description.isBlank()) {

            Log.d(tag, "No habit stored: title and/or description are blank.")
            displayErrorMessage("Your habit needs engaging title and description.")
            return
        } else if (imageBitmap == null) {

            Log.d(tag, "No habit stored: image not selected")
            displayErrorMessage("Your habit needs a motivating picture.")
            return
        }

        hideErrorMessage()

        val title = et_title.text.toString()
        val description = et_description.text.toString()
        val habit = Habit(title, description, imageBitmap!!)
        val id = HabitDbTable(this).store(habit)

        if (id == -1L) {
            displayErrorMessage("Error while trying to save habit into DB.")
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun displayErrorMessage(msg: String) {

        tv_error.text = msg
        tv_error.visibility = View.VISIBLE
    }

    private fun hideErrorMessage() {

        tv_error.text = ""
        tv_error.visibility = View.INVISIBLE
    }
}

private fun EditText.isBlank() = this.text.toString().isBlank()
