package com.demo.gradedreader.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.demo.gradedreader.ArticleViewModelFactory
import com.demo.gradedreader.R
import com.demo.gradedreader.lessonroom.Lesson
import com.demo.gradedreader.view.ContentAlignTextView
import com.demo.gradedreader.vm.ArticleViewModel
import com.demo.gradedreader.wordroom.WordDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleActivity : AppCompatActivity() {
    private lateinit var viewModel: ArticleViewModel
    private lateinit var seekBar: SeekBar

    private lateinit var tvTitle: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvContent: ContentAlignTextView
    private lateinit var tvVocabularyHeader: TextView
    private lateinit var llVocabularyList: LinearLayout
    private lateinit var tvTranslation: TextView
    private var allLevel: List<Int> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_article)

        // 初始化视图
        tvTitle = findViewById(R.id.tvTitle)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvContent = findViewById(R.id.tvContent)
        tvVocabularyHeader = findViewById(R.id.tvVocabularyHeader)
        llVocabularyList = findViewById(R.id.llVocabularyList)
        tvTranslation = findViewById(R.id.tvTranslation)
        seekBar = findViewById(R.id.seekBar)

        // 获取传递的文章
        val article = intent.getSerializableExtra("article") as? Lesson
        if (article == null) {
            finish()
            return
        }


        setTitleAndAuthor(article.titleContent, article.signature)
        setContent(article.content)
        setVocabulary(article.newWords)
        setTranslation(article.translation)

        viewModel =
            ViewModelProvider(this, ArticleViewModelFactory())[ArticleViewModel::class.java]

        // 监听高亮单词
        //可以针对传入的等级进行优化避免多次查找，避免频繁绘制
        viewModel.highlightedWords.observe(this) { words ->
            tvContent.setHighlightedWords(words)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // 获取数据库实例
            val wordDao = WordDatabase.getDatabase(this@ArticleActivity).wordDao()

            // 获取数据
            val maxLevel = wordDao.getMaxLevel() ?: 0
            val allLevel = wordDao.getAllLevel()

            // 切换回主线程更新 UI
            withContext(Dispatchers.Main) {
                seekBar.max = maxLevel
                this@ArticleActivity.allLevel = allLevel // 更新全局变量
            }
        }
        // SeekBar 监听
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.fetchWordsByLevel(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }

    private fun setTitleAndAuthor(title: String, author: String) {
        tvTitle.text = title
        tvAuthor.text = "Author: $author"
    }

    private fun setContent(content: String) {
        tvContent.setText(content)
    }

    private fun setVocabulary(newWords: String) {
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type
        val jsonMap: Map<String, String> = gson.fromJson(newWords, type)
        val jsonList = jsonMap.map { (key, value) -> "$key: $value" }
        llVocabularyList.removeAllViews()
        for (word in jsonList) {
            val textView = TextView(this)
            textView.text = word
            textView.textSize = 16f
            textView.setPadding(0, 8, 0, 8)
            textView.setOnClickListener {
                toggleHighlight(textView)
            }
            llVocabularyList.addView(textView)
        }
    }

    private fun setTranslation(translation: String) {
        tvTranslation.text = translation
    }

    private fun toggleHighlight(textView: TextView) {
        if (textView.currentTextColor == Color.RED) {
            textView.setTextColor(Color.BLACK)
            textView.setBackgroundColor(Color.TRANSPARENT)
        } else {
            // 否则设置为高亮颜色
            textView.setTextColor(Color.RED)
            textView.setBackgroundColor(Color.YELLOW)
        }
    }

}