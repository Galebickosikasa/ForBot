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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.physphile.forbot.Constants
import com.physphile.forbot.R
import com.physphile.forbot.profile.ProfileMenuDialog
import java.util.*

class FeedFragment : Fragment() {
    private var adapter: NewsAdapter? = null
    private var storage: FirebaseStorage? = null
    private var database: FirebaseDatabase? = null
    private var newsList: RecyclerView? = null
    private var v: View? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var admins: HashMap<*, *>? = null
    private var phis: CheckBox? = null
    private var math: CheckBox? = null
    private var rus: CheckBox? = null
    private var lit: CheckBox? = null
    private var inf: CheckBox? = null
    private var chem: CheckBox? = null
    private var his: CheckBox? = null
    private var ast: CheckBox? = null
    private var msk = 0 // маска предметов
    private var sp: SharedPreferences? = null
    private var spMxValue: SharedPreferences? = null
    private var oldestLocalMessageTime : Double? = null
    private var list : MutableList <NewsFirebaseItem> = ArrayList ()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //инициализация фрагмента
        v = inflater.inflate(R.layout.feed_fragment_backdrop, container, false)

        // инициализация предметов
        phis = v!!.findViewById(R.id.physicsCheck) // 0
        math = v!!.findViewById(R.id.mathCheck) // 1
        rus = v!!.findViewById(R.id.russianCheck) // 2
        lit = v!!.findViewById(R.id.literatureCheck) // 3
        inf = v!!.findViewById(R.id.informaticsCheck) // 4
        chem = v!!.findViewById(R.id.chemistryCheck) // 5
        his = v!!.findViewById(R.id.historyCheck) // 6
        ast = v!!.findViewById(R.id.astronomyCheck) // 7
        phis!!.setOnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 0)
            news
        }
        math!!.setOnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 1)
            news
        }
        rus!!.setOnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 2)
            news
        }
        lit!!.setOnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 3)
            news
        }
        inf!!.setOnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 4)
            news
        }
        chem!!.setOnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 5)
            news
        }
        his!!.setOnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 6)
            news
        }
        ast!!.setOnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 7)
            news
        }

        //инициализация переменных
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser
        sp = activity?.getSharedPreferences("FlagToRemove", Context.MODE_PRIVATE)
        spMxValue = activity?.getSharedPreferences("MxValue", Context.MODE_PRIVATE)

        //инициализация View-элементов
        initRecyclerView()
        val toolbar: Toolbar = v!!.findViewById(R.id.feedToolbar)
        mSwipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)

        //заполнение View-элементов
        mSwipeRefreshLayout!!.setOnRefreshListener { news }
        mSwipeRefreshLayout!!.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN)
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener)
        newsList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val lastVisible = layoutManager!!.findLastVisibleItemPosition()
//                Log.e ("kek", "last ${adapter!!.newsList[lastVisible].number}")
                if (adapter!!.newsList[lastVisible].number == oldestLocalMessageTime?.toInt ()) {
                    oldestLocalMessageTime = (adapter!!.newsList[lastVisible].number!! - 1).toDouble()
                    getNext5()
                }
            }

        })

        //остальные методы
        adapter = NewsAdapter(requireContext())
        newsList!!.adapter = adapter
        news

        // admins initialize
        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                admins = dataSnapshot.value as HashMap<*, *>?
                mAuth!!.addAuthStateListener { firebaseAuth ->
                    val user = firebaseAuth.currentUser
                    if (user != null && admins!!.containsValue(user.uid)) {
                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.admin_toolbar_menu)
                    } else {
                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.default_toolbar_menu)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return v
    }

    fun setMxValue(value: Int?) {
        var t = spMxValue?.getInt("mx", 0)
        if (value!! > t!!) t = value
        val e = spMxValue?.edit()
        e?.putInt("mx", t)
        e?.apply()
    }

    fun onNewsClick(position: Int) {
        val intent = Intent(Constants.NEWS_PAGE_ACTIVITY_PATH)
        intent.putExtra("newsTitle", adapter?.newsList?.get(position)?.title)
        intent.putExtra("newsText", adapter?.newsList?.get(position)?.text)
        intent.putExtra("newsDate", adapter?.newsList?.get(position)?.date)
        intent.putExtra("newsAuthor", adapter?.newsList?.get(position)?.author)
        intent.putExtra("newsTitleImageUri", adapter?.newsList?.get(position)?.uri)
        intent.putExtra("newsNumber", "" + adapter?.newsList?.get(position)?.number)
        startActivityForResult(intent, 1)
    }

    private fun initRecyclerView() {
        newsList = v!!.findViewById(R.id.NewsList)
        val linearLayoutManager = LinearLayoutManager(context)
        newsList!!.layoutManager = linearLayoutManager
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val f = sp!!.getBoolean("RemovedNews", false)
        if (f) news
        val e = sp!!.edit()
        e.putBoolean("RemovedNews", false)
        e.apply()
    }

    private fun getNext5 () {
//        Log.e ("kek", "${oldestLocalMessageTime}")
        val ref = database!!.getReference(Constants.DATABASE_NEWS_PATH).limitToLast(5).endAt(oldestLocalMessageTime!!).orderByChild("number")
        ref.addChildEventListener (object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val item = dataSnapshot.getValue(NewsFirebaseItem::class.java)
                if (item != null) {
                    setMxValue(item.number)
                    mSwipeRefreshLayout!!.isRefreshing = false
                    if (msk == 0) {
                        list.add(item)
                    } else if (item.mask!! and msk != 0) {
                        list.add(item)
                    }
                    if (oldestLocalMessageTime!!.toInt () > item.number!!) oldestLocalMessageTime = item.number!!.toDouble()
//                    Log.e ("kek", "num ${item.number}")
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
                oldestLocalMessageTime = list[0].number!!.toDouble()
                list.reverse()
                for (x in list) adapter?.addItem (x)
                list.clear ()
            }
        })
    }

    private val news: Unit
        get() {
            adapter!!.clearItems()
            oldestLocalMessageTime = 4e18
            getNext5()
        }

    companion object {
        @JvmStatic
        fun newInstance(): FeedFragment {
            return FeedFragment()
        }
    }
}

