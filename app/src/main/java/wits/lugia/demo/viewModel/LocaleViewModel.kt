package wits.lugia.demo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wits.lugia.demo.model.LocaleConverterModel
import java.util.Locale

class LocaleViewModel(private var localeConverterModel: LocaleConverterModel): ViewModel()  {
    //建立LiveData
    private val convertLocale = MutableLiveData<Locale>()

    /**
     * 轉換Index至Locale
     * @param index Index
     */
    fun convertIndex(index: Int) {
        val convertedLocale = localeConverterModel.convertIndexToLocale(index)
        convertLocale.value = convertedLocale
    }

    /**
     * 取得Locale
     */
    fun getConvertLocale(): LiveData<Locale> {
        return convertLocale
    }
}