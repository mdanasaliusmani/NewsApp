package com.example.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentSavedNewsBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentSavedNewsBinding
    lateinit var newsAdapter: NewsAdapter
    private val TAG = "SavedNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedNewsBinding.bind(view)

        if (activity is NewsActivity) {
            viewModel = (activity as NewsActivity).viewModel
            setupRecyclerView()

            newsAdapter.setOnItemClickListener {
                val bundle = Bundle().apply {
                    putSerializable("article", it)
                }
                findNavController().navigate(
                    R.id.action_savedNewsFragment_to_articleFragment,
                    bundle
                )
            }

            val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                @SuppressLint("ShowToast")
                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val article = newsAdapter.differ.currentList[position]
                    viewModel.deleteArticle(article)
                    Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                        setAction("Undo") {
                            viewModel.saveArticle(article)
                        }
                    }
                }
            }

            ItemTouchHelper(itemTouchHelperCallback).apply{
                attachToRecyclerView(binding.rvSavedNews)
            }

            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
                newsAdapter.differ.submitList(articles)
            })
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}