package wits.lugia.demo.tool

import android.text.style.ClickableSpan
import android.view.View
import wits.lugia.demo.activity.WebActivity

/**
 * URL點擊事件
 */
class UrlClickableSpan(private val url: String): ClickableSpan() {
    override fun onClick(widget: View) {
        //開啟Activity，並將URL傳遞過去
        widget.context.startActivity(WebActivity.newIntent(widget.context, url))
    }
}