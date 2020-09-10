package com.jcDevelopment.tictactoeadfree.module.usedLibraries

import android.content.Context
import com.jcDevelopment.tictactoeadfree.R

class UsedLibrariesList (context: Context?) {
    val context = context
    fun getUsedLibraries() :List<LibraryDataClass>{
        return if (context != null)
            listOf(
                LibraryDataClass(context.getString(R.string.license_gson_headline),context.getString(R.string.license_gson_license_text)),
                LibraryDataClass(context.getString(R.string.license_koin_headline),context.getString(R.string.license_koin_license_text)),
                LibraryDataClass(context.getString(R.string.license_konfetti_license_headline),context.getString(R.string.license_konfetti_license_text)),
                LibraryDataClass(context.getString(R.string.license_rxAndroid_headline),context.getString(R.string.license_rxandroid_license_text)),
                LibraryDataClass(context.getString(R.string.license_rxBindings_headline),context.getString(R.string.license_rxbindings_license_text))
            )
        else emptyList()
    }
}