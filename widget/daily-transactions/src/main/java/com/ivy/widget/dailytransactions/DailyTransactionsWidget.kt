package com.ivy.widget.dailytransactions

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import com.ivy.base.model.TransactionType
import com.ivy.domain.AppStarter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object DailyTransactionsPrefsKey {
    const val APP_LOCKED = "daily_trns_app_locked"
    const val DATE_LABEL = "daily_trns_date_label"
    const val TRANSACTIONS = "daily_trns_items_v1"
}

class DailyTransactionsWidget(
    private val getAppStarter: () -> AppStarter,
) : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val appLocked = prefs[booleanPreferencesKey(DailyTransactionsPrefsKey.APP_LOCKED)] ?: false
            val dateLabel = prefs[stringPreferencesKey(DailyTransactionsPrefsKey.DATE_LABEL)] ?: ""
            val transactions = prefs[stringPreferencesKey(DailyTransactionsPrefsKey.TRANSACTIONS)]
                ?.let { json ->
                    runCatching { Json.decodeFromString<List<DailyTransactionItem>>(json) }.getOrNull()
                }
                ?: emptyList()

            GlanceTheme {
                DailyTransactionsWidgetContent(
                    appLocked = appLocked,
                    dateLabel = dateLabel,
                    transactions = transactions,
                    onAddClick = {
                        getAppStarter().addTransactionStart(TransactionType.EXPENSE)
                    },
                    onWidgetClick = {
                        getAppStarter().defaultStart()
                    },
                )
            }
        }
    }
}
