package com.jcDevelopment.tictactoeadfree.module.usedLibraries

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jcDevelopment.tictactoeadfree.R
import kotlinx.android.synthetic.main.fragment_used_libraries.*

class UsedLibrariesFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            UsedLibrariesFragment()
    }

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val librarySet: List<LibraryDataClass> by lazy {
        UsedLibrariesList(context).getUsedLibraries()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_used_libraries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(context)
        viewAdapter = UsedLibraryAdapter(librarySet)

        used_libraries_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}