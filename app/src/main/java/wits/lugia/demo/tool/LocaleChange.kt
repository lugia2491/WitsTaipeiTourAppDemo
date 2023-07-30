package wits.lugia.demo.tool

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

/**
 * 切換語言
 */
object LocaleChange {
    /**
     * 設定語系
     * @param context Context
     * @param locale 目標語系
     */
    fun setLocale(context: Context, locale: Locale) {
        val resources = context.resources
        val dm = resources.displayMetrics
        var config: Configuration? = null
        config = Resources.getSystem().configuration
        config.locale = locale
        resources.updateConfiguration(config, dm)
    }
}