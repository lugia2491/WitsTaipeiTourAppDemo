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
import wits.lugia.demo.R
import wits.lugia.demo.databinding.ActivityWebBinding

/** 網頁顯示頁面 */
class WebActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding

    companion object {
        //接收URL
        private const val EXTRA_URL = "EXTRA_URL"

        fun newIntent(context: Context, url: String): Intent {
            return Intent(context, WebActivity::class.java).putExtra(EXTRA_URL, url)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWebView()
        setListener()
        loadUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    /** 初始化網頁 */
    private fun initWebView(){
        //開啟JavaScript
        binding.wvWebArea.settings.javaScriptEnabled = true
        //開啟資料儲存
        binding.wvWebArea.settings.domStorageEnabled = true
        //讀取進度顯示
        binding.wvWebArea.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.pbWebLoading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                binding.tvWebTitle.text = view.title
                binding.pbWebLoading.visibility = View.INVISIBLE
            }
        }

        //顯示讀取進度
        binding.wvWebArea.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.pbWebLoading.progress = newProgress
            }
        }
    }

    /** 設定Listener */
    private fun setListener(){
        binding.ibWebClose.setOnClickListener { finish() }
    }

    /** 載入網址，如果讀不到就顯示錯誤 */
    private fun loadUrl(){
        intent.getStringExtra(EXTRA_URL)?.let { url ->
            binding.wvWebArea.loadUrl(url)
        } ?: run {
            binding.tvWebTitle.text = getString(R.string.url_error)
        }
    }

    /** 返回鍵事件複寫 */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            //檢查是否還可以上一頁
            if (binding.wvWebArea.canGoBack()) {
                binding.wvWebArea.goBack()
            }else{
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}