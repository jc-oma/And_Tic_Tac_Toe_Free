package com.jcDevelopment.tictactoeadfree.module.usedLibraries

import android.content.Context
import com.jcDevelopment.tictactoeadfree.R

class UsedLibrariesList (context: Context?) {
    val context = context
    fun getUsedLibraries() :List<LibraryDataClass>{
        return if (context != null)
            listOf(
                LibraryDataClass(context.getString(R.string.koin_license_headline),context.getString(R.string.koin_license_text))
            )
        else emptyList()
    }
}