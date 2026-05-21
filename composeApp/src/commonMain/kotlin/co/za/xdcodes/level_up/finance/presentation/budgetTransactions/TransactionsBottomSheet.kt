package co.za.xdcodes.level_up.finance.presentation.budgetTransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.common.widgets.CustomTextField
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.CategoryTransaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    state: TransactionsUiState,
    onDismiss: () -> Unit,
    onAction: (TransactionsActions) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current

    val isValid = state.transactionInput.name.isNotBlank() &&
            (state.transactionInput.amount.toDoubleOrNull() ?: 0.0) > 0.0

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = Color(0xFF1C1C1E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0x33FFFFFF)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Add Transaction",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Name field
            CustomTextField(
                value = state.transactionInput.name,
                onValueChange = { onAction(TransactionsActions.OnTransactionNameChanged(it)) },
                placeholder = "e.g. Checkers",
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            // Amount field
            CustomTextField(
                value = state.transactionInput.amount,
                onValueChange = { onAction(TransactionsActions.OnTransactionAmountChanged(it)) },
                placeholder = "0",
                filter = { it.filter { c -> c.isDigit() || c == '.' } },
                prefix = "R ",
                modifier = Modifier.fillMaxWidth().height(48.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Submit button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onAction(TransactionsActions.OnSubmitTransaction)
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    disabledContentColor = Color.White.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = "Add Transaction",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionBottomSheet(
    transaction: CategoryTransaction,
    onDismiss: () -> Unit,
    onAction: (TransactionsActions) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = Color(0xFF1C1C1E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0x33FFFFFF)) }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Transaction context at top
            Text(
                transaction.name.capitalize(Locale.current),
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
            Text(
                formatCurrency(transaction.amount),
                style = MaterialTheme.typography.displaySmall.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mark as paid / unpaid toggle
            SheetAction(
                label = if (transaction.isPaid) "Mark as Unpaid" else "Mark as Paid",
                icon = if (transaction.isPaid) "🟡" else "✅",
                onClick = {
                    onAction(TransactionsActions.OnPayTransaction(transaction.id))
                    onAction(TransactionsActions.OnDismissTransactionSheet)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Edit
            SheetAction(
                label = "Edit",
                icon = "✏️",
                onClick = {
//                onAction(BudgetScreenActions.OnEditTransaction(transaction))
                    onAction(TransactionsActions.OnDismissTransactionSheet)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Delete
            SheetAction(
                label = "Delete",
                icon = "🗑️",
                textColor = Color(0xFFFF2400),
                onClick = {
//                onAction(BudgetScreenActions.OnDeleteTransaction(transaction))
                    onAction(TransactionsActions.OnDismissTransactionSheet)
                }
            )
        }
    }
}

@Composable
fun SheetAction(
    label: String,
    icon: String,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(icon, fontSize = 18.sp)
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge.copy(color = textColor)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryBottomSheet(
    category: BudgetCategoryModel,
    onDismiss: () -> Unit,
    onAction: (TransactionsActions) -> Unit
) {
    // local state for the form — pre-filled with current values
    var name by remember { mutableStateOf(category.name) }
    var amount by remember { mutableStateOf(category.budget.toInt().toString()) }
    val focusManager = LocalFocusManager.current

    val isValid = name.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0.0
    val hasChanges = name != category.name || amount != category.budget.toInt().toString()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0x33FFFFFF)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Header ───────────────────────────────────────────
            Text(
                text = "Edit ${category.name}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── Name field ───────────────────────────────────────
            CustomTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Category name",
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            // ── Amount field ─────────────────────────────────────
            CustomTextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = "0",
                prefix = "R ",
                filter = { it.filter { c -> c.isDigit() || c == '.' } },
                modifier = Modifier.fillMaxWidth().height(48.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Save button ──────────────────────────────────────
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onAction(
                        TransactionsActions.OnUpdateCategory(
                            categoryId = category.id,
                            name = name,
                            budget = amount.toDoubleOrNull() ?: 0.0
                        )
                    )
                    onDismiss()
                },
                enabled = isValid && hasChanges,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.White.copy(alpha = 0.2f),
                    disabledContentColor = Color.White.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = "Save Changes",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            // ── Delete button ────────────────────────────────────
            SheetAction(
                label = "Delete Category",
                icon = "🗑️",
                textColor = Color(0xFFFF2400),
                onClick = {
                    onAction(TransactionsActions.OnDeleteCategory(category.id))
                    onDismiss()
                }
            )
        }
    }
}