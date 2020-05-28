package com.physphile.forbot.news

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.physphile.forbot.R
import com.physphile.forbot.news.NewsAdapter.NewsViewHolder
import java.util.*


class NewsAdapter(context: Context) : RecyclerView.Adapter<NewsViewHolder>() {
    var newsList: MutableList<NewsFirebaseItem> = ArrayList()
    private val news = HashMap<Int, Int>()
    private val onNewsClick: OnNewsClick

    interface OnNewsClick {
        fun onNewsClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.news_item, parent, false)
        val iw: ImageView = view.findViewById(R.id.NewsTitleImage)
        val lp = ConstraintLayout.LayoutParams(parent.width,
                parent.width * 10 / 16)
        iw.layoutParams = lp
        return NewsViewHolder(view)
    }

    fun addItem(item: NewsFirebaseItem) {
        if (news.containsKey(item.number)) return
//        Log.e ("kek", "add ${item.number}")
        news[item.number!!] = 1
        newsList.add(item)
        notifyItemChanged(itemCount - 1)
    }

    fun clearItems() {
        newsList.clear()
        news.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    inner class NewsViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        private val newsTitleImage: ImageView = itemView.findViewById(R.id.NewsTitleImage)
        private val newsText: TextView = itemView.findViewById(R.id.newsText)
        private val newsAuthor: TextView = itemView.findViewById(R.id.newsAuthor)
        private val newsDate: TextView = itemView.findViewById(R.id.newsDate)

        fun bind(item: NewsFirebaseItem) {
            newsTitle.text = item.title
            newsText.text = item.text
            newsAuthor.text = item.author
            newsDate.text = item.date
            Glide.with(itemView.context)
                    .load(item.uri)
                    .into(newsTitleImage)
            newsTitleImage.visibility = if (item.uri != null) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onNewsClick.onNewsClick(position)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    init {
        onNewsClick = context as OnNewsClick
    }
}