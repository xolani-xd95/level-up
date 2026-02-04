package co.za.xdcodes.level_up.finance.data

import co.za.xdcodes.level_up.finance.domain.BudgetRepository
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.BudgetOverviewModel
import co.za.xdcodes.level_up.finance.domain.dto.CategoryTransaction
import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirebaseBudgetRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BudgetRepository {

    override suspend fun getBudgetMonths(): List<BudgetOverviewModel> {
        val snapshot = firestore
            .collection("budgetOverview")
            .orderBy("monthIndex")
            .get()

        return snapshot.documents.map { month ->
            BudgetOverviewModel(
                month = month.get("month") ?: "",
                monthIndex = month.get("monthIndex") ?: 0,
                moneyIn = month.get("moneyIn") ?: 0.0,
                moneyOut = month.get("moneyOut") ?: 0.0,
                totalBudget = month.get("totalBudget") ?: 0.0,
                savings = month.get("savings") ?: 0.0,
            )
        }
    }

    override suspend fun getBudgetCategories(month: String): List<BudgetCategoryModel> {
        val snapshot = firestore
            .collection("budgetCategories")
            .document(month)
            .collection("categories")
            .get()

        return snapshot.documents.map { category ->
            BudgetCategoryModel(
                category = category.get("category") ?: "",
                month = category.get("month") ?: "",
                totalBudget = category.get("totalBudget") ?: 0.0,
                totalPaid = category.get("totalPaid") ?: 0.0
            )
        }
    }


    override suspend fun addTransaction(
        transaction: CategoryTransaction,
        category: BudgetCategoryModel
    ): Result<Unit> = try {
        val firestore = firestore

        firestore.collection("budgetCategories")
            .document(transaction.month)
            .collection("categories")
            .document(transaction.category)
            .collection("transactions")
            .document(transaction.id)
            .set(transaction)

        val updatedCategoryTotal = category.totalBudget + transaction.amount
        firestore
            .collection("budgetCategories")
            .document(category.month)
            .collection("categories")
            .document(category.category)
            .update(
                mapOf("totalBudget" to updatedCategoryTotal)
            )

        val monthOverviewRef = firestore
            .collection("budgetOverview")
            .document(category.month)

        val snapshot = monthOverviewRef.get()
        val currentTotalBudget = snapshot.get<Double>("totalBudget")

        monthOverviewRef.update(
            mapOf("totalBudget" to (currentTotalBudget + transaction.amount))
        )

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateCategoryField(
        month: String,
        category: String,
        field: String,
        value: Double
    ) {
        firestore
            .collection("budgetCategories")
            .document(month)
            .collection("categories")
            .document(category)
            .update(
                mapOf(field to value)
            )
    }

    override suspend fun payTransaction(
        category: String,
        month: String,
        transactionId: String
    ): Result<Unit> {
        return try {
            val firestore = firestore

            // ---- Transaction ----
            val transactionRef = firestore
                .collection("budgetCategories")
                .document(month)
                .collection("categories")
                .document(category)
                .collection("transactions")
                .document(transactionId)

            val transactionSnapshot = transactionRef.get()

            val amount = transactionSnapshot.get<Double>("amount") ?: return Result.success(Unit)
            val isPaid = transactionSnapshot.get<Boolean>("isPaid") ?: false

            if (isPaid) return Result.success(Unit) // 👈 prevent double-pay

            transactionRef.update(
                mapOf("isPaid" to true)
            )

            // ---- Category ----
            val categoryRef = firestore
                .collection("budgetCategories")
                .document(month)
                .collection("categories")
                .document(category)

            val categorySnapshot = categoryRef.get()
            val currentTotalPaid = categorySnapshot.get<Double>("totalPaid") ?: 0.0

            categoryRef.update(
                mapOf("totalPaid" to currentTotalPaid + amount)
            )

            // ---- Month overview ----
            val monthOverviewRef = firestore
                .collection("budgetOverview")
                .document(month)

            val monthSnapshot = monthOverviewRef.get()
            val currentMoneyOut = monthSnapshot.get<Double>("moneyOut") ?: 0.0

            monthOverviewRef.update(
                mapOf("moneyOut" to currentMoneyOut + amount)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryTransaction(
        month: String,
        category: String
    ): List<CategoryTransaction> {
        val snapshot = firestore
            .collection("budgetCategories")
            .document(month)
            .collection("categories")
            .document(category)
            .collection("transactions")
            .get()

        return snapshot.documents.map { transaction ->
            CategoryTransaction(
                id = transaction.id,
                name = transaction.get("name") ?: "",
                category = transaction.get("category") ?: "",
                month = transaction.get("month") ?: "",
                amount = transaction.get("amount") ?: 0.0,
                isPaid = transaction.get("isPaid") ?: false,
                date = transaction.get("date") ?: "",
            )
        }
    }
}