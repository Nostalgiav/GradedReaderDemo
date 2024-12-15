package com.demo.gradedreader

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPagerAdapter(activity: FragmentActivity,private val unitSize: Int) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int {
        return unitSize
    }

    override fun createFragment(position: Int): Fragment {
        return ArticleListFragment.newInstance(position + 1)
    }

}