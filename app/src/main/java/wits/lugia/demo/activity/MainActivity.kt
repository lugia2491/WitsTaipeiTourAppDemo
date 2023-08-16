package wits.lugia.demo.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import org.json.JSONObject
import wits.lugia.demo.R
import wits.lugia.demo.databinding.ActivityMainBinding
import wits.lugia.demo.factory.ViewModelFactory
import wits.lugia.demo.fragment.TourDetailedFragment
import wits.lugia.demo.fragment.TourListFragment
import wits.lugia.demo.model.LocaleConverterModel
import wits.lugia.demo.tool.AnimationTool
import wits.lugia.demo.tool.LocaleChange
import wits.lugia.demo.tool.ProgressBarDelay
import wits.lugia.demo.viewModel.LocaleViewModel

/** 主頁面 */
class MainActivity : AppCompatActivity() {

    companion object {
        /** 自訂語系 */
        const val LOCALE_INDEX = "LOCALE_INDEX"
    }

    //自動BindView，省去findViewById
    private lateinit var binding: ActivityMainBinding
    private lateinit var fm: FragmentManager
    private lateinit var fmTourList: TourListFragment
    private lateinit var fmTourDetailed: TourDetailedFragment
    private lateinit var mProgressBarDelay: ProgressBarDelay
    //ViewModel
    private lateinit var localeViewModel: LocaleViewModel
    private var currentLanguage = ""//目前語系

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //讀入Activity binding的內容
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //讀入設定的語言
        val receivedValue = intent.getIntExtra(LOCALE_INDEX, 0)
        //初始化語言
        currentLanguage = resources.getStringArray(R.array.country_code)[receivedValue]

        //初始化MVVM
        //用ViewModelFactory把LocaleViewModel跟LocaleConverterModel串起
        localeViewModel = ViewModelProviders.of(this, ViewModelFactory { LocaleViewModel(LocaleConverterModel()) }).get(LocaleViewModel::class.java)

        mProgressBarDelay = ProgressBarDelay()

        initUi()
        initObserve()
        setUiListener()
    }

    /** 初始化ui */
    private fun initUi(){
        //初始化Fragment
        fm = supportFragmentManager
        fmTourList = TourListFragment()
        fmTourDetailed = TourDetailedFragment()

        //效果等同
        //val transaction = fm.beginTransaction()
        //transaction.add(binding.fmMainList.id, fmTourList)
        //transaction.commit()
        //不需要再產生 transaction 節省記憶體使用
        fm.beginTransaction().add(binding.fmMainList.id, fmTourList).commit()
    }

    /** 初始化觀察者 */
    private fun initObserve(){
        //設定語系
        localeViewModel.getConvertLocale().observe(this) { locale ->
            LocaleChange.setLocale(this, locale)
        }
    }

    /** 設定Listener */
    private fun setUiListener(){
        //返回清單
        binding.ibMainBack.setOnClickListener{ backTourList() }

        //語言選擇
        binding.ibMainSelectLanguage.setOnClickListener {
            // 顯示語言選擇清單
            // 只有單次使用不用產生 val Dialog builder
            // 直接帶入語系清單，不須另外產生 val languageList = resources.getStringArray(R.array.language_list)
            AlertDialog.Builder(it.context)
                .setTitle(getString(R.string.select_language))
                .setItems(resources.getStringArray(R.array.language_list)) { _, which ->
                //設定目前選取語系(for api)
                currentLanguage = resources.getStringArray(R.array.country_code)[which]
                //重整
                localeViewModel.convertIndex(which)
                startActivity(Intent(this@MainActivity, MainActivity::class.java).putExtra(LOCALE_INDEX, which))
                finish()

            }.create().show()
        }
    }

    /**
     * 顯示詳細資訊
     * @param data 詳細資訊
     */
    fun goDetailed(data: JSONObject){
        fmTourDetailed.setData(data)

        //省略產生 val transaction 節省記憶體開銷
        //隱藏並顯示
        if(fmTourDetailed.isAdded){
            fm.beginTransaction().hide(fmTourList).show(fmTourDetailed).commit()
        }else{
            fm.beginTransaction().hide(fmTourList).add(binding.fmMainList.id, fmTourDetailed).commit()
        }

        AnimationTool.fadeOut(binding.tvMainAppTitle)
        AnimationTool.fadeOut(binding.ibMainSelectLanguage)
        binding.tvMainTitle.text = data.getString("name")
        AnimationTool.fadeIn(binding.tvMainTitle)
        AnimationTool.fadeIn(binding.ibMainBack)
    }

    /** 詳細資訊返回清單 */
    private fun backTourList(){
        //省略產生 val transaction 節省記憶體開銷
        //移除fmTourDetailed
        if(fmTourList.isAdded){
            fm.beginTransaction().remove(fmTourDetailed).show(fmTourList).commit()
        }else{
            fm.beginTransaction().remove(fmTourDetailed).add(binding.fmMainList.id, fmTourList).commit()
        }

        AnimationTool.fadeOut(binding.tvMainTitle)
        AnimationTool.fadeOut(binding.ibMainBack)
        AnimationTool.fadeIn(binding.ibMainSelectLanguage)
        AnimationTool.fadeIn(binding.tvMainAppTitle)
    }

    /** 取得目前語言設定 */
    fun getLanguage(): String{
        return currentLanguage
    }

    /** 顯示載入中 */
    fun showLoading(show: Boolean){
        mProgressBarDelay.progressDelay(binding.pbMainLoading, show)
    }

    /** 返回鍵事件複寫 */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if(fmTourDetailed.isAdded){
                backTourList()
            }else{
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}