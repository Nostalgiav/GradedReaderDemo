package com.demo.gradedreader.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.Utils
import com.demo.gradedreader.vm.MainViewModel
import com.demo.gradedreader.vm.MainViewModelFactory
import com.demo.gradedreader.R
import com.demo.gradedreader.TabPagerAdapter
import com.demo.gradedreader.lessonroom.LessonDatabase
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: TabPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        // 初始化 ViewModel
        val lessonDao = LessonDatabase.getDatabase(Utils.getApp()).lessonDao()
        viewModel = ViewModelProvider(
            this, MainViewModelFactory(lessonDao)
        )[MainViewModel::class.java]

        // 观察 unitSize
        viewModel.unitSize.observe(this) { unitSize ->
            adapter = TabPagerAdapter(this, unitSize)
            viewPager.adapter = adapter
            // 初始化 TabLayoutMediator
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = "单元 ${position + 1}"
            }.attach()
        }

    }
}