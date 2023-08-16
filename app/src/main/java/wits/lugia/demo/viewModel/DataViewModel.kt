package wits.lugia.demo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import wits.lugia.demo.model.DataResponseModel

class DataViewModel(private var dataResponseModel: DataResponseModel): ViewModel()  {
    private var apiLiveData = MutableLiveData<JSONObject>()
    private var errorLiveData = MutableLiveData<String>()
    private var tmpData: JSONObject? = JSONObject()//已下載的資料
    private var pageLoad = 1//目前頁碼

    fun callApi(addPage: Boolean, language: String) {
        //換頁邏輯
        if(!addPage){
            tmpData = null
            pageLoad = 1
        }else{
            pageLoad++
        }

        dataResponseModel.loadInfo(pageLoad.toString(), tmpData, language, object : DataResponseModel.OnTaskFinish {
            override fun onFinish(data: JSONObject) {
                tmpData = data
                apiLiveData.postValue(data)
            }

            override fun onError(errorMessage: String) {
                errorLiveData.postValue(errorMessage)
            }
        })
    }

    fun getApiLiveData(): LiveData<JSONObject>{
        return apiLiveData
    }

    fun getErrorLiveData(): LiveData<String>{
        return errorLiveData
    }
}