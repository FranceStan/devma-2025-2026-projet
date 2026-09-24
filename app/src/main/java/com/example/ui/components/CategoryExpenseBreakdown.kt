package com.example.ui.components

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.EcoBudgetDomain
import com.example.shared.resources.EcoBudgetStrings
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.VioletPrimary
import com.example.ui.theme.VioletPrimaryLight
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CategoryExpenseBreakdown(
    statistics: List<EcoBudgetDomain.CategoryExpenseStat>,
    modifier: Modifier = Modifier
) {
    val formatFcfa = remember {
        NumberFormat.getNumberInstance(Locale.FRENCH).apply {
            maximumFractionDigits = 0
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_expense_breakdown"),
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
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
                val color = if (index % 2 == 0) VioletPrimary else VioletPrimaryLight
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
                            text = "${(statistic.percentage * 100).toInt()}%  ${formatFcfa.format(statistic.total)} ${EcoBudgetStrings.currencyFcfa}",
                            color = DarkTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    LinearProgressIndicator(
                        progress = { statistic.percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = color,
                        trackColor = DarkOutline,
                        drawStopIndicator = {}
                    )
                }
            }
        }
    }
}