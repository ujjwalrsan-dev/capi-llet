package com.ivy.widget.dailytransactions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.ivy.ui.R

// Semantic money colors stay fixed; everything else comes from GlanceTheme
// (Material You wallpaper colors on Android 12+, light/dark aware).
private val IncomeGreen = Color(0xFF12B880)
private val ExpenseRed = Color(0xFFE53950)

@Composable
fun DailyTransactionsWidgetContent(
    appLocked: Boolean,
    dateLabel: String,
    transactions: List<DailyTransactionItem>,
    onAddClick: () -> Unit,
    onWidgetClick: () -> Unit,
) {
    Box(
        GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(24.dp)
            .background(GlanceTheme.colors.widgetBackground),
    ) {
        if (appLocked) {
            Box(
                GlanceModifier.fillMaxSize().clickable(onWidgetClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = LocalContext.current.resources.getString(R.string.app_locked),
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = GlanceTheme.colors.onSurface,
                        textAlign = TextAlign.Center
                    )
                )
            }
        } else {
            Column(GlanceModifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 12.dp)) {
                Header(
                    dateLabel = dateLabel,
                    count = transactions.size,
                    onAddClick = onAddClick,
                    onTitleClick = onWidgetClick,
                )
                Spacer(GlanceModifier.height(10.dp))
                if (transactions.isEmpty()) {
                    EmptyState(onAddClick)
                } else {
                    LazyColumn(GlanceModifier.fillMaxSize()) {
                        items(transactions) { item ->
                            TransactionRow(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    dateLabel: String,
    count: Int,
    onAddClick: () -> Unit,
    onTitleClick: () -> Unit,
) {
    Row(
        GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(GlanceModifier.defaultWeight().clickable(onTitleClick)) {
            Text(
                text = "Today",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onSurface
                )
            )
            val countLabel = when (count) {
                0 -> "No transactions"
                1 -> "1 transaction"
                else -> "$count transactions"
            }
            Text(
                text = listOf(dateLabel, countLabel).filter { it.isNotEmpty() }.joinToString(" · "),
                style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurfaceVariant),
                maxLines = 1
            )
        }
        Box(
            GlanceModifier
                .size(40.dp)
                .cornerRadius(20.dp)
                .background(GlanceTheme.colors.primary)
                .clickable(onAddClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onPrimary,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun TransactionRow(item: DailyTransactionItem) {
    Column {
        Row(
            GlanceModifier
                .fillMaxWidth()
                .cornerRadius(12.dp)
                .background(GlanceTheme.colors.secondaryContainer)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                GlanceModifier
                    .size(10.dp)
                    .cornerRadius(5.dp)
                    .background(Color(item.colorArgb))
            ) {}
            Spacer(GlanceModifier.width(10.dp))
            Column(GlanceModifier.defaultWeight()) {
                Text(
                    text = item.title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = GlanceTheme.colors.onSecondaryContainer
                    ),
                    maxLines = 1
                )
                Text(
                    text = item.subtitle,
                    style = TextStyle(fontSize = 11.sp, color = GlanceTheme.colors.onSurfaceVariant),
                    maxLines = 1
                )
            }
            Spacer(GlanceModifier.width(8.dp))
            Text(
                text = item.amount,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (item.type) {
                        DailyTransactionType.INCOME -> ColorProvider(IncomeGreen)
                        DailyTransactionType.EXPENSE -> ColorProvider(ExpenseRed)
                        DailyTransactionType.TRANSFER -> GlanceTheme.colors.onSecondaryContainer
                    }
                ),
                maxLines = 1
            )
        }
        Spacer(GlanceModifier.height(6.dp))
    }
}

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    Box(
        GlanceModifier.fillMaxSize().clickable(onAddClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Nothing spent yet today",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlanceTheme.colors.onSurface,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = "Tap + to add a transaction",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
