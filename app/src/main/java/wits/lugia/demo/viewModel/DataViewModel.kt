package wits.lugia.demo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import wits.lugia.demo.model.DataRepository

class DataViewModel(private var dataRepository: DataRepository): ViewModel()  {
    private var apiLiveData = MutableLiveData<JSONObject>()
    private var errorLiveData = MutableLiveData<String>()

    fun callApi(page: String, addPage: JSONObject?, language: String) {
        dataRepository.loadInfo(page, addPage, language, object : DataRepository.OnTaskFinish {
            override fun onFinish(data: JSONObject) {
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