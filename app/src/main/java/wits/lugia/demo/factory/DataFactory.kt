package wits.lugia.demo.factory

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import wits.lugia.demo.model.DataRepository
import wits.lugia.demo.viewModel.DataViewModel

class DataFactory(private var dataRepository: DataRepository) : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(@NonNull modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
            return DataViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}