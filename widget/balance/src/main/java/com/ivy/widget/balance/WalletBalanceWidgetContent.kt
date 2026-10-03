package com.ivy.widget.balance

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.ivy.ui.R

// Income keeps Ivy's green because it carries meaning; everything else comes from
// GlanceTheme (Material You wallpaper colors on Android 12+, light/dark aware).
private val IncomeGreen = Color(0xFF14CC9E)
private val OnIncomeGreen = Color.White

@Composable
fun WalletBalanceWidgetContent(
    appLocked: Boolean,
    balance: String,
    currency: String,
    income: String,
    expense: String,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransferClick: () -> Unit,
    onWidgetClick: () -> Unit,
) {
    val resources = LocalContext.current.resources
    Box(
        GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(24.dp)
            .background(GlanceTheme.colors.widgetBackground)
            .clickable(onWidgetClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = if (appLocked) Alignment.CenterHorizontally else Alignment.Start
        ) {
            if (appLocked) {
                Text(
                    modifier = GlanceModifier.padding(8.dp),
                    text = resources.getString(R.string.app_locked),
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = GlanceTheme.colors.onSurface,
                        textAlign = TextAlign.Center
                    )
                )
            } else {
                BalanceSection(balance, currency)
                IncomeExpenseSection(income, expense, currency)
                ButtonsSection(onIncomeClick, onExpenseClick, onTransferClick)
            }
        }
    }
}

@Composable
fun RowScope.WidgetClickableItem(
    @DrawableRes icon: Int,
    contentDescription: String,
    background: ColorProvider,
    iconTint: ColorProvider,
    onClick: () -> Unit,
) {
    Box(
        GlanceModifier.defaultWeight().clickable(onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            GlanceModifier
                .size(52.dp)
                .cornerRadius(26.dp)
                .background(background),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = GlanceModifier.size(32.dp),
                provider = ImageProvider(icon),
                contentDescription = contentDescription,
                colorFilter = ColorFilter.tint(iconTint)
            )
        }
    }
}

@Composable
fun BalanceSection(
    balance: String,
    currency: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
    ) {
        Text(
            text = currency,
            style = TextStyle(
                fontSize = 30.sp,
                color = GlanceTheme.colors.onSurfaceVariant
            )
        )
        Spacer(GlanceModifier.width(10.dp))
        Text(
            text = balance,
            style = TextStyle(
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onSurface
            )
        )
    }
}

@Composable
fun IncomeExpenseSection(
    income: String,
    expense: String,
    currency: String,
) {
    val resources = LocalContext.current.resources
    Row(
        GlanceModifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AmountPill(
            icon = R.drawable.ic_income_white,
            contentDescription = resources.getString(R.string.income),
            text = "$income $currency",
            background = ColorProvider(IncomeGreen),
            content = ColorProvider(OnIncomeGreen),
        )
        Spacer(GlanceModifier.width(8.dp))
        AmountPill(
            icon = R.drawable.ic_expense,
            contentDescription = resources.getString(R.string.expense),
            text = "$expense $currency",
            background = GlanceTheme.colors.secondaryContainer,
            content = GlanceTheme.colors.onSecondaryContainer,
        )
    }
}

@Composable
private fun RowScope.AmountPill(
    @DrawableRes icon: Int,
    contentDescription: String,
    text: String,
    background: ColorProvider,
    content: ColorProvider,
) {
    Row(
        GlanceModifier
            .defaultWeight()
            .cornerRadius(16.dp)
            .background(background)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(content)
        )
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = content
            ),
            maxLines = 1
        )
    }
}

@Composable
fun ButtonsSection(
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransferClick: () -> Unit,
) {
    val resources = LocalContext.current.resources
    Row(
        GlanceModifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidgetClickableItem(
            icon = R.drawable.ic_income_white,
            contentDescription = resources.getString(R.string.income),
            background = ColorProvider(IncomeGreen),
            iconTint = ColorProvider(OnIncomeGreen),
            onClick = onIncomeClick
        )
        WidgetClickableItem(
            icon = R.drawable.ic_expense,
            contentDescription = resources.getString(R.string.expense),
            background = GlanceTheme.colors.secondaryContainer,
            iconTint = GlanceTheme.colors.onSecondaryContainer,
            onClick = onExpenseClick
        )
        WidgetClickableItem(
            icon = R.drawable.ic_transfer,
            contentDescription = resources.getString(R.string.transfer),
            background = GlanceTheme.colors.primary,
            iconTint = GlanceTheme.colors.onPrimary,
            onClick = onTransferClick
        )
    }
}
