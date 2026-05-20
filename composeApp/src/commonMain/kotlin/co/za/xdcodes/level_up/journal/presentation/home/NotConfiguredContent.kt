package co.za.xdcodes.level_up.journal.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.toMonthName

@Composable
fun NotConfiguredContent(
    monthIndex: Int,
    year: Int,
    hasGlobalConfig: Boolean,
    onSetup: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowUp,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = if (hasGlobalConfig) "No target set"
                else "Journal not configured",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (hasGlobalConfig)
                    "Set your profit target for ${monthIndex.toMonthName()} $year to start tracking"
                else
                    "Set up your trading rules and monthly target to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0x99FFFFFF),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onSetup,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (hasGlobalConfig) "Set Monthly Target"
                    else "Set Up Journal",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}