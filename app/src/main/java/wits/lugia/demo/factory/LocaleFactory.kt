package wits.lugia.demo.factory

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import wits.lugia.demo.model.LocaleConverter
import wits.lugia.demo.viewModel.LocaleViewModel

class LocaleFactory(private var localeConverter: LocaleConverter) : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(@NonNull modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocaleViewModel::class.java)) {
            return LocaleViewModel(localeConverter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}