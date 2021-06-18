package com.example.blogsystem.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogsystem.R
import com.example.blogsystem.adapter.ArticleAdapter
import com.example.blogsystem.base.BaseFragment
import com.example.blogsystem.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val mBinding get() = _binding!!
    private val articleAdapter by lazy {
        ArticleAdapter()
    }
    private val mArticleList = arrayListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        callAllInit()
    }

    override fun initEvent() {

    }

    override fun initData() {
        repeat(20) {
            mArticleList.add(it.toString())
        }
        articleAdapter.setData(mArticleList)
    }

    override fun initView() {
        mBinding.articleListRv.let {
            it.adapter = articleAdapter
            it.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}