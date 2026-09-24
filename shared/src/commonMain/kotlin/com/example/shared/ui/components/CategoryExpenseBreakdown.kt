package com.example.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.EcoBudgetDomain
import com.example.shared.resources.EcoBudgetStrings

private val breakdownSurface = Color(0xFF22242E)
private val breakdownOutline = Color(0xFF383A48)
private val breakdownTextSecondary = Color(0xFFB4B7C5)
private val breakdownPrimary = Color(0xFF7C3AED)
private val breakdownPrimaryLight = Color(0xFF8B5CF6)

private fun formatAmount(amount: Double): String =
    if (amount % 1.0 == 0.0) amount.toLong().toString() else amount.toString()

@Composable
fun CategoryExpenseBreakdown(
    statistics: List<EcoBudgetDomain.CategoryExpenseStat>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_expense_breakdown"),
        color = breakdownSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, breakdownOutline)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = EcoBudgetStrings.categoryDistributionTitle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            statistics.forEachIndexed { index, statistic ->
                val color = if (index % 2 == 0) breakdownPrimary else breakdownPrimaryLight
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${statistic.category.emoji} ${statistic.category.label}",
                            modifier = Modifier.weight(1f),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${(statistic.percentage * 100).toInt()}%  ${formatAmount(statistic.total)} ${EcoBudgetStrings.currencyFcfa}",
                            color = breakdownTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    LinearProgressIndicator(
                        progress = { statistic.percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = color,
                        trackColor = breakdownOutline,
                        drawStopIndicator = {}
                    )
                }
            }
        }
    }
}