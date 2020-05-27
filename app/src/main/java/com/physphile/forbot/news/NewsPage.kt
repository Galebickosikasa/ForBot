package com.physphile.forbot.news

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.physphile.forbot.BaseSwipeActivity
import com.physphile.forbot.Constants
import com.physphile.forbot.R
import java.io.FileOutputStream
import java.util.*

class NewsPage: BaseSwipeActivity() {
    private var user: FirebaseUser? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var admins: HashMap<*, *>
    private lateinit var sp: SharedPreferences
    private lateinit var spItem : SharedPreferences
    private lateinit var toolbar : Toolbar
    private lateinit var newsText : TextView
    private lateinit var newsTitle : TextView
    private lateinit var newsDate : TextView
    private lateinit var newsAuthor : TextView
    private lateinit var newsTitleImage : ImageView
    private lateinit var appBarLayout : AppBarLayout

    private val onMenuItemClickListener = Toolbar.OnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.editNews -> {
                val intent = Intent (Constants.NEWS_CREATE_ACTIVITY_PATH)
                intent.putExtra("Edit", true)
                intent.putExtra("Text", newsText.text.toString())
                intent.putExtra("Title", newsTitle.text.toString())
                intent.putExtra("Mask", getIntent().getIntExtra("newsMask", 0))
                intent.putExtra("Number", getIntent().getStringExtra("newsNumber"))
                intent.putExtra("Author", getIntent().getStringExtra("newsAuthor"))
                intent.putExtra("Date", getIntent().getStringExtra("newsDate"))
                val bmp = (newsTitleImage.drawable as BitmapDrawable).bitmap
                val filename = "bitmap.png"
                val stream: FileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close ()
                intent.putExtra("Bitmap", filename)
                startActivityForResult(intent, 1)
            }
            R.id.removeNews -> {
                val e = sp.edit()
                e.putBoolean("RemovedNews", true)
                e.apply()
                val num = intent.getStringExtra("newsNumber")
                database.getReference(Constants.DATABASE_NEWS_PATH + num).removeValue()
                storage.getReference(Constants.STORAGE_NEWS_IMAGE_PATH + num).delete()
                super@NewsPage.finish()
            }
        }
        false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val e = spItem.edit()
            e.putBoolean("Edit", false)
            e.apply()
            super@NewsPage.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //инициализация активити
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_page)

        //инициализация переменных
        user = FirebaseAuth.getInstance().currentUser
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        sp = getSharedPreferences("FlagToRemove", Context.MODE_PRIVATE)
        spItem = getSharedPreferences("NewsItem", Context.MODE_PRIVATE)

        appBarLayout = findViewById(R.id.main_appbar)
        toolbar = findViewById(R.id.newsToolbar)
        newsText = findViewById(R.id.newsText)
        newsTitle = findViewById(R.id.newsTitle)
        newsDate = findViewById(R.id.newsDate)
        newsAuthor = findViewById(R.id.newsAuthor)
        newsTitleImage = findViewById(R.id.newsTitleImage)

        //заполнение View-элементов
        val width = windowManager.defaultDisplay.width
        appBarLayout.layoutParams = CoordinatorLayout.LayoutParams(width,
                width * 10 / 16)
        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                admins = dataSnapshot.value as HashMap<*, *>
                if (user != null && admins.containsValue(user!!.uid)) {
                    toolbar.menu.clear()
                    toolbar.inflateMenu(R.menu.admin_news_page_menu)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        newsTitle.text = intent.getStringExtra("newsTitle")
        newsAuthor.text = intent.getStringExtra("newsAuthor")
        newsDate.text = intent.getStringExtra("newsDate")
        newsText.text = intent.getStringExtra("newsText")
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener)
        Glide.with(this).load(intent.getStringExtra("newsTitleImageUri")).into(newsTitleImage)
    }

    override val layoutId: Int
        get() {
            return R.layout.activity_news_page
        }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {}
}