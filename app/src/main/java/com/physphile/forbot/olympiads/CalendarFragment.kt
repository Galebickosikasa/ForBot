package com.physphile.forbot.olympiads

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.physphile.forbot.Constants.AUTH_ACTIVITY_PATH
import com.physphile.forbot.Constants.FRAGMENT_DIALOG_PROFILE_TAG
import com.physphile.forbot.Constants.OLYMPS_CREATE_ACTIVITY_PATH
import com.physphile.forbot.Constants.OLYMP_PAGE_ACTIVITY_PATH
import com.physphile.forbot.R
import com.physphile.forbot.profile.ProfileMenuDialog
import java.util.*

class CalendarFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var olympsAdapter: OlympsAdapter
    private lateinit var v: View
    private lateinit var OlympsList: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var admins: HashMap<*, *>
    private lateinit var calendarView: CalendarView
    private lateinit var calendar: Calendar
    private lateinit var sp: SharedPreferences
    private var YEAR = 0
    private var MONTH = 0
    private var DAYOFMONTH = 0
    private val onDateChangeListener = OnDateChangeListener { _, year, month, dayOfMonth ->
        YEAR = year
        MONTH = month
        DAYOFMONTH = dayOfMonth
        getOlymps()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_calendar, container, false)
        OlympsList = v.findViewById(R.id.OlympsList)
        calendarView = v.findViewById(R.id.calendar)
        val toolbar: Toolbar = v.findViewById(R.id.calendarToolbar)
        auth = FirebaseAuth.getInstance()

        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                admins = dataSnapshot.value as HashMap<*, *>
                auth.addAuthStateListener { firebaseAuth ->
                    val user = firebaseAuth.currentUser
                    if (user != null && admins.containsValue(user.uid)) {
                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.admin_menu_calendar_fragment)
                    } else {
                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.toolbar_menu_calendar_fragment)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.editOlymp -> startActivityForResult(Intent(OLYMPS_CREATE_ACTIVITY_PATH), 1)
                R.id.set_today -> {
                    olympsAdapter.clearItems()
                    calendarView.date = Calendar.getInstance().time.time
                }
                R.id.profile -> if (FirebaseAuth.getInstance().currentUser != null) {
                    val profileDialog: DialogFragment = ProfileMenuDialog()
                    profileDialog.show(childFragmentManager, FRAGMENT_DIALOG_PROFILE_TAG)
                } else {
                    startActivity(Intent(AUTH_ACTIVITY_PATH))
                }
            }
            false
        }

        OlympsList.layoutManager = LinearLayoutManager(context)
        olympsAdapter = OlympsAdapter(context)
        OlympsList.adapter = olympsAdapter
        sp = activity?.getSharedPreferences("FlagToRemove", Context.MODE_PRIVATE)!!
        calendar = Calendar.getInstance()
        calendarView.setOnDateChangeListener(onDateChangeListener)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        YEAR = calendar.get(Calendar.YEAR)
        MONTH = calendar.get(Calendar.MONTH)
        DAYOFMONTH = calendar.get(Calendar.DATE)

        getOlymps()
        return v
    }

    fun onOlympsClick(pos: Int) {
        val intent = Intent(OLYMP_PAGE_ACTIVITY_PATH)
        intent.putExtra("olympName", olympsAdapter.olympsList[pos].getName())
        intent.putExtra("olympDate", olympsAdapter.olympsList[pos].date)
        intent.putExtra("olympLevel", olympsAdapter.olympsList[pos].getLevel())
        intent.putExtra("olympText", olympsAdapter.olympsList[pos].getText())
        intent.putExtra("olympUri", olympsAdapter.olympsList[pos].getUri())
        intent.putExtra("olympPath", olympsAdapter.olympsList[pos].path)
        intent.putExtra("olympNum", olympsAdapter.olympsList[pos].getNum().toString())
        intent.putExtra("olympYear", olympsAdapter.olympsList[pos].getYear())
        intent.putExtra("olympMonth", olympsAdapter.olympsList[pos].getMonth())
        intent.putExtra("olympDay", olympsAdapter.olympsList[pos].getDayOfMonth())
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            val f = sp.getBoolean("RemovedOlymp", false)
            if (f) getOlymps()
            Log.e("kek", "date " + calendar[Calendar.YEAR] + " " + calendar[Calendar.MONTH] + " " + calendar[Calendar.DATE])
            val e = sp.edit()
            e.putBoolean("RemovedOlymp", false)
            e.apply()
        }
    }

    private fun makePath(year: Int, month: Int, dayOfMonth: Int): String {
        return "$year/$month/$dayOfMonth"
    }

    private fun getOlymps () {
        olympsAdapter.clearItems()
        database.getReference(makePath(YEAR, MONTH, DAYOFMONTH))
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                        val item = dataSnapshot.getValue(OlympsListItem::class.java)
                        if (item!!.getYear() == YEAR && item.getMonth() == MONTH && item.getDayOfMonth() == DAYOFMONTH) olympsAdapter.addItems(item)
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }

    companion object {
        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }
}