package wits.lugia.demo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONArray
import wits.lugia.demo.R
import wits.lugia.demo.data.AttractionItemData
import wits.lugia.demo.databinding.ListAttractionsBinding

/**
 * 景點清單Adapter
 * @param data 景點資料清單
 * @param context Context
 */
class AttractionsListAdapter(private var data: JSONArray, private val context: Context): RecyclerView.Adapter<AttractionsListAdapter.ViewHolder>()  {

    //RxJava 實現按鈕點擊事件
    private val itemClickSubject = PublishSubject.create<AttractionItemData>()

// 直接於new時就把資料帶進來，不用另外init
//    fun initDeviceUserManagerAdapter(context: Context, data: JSONArray) {
//        this.context = context
//        this.data = data
//    }

    /** ui初始化 */
    inner class ViewHolder(bindingList: ListAttractionsBinding): RecyclerView.ViewHolder(bindingList.root) {
        //綁定元件
        var binding: ListAttractionsBinding
        init {
            binding = bindingList
            binding.clListAttractionsRoot.setOnClickListener {//傳送點擊事件
                itemClickSubject.onNext(AttractionItemData(data.getJSONObject(adapterPosition), adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListAttractionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //景點名稱
        holder.binding.tvListAttractionsTitle.text = data.getJSONObject(position).getString("name")
        //景點介紹
        holder.binding.tvListAttractionsContent.text = data.getJSONObject(position).getString("introduction")
        //檢查是否有圖片
        if(data.getJSONObject(position).getJSONArray("images").length() > 0){
            //取第一張圖來用
            val imgUrl = data.getJSONObject(position).getJSONArray("images").getJSONObject(0).getString("src")
            Glide.with(context).clear(holder.binding.ivListAttractionsImg)
            Glide.with(context).load(imgUrl)
                .placeholder(R.drawable.loading_bar)
                .error(R.drawable.error_pictures)
                .into(holder.binding.ivListAttractionsImg)

        }else{//沒圖就用預設圖
            Glide.with(context).load(R.drawable.no_pictures)
                .fitCenter()
                .into(holder.binding.ivListAttractionsImg)
        }
    }

    /**
     * 更新資料
     * @param data 新的清單
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(data: JSONArray){
        this.data = data
        //直接刷整個畫面，如數量多就要用差異算法局部更新
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.length()

    /** ItemClick訂閱通道 */
    fun getItemClickObservable(): Observable<AttractionItemData> {
        return itemClickSubject
    }
}