package wits.lugia.demo.tool

import android.content.Context
import java.util.Locale

/**
 * 切換語言
 * 作者：洪斌峰
 * 日期：2023/08/16
 */
object LocaleChange {
    /**
     * 設定語系
     * @param context Context
     * @param locale 目標語系
     */
    fun setLocale(context: Context, locale: Locale) {
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
}