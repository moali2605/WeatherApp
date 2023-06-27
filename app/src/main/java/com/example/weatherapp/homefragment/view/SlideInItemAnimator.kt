package com.example.weatherapp.homefragment.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

class SlideInItemAnimator : SimpleItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = 0f
        holder.itemView.translationX = holder.itemView.width.toFloat()
        holder.itemView.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(1000)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchAddFinished(holder)
                }
            })
            .start()
        return false
    }


    override fun animateMove(
        holder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun runPendingAnimations() {

    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {

    }

    override fun endAnimations() {

    }

    override fun isRunning(): Boolean {
        TODO("Not yet implemented")
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        return false
    }
}