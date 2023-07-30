package wits.lugia.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import wits.lugia.demo.R

/**
 * 照片清單Adapter
 */
class PhotoListAdapter: RecyclerView.Adapter<PhotoListAdapter.ViewHolder>()  {

    private lateinit var context: Context
    private lateinit var photos: JSONArray

    fun initPhotoListAdapter(context: Context, photos: JSONArray) {
        this.context = context
        this.photos = photos
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        //初始化元件
        private val clRoot: ConstraintLayout = itemView.findViewById(R.id.cl_list_photo_root)
        val ivImg: ImageView = itemView.findViewById(R.id.iv_list_photo)

//        init {
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListAdapter.ViewHolder {
        //載入畫面
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoListAdapter.ViewHolder, position: Int) {
        //顯示每張圖
        val imgUrl = photos.getJSONObject(position).getString("src")
        Glide.with(context).clear(holder.ivImg)
        Glide.with(context).load(imgUrl)
            .placeholder(R.drawable.loading_bar)
            .error(R.drawable.error_pictures)
            .into(holder.ivImg)
    }

    override fun getItemCount(): Int {
        return photos.length()
    }

}