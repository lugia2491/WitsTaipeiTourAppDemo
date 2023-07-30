package wits.lugia.demo.tool

import android.text.style.ClickableSpan
import android.view.View
import wits.lugia.demo.activity.WebActivity

class UrlClickableSpan(private val url: String): ClickableSpan() {
    override fun onClick(widget: View) {
        //URL點擊事件
        //開啟Activity，並將URL傳遞過去
        val intent = WebActivity.newIntent(widget.context, url)
        widget.context.startActivity(intent)
    }
}