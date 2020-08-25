package com.jcDevelopment.tictactoeadfree.module.baseClasses

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    open fun onBackPressed(): Boolean {
        return true
    }
}