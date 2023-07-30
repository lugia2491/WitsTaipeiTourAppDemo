package wits.lugia.demo.tool

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

/**
 * 動畫工具庫
 * 作者：LUGIA
 * 日期：2023/07/30
 */
object AnimationTool {
    /**
     * 淡入
     * @param view 目標元件
     * @param duration 時間(毫秒)
     */
    fun fadeIn(view: View, duration: Long = 300) {
        view.alpha = 0.0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1.0f)
            .setDuration(duration)
            .setListener(null)
    }

    /**
     * 淡出
     * @param view 目標元件
     * @param duration 時間(毫秒)
     */
    fun fadeOut(view: View, duration: Long = 300) {
        view.animate()
            .alpha(0.0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }
}