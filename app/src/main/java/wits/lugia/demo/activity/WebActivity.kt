package wits.lugia.demo.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import wits.lugia.demo.R

class WebActivity : AppCompatActivity() {

    private lateinit var wvWeb: WebView
    private lateinit var tvTitle: TextView
    private lateinit var ibClose: ImageButton
    private lateinit var pbLoading: ProgressBar

    //接收url
    companion object {
        private const val EXTRA_URL = "extra_url"

        fun newIntent(context: Context, url: String): Intent {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        initUi()
        initListener()
        initWebView()

        //取得網址
        val url = intent.getStringExtra(EXTRA_URL)
        //開啟網頁
        if (url != null) {
            wvWeb.loadUrl(url)
        }else{
            tvTitle.text = getString(R.string.url_error)
        }
    }

    private fun initUi(){
        wvWeb = findViewById(R.id.wv_web_web)
        tvTitle = findViewById(R.id.tv_web_title)
        ibClose = findViewById(R.id.ib_web_close)
        pbLoading = findViewById(R.id.pb_web_loading)
    }

    private fun initListener(){
        ibClose.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(){
        //開啟JavaScript
        wvWeb.settings.javaScriptEnabled = true
        wvWeb.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                pbLoading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                tvTitle.text = view?.title
                pbLoading.visibility = View.INVISIBLE
            }
        }

        //Client端控制
        wvWeb.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                pbLoading.progress = newProgress
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            //是否還可以上一頁
            if (wvWeb.canGoBack()) {
                wvWeb.goBack()//上一頁
            }else{
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}