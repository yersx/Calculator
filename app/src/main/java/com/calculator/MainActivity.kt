package com.calculator

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mOne.setOnClickListener { addtoExpression("1",true) }
        mTwo.setOnClickListener { addtoExpression("2",true) }
        mThree.setOnClickListener { addtoExpression("3",true) }
        mFour.setOnClickListener { addtoExpression("4",true) }
        mFive.setOnClickListener { addtoExpression("5",true) }
        mSix.setOnClickListener { addtoExpression("6",true) }
        mSeven.setOnClickListener { addtoExpression("7",true) }
        mEight.setOnClickListener { addtoExpression("8",true) }
        mNine.setOnClickListener { addtoExpression("9",true) }
        mZero.setOnClickListener { addtoExpression("0",true) }
        mDot.setOnClickListener { addtoExpression(".",true) }

        mPlus.setOnClickListener { addtoExpression("+",false) }
        mMinus.setOnClickListener { addtoExpression("-",false) }
        mDel.setOnClickListener { addtoExpression("/",false) }
        mMul.setOnClickListener { addtoExpression("*",false) }
        mOpen.setOnClickListener { addtoExpression("(",false) }
        mClose.setOnClickListener { addtoExpression(")",false) }

        mClear.setOnClickListener {
            mExpression.text=""
            mResult.text=""
        }
        mBack.setOnClickListener {
            val str = mExpression.text.toString()
            if(str.isNotEmpty()){
                mExpression.text = str.substring(0,str.length-1)
                }
            mResult.text=""
        }
        mEqual.setOnClickListener {
            try {
                val expression = ExpressionBuilder(mExpression.text.toString()).build()
                val res = expression.evaluate()
                val longRes = res.toLong()
                if(res == longRes.toDouble())
                    mResult.text = longRes.toString()
                else
                    mResult.text = res.toString()

            } catch (e: Exception){
                Log.d("log","msg"+ e.message)
            }
        }
    }

    fun addtoExpression(str: String, clear: Boolean){
        if(clear){
            mResult.text=""
            mExpression.append(str)
        } else {
            mExpression.append(mResult.text)
            mExpression.append(str)
            mResult.text=""
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.upload){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    pickImageFromGallery();
                }
            }

        } else if(item.itemId == R.id.exit){
            var builder = AlertDialog.Builder(this)
            builder.setMessage("Действительно закрыть?")
            builder.setCancelable(true)
            builder.setNegativeButton("Нет") { dialog, _ ->  dialog.cancel()}
            builder.setPositiveButton("Да") { _, _ -> finish() }

            var alerDialog = builder.create()
            alerDialog.show()
        }
        return true
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000 // image pick code
        private val PERMISSION_CODE = 1001 //  permission code
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery() // granted from popup
                }
                else
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()

            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            val uri = data!!.data
            try {
                val inputStream = uri?.let { contentResolver.openInputStream(it) }
                displayPart.background = Drawable.createFromStream(inputStream, uri.toString())
            } catch (e: Exception ){

            }
        }
    }
}
