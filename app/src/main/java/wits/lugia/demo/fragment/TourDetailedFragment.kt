package wits.lugia.demo.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import org.json.JSONObject
import wits.lugia.demo.R
import wits.lugia.demo.tool.UrlClickableSpan
import wits.lugia.demo.adapter.PhotoListAdapter

/**
 * 詳細資料頁面
 */
class TourDetailedFragment : Fragment() {
    private lateinit var v: View
    private lateinit var tvOpenTime: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvUrl: TextView
    private lateinit var tvIntroduce: TextView
    private lateinit var rvList: RecyclerView
    private lateinit var ivNoPhotos: ImageView
    private lateinit var mPhotoListAdapter: PhotoListAdapter

    //詳細資料
    private var dataDetailed: JSONObject? = null

    /**
     * 資料進入點
     * @param data 要顯示的詳細資料
     */
    fun setData(data: JSONObject){
        this.dataDetailed = data
        Log.d("收到資料", data.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //進場動畫
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        //出場
        exitTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    //載入元件
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_tour_detailed, container, false)
        rvList = v.findViewById(R.id.rv_tour_detailed_photos)
        tvOpenTime = v.findViewById(R.id.tv_tour_detailed_open_time)
        tvAddress = v.findViewById(R.id.tv_tour_detailed_address)
        tvUrl = v.findViewById(R.id.tv_tour_detailed_url)
        tvIntroduce = v.findViewById(R.id.tv_tour_detailed_introduce)
        ivNoPhotos = v.findViewById(R.id.iv_tour_detailed_no_photos)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //先將Adapter new出來
        mPhotoListAdapter = PhotoListAdapter()
        //初始化相片清單 沒照片就顯示空照片layout
        val images = dataDetailed?.getJSONArray("images")
        if(images != null && images.length() > 0){
            mPhotoListAdapter.initPhotoListAdapter(v.context, images)

            //橫向清單
            val layoutManager = LinearLayoutManager(v.context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            rvList.layoutManager = layoutManager

            //套用
            rvList.adapter = mPhotoListAdapter

        }else{
            Log.d("圖片", "沒有圖片")
            ivNoPhotos.visibility = View.VISIBLE
            rvList.visibility = View.GONE

        }

        //詳細資訊
        val openTime = dataDetailed?.getString("open_time")
        if(openTime != null && openTime != ""){
            tvOpenTime.text = "營業時間: $openTime"
        }else{
            tvOpenTime.text = "營業時間: 無資料"
        }

        val address = dataDetailed?.getString("address")
        if(address != null && address != ""){
            tvAddress.text = "地址: $address"
        }else{
            tvAddress.text = "地址: 無資料"
        }

        val url = dataDetailed?.getString("url")
        if(url != null && url != ""){
            //前置字元
            val urlHeaderText = "網址："
            // 建立SpannableStringBuilder塞入文字
            val spannableBuilder = SpannableStringBuilder(urlHeaderText + url)
            // 設定spannableBuilder
            spannableBuilder.setSpan(
                UrlClickableSpan(url),
                urlHeaderText.length, //超連結開始位置(不含前置字元)
                urlHeaderText.length + url.length, //超連結結束位置
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvUrl.text = spannableBuilder
            //連結點擊
            tvUrl.movementMethod = LinkMovementMethod.getInstance()

        }else{
            tvUrl.text = "網址: 無資料"
        }

        val introduction = dataDetailed?.getString("introduction")
        if(introduction != null && introduction != ""){
            tvIntroduce.text = "介紹: $introduction"
        }else{
            tvIntroduce.text = "介紹: 無資料"
        }

    }
}