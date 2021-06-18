package com.example.blogsystem.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment, Init {

    protected val TAG: String = javaClass.simpleName
    protected lateinit var mRootView: View

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = super.onCreateView(inflater, container, savedInstanceState)!!
        return mRootView
    }
}
