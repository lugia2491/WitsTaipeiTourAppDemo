package wits.lugia.demo.tool;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

/**
 * ProgressBar的延遲+淡入淡出工具
 * 使用方式：
 * private ProgressBarDelay mProgressBarDelay;
 * mProgressBarDelay = new ProgressBarDelay();
 * mProgressBarDelay.progressDelay(ProgressBar的物件, 顯示t或隱藏f);
 * 作者：LUGIA
 */
public class ProgressBarDelay {

    private long startTime = 0;
    private int delayTimeMs = 700;//最短的顯示時間
    private int animatorTime = 400;//動畫時間
    private ObjectAnimator animator;

    //可以讓ProgressBar至少顯示delayTimeMs毫秒才消失，才不會造成有些指令執行很快，畫面就閃一下
    public void progressDelay(ProgressBar progressBar, boolean OnOff){
        if(OnOff){
            progressBar.setVisibility(View.VISIBLE);
            animator = ObjectAnimator.ofFloat(progressBar,"alpha",0,1);
            animator.setDuration(animatorTime+50);
            animator.start();
            startTime = System.currentTimeMillis();
        }else{
            if(System.currentTimeMillis()-startTime > delayTimeMs){
                animator = ObjectAnimator.ofFloat(progressBar,"alpha",1,0);
                animator.setDuration(animatorTime);
                animator.start();
                new Handler().postDelayed(() -> {
                    //延遲的程式碼
                    progressBar.setVisibility(View.GONE);
                }, (animatorTime));
                startTime = 0;
            }else{
                new Handler().postDelayed(() -> {
                    //延遲的程式碼
                    progressDelay(progressBar, false);
                }, (delayTimeMs+200)-(System.currentTimeMillis()-startTime));
            }
        }
    }

}
