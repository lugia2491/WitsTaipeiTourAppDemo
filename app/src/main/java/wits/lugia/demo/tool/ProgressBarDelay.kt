package wits.lugia.demo.tool

import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar

/**
 * ProgressBar的延遲+淡入淡出工具
 * 使用方式：
 * private ProgressBarDelay mProgressBarDelay;
 * mProgressBarDelay = new ProgressBarDelay();
 * mProgressBarDelay.progressDelay(ProgressBar的物件, 顯示t或隱藏f);
 * 作者：洪斌峰
 * 日期：2023/08/16
 */
class ProgressBarDelay {
    private var startTime: Long = 0
    private val delayTimeMs = 700 //最短的顯示時間
    private val animatorTime = 400 //動畫時間

    //可以讓ProgressBar至少顯示delayTimeMs毫秒才消失，才不會造成有些指令執行很快，畫面就閃一下
    fun progressDelay(progressBar: ProgressBar, onOff: Boolean) {
        if (onOff) {
            progressBar.visibility = View.VISIBLE
            val animator = ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 1f)
            animator.duration = (animatorTime + 50).toLong()
            animator.start()
            startTime = System.currentTimeMillis()
        } else {
            if (System.currentTimeMillis() - startTime > delayTimeMs) {
                val animator = ObjectAnimator.ofFloat(progressBar, "alpha", 1f, 0f)
                animator.duration = animatorTime.toLong()
                animator.start()
                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.visibility = View.GONE
                }, animatorTime.toLong())
                startTime = 0
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDelay(progressBar, false)
                }, delayTimeMs + 200 - (System.currentTimeMillis() - startTime))
            }
        }
    }
}