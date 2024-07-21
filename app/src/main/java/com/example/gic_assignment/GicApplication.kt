package com.example.gic_assignment

import android.app.Application
import android.graphics.drawable.AnimationDrawable
import androidx.core.content.res.ResourcesCompat
import com.example.gic_assignment.utils.PrefHelper

class GicApplication: Application() {

    init {
        instance = this
    }
    companion object {
        private var instance: Application? = null
        private var isActive: Boolean = false
        lateinit var prefHelper: PrefHelper
        var mLoadingAnimDrawable: AnimationDrawable? = null

    }

    override fun onCreate() {
        super.onCreate()
        prefHelper = PrefHelper(this)
//
//        mLoadingAnimDrawable = ResourcesCompat.getDrawable(
//            resources,
//            R.drawable.app_loader_animation_list, theme
//        ) as AnimationDrawable?
    }
}