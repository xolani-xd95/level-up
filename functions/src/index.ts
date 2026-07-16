import * as admin from "firebase-admin";
import { onDocumentUpdated } from "firebase-functions/v2/firestore";
import Anthropic from "@anthropic-ai/sdk";

// ── Init ──────────────────────────────────────────────────────────
admin.initializeApp();
const db = admin.firestore();

// ── Types ─────────────────────────────────────────────────────────
interface Category {
  name: string;
  budget: number;
  totalPaid: number;
}

interface Month {
  monthIndex: number;
  year: number;
  moneyIn: number;
  moneyOut: number;
  totalBudget: number;
}

interface AgentInsight {
  message: string;
  severity: "info" | "warning" | "critical";
  categoryName: string | null;
  generatedAt: string;
}

// ── Trigger ───────────────────────────────────────────────────────
export const onTransactionPaid = onDocumentUpdated(
  "months/{monthId}/categories/{categoryId}/transactions/{transactionId}",
  async (event) => {
    const before = event.data?.before?.data();
    const after = event.data?.after?.data();

    // Only fire when isPaid flips false → true
    if (!before || !after) return;
    if (before.isPaid === true || after.isPaid !== true) return;

    const { monthId } = event.params;

    try {
      // ── 1. Read month document ───────────────────────────────────
      const monthSnap = await db
        .collection("months")
        .doc(monthId)
        .get();

      if (!monthSnap.exists) {
        console.log(`Month ${monthId} not found`);
        return;
      }

      const month = monthSnap.data() as Month;

      // ── 2. Read all categories ───────────────────────────────────
      const categoriesSnap = await db
        .collection("months")
        .doc(monthId)
        .collection("categories")
        .get();

      const categories: (Category & { id: string })[] = categoriesSnap.docs.map(
        (doc) => ({ id: doc.id, ...(doc.data() as Category) })
      );

      // ── 3. Build prompt ──────────────────────────────────────────
      const prompt = buildPrompt(month, categories, monthId);

      // ── 4. Call Anthropic ────────────────────────────────────────
      const client = new Anthropic({
        apiKey: process.env.ANTHROPIC_API_KEY,
      });

      const response = await client.messages.create({
        model: "claude-sonnet-4-5",
        max_tokens: 300,
        messages: [{ role: "user", content: prompt }],
      });

      // ── 5. Parse response ────────────────────────────────────────
      const rawText = response.content
        .filter((b) => b.type === "text")
        .map((b) => (b as { type: "text"; text: string }).text)
        .join("");

      const insight = parseInsight(rawText);

      // ── 6. Write insight to Firestore ────────────────────────────
      await db
        .collection("months")
        .doc(monthId)
        .collection("agentInsight")
        .doc("latest")
        .set(insight);

      console.log(`✅ Insight written for month ${monthId}:`, insight);
    } catch (err) {
      console.error("❌ Budget agent error:", err);
    }
  }
);

// ── Prompt Builder ────────────────────────────────────────────────
function buildPrompt(
  month: Month,
  categories: (Category & { id: string })[],
  monthId: string
): string {
  const monthNames = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
  ];

  const monthName = monthNames[month.monthIndex] ?? monthId;
  const remaining = month.moneyIn - month.moneyOut;
  const spendPercent =
    month.moneyIn > 0
      ? Math.round((month.moneyOut / month.moneyIn) * 100)
      : 0;

  const categoryLines = categories
    .map((c) => {
      const percent =
        c.budget > 0 ? Math.round((c.totalPaid / c.budget) * 100) : 0;
      const status =
        percent >= 100
          ? "OVER BUDGET"
          : percent >= 80
          ? "near limit"
          : "on track";
      return `- ${c.name}: budgeted R${c.budget}, spent R${c.totalPaid} (${percent}% — ${status})`;
    })
    .join("\n");

  return `
You are a supportive personal finance assistant. Respond ONLY with a JSON object, no explanation, no markdown.

The user's budget for ${monthName} ${month.year}:
- Total income: R${month.moneyIn}
- Total spent: R${month.moneyOut} (${spendPercent}% of income)
- Remaining: R${remaining}
- Total budgeted across categories: R${month.totalBudget}

Category breakdown:
${categoryLines}

Give the user one clear, encouraging insight. Focus on the most important thing — a warning about overspending, a positive reinforcement if they're doing well, or a heads up about a category near its limit.

Respond with ONLY this JSON structure:
{
  "message": "your message here (max 2 sentences, warm and supportive tone)",
  "severity": "info" or "warning" or "critical",
  "categoryName": "Category Name" or null
}

Rules:
- severity is "critical" if any category is over budget
- severity is "warning" if any category is above 80% of its budget
- severity is "info" if everything is on track
- categoryName is the name of the most concerning category, or null if the insight is general
- Keep it human, warm, never robotic
`.trim();
}

// ── Response Parser ───────────────────────────────────────────────
function parseInsight(raw: string): AgentInsight {
  try {
    const cleaned = raw.replace(/```json|```/g, "").trim();
    const parsed = JSON.parse(cleaned);

    return {
      message: parsed.message ?? "Keep going, you're doing great!",
      severity: parsed.severity ?? "info",
      categoryName: parsed.categoryName ?? null,
      generatedAt: new Date().toISOString(),
    };
  } catch {
    return {
      message: "Your spending is being reviewed. Keep tracking those expenses!",
      severity: "info",
      categoryName: null,
      generatedAt: new Date().toISOString(),
    };
  }
}
