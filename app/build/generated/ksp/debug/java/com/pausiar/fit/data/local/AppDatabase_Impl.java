package com.pausiar.fit.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.pausiar.fit.data.local.dao.ProgressDao;
import com.pausiar.fit.data.local.dao.ProgressDao_Impl;
import com.pausiar.fit.data.local.dao.WorkoutDao;
import com.pausiar.fit.data.local.dao.WorkoutDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile WorkoutDao _workoutDao;

  private volatile ProgressDao _progressDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `workout_day` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `focus` TEXT NOT NULL, `dayOfWeek` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exercise` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dayId` INTEGER NOT NULL, `name` TEXT NOT NULL, `muscleGroup` TEXT NOT NULL, `targetSets` INTEGER NOT NULL, `targetReps` TEXT NOT NULL, `notes` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, FOREIGN KEY(`dayId`) REFERENCES `workout_day`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_dayId` ON `exercise` (`dayId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `set_progress` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exerciseId` INTEGER NOT NULL, `date` TEXT NOT NULL, `setIndex` INTEGER NOT NULL, `completedAt` INTEGER NOT NULL, FOREIGN KEY(`exerciseId`) REFERENCES `exercise`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_set_progress_exerciseId_date_setIndex` ON `set_progress` (`exerciseId`, `date`, `setIndex`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_set_progress_date` ON `set_progress` (`date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a7693030807baf26826e2dfd23e91543')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `workout_day`");
        db.execSQL("DROP TABLE IF EXISTS `exercise`");
        db.execSQL("DROP TABLE IF EXISTS `set_progress`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsWorkoutDay = new HashMap<String, TableInfo.Column>(4);
        _columnsWorkoutDay.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutDay.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutDay.put("focus", new TableInfo.Column("focus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutDay.put("dayOfWeek", new TableInfo.Column("dayOfWeek", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutDay = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkoutDay = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkoutDay = new TableInfo("workout_day", _columnsWorkoutDay, _foreignKeysWorkoutDay, _indicesWorkoutDay);
        final TableInfo _existingWorkoutDay = TableInfo.read(db, "workout_day");
        if (!_infoWorkoutDay.equals(_existingWorkoutDay)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_day(com.pausiar.fit.data.local.entity.WorkoutDayEntity).\n"
                  + " Expected:\n" + _infoWorkoutDay + "\n"
                  + " Found:\n" + _existingWorkoutDay);
        }
        final HashMap<String, TableInfo.Column> _columnsExercise = new HashMap<String, TableInfo.Column>(8);
        _columnsExercise.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercise.put("dayId", new TableInfo.Column("dayId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercise.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercise.put("muscleGroup", new TableInfo.Column("muscleGroup", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercise.put("targetSets", new TableInfo.Column("targetSets", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercise.put("targetReps", new TableInfo.Column("targetReps", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercise.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercise.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExercise = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysExercise.add(new TableInfo.ForeignKey("workout_day", "CASCADE", "NO ACTION", Arrays.asList("dayId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesExercise = new HashSet<TableInfo.Index>(1);
        _indicesExercise.add(new TableInfo.Index("index_exercise_dayId", false, Arrays.asList("dayId"), Arrays.asList("ASC")));
        final TableInfo _infoExercise = new TableInfo("exercise", _columnsExercise, _foreignKeysExercise, _indicesExercise);
        final TableInfo _existingExercise = TableInfo.read(db, "exercise");
        if (!_infoExercise.equals(_existingExercise)) {
          return new RoomOpenHelper.ValidationResult(false, "exercise(com.pausiar.fit.data.local.entity.ExerciseEntity).\n"
                  + " Expected:\n" + _infoExercise + "\n"
                  + " Found:\n" + _existingExercise);
        }
        final HashMap<String, TableInfo.Column> _columnsSetProgress = new HashMap<String, TableInfo.Column>(5);
        _columnsSetProgress.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetProgress.put("exerciseId", new TableInfo.Column("exerciseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetProgress.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetProgress.put("setIndex", new TableInfo.Column("setIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetProgress.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSetProgress = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSetProgress.add(new TableInfo.ForeignKey("exercise", "CASCADE", "NO ACTION", Arrays.asList("exerciseId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSetProgress = new HashSet<TableInfo.Index>(2);
        _indicesSetProgress.add(new TableInfo.Index("index_set_progress_exerciseId_date_setIndex", true, Arrays.asList("exerciseId", "date", "setIndex"), Arrays.asList("ASC", "ASC", "ASC")));
        _indicesSetProgress.add(new TableInfo.Index("index_set_progress_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        final TableInfo _infoSetProgress = new TableInfo("set_progress", _columnsSetProgress, _foreignKeysSetProgress, _indicesSetProgress);
        final TableInfo _existingSetProgress = TableInfo.read(db, "set_progress");
        if (!_infoSetProgress.equals(_existingSetProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "set_progress(com.pausiar.fit.data.local.entity.SetProgressEntity).\n"
                  + " Expected:\n" + _infoSetProgress + "\n"
                  + " Found:\n" + _existingSetProgress);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "a7693030807baf26826e2dfd23e91543", "f7bd31700244f03eaf87e721dc4996ed");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "workout_day","exercise","set_progress");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `workout_day`");
      _db.execSQL("DELETE FROM `exercise`");
      _db.execSQL("DELETE FROM `set_progress`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(WorkoutDao.class, WorkoutDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProgressDao.class, ProgressDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public WorkoutDao workoutDao() {
    if (_workoutDao != null) {
      return _workoutDao;
    } else {
      synchronized(this) {
        if(_workoutDao == null) {
          _workoutDao = new WorkoutDao_Impl(this);
        }
        return _workoutDao;
      }
    }
  }

  @Override
  public ProgressDao progressDao() {
    if (_progressDao != null) {
      return _progressDao;
    } else {
      synchronized(this) {
        if(_progressDao == null) {
          _progressDao = new ProgressDao_Impl(this);
        }
        return _progressDao;
      }
    }
  }
}
