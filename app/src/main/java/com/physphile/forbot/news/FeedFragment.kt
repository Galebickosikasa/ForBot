package com.physphile.forbot.news

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.physphile.forbot.Constants
import com.physphile.forbot.R
import com.physphile.forbot.profile.ProfileMenuDialog
import java.util.*

class FeedFragment : Fragment() {
    private var adapter: NewsAdapter? = null
    private var storage: FirebaseStorage? = null
    private val storageReference: StorageReference? = null
    private var database: FirebaseDatabase? = null
    private var newsList: RecyclerView? = null
    private var v: View? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var admins: HashMap<String, String>? = null
    private val viewPager: ViewPager? = null
    private var phis: CheckBox? = null
    private var math: CheckBox? = null
    private var rus: CheckBox? = null
    private var lit: CheckBox? = null
    private var inf: CheckBox? = null
    private var chem: CheckBox? = null
    private var his: CheckBox? = null
    private var ast: CheckBox? = null
    private var msk = 0 // маска предметов
    private var flag = 0
    private val onMenuItemClickListener = Toolbar.OnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.profile -> if (FirebaseAuth.getInstance().currentUser != null) {
                val profileDialog: DialogFragment = ProfileMenuDialog()
                profileDialog.show(parentFragmentManager, Constants.FRAGMENT_DIALOG_PROFILE_TAG)
            } else {
                startActivity(Intent(Constants.AUTH_ACTIVITY_PATH))
            }
            R.id.createNews -> startActivityForResult(Intent(Constants.NEWS_CREATE_ACTIVITY_PATH), 1)
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
        phis!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 0)
            news
        })
        math!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 1)
            news
        })
        rus!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 2)
            news
        })
        lit!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 3)
            news
        })
        inf!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 4)
            news
        })
        chem!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 5)
            news
        })
        his!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 6)
            news
        })
        ast!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            msk = msk xor (1 shl 7)
            news
        })

        //инициализация переменных
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser

        //инициализация View-элементов
        initRecyclerView()
        val toolbar: Toolbar = v!!.findViewById(R.id.feedToolbar)
        mSwipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)

        //заполнение View-элементов
        mSwipeRefreshLayout!!.setOnRefreshListener {
            Log.e ("kek", "upd")
            flag = 1
            news
        }
        mSwipeRefreshLayout!!.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN)
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener)

        //остальные методы
        adapter = NewsAdapter(context)
        newsList!!.adapter = adapter
        adapter!!.clearItems()
        news
        // admins initialize
        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                admins = dataSnapshot.value as HashMap<String, String>?
                mAuth!!.addAuthStateListener { firebaseAuth ->
                    val _user = firebaseAuth.currentUser
                    if (_user != null && admins!!.containsValue(_user.uid)) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e ("kek", "result " + requestCode)
//        news
//        news
//        news
    }

    private fun initRecyclerView() {
        newsList = v!!.findViewById(R.id.NewsList)
        var linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        newsList!!.layoutManager = linearLayoutManager
    }

    private val news: Unit
        private get() {
            Log.e ("kek", "UpdateFromNews " + adapter?.needToAdd)
            adapter!!.clearItems()
            adapter!!.news.clear ()
            val ref = database!!.getReference(Constants.DATABASE_NEWS_PATH)
            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val item = dataSnapshot.getValue(NewsFirebaseItem::class.java)
                    if (item != null) {
                        NewsAdapter.setMx(item.number)
                        mSwipeRefreshLayout!!.isRefreshing = false
                        adapter?.needToAdd = 1
                        if (msk == 0) {
                            adapter!!.addItem(item)
                        } else if (item.mask and msk != 0) {
                            adapter!!.addItem(item)
                        }
                        adapter?.needToAdd = 0
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
            adapter!!.newsList.reverse()
            adapter!!.notifyDataSetChanged()

//            adapter?.needToAdd = 0
        }

    companion object {
        @JvmStatic
        fun newInstance(): FeedFragment {
            return FeedFragment()
        }
    }
}