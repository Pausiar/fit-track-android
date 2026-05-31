package com.pausiar.fit.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pausiar.fit.data.local.entity.ExerciseEntity;
import com.pausiar.fit.data.local.entity.WorkoutDayEntity;
import com.pausiar.fit.data.local.relation.DayWithExercises;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WorkoutDao_Impl implements WorkoutDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutDayEntity> __insertionAdapterOfWorkoutDayEntity;

  private final EntityInsertionAdapter<ExerciseEntity> __insertionAdapterOfExerciseEntity;

  private final EntityDeletionOrUpdateAdapter<ExerciseEntity> __deletionAdapterOfExerciseEntity;

  private final EntityDeletionOrUpdateAdapter<ExerciseEntity> __updateAdapterOfExerciseEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearExercises;

  private final SharedSQLiteStatement __preparedStmtOfClearDays;

  public WorkoutDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutDayEntity = new EntityInsertionAdapter<WorkoutDayEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workout_day` (`id`,`name`,`focus`,`dayOfWeek`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkoutDayEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getFocus());
        statement.bindLong(4, entity.getDayOfWeek());
      }
    };
    this.__insertionAdapterOfExerciseEntity = new EntityInsertionAdapter<ExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `exercise` (`id`,`dayId`,`name`,`muscleGroup`,`targetSets`,`targetReps`,`notes`,`orderIndex`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDayId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getMuscleGroup());
        statement.bindLong(5, entity.getTargetSets());
        statement.bindString(6, entity.getTargetReps());
        statement.bindString(7, entity.getNotes());
        statement.bindLong(8, entity.getOrderIndex());
      }
    };
    this.__deletionAdapterOfExerciseEntity = new EntityDeletionOrUpdateAdapter<ExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `exercise` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfExerciseEntity = new EntityDeletionOrUpdateAdapter<ExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `exercise` SET `id` = ?,`dayId` = ?,`name` = ?,`muscleGroup` = ?,`targetSets` = ?,`targetReps` = ?,`notes` = ?,`orderIndex` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDayId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getMuscleGroup());
        statement.bindLong(5, entity.getTargetSets());
        statement.bindString(6, entity.getTargetReps());
        statement.bindString(7, entity.getNotes());
        statement.bindLong(8, entity.getOrderIndex());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfClearExercises = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM exercise";
        return _query;
      }
    };
    this.__preparedStmtOfClearDays = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM workout_day";
        return _query;
      }
    };
  }

  @Override
  public Object insertDays(final List<WorkoutDayEntity> days,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkoutDayEntity.insert(days);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertExercises(final List<ExerciseEntity> exercises,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExerciseEntity.insert(exercises);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertExercise(final ExerciseEntity exercise,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfExerciseEntity.insertAndReturnId(exercise);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExercise(final ExerciseEntity exercise,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfExerciseEntity.handle(exercise);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateExercise(final ExerciseEntity exercise,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfExerciseEntity.handle(exercise);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateExercises(final List<ExerciseEntity> exercises,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfExerciseEntity.handleMultiple(exercises);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearExercises(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearExercises.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearExercises.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearDays(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDays.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearDays.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DayWithExercises>> observeDaysWithExercises() {
    final String _sql = "SELECT * FROM workout_day ORDER BY dayOfWeek";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"exercise",
        "workout_day"}, new Callable<List<DayWithExercises>>() {
      @Override
      @NonNull
      public List<DayWithExercises> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfFocus = CursorUtil.getColumnIndexOrThrow(_cursor, "focus");
            final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
            final LongSparseArray<ArrayList<ExerciseEntity>> _collectionExercises = new LongSparseArray<ArrayList<ExerciseEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionExercises.containsKey(_tmpKey)) {
                _collectionExercises.put(_tmpKey, new ArrayList<ExerciseEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipexerciseAscomPausiarFitDataLocalEntityExerciseEntity(_collectionExercises);
            final List<DayWithExercises> _result = new ArrayList<DayWithExercises>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final DayWithExercises _item;
              final WorkoutDayEntity _tmpDay;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpFocus;
              _tmpFocus = _cursor.getString(_cursorIndexOfFocus);
              final int _tmpDayOfWeek;
              _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
              _tmpDay = new WorkoutDayEntity(_tmpId,_tmpName,_tmpFocus,_tmpDayOfWeek);
              final ArrayList<ExerciseEntity> _tmpExercisesCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpExercisesCollection = _collectionExercises.get(_tmpKey_1);
              _item = new DayWithExercises(_tmpDay,_tmpExercisesCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<DayWithExercises> observeDayWithExercises(final int dayId) {
    final String _sql = "SELECT * FROM workout_day WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"exercise",
        "workout_day"}, new Callable<DayWithExercises>() {
      @Override
      @Nullable
      public DayWithExercises call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfFocus = CursorUtil.getColumnIndexOrThrow(_cursor, "focus");
            final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
            final LongSparseArray<ArrayList<ExerciseEntity>> _collectionExercises = new LongSparseArray<ArrayList<ExerciseEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionExercises.containsKey(_tmpKey)) {
                _collectionExercises.put(_tmpKey, new ArrayList<ExerciseEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipexerciseAscomPausiarFitDataLocalEntityExerciseEntity(_collectionExercises);
            final DayWithExercises _result;
            if (_cursor.moveToFirst()) {
              final WorkoutDayEntity _tmpDay;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpFocus;
              _tmpFocus = _cursor.getString(_cursorIndexOfFocus);
              final int _tmpDayOfWeek;
              _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
              _tmpDay = new WorkoutDayEntity(_tmpId,_tmpName,_tmpFocus,_tmpDayOfWeek);
              final ArrayList<ExerciseEntity> _tmpExercisesCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpExercisesCollection = _collectionExercises.get(_tmpKey_1);
              _result = new DayWithExercises(_tmpDay,_tmpExercisesCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object dayCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM workout_day";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object exercisesForDay(final int dayId,
      final Continuation<? super List<ExerciseEntity>> $completion) {
    final String _sql = "SELECT * FROM exercise WHERE dayId = ? ORDER BY orderIndex";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExerciseEntity>>() {
      @Override
      @NonNull
      public List<ExerciseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDayId = CursorUtil.getColumnIndexOrThrow(_cursor, "dayId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMuscleGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "muscleGroup");
          final int _cursorIndexOfTargetSets = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSets");
          final int _cursorIndexOfTargetReps = CursorUtil.getColumnIndexOrThrow(_cursor, "targetReps");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final List<ExerciseEntity> _result = new ArrayList<ExerciseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExerciseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpDayId;
            _tmpDayId = _cursor.getInt(_cursorIndexOfDayId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMuscleGroup;
            _tmpMuscleGroup = _cursor.getString(_cursorIndexOfMuscleGroup);
            final int _tmpTargetSets;
            _tmpTargetSets = _cursor.getInt(_cursorIndexOfTargetSets);
            final String _tmpTargetReps;
            _tmpTargetReps = _cursor.getString(_cursorIndexOfTargetReps);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new ExerciseEntity(_tmpId,_tmpDayId,_tmpName,_tmpMuscleGroup,_tmpTargetSets,_tmpTargetReps,_tmpNotes,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object allExercises(final Continuation<? super List<ExerciseEntity>> $completion) {
    final String _sql = "SELECT * FROM exercise ORDER BY dayId, orderIndex";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExerciseEntity>>() {
      @Override
      @NonNull
      public List<ExerciseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDayId = CursorUtil.getColumnIndexOrThrow(_cursor, "dayId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMuscleGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "muscleGroup");
          final int _cursorIndexOfTargetSets = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSets");
          final int _cursorIndexOfTargetReps = CursorUtil.getColumnIndexOrThrow(_cursor, "targetReps");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final List<ExerciseEntity> _result = new ArrayList<ExerciseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExerciseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpDayId;
            _tmpDayId = _cursor.getInt(_cursorIndexOfDayId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMuscleGroup;
            _tmpMuscleGroup = _cursor.getString(_cursorIndexOfMuscleGroup);
            final int _tmpTargetSets;
            _tmpTargetSets = _cursor.getInt(_cursorIndexOfTargetSets);
            final String _tmpTargetReps;
            _tmpTargetReps = _cursor.getString(_cursorIndexOfTargetReps);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new ExerciseEntity(_tmpId,_tmpDayId,_tmpName,_tmpMuscleGroup,_tmpTargetSets,_tmpTargetReps,_tmpNotes,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object allDays(final Continuation<? super List<WorkoutDayEntity>> $completion) {
    final String _sql = "SELECT * FROM workout_day ORDER BY dayOfWeek";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WorkoutDayEntity>>() {
      @Override
      @NonNull
      public List<WorkoutDayEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFocus = CursorUtil.getColumnIndexOrThrow(_cursor, "focus");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final List<WorkoutDayEntity> _result = new ArrayList<WorkoutDayEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutDayEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpFocus;
            _tmpFocus = _cursor.getString(_cursorIndexOfFocus);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            _item = new WorkoutDayEntity(_tmpId,_tmpName,_tmpFocus,_tmpDayOfWeek);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object maxOrder(final int dayId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT MAX(orderIndex) FROM exercise WHERE dayId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipexerciseAscomPausiarFitDataLocalEntityExerciseEntity(
      @NonNull final LongSparseArray<ArrayList<ExerciseEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipexerciseAscomPausiarFitDataLocalEntityExerciseEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`dayId`,`name`,`muscleGroup`,`targetSets`,`targetReps`,`notes`,`orderIndex` FROM `exercise` WHERE `dayId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "dayId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfDayId = 1;
      final int _cursorIndexOfName = 2;
      final int _cursorIndexOfMuscleGroup = 3;
      final int _cursorIndexOfTargetSets = 4;
      final int _cursorIndexOfTargetReps = 5;
      final int _cursorIndexOfNotes = 6;
      final int _cursorIndexOfOrderIndex = 7;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<ExerciseEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final ExerciseEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final int _tmpDayId;
          _tmpDayId = _cursor.getInt(_cursorIndexOfDayId);
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          final String _tmpMuscleGroup;
          _tmpMuscleGroup = _cursor.getString(_cursorIndexOfMuscleGroup);
          final int _tmpTargetSets;
          _tmpTargetSets = _cursor.getInt(_cursorIndexOfTargetSets);
          final String _tmpTargetReps;
          _tmpTargetReps = _cursor.getString(_cursorIndexOfTargetReps);
          final String _tmpNotes;
          _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
          final int _tmpOrderIndex;
          _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
          _item_1 = new ExerciseEntity(_tmpId,_tmpDayId,_tmpName,_tmpMuscleGroup,_tmpTargetSets,_tmpTargetReps,_tmpNotes,_tmpOrderIndex);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
