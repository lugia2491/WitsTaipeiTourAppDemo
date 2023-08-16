package wits.lugia.demo.data

import org.json.JSONObject

/**
 * 景點的Item物件
 * @param itemData 單筆的景點資訊
 * @param adapterPosition 點擊位置
 */
data class AttractionItemData(val itemData: JSONObject, val adapterPosition: Int)