package com.physphile.forbot.news

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.physphile.forbot.BaseSwipeActivity
import com.physphile.forbot.ClassHelper
import com.physphile.forbot.Constants
import com.physphile.forbot.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.IOException
import java.util.*

class NewsCreateActivity : BaseSwipeActivity() {
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var NewsTitleImage: ImageView? = null
    private var NewsTitle: EditText? = null
    private var database: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var newsText: EditText? = null
    private var btn: CircularProgressImageButton? = null
    private var parent: CoordinatorLayout? = null
    private var toolbar: Toolbar? = null
    private var num = 0
    private var mask = 0
    private var phis: CheckBox? = null
    private var math: CheckBox? = null
    private var rus: CheckBox? = null
    private var lit: CheckBox? = null
    private var inf: CheckBox? = null
    private var chem: CheckBox? = null
    private var his: CheckBox? = null
    private var ast: CheckBox? = null
    private val onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.newsTitleImage -> CropImage.activity()
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(16, 10)
                    .start(this@NewsCreateActivity)
            R.id.newsDoneBtn -> if (newsText!!.text.toString() != "" && NewsTitle!!.text.toString() != "") {
                putNewsFirebase(NewsTitle!!.text.toString(), newsText!!.text.toString())
                finish()
            } else {
                Snackbar.make(v, "Сначала заполните все поля", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //инициализация активити
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_create)

        // инициализация предметов
        phis = findViewById(R.id.physicsCheck) // 0
        math = findViewById(R.id.mathCheck) // 1
        rus = findViewById(R.id.russianCheck) // 2
        lit = findViewById(R.id.literatureCheck) // 3
        inf = findViewById(R.id.informaticsCheck) // 4
        chem = findViewById(R.id.chemistryCheck) // 5
        his = findViewById(R.id.historyCheck) // 6
        ast = findViewById(R.id.astronomyCheck) // 7

        phis!!.setOnCheckedChangeListener { buttonView, isChecked -> mask = mask xor (1 shl 0) }
        math!!.setOnCheckedChangeListener { buttonView, isChecked -> mask = mask xor (1 shl 1) }
        rus!!.setOnCheckedChangeListener { buttonView, isChecked -> mask = mask xor (1 shl 2) }
        lit!!.setOnCheckedChangeListener { buttonView, isChecked -> mask = mask xor (1 shl 3) }
        inf!!.setOnCheckedChangeListener { buttonView, isChecked -> mask = mask xor (1 shl 4) }
        chem!!.setOnCheckedChangeListener { buttonView, isChecked -> mask = mask xor (1 shl 5) }
        his!!.setOnCheckedChangeListener { buttonView, isChecked -> mask = mask xor (1 shl 6) }
        ast!!.setOnCheckedChangeListener { buttonView, isChecked -> mask = mask xor (1 shl 7) }

        //инициализация переменных
        val sp = getSharedPreferences("MxValue", Context.MODE_PRIVATE)
        num = sp.getInt("mx", 0) + 1
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        //инициализация View-элементов
        NewsTitleImage = findViewById(R.id.newsTitleImage)
        parent = findViewById(R.id.parent)
        toolbar = findViewById(R.id.newsToolbar)
        newsText = findViewById(R.id.newsText)
        NewsTitle = findViewById(R.id.newsTitle)

        //заполнение View-элементов
        val width = windowManager.defaultDisplay.width
        NewsTitleImage!!.layoutParams = CollapsingToolbarLayout.LayoutParams(width, width * 10 / 16)
        NewsTitleImage!!.setOnClickListener(onClickListener)
        btn = getBtn()
        parent!!.addView(btn)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar!!.setNavigationOnClickListener { finish() }
        toolbar!!.setOnMenuItemClickListener(ClassHelper(this, supportFragmentManager).onMenuItemClickListener)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_news_create
    }

    private fun getBtn(): CircularProgressImageButton {
        btn = CircularProgressImageButton(this)
        val classHelper = ClassHelper(this)
        val lp = CoordinatorLayout.LayoutParams(classHelper.dpToPx(70f), classHelper.dpToPx(70f))
        lp.anchorId = R.id.newsTitleImage
        lp.anchorGravity = Gravity.BOTTOM or Gravity.END
        lp.marginEnd = classHelper.dpToPx(16f)
        btn!!.layoutParams = lp
        btn!!.background = getDrawable(R.drawable.circle_shape)
        btn!!.id = R.id.newsDoneBtn
        btn!!.setOnClickListener(onClickListener)
        btn!!.isClickable = false
        btn!!.elevation = classHelper.dpToPx(8f).toFloat()
        btn!!.setImageResource(R.drawable.ic_block_black_24dp)
        btn!!.finalCorner = classHelper.dpToPx(35f).toFloat()
        btn!!.initialCorner = classHelper.dpToPx(35f).toFloat()
        btn!!.spinningBarColor = resources.getColor(R.color.colorSecond)
        return btn as CircularProgressImageButton
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this).contentResolver, resultUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                NewsTitleImage!!.setImageBitmap(bitmap)
                btn!!.startAnimation()
                btn!!.setImageResource(R.drawable.ic_file_download_black_24dp)
                uploadImage(resultUri, num.toString())
            }
        }
    }

    private fun uploadImage(filePath: Uri, path: String) {
        storageReference = storage!!.getReference(Constants.STORAGE_NEWS_IMAGE_PATH + path)
        storageReference!!.putFile(filePath).addOnSuccessListener {
            btn!!.revertAnimation()
            btn!!.setImageResource(R.drawable.ic_done_black_24dp)
            btn!!.isClickable = true
        }
    }

    fun putNewsFirebase(title: String?, text: String?) {
        databaseReference = database!!.getReference(Constants.DATABASE_NEWS_PATH + num)
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                storage!!.getReference(Constants.STORAGE_NEWS_IMAGE_PATH + num)
                        .downloadUrl.addOnSuccessListener { uri ->
                            val calendar = Calendar.getInstance()
                            val nfi = NewsFirebaseItem(title,
                                    uri.toString(),
                                    text,
                                    FirebaseAuth.getInstance().currentUser!!.email,
                                    calendar[Calendar.DATE].toString() + "." + (calendar[Calendar.MONTH] + 1) + "." + calendar[Calendar.YEAR],
                                    num, mask
                            )
                            databaseReference!!.setValue(nfi)
                        }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {}
}