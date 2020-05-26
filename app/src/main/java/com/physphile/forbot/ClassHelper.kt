package com.physphile.forbot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.CalendarView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.physphile.forbot.olympiads.OlympsAdapter
import com.physphile.forbot.profile.ProfileMenuDialog
import java.io.FileNotFoundException
import java.util.*

class ClassHelper {
    private var activity: Activity? = null
    private var olympsAdapter: OlympsAdapter? = null
    private var fragmentManager: FragmentManager? = null
    private var calendarView: CalendarView? = null
    @JvmField
    var onMenuItemClickListener = Toolbar.OnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.profile -> if (FirebaseAuth.getInstance().currentUser != null) {
                val profileDialog: DialogFragment = ProfileMenuDialog()
                profileDialog.show(fragmentManager!!, Constants.FRAGMENT_DIALOG_PROFILE_TAG)
            } else {
                activity!!.startActivity(Intent(Constants.AUTH_ACTIVITY_PATH))
            }
            R.id.createNews -> activity!!.startActivity(Intent(Constants.NEWS_CREATE_ACTIVITY_PATH))
            R.id.set_today -> {
                olympsAdapter!!.clearItems()
                calendarView!!.date = Calendar.getInstance().time.time
            }
        }
        false
    }

    constructor(activity: Activity) {
        this.activity = activity
    }

    constructor(activity: Activity, fragmentManager: FragmentManager?) {
        this.activity = activity
        this.fragmentManager = fragmentManager
    }

    fun saveFile(bitmap: Bitmap, name: String?) {
        try {
            val out = activity!!.openFileOutput(name, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            //            Log.e(LOG_NAME, "файл сохранен");
            out.close()
        } catch (ignored: Exception) {
//            Log.e(LOG_NAME, "файл не сохранен");
        }
    }

    @Throws(FileNotFoundException::class)
    fun readFile(name: String?): Bitmap {
        val `is` = activity!!.openFileInput(name)
        //        Log.e(LOG_NAME, "файл прочитан");
        return BitmapFactory.decodeStream(`is`)
    }

    fun dpToPx(dp: Float): Int {
        return (dp * activity!!.resources.displayMetrics.density).toInt()
    }
}