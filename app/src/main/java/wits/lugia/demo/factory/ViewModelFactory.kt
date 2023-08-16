package wits.lugia.demo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 通用Factory
 */
class ViewModelFactory<T1 : ViewModel?>(private val listener: () -> T1? = { null } ) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return listener() as? T ?: modelClass.newInstance()
    }
}