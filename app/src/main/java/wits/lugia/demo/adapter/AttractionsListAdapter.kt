package wits.lugia.demo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import wits.lugia.demo.R

/**
 * 景點清單Adapter
 */
class AttractionsListAdapter: RecyclerView.Adapter<AttractionsListAdapter.ViewHolder>()  {

    private lateinit var context: Context
    private lateinit var data: JSONArray

    private lateinit var mAttractionsListClickListener: AttractionsListClickListener

    //點擊
    interface AttractionsListClickListener {
        //將點擊功能導至主畫面
        fun viewClickListener(view: View?, clickData: JSONObject, position: Int)
    }

    fun setAttractionsListClickListener(ViewClickListener: AttractionsListClickListener) {
        this.mAttractionsListClickListener = ViewClickListener
    }

    //初始化
    fun initDeviceUserManagerAdapter(context: Context, data: JSONArray) {
        this.context = context
        this.data = data
    }

    //ui初始化
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        //初始化元件
        private val clRoot: ConstraintLayout = itemView.findViewById(R.id.cl_list_attractions_root)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_list_attractions_title)
        val tvContent: TextView = itemView.findViewById(R.id.tv_list_attractions_content)
        val ivImg: ImageView = itemView.findViewById(R.id.iv_list_attractions_img)

        init {
            //點擊控制
            clRoot.setOnClickListener {
                mAttractionsListClickListener.viewClickListener(it, data.getJSONObject(adapterPosition), adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionsListAdapter.ViewHolder {
        //載入畫面
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_attractions, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionsListAdapter.ViewHolder, position: Int) {
        //景點名稱
        holder.tvTitle.text = data.getJSONObject(position).getString("name")
        //景點介紹
        holder.tvContent.text = data.getJSONObject(position).getString("introduction")
        //檢查是否有圖片
        if(data.getJSONObject(position).getJSONArray("images").length() > 0){
            //取第一張圖來用
            val imgUrl = data.getJSONObject(position).getJSONArray("images").getJSONObject(0).getString("src")
            Glide.with(context).clear(holder.ivImg)
            Glide.with(context).load(imgUrl)
                .placeholder(R.drawable.loading_bar)
                .error(R.drawable.error_pictures)
                .into(holder.ivImg)

        }else{//沒圖就用預設圖
            Glide.with(context).load(R.drawable.no_pictures)
                .fitCenter()
                .into(holder.ivImg)
        }
    }

    /**
     * 更新資料
     * @param data 新的清單
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(data: JSONArray){
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.length()
    }

}