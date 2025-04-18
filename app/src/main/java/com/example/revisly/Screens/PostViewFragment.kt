package com.example.revisly.Screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Dao
import com.example.revisly.Adapters.ShowPostsAdapter
import com.example.revisly.Posts
import com.example.revisly.R
import com.example.revisly.RoomDatabase.Data
import com.example.revisly.databinding.FragmentPostViewBinding


class PostViewFragment : Fragment() {
    lateinit var binding: FragmentPostViewBinding
    lateinit var postsAdapter: ShowPostsAdapter
    val postslist = mutableListOf<Posts>()

    lateinit var db : Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostViewBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db= Data.getInstance(requireContext())

        postsAdapter = ShowPostsAdapter(postslist)
//        binding.ShowImages.layoutManager = StaggeredGridLayoutManager(
//            2, StaggeredGridLayoutManager.VERTICAL
//        ).apply {
//            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
//        }
//        binding.ShowImages.adapter = postsAdapter
//        binding.ShowImages.itemAnimator = DefaultItemAnimator()

        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        layoutManager.isItemPrefetchEnabled = true

        binding.ShowImages.layoutManager = layoutManager
        binding.ShowImages.setHasFixedSize(true)
        binding.ShowImages.adapter = postsAdapter


        GetPosts()


    }

    private fun GetPosts() {
        postslist.clear()
        postslist.addAll(db.inter().GetPost())
        postsAdapter.notifyDataSetChanged()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostViewFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}