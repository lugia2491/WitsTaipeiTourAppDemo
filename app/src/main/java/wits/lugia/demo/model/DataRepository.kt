package wits.lugia.demo.model

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class DataRepository {
    fun loadInfo(page: String, addPage: JSONObject?, language: String, task: OnTaskFinish) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.travel.taipei/open-api/$language/Attractions/All?page=$page")
            .addHeader("accept", "application/json")
            .build()

        //執行GET
        client.newCall(request).enqueue(object : Callback {
            //通常是網路連線錯誤的情況
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                task.onError("連線錯誤")
            }
            //收到回覆區域
            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body?.string()
                val responseCode = response.code
                responseString?.let { Log.d("onResponse", it) }
                //進行自己的判斷、下一步
                when (responseCode) {
                    200 -> {
                        if (responseString != null) {
                            try{
                                val jsonResponse = JSONObject(responseString)
                                if(addPage == null){
                                    task.onFinish(jsonResponse)
                                }else{
                                    for(i in 0 until jsonResponse.getJSONArray("data").length()){
                                        addPage.getJSONArray("data").put(jsonResponse.getJSONArray("data").getJSONObject(i))
                                    }
//                                    Log.d("組合", addPage.toString())
                                    task.onFinish(addPage)
                                }

                            }catch (e: Exception){
                                task.onError("資料錯誤: $responseString")
                            }
                        }
                    }
                    204 -> {
                        task.onError("$responseCode No Content")
                    }
                    403 -> {
                        task.onError("$responseCode Forbidden")
                    }
                    404 -> {
                        task.onError("$responseCode Not Found")
                    }
                    500 -> {
                        task.onError("$responseCode System Busy")
                    }
                    else -> {
                        task.onError("$responseCode 連線錯誤")
                    }
                }
            }
        })
    }

    interface OnTaskFinish {
        fun onFinish(data: JSONObject)
        fun onError(errorMessage: String)
    }
}