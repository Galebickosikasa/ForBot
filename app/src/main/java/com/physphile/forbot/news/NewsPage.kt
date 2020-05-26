package com.physphile.forbot.news

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import java.util.*

class NewsPage: BaseSwipeActivity() {
    private var user: FirebaseUser? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    var admins: HashMap<String, String>? = null
    private var sp: SharedPreferences? = null
    private val onMenuItemClickListener = Toolbar.OnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.editNews -> {
            }
            R.id.removeNews -> {
                val e = sp!!.edit()
                e.putBoolean("RemovedNews", true)
                e.apply()
                val num = intent!!.getStringExtra("newsNumber")
                database!!.getReference(Constants.DATABASE_NEWS_PATH + num).removeValue()
                storage!!.getReference(Constants.STORAGE_NEWS_IMAGE_PATH + num).delete()
                super@NewsPage.finish()
            }
        }
        false
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

        //инициализация View-элементов
        val toolbar = findViewById<Toolbar>(R.id.newsToolbar)
        val newsText = findViewById<TextView>(R.id.newsText)
        val newsTitle = findViewById<TextView>(R.id.newsTitle)
        val newsDate = findViewById<TextView>(R.id.newsDate)
        val newsAuthor = findViewById<TextView>(R.id.newsAuthor)
        val newsTitleImage = findViewById<ImageView>(R.id.newsTitleImage)
        val appBarLayout = findViewById<AppBarLayout>(R.id.main_appbar)

        //заполнение View-элементов
        val width = windowManager.defaultDisplay.width
        appBarLayout.layoutParams = CoordinatorLayout.LayoutParams(width,
                width * 10 / 16)
        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                admins = dataSnapshot.value as HashMap<String, String>?
                if (user != null && admins!!.containsValue(user!!.uid)) {
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