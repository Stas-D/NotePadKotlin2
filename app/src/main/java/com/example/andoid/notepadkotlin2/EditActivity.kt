package com.example.andoid.notepadkotlin2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.andoid.notepad_kotlin.db.MyDbManager
import kotlinx.android.synthetic.main.activity_edit.*
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    var id = 0
    var isEditState = false
    val imageRequestCode = 10
    var tempUri = "empty"
    val myDbManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        getMyIntents()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            imNewImage.setImageURI(data?.data)
            tempUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(
                data?.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }


    fun onClickChooseImage(view: View) {
        val chooser = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, imageRequestCode)
    }

    fun onClickDeleteImage(view: View) {
        imageContainer.visibility = View.GONE
        fbAddImage.visibility = View.VISIBLE
        tempUri = "empty"
    }
    fun onClickSave(view: View) {

        val myTitle = id_title.text.toString()
        val myDisc = id_discr.text.toString()

        if (myTitle != "" || myDisc != "") {
            /*  Toast.makeText(this, R.string.text_empty, Toast.LENGTH_SHORT).show()
        } else {*/
            if (isEditState) {
                myDbManager.updateItem(myTitle, myDisc, tempUri, id, getCurrentTime())
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
            } else {
                myDbManager.insertToDb(myTitle, myDisc, tempUri, getCurrentTime())
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
            }
        }
        myDbManager.closeDb()
        finish()

    }



    fun onClickAddImage(view: View) {
        imageContainer.visibility = View.VISIBLE
        fbAddImage.visibility = View.GONE
    }



    fun onEditEnable(view: View) {
        id_title.isEnabled = true
        id_discr.isEnabled = true
        fbEdit.visibility = View.GONE
        fbAddImage.visibility = View.VISIBLE
        if(tempUri == "empty") return
        imEdiImage.visibility = View.VISIBLE
        inDeleteImage.visibility = View.VISIBLE

    }



    fun getMyIntents() {
        fbEdit.visibility = View.GONE
        val i = intent

        if (i != null) {

            if (i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null) {
                fbAddImage.visibility = View.GONE
                id_title.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                isEditState = true
                id_title.isEnabled = false
                id_discr.isEnabled = false
                fbEdit.visibility = View.VISIBLE
                id_discr.setText(i.getStringExtra(MyIntentConstants.I_DISC_KEY))
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY, 0)
                if (i.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty") {
                    imageContainer.visibility = View.VISIBLE

                    tempUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                    imNewImage.setImageURI(Uri.parse(i.getStringExtra(MyIntentConstants.I_URI_KEY)))
                    inDeleteImage.setVisibility(View.GONE)
                    imEdiImage.setVisibility(View.GONE)
                }
            }
        }
    }

private fun  getCurrentTime():String{
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat ("dd-MM-yy kk:mm", Locale.getDefault())
    return formatter.format(time)
}
}
