package com.ivy.widget.dailytransactions

import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.model.getFromValue
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.TransactionRepository
import kotlinx.serialization.Serializable
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

enum class DailyTransactionType { INCOME, EXPENSE, TRANSFER }

@Serializable
data class DailyTransactionItem(
    val title: String,
    val subtitle: String,
    val amount: String,
    val type: DailyTransactionType,
    val colorArgb: Int,
)

class FetchDailyTransactionsAct @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) {
    private val amountFormat = DecimalFormat("#,##0.00")
    private val timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        .withZone(ZoneId.systemDefault())

    suspend fun execute(input: Input): List<DailyTransactionItem> {
        val categories = categoryRepository.findAll().associateBy { it.id }
        return transactionRepository.findAllBetween(input.startDate, input.endDate)
            .filter { it.settled }
            .sortedByDescending { it.time }
            .map { trn ->
                val category = trn.category?.let { categories[it] }
                val type = trn.type()
                val value = trn.getFromValue()
                val fallbackTitle = when (type) {
                    DailyTransactionType.INCOME -> "Income"
                    DailyTransactionType.EXPENSE -> "Expense"
                    DailyTransactionType.TRANSFER -> "Transfer"
                }
                val sign = when (type) {
                    DailyTransactionType.INCOME -> "+"
                    DailyTransactionType.EXPENSE -> "-"
                    DailyTransactionType.TRANSFER -> ""
                }
                DailyTransactionItem(
                    title = trn.title?.value ?: category?.name?.value ?: fallbackTitle,
                    subtitle = listOfNotNull(
                        category?.name?.value,
                        timeFormat.format(trn.time),
                    ).joinToString(" · "),
                    amount = "$sign${amountFormat.format(value.amount.value)} ${value.asset.code}",
                    type = type,
                    colorArgb = category?.color?.value ?: DEFAULT_COLOR,
                )
            }
    }

    private fun Transaction.type(): DailyTransactionType = when (this) {
        is Income -> DailyTransactionType.INCOME
        is Expense -> DailyTransactionType.EXPENSE
        is Transfer -> DailyTransactionType.TRANSFER
    }

    data class Input(
        val startDate: Instant,
        val endDate: Instant,
    )

    private companion object {
        const val DEFAULT_COLOR = 0xFF5C3DF5.toInt()
    }
}
