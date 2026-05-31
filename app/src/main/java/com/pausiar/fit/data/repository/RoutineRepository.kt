package com.pausiar.fit.data.repository

import com.pausiar.fit.data.local.SeedData
import com.pausiar.fit.data.local.dao.ProgressDao
import com.pausiar.fit.data.local.dao.WorkoutDao
import com.pausiar.fit.data.local.entity.ExerciseEntity
import com.pausiar.fit.data.local.entity.SetProgressEntity
import com.pausiar.fit.data.local.entity.WorkoutDayEntity
import com.pausiar.fit.data.local.relation.DayWithExercises
import com.pausiar.fit.domain.HistoryStats
import com.pausiar.fit.util.DateUtils
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/**
 * Single source of truth for routine + progress data. All persistence goes through here.
 */
class RoutineRepository(
    private val workoutDao: WorkoutDao,
    private val progressDao: ProgressDao
) {

    private data class ParsedRoutineDay(
        val day: WorkoutDayEntity,
        val exercises: List<ExerciseEntity>
    )

    private data class ParsedBackupDays(
        val days: List<WorkoutDayEntity>,
        val dayIdMap: Map<Int, Int>
    )

    // ---- Routine ----------------------------------------------------------

    fun observeDaysWithExercises(): Flow<List<DayWithExercises>> =
        workoutDao.observeDaysWithExercises()

    fun observeDay(dayId: Int): Flow<DayWithExercises?> =
        workoutDao.observeDayWithExercises(dayId)

    /** Seeds the initial routine the first time the app runs. */
    suspend fun ensureSeeded() {
        if (workoutDao.dayCount() == 0) {
            workoutDao.insertDays(SeedData.days)
            workoutDao.insertExercises(SeedData.exercises)
        }
    }

    suspend fun addExercise(
        dayId: Int,
        name: String,
        muscleGroup: String,
        targetSets: Int,
        targetReps: String,
        notes: String = ""
    ) {
        val nextOrder = (workoutDao.maxOrder(dayId) ?: -1) + 1
        workoutDao.insertExercise(
            ExerciseEntity(
                dayId = dayId,
                name = name,
                muscleGroup = muscleGroup,
                targetSets = targetSets.coerceAtLeast(1),
                targetReps = targetReps,
                notes = notes,
                orderIndex = nextOrder
            )
        )
    }

    suspend fun updateExercise(exercise: ExerciseEntity) =
        workoutDao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: ExerciseEntity) =
        workoutDao.deleteExercise(exercise)

    /** Persists the new order after a drag/reorder. */
    suspend fun reorderExercises(ordered: List<ExerciseEntity>) {
        val reindexed = ordered.mapIndexed { index, e -> e.copy(orderIndex = index) }
        workoutDao.updateExercises(reindexed)
    }

    suspend fun saveNote(exercise: ExerciseEntity, note: String) =
        workoutDao.updateExercise(exercise.copy(notes = note))

    // ---- Progress ---------------------------------------------------------

    fun observeProgressForDate(dateIso: String): Flow<List<SetProgressEntity>> =
        progressDao.observeForDate(dateIso)

    fun observeProgressBetween(start: String, end: String): Flow<List<SetProgressEntity>> =
        progressDao.observeBetween(start, end)

    fun observeAllProgress(): Flow<List<SetProgressEntity>> =
        progressDao.observeAll()

    /** Toggles a single set on a date: inserts if missing, deletes if present. */
    suspend fun toggleSet(exerciseId: Long, dateIso: String, setIndex: Int) {
        val existing = progressDao.find(exerciseId, dateIso, setIndex)
        if (existing != null) {
            progressDao.deleteById(existing.id)
        } else {
            progressDao.insert(
                SetProgressEntity(
                    exerciseId = exerciseId,
                    date = dateIso,
                    setIndex = setIndex,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun resetExercise(exerciseId: Long, dateIso: String) =
        progressDao.deleteForExerciseOnDate(exerciseId, dateIso)

    suspend fun resetDate(dateIso: String) =
        progressDao.deleteForDate(dateIso)

    /** Resets the whole current week (Mon–Sun) without touching other weeks. */
    suspend fun resetWeek(reference: LocalDate = DateUtils.today()) {
        progressDao.deleteBetween(
            DateUtils.toIso(DateUtils.startOfWeek(reference)),
            DateUtils.toIso(DateUtils.endOfWeek(reference))
        )
    }

    suspend fun resetAllProgress() = progressDao.clearAll()

    /** Wipes everything and restores the default routine. */
    suspend fun resetAllData() {
        progressDao.clearAll()
        workoutDao.clearExercises()
        workoutDao.clearDays()
        workoutDao.insertDays(SeedData.days)
        workoutDao.insertExercises(SeedData.exercises)
    }

    // ---- Stats ------------------------------------------------------------

    /**
     * Computes streaks and totals from the raw progress rows. A day "counts" as
     * trained when it has at least one completed set.
     */
    fun buildStats(progress: List<SetProgressEntity>): HistoryStats {
        val datesTrained = progress.map { it.date }.toSortedSet()
        val localDates = datesTrained.map { DateUtils.parse(it) }.sorted()

        var best = 0
        var run = 0
        var previous: LocalDate? = null
        for (date in localDates) {
            run = if (previous != null && DateUtils.daysBetween(previous, date) == 1L) run + 1 else 1
            if (run > best) best = run
            previous = date
        }

        // Current streak counts consecutive days ending today or yesterday.
        val today = DateUtils.today()
        var current = 0
        var cursor = when {
            datesTrained.contains(DateUtils.toIso(today)) -> today
            datesTrained.contains(DateUtils.toIso(today.minusDays(1))) -> today.minusDays(1)
            else -> null
        }
        while (cursor != null && datesTrained.contains(DateUtils.toIso(cursor))) {
            current++
            cursor = cursor.minusDays(1)
        }

        return HistoryStats(
            totalCompletedSets = progress.size,
            completedDays = datesTrained.size,
            currentStreak = current,
            bestStreak = best
        )
    }

    // ---- Export / Import (local JSON) ------------------------------------

    suspend fun exportJson(): String {
        val days = workoutDao.allDays()
        val exercises = workoutDao.allExercises()
        val progress = progressDao.allProgress()

        val root = JSONObject()
        root.put("version", 1)
        root.put("type", "backup")
        root.put("exportedAt", System.currentTimeMillis())

        val daysArr = JSONArray()
        days.forEach { d ->
            daysArr.put(
                JSONObject()
                    .put("id", d.id)
                    .put("name", d.name)
                    .put("focus", d.focus)
                    .put("dayOfWeek", d.dayOfWeek)
            )
        }
        root.put("days", daysArr)

        val exArr = JSONArray()
        exercises.forEach { e ->
            exArr.put(
                JSONObject()
                    .put("id", e.id)
                    .put("dayId", e.dayId)
                    .put("name", e.name)
                    .put("muscleGroup", e.muscleGroup)
                    .put("targetSets", e.targetSets)
                    .put("targetReps", e.targetReps)
                    .put("notes", e.notes)
                    .put("orderIndex", e.orderIndex)
            )
        }
        root.put("exercises", exArr)

        val progArr = JSONArray()
        progress.forEach { p ->
            progArr.put(
                JSONObject()
                    .put("exerciseId", p.exerciseId)
                    .put("date", p.date)
                    .put("setIndex", p.setIndex)
                    .put("completedAt", p.completedAt)
            )
        }
        root.put("progress", progArr)

        return root.toString(2)
    }

    /** Replaces routine + progress with the contents of a backup JSON string. */
    suspend fun importJson(json: String) {
        val root = JSONObject(json)

        when {
            looksLikeRoutineJson(root) -> importRoutineJson(root)
            root.has("days") || root.has("exercises") || root.has("progress") -> importBackupJson(root)
            else -> error("Unsupported JSON format")
        }
    }

    private fun looksLikeRoutineJson(root: JSONObject): Boolean {
        if (root.optString("type").equals("routine", ignoreCase = true)) return true

        val daysArr = root.optJSONArray("days") ?: return false
        for (i in 0 until daysArr.length()) {
            val day = daysArr.optJSONObject(i) ?: continue
            if (day.has("exercises")) return true
        }
        return false
    }

    private suspend fun importRoutineJson(root: JSONObject) {
        val parsedDays = parseRoutineDays(root.optJSONArray("days"))
        require(parsedDays.isNotEmpty()) { "Routine JSON must contain at least one day" }

        clearRoutineAndProgress()
        workoutDao.insertDays(normalizeDays(parsedDays.map { it.day }))

        val exercises = parsedDays.flatMap { it.exercises }
        if (exercises.isNotEmpty()) {
            workoutDao.insertExercises(exercises)
        }
    }

    private suspend fun importBackupJson(root: JSONObject) {
        val daysArr = root.optJSONArray("days")
        val exArr = root.optJSONArray("exercises")
        val progArr = root.optJSONArray("progress")

        if ((daysArr == null || daysArr.length() == 0) && exArr == null && progArr == null) {
            error("Backup JSON is empty")
        }

        clearRoutineAndProgress()

        val parsedDays = parseBackupDays(daysArr)
        workoutDao.insertDays(parsedDays.days)

        if (exArr != null) {
            val parsed = (0 until exArr.length()).map { i ->
                val o = exArr.getJSONObject(i)
                val originalDayId = o.optInt("dayId", 1)
                ExerciseEntity(
                    id = o.optLong("id", 0L),
                    dayId = parsedDays.dayIdMap[originalDayId] ?: originalDayId.coerceIn(1, 7),
                    name = o.getString("name"),
                    muscleGroup = firstNonBlank(
                        o.optString("muscleGroup", ""),
                        o.optString("category", "")
                    ),
                    targetSets = o.optInt("targetSets", o.optInt("sets", 1)).coerceAtLeast(1),
                    targetReps = firstNonBlank(
                        o.optString("targetReps", ""),
                        o.optString("reps", ""),
                        o.optString("duration", "")
                    ),
                    notes = firstNonBlank(
                        o.optString("notes", ""),
                        o.optString("block", "")
                    ),
                    orderIndex = o.optInt("orderIndex", i)
                )
            }
            if (parsed.isNotEmpty()) {
                workoutDao.insertExercises(parsed)
            }
        }

        if (progArr != null) {
            val parsed = (0 until progArr.length()).map { i ->
                val o = progArr.getJSONObject(i)
                SetProgressEntity(
                    exerciseId = o.getLong("exerciseId"),
                    date = o.getString("date"),
                    setIndex = o.getInt("setIndex"),
                    completedAt = o.optLong("completedAt", System.currentTimeMillis())
                )
            }
            if (parsed.isNotEmpty()) {
                progressDao.insertAll(parsed)
            }
        }
    }

    private suspend fun clearRoutineAndProgress() {
        progressDao.clearAll()
        workoutDao.clearExercises()
        workoutDao.clearDays()
    }

    private fun parseRoutineDays(daysArr: JSONArray?): List<ParsedRoutineDay> {
        if (daysArr == null) return emptyList()

        val parsedByDay = linkedMapOf<Int, ParsedRoutineDay>()
        for (i in 0 until daysArr.length()) {
            val dayObj = daysArr.optJSONObject(i) ?: continue
            val dayOfWeek = dayObj.optInt("dayOfWeek", i + 1).coerceIn(1, 7)
            val dayName = firstNonBlank(dayObj.optString("name", ""), defaultDayName(dayOfWeek))
            val focus = firstNonBlank(dayObj.optString("focus", ""), dayName)
            val exercisesArr = dayObj.optJSONArray("exercises")

            val exercises = buildList {
                if (exercisesArr != null) {
                    for (exerciseIndex in 0 until exercisesArr.length()) {
                        val exerciseObj = exercisesArr.optJSONObject(exerciseIndex) ?: continue
                        val name = exerciseObj.optString("name", "").trim()
                        if (name.isBlank()) continue

                        add(
                            ExerciseEntity(
                                dayId = dayOfWeek,
                                name = name,
                                muscleGroup = firstNonBlank(
                                    exerciseObj.optString("muscleGroup", ""),
                                    exerciseObj.optString("category", ""),
                                    focus
                                ),
                                targetSets = exerciseObj
                                    .optInt("targetSets", exerciseObj.optInt("sets", 1))
                                    .coerceAtLeast(1),
                                targetReps = firstNonBlank(
                                    exerciseObj.optString("targetReps", ""),
                                    exerciseObj.optString("reps", ""),
                                    exerciseObj.optString("duration", "")
                                ),
                                notes = firstNonBlank(
                                    exerciseObj.optString("notes", ""),
                                    exerciseObj.optString("block", "")
                                ),
                                orderIndex = exerciseIndex
                            )
                        )
                    }
                }
            }

            parsedByDay[dayOfWeek] = ParsedRoutineDay(
                day = WorkoutDayEntity(
                    id = dayOfWeek,
                    name = dayName,
                    focus = focus,
                    dayOfWeek = dayOfWeek
                ),
                exercises = exercises
            )
        }

        return parsedByDay.values.sortedBy { it.day.dayOfWeek }
    }

    private fun parseBackupDays(daysArr: JSONArray?): ParsedBackupDays {
        if (daysArr == null || daysArr.length() == 0) {
            return ParsedBackupDays(SeedData.days, emptyMap())
        }

        val daysByWeek = linkedMapOf<Int, WorkoutDayEntity>()
        val dayIdMap = mutableMapOf<Int, Int>()

        for (i in 0 until daysArr.length()) {
            val dayObj = daysArr.optJSONObject(i) ?: continue
            val dayOfWeek = dayObj.optInt("dayOfWeek", i + 1).coerceIn(1, 7)
            val originalId = dayObj.optInt("id", dayOfWeek)
            dayIdMap[originalId] = dayOfWeek
            daysByWeek[dayOfWeek] = WorkoutDayEntity(
                id = dayOfWeek,
                name = firstNonBlank(dayObj.optString("name", ""), defaultDayName(dayOfWeek)),
                focus = firstNonBlank(dayObj.optString("focus", ""), defaultDayName(dayOfWeek)),
                dayOfWeek = dayOfWeek
            )
        }

        return ParsedBackupDays(normalizeDays(daysByWeek.values.toList()), dayIdMap)
    }

    private fun normalizeDays(days: List<WorkoutDayEntity>): List<WorkoutDayEntity> {
        val byWeekday = days.associateBy { it.dayOfWeek }
        return (1..7).map { dayOfWeek ->
            byWeekday[dayOfWeek] ?: WorkoutDayEntity(
                id = dayOfWeek,
                name = defaultDayName(dayOfWeek),
                focus = "Descanso",
                dayOfWeek = dayOfWeek
            )
        }
    }

    private fun defaultDayName(dayOfWeek: Int): String = when (dayOfWeek) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        7 -> "Domingo"
        else -> "Día"
    }

    private fun firstNonBlank(vararg values: String): String =
        values.firstOrNull { it.isNotBlank() }?.trim().orEmpty()
}
