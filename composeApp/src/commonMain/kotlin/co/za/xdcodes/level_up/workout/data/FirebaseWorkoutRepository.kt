package co.za.xdcodes.level_up.workout.data

import co.za.xdcodes.level_up.workout.domain.WorkoutRepository
import co.za.xdcodes.level_up.workout.domain.dto.ExerciseCategory
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutExercise
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutSetModel
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutWithSets
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi

class FirebaseWorkoutRepository(
    private val firestore: FirebaseFirestore
) : WorkoutRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getExerciseByMuscle(muscle: String): List<WorkoutExercise> {
        val snapshot = firestore
            .collection("exerciseLibrary")
            .document(muscle)
            .collection("exercises")
            .get()

        return snapshot.documents.map { exercise ->
            WorkoutExercise(
                exerciseId = exercise.id,
                name = exercise.get<String>("name"),
                category = exercise.get<ExerciseCategory>("category"),
                equipment = exercise.get<String>("equipment")
            )
        }
    }

    override suspend fun createWorkout(
        schedule: List<LocalDate>,
        exercises: List<WorkoutExercise>
    ) {
        val firestore = firestore

        schedule.forEach { date ->
            val dayRef = firestore.collection("workoutDays")
                .document(date.toString())

            dayRef.set(
                mapOf(
                    "date" to date,
                    "dayOfWeek" to date.dayOfWeek.toString(),
                    "isCompleted" to false
                )
            )

            exercises.forEachIndexed { index, exercise ->
                val workoutRef = dayRef
                    .collection("workouts")
                    .document(exercise.exerciseId)

                workoutRef.set(
                    mapOf(
                        "id" to exercise.exerciseId,
                        "name" to exercise.name,
                        "category" to exercise.category.name,
                        "order" to index
                    )
                )

                repeat(exercise.sets) { setIndex ->
                    workoutRef.collection("sets")
                        .document("set_${setIndex + 1}")
                        .set(
                            mapOf(
                                "setNumber" to setIndex + 1,
                                "weight" to 0,
                                "reps" to 0,
                                "isCompleted" to false
                            )
                        )
                }
            }
        }
    }

    override suspend fun getWorkoutForToday(date: String): List<WorkoutWithSets> {
        val workoutSnapshot = firestore.collection("workoutDays")
            .document(date)
            .collection("workouts")
            .get()

        return workoutSnapshot.documents.map { workoutDoc ->
            val setsSnapshot = workoutDoc.reference
                .collection("sets")
                .get()

            val sets = setsSnapshot.documents.map { setDoc ->

                WorkoutSetModel(
                    id = setDoc.id,
                    setNumber = setDoc.get<Int>("setNumber"),
                    weight = setDoc.get<Int>("weight"),
                    reps = setDoc.get<Int>("reps"),
                    isComplete = setDoc.get("isCompleted") ?: false
                )
            }.sortedBy { it.setNumber }

            WorkoutWithSets(
                workoutId = workoutDoc.id,
                name = workoutDoc.get<String>("name"),
                category = ExerciseCategory.valueOf(
                    workoutDoc.get<String>("category")
                ),
                isCompleted = workoutDoc.get("isCompleted") ?: false,
                sets = sets
            )
        }
    }

    override suspend fun completeWorkoutSet(date: String, workoutId: String, set: WorkoutSetModel) {
        firestore
            .collection("workoutDays")
            .document(date)
            .collection("workouts")
            .document(workoutId)
            .collection("sets")
            .document(set.id)
            .update(
                mapOf(
                    "reps" to set.reps,
                    "weight" to set.weight,
                    "isCompleted" to set.isComplete
                )
            )
    }
}