package wits.lugia.demo.model

import java.util.Locale

class LocaleConverter {

    /**
     * 將dialog的index轉換成app所需的Locale
     */
    fun convertIndexToLocale(index: Int): Locale {
        when(index){
            0 -> return Locale.TAIWAN
            1 -> return Locale.CHINA
            2 -> return Locale.US
            3 -> return Locale.JAPAN
            4 -> return Locale.KOREA
        }
        return Locale.TAIWAN
    }
}