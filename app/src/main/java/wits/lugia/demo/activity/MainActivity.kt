package wits.lugia.demo.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import wits.lugia.demo.R
import wits.lugia.demo.factory.DataFactory
import wits.lugia.demo.factory.LocaleFactory
import wits.lugia.demo.fragment.TourDetailedFragment
import wits.lugia.demo.fragment.TourListFragment
import wits.lugia.demo.model.DataRepository
import wits.lugia.demo.model.LocaleConverter
import wits.lugia.demo.tool.AnimationTool
import wits.lugia.demo.tool.LocaleChange
import wits.lugia.demo.tool.ProgressBarDelay
import wits.lugia.demo.viewModel.DataViewModel
import wits.lugia.demo.viewModel.LocaleViewModel


class MainActivity : AppCompatActivity() {

    companion object {
        const val LOCALE_INDEX = "LOCALE_INDEX" //自訂語系
    }

    private lateinit var ibBack: ImageButton
    private lateinit var ibSelectLanguage: ImageButton
    private lateinit var tvAppTitle: TextView
    private lateinit var tvTitle: TextView
    private lateinit var pbLoading: ProgressBar

    private lateinit var fm: FragmentManager
    private lateinit var fmTourList: TourListFragment
    private lateinit var fmTourDetailed: TourDetailedFragment

    private lateinit var mProgressBarDelay: ProgressBarDelay
    //model
    private lateinit var dataRepository: DataRepository
    private lateinit var localeConverter: LocaleConverter
    //ViewModel
    private lateinit var dataViewModel: DataViewModel
    private lateinit var localeViewModel: LocaleViewModel
    //Factory
    private lateinit var dataFactory: DataFactory
    private lateinit var localeFactory: LocaleFactory

    private var inTourDetailed = false//fm狀態
    private var currentLanguage = ""//目前語系

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val receivedValue = intent.getIntExtra(LOCALE_INDEX, 0)

        //初始化語言
        currentLanguage = resources.getStringArray(R.array.country_code)[receivedValue]

        //初始化MVVM
        dataRepository = DataRepository()
        localeConverter = LocaleConverter()
        dataFactory = DataFactory(dataRepository)
        localeFactory = LocaleFactory(localeConverter)
        dataViewModel = ViewModelProvider(this, dataFactory).get(DataViewModel::class.java)
        localeViewModel = ViewModelProvider(this, localeFactory).get(LocaleViewModel::class.java)

        mProgressBarDelay = ProgressBarDelay()

        initUi()
        initObserve()
        setUiListener()

    }

    private fun initUi(){
        ibBack = findViewById(R.id.ib_main_back)
        ibSelectLanguage = findViewById(R.id.ib_main_select_language)
        tvAppTitle = findViewById(R.id.tv_main_app_title)
        tvTitle = findViewById(R.id.tv_main_title)
        pbLoading = findViewById(R.id.pb_main_Loading)

        //初始化Fragment
        fm = supportFragmentManager
        fmTourList = TourListFragment()
        fmTourDetailed = TourDetailedFragment()

        val transaction = fm.beginTransaction()
        transaction.add(R.id.fm_main_list, fmTourList)
        transaction.commit()
    }

    private fun initObserve(){
        //設定語系
        localeViewModel.getConvertLocale().observe(this) { locale ->
            LocaleChange.setLocale(this, locale)
        }
    }

    private fun setUiListener(){
        //返回清單
        ibBack.setOnClickListener{
            backTourList()
        }

        //語言選擇
        ibSelectLanguage.setOnClickListener {
            //載入語言清單
            val languageList = arrayOf(
                getString(R.string.traditional_chinese)
                , getString(R.string.simplified_chinese)
                , getString(R.string.english)
                , getString(R.string.japanese)
                , getString(R.string.korean))

            val builder = AlertDialog.Builder(it.context)
            builder.setTitle(getString(R.string.select_language)).setItems(languageList) { dialog, which ->
                //設定目前選取語系(for api)
                currentLanguage = resources.getStringArray(R.array.country_code)[which]
                Log.d("語系選擇", currentLanguage)
                //重整
                localeViewModel.convertIndex(which)

                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.putExtra(LOCALE_INDEX, which)
                startActivity(intent)
                finish()
            }
            builder.create()
            builder.show()
        }
    }

    /**
     * 顯示詳細資訊
     * @param data 詳細資訊
     */
    fun goDetailed(data: JSONObject){
        inTourDetailed = true
        fmTourDetailed.setData(data)
        val transaction = fm.beginTransaction()
        //隱藏並顯示
        if(fmTourDetailed.isAdded){
            transaction.hide(fmTourList).show(fmTourDetailed).commit()
        }else{
            transaction.hide(fmTourList).add(R.id.fm_main_list, fmTourDetailed).commit()
        }

        AnimationTool.fadeOut(tvAppTitle)
        AnimationTool.fadeOut(ibSelectLanguage)
        tvTitle.text = data.getString("name")
        AnimationTool.fadeIn(tvTitle)
        AnimationTool.fadeIn(ibBack)
    }

    /**
     * 詳細資訊返回清單
     */
    private fun backTourList(){
        inTourDetailed = false
        val transaction = fm.beginTransaction()
        //移除fmTourDetailed
        if(fmTourList.isAdded){
            transaction.remove(fmTourDetailed).show(fmTourList).commit()
        }else{
            transaction.remove(fmTourDetailed).add(R.id.fm_main_list, fmTourList).commit()
        }

        AnimationTool.fadeOut(tvTitle)
        AnimationTool.fadeOut(ibBack)
        AnimationTool.fadeIn(ibSelectLanguage)
        AnimationTool.fadeIn(tvAppTitle)
    }

    /**
     * 取得目前語言
     */
    fun getLanguage(): String{
        return currentLanguage
    }

    /**
     * 顯示載入中
     */
    fun showLoading(show: Boolean){
        mProgressBarDelay.progressDelay(pbLoading, show)
    }

    //返回鍵攔截
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if(inTourDetailed){
                backTourList()
            }else{
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}