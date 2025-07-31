package com.example.phychosiolz.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.core.text.isDigitsOnly
import com.bumptech.glide.Glide
import com.example.phychosiolz.R

object GlideUtil {
    fun glideAvatar(context: Context, sex: String, uri: String?, view: ImageView) {
        try {
            if (uri != null)
                Glide.with(context).load(
                    if (uri!!.isDigitsOnly()) uri?.toInt()
                    else uri
                ).error(
                    if (sex == "男") R.drawable.man
                    else R.drawable.woman
                ).placeholder(R.drawable.tab_mine).circleCrop()
                    .into(view)
        } catch (e: Exception) {
            Log.e(
                "GlideUtil",
                "glideAvatar: ${e.message}"
            )
        }
    }


    fun glideImage(context: Context, uri: String, view: ImageView) {
        try {
            Glide.with(context).load(
                if (uri.isDigitsOnly()) uri.toInt()
                else uri
            ).error(R.drawable.tab_mine).placeholder(R.drawable.tab_mine)
                .into(view)
        } catch (e: Exception) {
            Log.e(
                "GlideUtil",
                "glideAvatar: ${e.message}"
            )
        }
    }

    fun glideEmotionImage(context: Context, emotion: Int?, ivFace: ImageView) {
        when (emotion) {
            1 -> Glide.with(context).load(R.drawable.happy).into(ivFace)
            2 -> Glide.with(context).load(R.drawable.angry).into(ivFace)
            3 -> Glide.with(context).load(R.drawable.sad).into(ivFace)
            4 -> Glide.with(context).load(R.drawable.sick).into(ivFace)
            5 -> Glide.with(context).load(R.drawable.scared).into(ivFace)
            6 -> Glide.with(context).load(R.drawable.neutral).into(ivFace)
        }
    }
}