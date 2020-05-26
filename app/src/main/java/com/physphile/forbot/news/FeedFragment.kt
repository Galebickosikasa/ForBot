package com.physphile.forbot.news

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.physphile.forbot.Constants
import com.physphile.forbot.R
import com.physphile.forbot.profile.ProfileMenuDialog
import java.util.*

class FeedFragment : Fragment() {
    private lateinit var adapter: NewsAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private lateinit var newsList: RecyclerView
    private lateinit var v: View
    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var admins: HashMap<*, *>
    private lateinit var phis: CheckBox
    private lateinit var math: CheckBox
    private lateinit var rus: CheckBox
    private lateinit var lit: CheckBox
    private lateinit var inf: CheckBox
    private lateinit var chem: CheckBox
    private lateinit var his: CheckBox
    private lateinit var ast: CheckBox
    private var msk = 0 // маска предметов
    private lateinit var sp: SharedPreferences
    private lateinit var spMxValue: SharedPreferences
    private var oldestLocalMessageTime: Double? = null
    private var list: MutableList<NewsFirebaseItem> = ArrayList()
    private lateinit var toolbar: MaterialToolbar
    private var needToUpd : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //инициализация фрагмента
        v = inflater.inflate(R.layout.feed_fragment_backdrop, container, false)

        // инициализация предметов
        phis = v.findViewById(R.id.physicsCheck) // 0
        math = v.findViewById(R.id.mathCheck) // 1
        rus = v.findViewById(R.id.russianCheck) // 2
        lit = v.findViewById(R.id.literatureCheck) // 3
        inf = v.findViewById(R.id.informaticsCheck) // 4
        chem = v.findViewById(R.id.chemistryCheck) // 5
        his = v.findViewById(R.id.historyCheck) // 6
        ast = v.findViewById(R.id.astronomyCheck) // 7

        //инициализация переменных
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser
        sp = activity?.getSharedPreferences("FlagToRemove", Context.MODE_PRIVATE)!!
        spMxValue = activity?.getSharedPreferences("MxValue", Context.MODE_PRIVATE)!!

        //инициализация View-элементов
        initRecyclerView()
        toolbar = v.findViewById(R.id.feedToolbar)
        mSwipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout)

        //заполнение View-элементов
        mSwipeRefreshLayout.setOnRefreshListener { getNews() }
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN)
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener)
        newsList.addOnScrollListener(onScroll)

        onChecked()

        //остальные методы
        adapter = NewsAdapter(requireContext())
        newsList.adapter = adapter
        getNews()

        // admins initialize
        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(adminsListener)
        return v
    }

    private val adminsListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            admins = (dataSnapshot.value as HashMap<*, *>?)!!
            mAuth.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null && admins.containsValue(user.uid)) {
                    toolbar.menu?.clear()
                    toolbar.inflateMenu(R.menu.admin_toolbar_menu)
                } else {
                    toolbar.menu?.clear()
                    toolbar.inflateMenu(R.menu.default_toolbar_menu)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private val onMenuItemClickListener = Toolbar.OnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.profile -> if (FirebaseAuth.getInstance().currentUser != null) {
                val profileDialog: DialogFragment = ProfileMenuDialog()
                profileDialog.show(parentFragmentManager, Constants.FRAGMENT_DIALOG_PROFILE_TAG)
            } else {
                startActivity(Intent(Constants.AUTH_ACTIVITY_PATH))
            }
            R.id.createNews -> startActivity(Intent(Constants.NEWS_CREATE_ACTIVITY_PATH))
        }
        false
    }

    fun setMxValue(value: Int?) {
        var t = spMxValue.getInt("mx", 0)
        if (value!! > t) t = value
        val e = spMxValue.edit()
        e?.putInt("mx", t)
        e?.apply()
    }

    private val onScroll = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
            val lastVisible = layoutManager!!.findLastVisibleItemPosition()
            if (adapter.newsList[lastVisible].number == needToUpd) {
                Log.e ("kek", "next upd")
                getNext5()
            }
        }
    }

    fun onNewsClick(position: Int) {
        val intent = Intent(Constants.NEWS_PAGE_ACTIVITY_PATH)
        intent.putExtra("newsTitle", adapter.newsList[position].title)
        intent.putExtra("newsText", adapter.newsList[position].text)
        intent.putExtra("newsDate", adapter.newsList[position].date)
        intent.putExtra("newsAuthor", adapter.newsList[position].author)
        intent.putExtra("newsTitleImageUri", adapter.newsList[position].uri)
        intent.putExtra("newsNumber", "" + adapter.newsList[position].number)
        startActivityForResult(intent, 1)
    }

    private fun initRecyclerView() {
        newsList = v.findViewById(R.id.NewsList)
        val linearLayoutManager = LinearLayoutManager(context)
        newsList.layoutManager = linearLayoutManager
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val f = sp.getBoolean("RemovedNews", false)
        if (f) getNews()
        val e = sp.edit()
        e.putBoolean("RemovedNews", false)
        e.apply()
    }

    private fun getNext5() {
        val ref = database.getReference(Constants.DATABASE_NEWS_PATH).limitToLast(10).endAt(oldestLocalMessageTime!!).orderByChild("number")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val item = dataSnapshot.getValue(NewsFirebaseItem::class.java)
                if (item != null) {
                    setMxValue(item.number)
                    mSwipeRefreshLayout.isRefreshing = false
                    if (msk == 0) {
                        list.add(item)
                    } else if (item.mask!! and msk != 0) {
                        list.add(item)
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (list.isEmpty()) return
                oldestLocalMessageTime = list[0].number!!.toDouble() - 1
                val s = list.size
                needToUpd = list[s / 2].number!!
                list.reverse()
                for (x in list) adapter.addItem(x)
                list.clear()
            }
        })
    }

    private fun onChecked() {
        phis.setOnCheckedChangeListener { _, _ ->
            msk = msk xor (1 shl 0)
            getNews()
        }
        math.setOnCheckedChangeListener { _, _ ->
            msk = msk xor (1 shl 1)
            getNews()
        }
        rus.setOnCheckedChangeListener { _, _ ->
            msk = msk xor (1 shl 2)
            getNews()
        }
        lit.setOnCheckedChangeListener { _, _ ->
            msk = msk xor (1 shl 3)
            getNews()
        }
        inf.setOnCheckedChangeListener { _, _ ->
            msk = msk xor (1 shl 4)
            getNews()
        }
        chem.setOnCheckedChangeListener { _, _ ->
            msk = msk xor (1 shl 5)
            getNews()
        }
        his.setOnCheckedChangeListener { _, _ ->
            msk = msk xor (1 shl 6)
            getNews()
        }
        ast.setOnCheckedChangeListener { _, _ ->
            msk = msk xor (1 shl 7)
            getNews()
        }
    }

    private fun getNews() {
        adapter.clearItems()
        oldestLocalMessageTime = 4e18
        if (msk == 0) {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.reverseLayout = false
            linearLayoutManager.stackFromEnd = false
            newsList.layoutManager = linearLayoutManager
            getNext5()
        }
        else {
            adapter.clearItems()
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true
            newsList.layoutManager = linearLayoutManager
            val ref = database.getReference(Constants.DATABASE_NEWS_PATH)
            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val item = dataSnapshot.getValue(NewsFirebaseItem::class.java)
                    if (item != null) {
                        setMxValue(item.number)
                        mSwipeRefreshLayout.isRefreshing = false
                        if (item.mask!! and msk != 0) {
                            adapter.addItem(item)
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                override fun onChildRemoved(p0: DataSnapshot) {}
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): FeedFragment {
            return FeedFragment()
        }
    }
}

