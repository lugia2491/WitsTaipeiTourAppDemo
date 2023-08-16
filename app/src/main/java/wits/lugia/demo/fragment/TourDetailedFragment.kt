package wits.lugia.demo.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import org.json.JSONObject
import wits.lugia.demo.R
import wits.lugia.demo.tool.UrlClickableSpan
import wits.lugia.demo.adapter.PhotoListAdapter
import wits.lugia.demo.databinding.FragmentTourDetailedBinding

/**
 * 詳細資料頁面
 */
class TourDetailedFragment : Fragment() {
    private var _binding: FragmentTourDetailedBinding? = null
    private val binding get() = _binding!!
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentTourDetailedBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //初始化相片清單 沒照片就顯示空照片layout
        val images = dataDetailed?.getJSONArray("images")
        if(images != null && images.length() > 0){
            mPhotoListAdapter = PhotoListAdapter(images, binding.root.context)

            //橫向清單
            val layoutManager = LinearLayoutManager(binding.root.context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            binding.rvTourDetailedPhotos.layoutManager = layoutManager

            //套用
            binding.rvTourDetailedPhotos.adapter = mPhotoListAdapter

        }else{
            Log.d("圖片", "沒有圖片")
            binding.ivTourDetailedNoPhotos.visibility = View.VISIBLE
            binding.rvTourDetailedPhotos.visibility = View.GONE

        }

        //詳細資訊
        //比較喜歡傳統方式直覺
        val openTime: String? = dataDetailed?.getString("open_time")
//        if(openTime != null && openTime != ""){
//            binding.tvTourDetailedOpenTime.text = "營業時間: $openTime"
//        }else{
//            binding.tvTourDetailedOpenTime.text = "營業時間: 無資料"
//        }
        //字串正常流程要放在資源檔內，這邊省略時間
        binding.tvTourDetailedOpenTime.text = "營業時間: ${openTime?.takeIf { it.isNotEmpty() } ?: "無資料"}"

        val address: String? = dataDetailed?.getString("address")
        binding.tvTourDetailedAddress.text = "地址: ${address?.takeIf { it.isNotEmpty() } ?: "無資料"}"

        val url: String? = dataDetailed?.getString("url")
        val urlHeaderText = "網址:"//前置字元
        if(url != null && url != ""){
            // 建立SpannableStringBuilder塞入文字
            val spannableBuilder = SpannableStringBuilder(urlHeaderText + url)
            // 設定spannableBuilder
            spannableBuilder.setSpan(
                UrlClickableSpan(url),
                urlHeaderText.length, //超連結開始位置(不含前置字元)
                urlHeaderText.length + url.length, //超連結結束位置
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvTourDetailedUrl.text = spannableBuilder
            //連結點擊
            binding.tvTourDetailedUrl.movementMethod = LinkMovementMethod.getInstance()

        }else{
            binding.tvTourDetailedUrl.text = "$urlHeaderText 無資料"
        }

        val introduction: String? = dataDetailed?.getString("introduction")
        binding.tvTourDetailedIntroduce.text = "介紹: ${introduction?.takeIf { it.isNotEmpty() } ?: "無資料"}"
    }
}