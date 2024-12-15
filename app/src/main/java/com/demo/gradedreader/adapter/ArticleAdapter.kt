package com.demo.gradedreader.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.gradedreader.R
import com.demo.gradedreader.lessonroom.Lesson

class ArticleAdapter(

private var articles: List<Lesson>,
private val onClick: (Lesson) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.articleTitle)
        val chineseTitle :TextView = view.findViewById(R.id.articleChineseTitle)
        val articleTitleLesson :TextView = view.findViewById(R.id.articleTitleLesson)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.title.text = article.englishTitle
        holder.chineseTitle.text = "   ("+article.chineseTitle+")"
        holder.articleTitleLesson.text = article.lesson.toString()+"："
        holder.itemView.setOnClickListener { onClick(article) }
    }

    override fun getItemCount(): Int = articles.size

    fun updateData(newArticles: List<Lesson>) {
        articles = newArticles
        notifyDataSetChanged()
    }

}