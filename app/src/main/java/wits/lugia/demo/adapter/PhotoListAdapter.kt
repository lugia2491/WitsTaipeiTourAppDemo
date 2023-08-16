package wits.lugia.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import wits.lugia.demo.R
import wits.lugia.demo.databinding.ListPhotoBinding

/**
 * 照片清單Adapter
 * @param photos 照片清單
 * @param context Context
 */
class PhotoListAdapter(private var photos: JSONArray, private val context: Context): RecyclerView.Adapter<PhotoListAdapter.ViewHolder>()  {

    inner class ViewHolder(bindingList: ListPhotoBinding):RecyclerView.ViewHolder(bindingList.root){
        //綁定元件
        var binding: ListPhotoBinding
        init {
            binding = bindingList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListAdapter.ViewHolder {
        val binding = ListPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoListAdapter.ViewHolder, position: Int) {
        //顯示每張圖
        val imgUrl = photos.getJSONObject(position).getString("src")
        Glide.with(context).clear(holder.binding.ivListPhoto)
        Glide.with(context).load(imgUrl)
            .placeholder(R.drawable.loading_bar)
            .error(R.drawable.error_pictures)
            .into(holder.binding.ivListPhoto)
    }

    override fun getItemCount(): Int = photos.length()

}