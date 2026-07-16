package co.za.xdcodez.wealthbuilder.finance.data

import co.za.xdcodez.wealthbuilder.finance.domain.BudgetRepository
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryInput
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetMonthModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.CategoryTransaction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class FirebaseBudgetRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BudgetRepository {

    // ── Month ────────────────────────────────────────────────────

    override suspend fun getMonth(monthId: String): BudgetMonthModel? {
        return try {
            val snapshot = firestore
                .collection("months")
                .document(monthId)
                .get()

            if (!snapshot.exists) return null

            BudgetMonthModel(
                monthId = monthId,
                startDate = snapshot.get("startDate") ?: "",
                endDate = snapshot.get("endDate") ?: "",
                moneyIn = snapshot.get("moneyIn") ?: 0.0,
                moneyOut = snapshot.get("moneyOut") ?: 0.0,
                totalBudget = snapshot.get("totalBudget") ?: 0.0
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createMonth(month: BudgetMonthModel): Result<Unit> = try {

        val batch = firestore.batch()

        // ── 1. Month document ────────────────────────────────────
        val monthRef = firestore
            .collection("months")
            .document(month.monthId)

        batch.set(
            monthRef, mapOf(
                "startDate" to month.startDate,
                "endDate" to month.endDate,
                "moneyIn" to month.moneyIn,
                "moneyOut" to 0.0,
                "totalBudget" to month.totalBudget
            )
        )

        // ── 2. Income sources ────────────────────────────────────
        month.incomeSources.forEach { source ->
            val sourceRef = monthRef
                .collection("incomeSources")
                .document(source.id)

            batch.set(
                sourceRef, mapOf(
                    "name" to source.name,
                    "amount" to (source.amount ?: 0.0)
                )
            )
        }

        // ── 3. Categories ────────────────────────────────────────
        month.categories.forEach { category ->
            val categoryRef = monthRef
                .collection("categories")
                .document(category.id)

            batch.set(
                categoryRef, mapOf(
                    "name" to category.name,
                    "budget" to (category.amount ?: 0.0),
                    "totalPaid" to 0.0
                )
            )
        }

        batch.commit()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ── Categories ───────────────────────────────────────────────

    override suspend fun getCategory(monthId: String, categoryId: String): BudgetCategoryModel? {
        return try {
            val snapshot = firestore
                .collection("months")
                .document(monthId)
                .collection("categories")
                .document(categoryId)
                .get()

            if (!snapshot.exists) return null

            BudgetCategoryModel(
                id = snapshot.id,
                name = snapshot.get("name") ?: "",
                budget = snapshot.get("budget") ?: 0.0,
                totalPaid = snapshot.get("totalPaid") ?: 0.0
            )
        }  catch (e: Exception) {
            null
        }
    }

    override suspend fun getCategories(monthId: String): List<BudgetCategoryModel> {
        return try {
            val snapshot = firestore
                .collection("months")
                .document(monthId)
                .collection("categories")
                .get()

            snapshot.documents.map { doc ->
                BudgetCategoryModel(
                    id = doc.id,
                    name = doc.get("name") ?: "",
                    budget = doc.get("budget") ?: 0.0,
                    totalPaid = doc.get("totalPaid") ?: 0.0
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addCategory(
        monthId: String,
        category: BudgetCategoryInput
    ): Result<Unit> = try {
        val amount = category.amount.toDoubleOrNull() ?: 0.0

        // ── Add category document ────────────────────────────────
        firestore
            .collection("months")
            .document(monthId)
            .collection("categories")
            .document(category.id)
            .set(
                mapOf(
                    "name" to category.name,
                    "budget" to amount,
                    "totalPaid" to 0.0
                )
            )

        // ── Update month totalBudget ─────────────────────────────
        val monthRef = firestore
            .collection("months")
            .document(monthId)

        val monthSnapshot = monthRef.get()
        val currentTotal = monthSnapshot.get<Double>("totalBudget") ?: 0.0

        monthRef.update(mapOf("totalBudget" to currentTotal + amount))

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateCategory(
        monthId: String,
        categoryId: String,
        name: String,
        budget: Double
    ): Result<Unit> = try {
        val categoryRef = firestore
            .collection("months")
            .document(monthId)
            .collection("categories")
            .document(categoryId)

        // get old budget to calculate difference for month totalBudget
        val snapshot = categoryRef.get()
        val oldBudget = snapshot.get<Double>("budget") ?: 0.0
        val budgetDiff = budget - oldBudget

        categoryRef.update(mapOf("name" to name, "budget" to budget))

        // update month totalBudget with the difference
        if (budgetDiff != 0.0) {
            val monthRef = firestore.collection("months").document(monthId)
            val monthSnapshot = monthRef.get()
            val currentTotal = monthSnapshot.get<Double>("totalBudget") ?: 0.0
            monthRef.update(mapOf("totalBudget" to currentTotal + budgetDiff))
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteCategory(
        monthId: String,
        categoryId: String
    ): Result<Unit> = try {
        val categoryRef = firestore
            .collection("months")
            .document(monthId)
            .collection("categories")
            .document(categoryId)

        // get budget before deleting to update month totalBudget
        val snapshot = categoryRef.get()
        val budget = snapshot.get<Double>("budget") ?: 0.0
        val totalPaid = snapshot.get<Double>("totalPaid") ?: 0.0

        // delete all transactions first
        val transactions = categoryRef.collection("transactions").get()
        transactions.documents.forEach { it.reference.delete() }

        // delete category
        categoryRef.delete()

        // update month totals
        val monthRef = firestore.collection("months").document(monthId)
        val monthSnapshot = monthRef.get()
        val currentTotal = monthSnapshot.get<Double>("totalBudget") ?: 0.0
        val currentMoneyOut = monthSnapshot.get<Double>("moneyOut") ?: 0.0

        monthRef.update(mapOf(
            "totalBudget" to (currentTotal - budget).coerceAtLeast(0.0),
            "moneyOut" to (currentMoneyOut - totalPaid).coerceAtLeast(0.0)
        ))

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ── Transactions ─────────────────────────────────────────────

    override suspend fun getTransactions(
        monthId: String,
        categoryId: String
    ): List<CategoryTransaction> {
        return try {
            val snapshot = firestore
                .collection("months")
                .document(monthId)
                .collection("categories")
                .document(categoryId)
                .collection("transactions")
                .get()

            snapshot.documents.map { doc ->
                CategoryTransaction(
                    id = doc.id,
                    name = doc.get("name") ?: "",
                    amount = doc.get("amount") ?: 0.0,
                    isPaid = doc.get("isPaid") ?: false,
                    date = doc.get("date") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addTransaction(
        monthId: String,
        categoryId: String,
        transaction: CategoryTransaction
    ): Result<Unit> = try {
        firestore
            .collection("months")
            .document(monthId)
            .collection("categories")
            .document(categoryId)
            .collection("transactions")
            .document(transaction.id)
            .set(
                mapOf(
                    "name" to transaction.name,
                    "amount" to transaction.amount,
                    "isPaid" to false,
                    "date" to transaction.date
                )
            )

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateTransaction(
        monthId: String,
        categoryId: String,
        transactionId: String,
        name: String,
        amount: Double
    ): Result<Unit> = try {
        firestore
            .collection("months")
            .document(monthId)
            .collection("categories")
            .document(categoryId)
            .collection("transactions")
            .document(transactionId)
            .update(
                mapOf(
                    "name" to name,
                    "amount" to amount
                )
            )

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteTransaction(
        monthId: String,
        categoryId: String,
        transactionId: String
    ): Result<Unit> = try {
        val transactionRef = firestore
            .collection("months")
            .document(monthId)
            .collection("categories")
            .document(categoryId)
            .collection("transactions")
            .document(transactionId)

        // if paid, reverse totalPaid and moneyOut before deleting
        val snapshot = transactionRef.get()
        val isPaid = snapshot.get<Boolean>("isPaid") ?: false
        val amount = snapshot.get<Double>("amount") ?: 0.0

        if (isPaid) {
            reversePayment(monthId, categoryId, amount)
        }

        transactionRef.delete()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun payTransaction(
        monthId: String,
        categoryId: String,
        transactionId: String
    ): Result<Unit> {
        return try {
            val timeZone = TimeZone.currentSystemDefault()
            val currentDate = Clock.System.todayIn(timeZone)

            val transactionRef = firestore
                .collection("months")
                .document(monthId)
                .collection("categories")
                .document(categoryId)
                .collection("transactions")
                .document(transactionId)

            val snapshot = transactionRef.get()
            val isPaid = snapshot.get<Boolean>("isPaid") ?: false
            if (isPaid) return Result.success(Unit) // guard against double pay

            val amount = snapshot.get<Double>("amount") ?: 0.0

            // ── Mark transaction paid ────────────────────────────────
            transactionRef.update(mapOf("isPaid" to true))
            transactionRef.update(mapOf("date" to currentDate))

            // ── Update category totalPaid ────────────────────────────
            val categoryRef = firestore
                .collection("months")
                .document(monthId)
                .collection("categories")
                .document(categoryId)

            val categorySnapshot = categoryRef.get()
            val currentTotalPaid = categorySnapshot.get<Double>("totalPaid") ?: 0.0
            categoryRef.update(mapOf("totalPaid" to currentTotalPaid + amount))

            // ── Update month moneyOut ────────────────────────────────
            val monthRef = firestore.collection("months").document(monthId)
            val monthSnapshot = monthRef.get()
            val currentMoneyOut = monthSnapshot.get<Double>("moneyOut") ?: 0.0
            monthRef.update(mapOf("moneyOut" to currentMoneyOut + amount))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unpayTransaction(
        monthId: String,
        categoryId: String,
        transactionId: String
    ): Result<Unit> {
        return try {
            val transactionRef = firestore
                .collection("months")
                .document(monthId)
                .collection("categories")
                .document(categoryId)
                .collection("transactions")
                .document(transactionId)

            val snapshot = transactionRef.get()
            val isPaid = snapshot.get<Boolean>("isPaid") ?: false
            if (!isPaid) return Result.success(Unit) // already unpaid

            val amount = snapshot.get<Double>("amount") ?: 0.0

            transactionRef.update(mapOf("isPaid" to false))
            reversePayment(monthId, categoryId, amount)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Private helpers ──────────────────────────────────────────

    private suspend fun reversePayment(
        monthId: String,
        categoryId: String,
        amount: Double
    ) {
        val categoryRef = firestore
            .collection("months")
            .document(monthId)
            .collection("categories")
            .document(categoryId)

        val categorySnapshot = categoryRef.get()
        val currentTotalPaid = categorySnapshot.get<Double>("totalPaid") ?: 0.0
        categoryRef.update(
            mapOf("totalPaid" to (currentTotalPaid - amount).coerceAtLeast(0.0))
        )

        val monthRef = firestore.collection("months").document(monthId)
        val monthSnapshot = monthRef.get()
        val currentMoneyOut = monthSnapshot.get<Double>("moneyOut") ?: 0.0
        monthRef.update(
            mapOf("moneyOut" to (currentMoneyOut - amount).coerceAtLeast(0.0))
        )
    }
}