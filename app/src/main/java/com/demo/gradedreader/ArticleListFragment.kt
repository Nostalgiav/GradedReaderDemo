package com.demo.gradedreader

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.gradedreader.activity.ArticleActivity
import com.demo.gradedreader.adapter.ArticleAdapter
import com.demo.gradedreader.vm.ArticleViewModel

class ArticleListFragment :Fragment() {
    private lateinit var viewModel: ArticleViewModel
    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance(unitId: Int): ArticleListFragment {
            return ArticleListFragment().apply {
                arguments = Bundle().apply { putInt("unitId", unitId) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ArticleAdapter(emptyList()) { article ->
            val intent = Intent(activity, ArticleActivity::class.java)
            intent.putExtra("article", article)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ArticleViewModel::class.java]
        val unitId = arguments?.getInt("unitId") ?: 1

        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            (recyclerView.adapter as ArticleAdapter).updateData(articles)
        }

        viewModel.fetchArticles(unitId)
    }


}