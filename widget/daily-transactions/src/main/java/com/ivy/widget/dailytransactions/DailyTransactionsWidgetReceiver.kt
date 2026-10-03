package com.ivy.widget.dailytransactions

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.ivy.base.legacy.SharedPrefs
import com.ivy.domain.AppStarter
import com.ivy.widgets.WidgetBase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Keep
@AndroidEntryPoint
class DailyTransactionsWidgetReceiver : GlanceAppWidgetReceiver() {
    companion object {
        fun updateBroadcast(context: Context) {
            WidgetBase.updateBroadcast(context, DailyTransactionsWidgetReceiver::class.java)
        }
    }

    override val glanceAppWidget: GlanceAppWidget = DailyTransactionsWidget(
        getAppStarter = { appStarter }
    )
    private val coroutineScope = MainScope()

    @Inject
    lateinit var appStarter: AppStarter

    @Inject
    lateinit var fetchDailyTransactionsAct: FetchDailyTransactionsAct

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateData(context)
    }

    private fun updateData(context: Context) {
        coroutineScope.launch {
            val appLocked = withContext(Dispatchers.IO) {
                sharedPrefs.getBoolean(SharedPrefs.APP_LOCK_ENABLED, false)
            }
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val transactions = fetchDailyTransactionsAct.execute(
                FetchDailyTransactionsAct.Input(
                    startDate = today.atStartOfDay(zone).toInstant(),
                    endDate = today.plusDays(1).atStartOfDay(zone).toInstant(),
                )
            )
            val dateLabel = today.format(DateTimeFormatter.ofPattern("EEE, d MMM"))
            val transactionsJson = Json.encodeToString(transactions)

            GlanceAppWidgetManager(context).getGlanceIds(DailyTransactionsWidget::class.java)
                .forEach { id ->
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { pref ->
                        pref.toMutablePreferences().apply {
                            this[booleanPreferencesKey(DailyTransactionsPrefsKey.APP_LOCKED)] = appLocked
                            this[stringPreferencesKey(DailyTransactionsPrefsKey.DATE_LABEL)] = dateLabel
                            this[stringPreferencesKey(DailyTransactionsPrefsKey.TRANSACTIONS)] =
                                transactionsJson
                        }
                    }
                    glanceAppWidget.update(context, id)
                }
        }
    }
}
