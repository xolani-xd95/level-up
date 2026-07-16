package co.za.xdcodez.wealthbuilder.finance.presentation.budgetTransactions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.common.toFormattedDate
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.CategoryTransaction
import co.za.xdcodez.wealthbuilder.finance.presentation.composables.BudgetCategoryHeaderSection
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetTransactionScreenRoute(
    viewModel: TransactionsViewModel = koinViewModel<TransactionsViewModel>(),
    monthId: String,
    categoryId: String,
    onNavigate: () -> Unit
) {
    LaunchedEffect(monthId, categoryId) {
        viewModel.init(monthId, categoryId)
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                TransactionNavigationEvent.NavigateBack -> {
                    onNavigate()
                }
            }
        }
    }

    BudgetTransactionScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BudgetTransactionScreen(
    state: TransactionsUiState,
    onAction: (TransactionsActions) -> Unit,
    onNavigate: () -> Unit
) {
    val category = state.category
    val transactions = state.categoryTransactions
    val unpaid = transactions.filter { !it.isPaid }
    val paid = transactions.filter { it.isPaid }
    var showAddTransactionSheet by remember { mutableStateOf(false) }
    var showEditCategorySheet by remember { mutableStateOf(false) }

    // ── Add transaction sheet ────────────────────────────────────
    if (showAddTransactionSheet) {
        AddTransactionBottomSheet(
            state = state,
            onDismiss = { showAddTransactionSheet = false },
            onAction = { action ->
                if (action is TransactionsActions.OnSubmitTransaction) {
                    showAddTransactionSheet = false
                }
                onAction(action)
            }
        )
    }

    // ── Edit Category sheet ─────────────────────────────────
    if (showEditCategorySheet) {
        category?.let {
            EditCategoryBottomSheet(
                category = category,
                onDismiss = { showEditCategorySheet = false },
                onAction = onAction
            )
        }
    }

    // ── Transaction action sheet ─────────────────────────────────
    state.selectedTransaction?.let { transaction ->
        TransactionBottomSheet(
            transaction = transaction,
            onDismiss = { onAction(TransactionsActions.OnDismissTransactionSheet) },
            onAction = onAction
        )
    }

    // ── Edit transaction sheet ─────────────────────────────────
    if (state.editingTransaction != null) {
        EditTransactionBottomSheet(
            state = state,
            onDismiss = { onAction(TransactionsActions.OnDismissTransactionSheet) },
            onAction = onAction
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigate() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Edit category",
                    tint = Color.White
                )
            }
            Text(
                text = state.category?.name.orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            IconButton(onClick = { showEditCategorySheet = true }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit category",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            category?.let {
                BudgetCategoryHeaderSection(category, Modifier.padding(horizontal = 8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 68.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { showAddTransactionSheet = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("➕ ", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add Transaction",
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.Black)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Text(
                        "Unpaid Transactions (${unpaid.size})",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
                    )
                }
                // Add a ✅ All transactions paid statment  when everything is paid instead of having an empty state
                items(unpaid) { transaction ->
                    TransactionCard(transaction) {
                        onAction(TransactionsActions.OnTransactionClicked(transaction))
                    }
                }
                item {
                    Text(
                        "Paid Transactions (${paid.size})",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
                    )
                }
                items(paid) { transaction ->
                    TransactionCard(transaction) {
                        onAction(TransactionsActions.OnTransactionClicked(transaction))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: CategoryTransaction,
    onClick: () -> Unit
) {
    val contentAlpha = if (transaction.isPaid) 0.4f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                transaction.name.capitalize(Locale.current),
                fontSize = 15.sp,
                color = Color.White.copy(alpha = contentAlpha)
            )
            if (transaction.isPaid)
                Text(
                    "paid on: ${transaction.date.toFormattedDate()}",
                    fontSize = 13.sp,
                    color = Color(0x99FFFFFF).copy(alpha = 0.4f)
                )
        }
        Text(
            formatCurrency(transaction.amount),
            fontSize = 15.sp,
            color = Color.White.copy(alpha = contentAlpha)
        )
    }
}

@Preview
@Composable
fun BudgetTransactionScreenPreview() {
    WealthBuilderTheme {
        BudgetTransactionScreen(
            TransactionsUiState(
                category = BudgetCategoryModel(
                    id = "",
                    name = "Events",
                    totalPaid = 5000.45,
                    budget = 8000.00,
                ),
                categoryTransactions = listOf(
                    CategoryTransaction(
                        id = "",
                        name = "pertrol",
                        amount = 500.26,
                        date = "",
                        isPaid = false
                    ),
                    CategoryTransaction(
                        id = "",
                        name = "pertrol",
                        amount = 500.26,
                        date = "",
                        isPaid = false
                    ),
                    CategoryTransaction(
                        id = "",
                        name = "pertrol",
                        amount = 500.26,
                        date = "",
                        isPaid = false
                    ),
                    CategoryTransaction(
                        id = "",
                        name = "food",
                        amount = 8045.26,
                        date = "25-Apr",
                        isPaid = true
                    ),
                    CategoryTransaction(
                        id = "",
                        name = "food",
                        amount = 8045.26,
                        date = "25-Apr",
                        isPaid = true
                    ),
                    CategoryTransaction(
                        id = "",
                        name = "food",
                        amount = 8045.26,
                        date = "25-Apr",
                        isPaid = true
                    ),
                    CategoryTransaction(
                        id = "",
                        name = "food",
                        amount = 8045.26,
                        date = "25-Apr",
                        isPaid = true
                    ),
                    CategoryTransaction(
                        id = "",
                        name = "food",
                        amount = 8045.26,
                        date = "25-Apr",
                        isPaid = true
                    ),
                )
            ), {}, {})
    }
}
