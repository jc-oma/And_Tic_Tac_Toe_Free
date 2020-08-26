package com.jcDevelopment.tictactoeadfree.module.usedLibraries

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jcDevelopment.tictactoeadfree.R
import kotlinx.android.synthetic.main.recyclerview_used_library.view.*

class UsedLibraryAdapter(private val libraryDataClass: List<LibraryDataClass>) :
    RecyclerView.Adapter<UsedLibraryAdapter.UsedLibraryViewHolder>() {

    class UsedLibraryViewHolder(private val holderView: ConstraintLayout) : RecyclerView.ViewHolder(holderView) {
        fun bindLibraryData(libraryDataClass: LibraryDataClass) {
            holderView.used_library_headline.text = libraryDataClass.headlineText
            holderView.used_library_text.text = libraryDataClass.licenceText
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsedLibraryViewHolder {
        val usedLibraryItem = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_used_library, parent, false) as ConstraintLayout
        return UsedLibraryViewHolder(usedLibraryItem)
    }

    override fun onBindViewHolder(holder: UsedLibraryViewHolder, position: Int) {
        holder.bindLibraryData(libraryDataClass[position])
    }

    override fun getItemCount() = libraryDataClass.size
}
