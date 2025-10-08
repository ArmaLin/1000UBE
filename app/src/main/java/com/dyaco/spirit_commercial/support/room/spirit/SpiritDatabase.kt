package com.dyaco.spirit_commercial.support.room.spirit;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dyaco.spirit_commercial.support.room.Converters;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.DeviceEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.EgymEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.ErrorMsgEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.RankEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UploadWorkoutDataEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.UserProfileEntity;

//@Database(entities = {UserProfileEntity.class, ErrorMsgEntity.class, RankEntity.class, DeviceEntity.class, UploadWorkoutDataEntity.class},
//        version = 2)

@Database(entities = {UserProfileEntity.class, ErrorMsgEntity.class,
        RankEntity.class, DeviceEntity.class,
        UploadWorkoutDataEntity.class, MediaAppsEntity.class, EgymEntity.class},
        version = 5)
@TypeConverters({Converters.class})
public abstract class SpiritDatabase extends RoomDatabase {
    public abstract SpiritDao spiritDao();

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE IF NOT EXISTS `upload_workout_data` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dataJson` TEXT, `isUploaded` INTEGER NOT NULL)");


//            database.execSQL("CREATE TABLE IF NOT EXISTS `upload_workout_data` (`uid` INTEGER NOT NULL, "
//                    + "`dataJson` TEXT, `isUploaded` INTEGER NOT NULL DEFAULT(0), PRIMARY KEY(`uid`))");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE IF NOT EXISTS `media_apps_entity` " +
                    "(`packageName` TEXT PRIMARY KEY NOT NULL DEFAULT '', " +
                    "`appName` TEXT, " +
                    "`mediaType` TEXT, " +
                    "`version` TEXT, " +
                    "`comment` TEXT, " +
                    "`gmsNeeded` TEXT, " +
                    "`forceUpdates` TEXT, " +
                    "`md5` TEXT, " +
                    "`downloadUrl` TEXT, " +
                    "`path` TEXT, " +
                    "`webUrl` TEXT, " +
                    "`appIconS` BLOB, " +
                    "`appIconM` BLOB, " +
                    "`appIconL` BLOB, " +
                    "`isUpdate` INTEGER NOT NULL, " +
                    "`versionCode` INTEGER NOT NULL DEFAULT 0, " +
                    "`sort` INTEGER NOT NULL DEFAULT 0, " +
                    "`updateTime` INTEGER NOT NULL)");

//            database.execSQL("CREATE TABLE IF NOT EXISTS `media_apps_entity` " +
//                    "(`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
//                    "`appName` TEXT, " +
//                    "`packageName` TEXT, " +
//                    "`mediaType` TEXT, " +
//                    "`version` TEXT, " +
//                    "`comment` TEXT, " +
//                    "`gmsNeeded` TEXT, " +
//                    "`forceUpdates` TEXT, " +
//                    "`md5` TEXT, " +
//                    "`downloadUrl` TEXT, " +
//                    "`path` TEXT, " +
//                    "`webUrl` TEXT, " +
//                    "`appIconS` BLOB, " +
//                    "`appIconM` BLOB, " +
//                    "`appIconL` BLOB, " +
//                    "`versionCode` INTEGER NOT NULL DEFAULT 0, " +
//                    "`updateTime` INTEGER NOT NULL)");
//
        }
    };



//    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//            database.execSQL("CREATE TABLE IF NOT EXISTS `EGYM_TABLE` " +
//                    "(`uid` INTEGER PRIMARY KEY NOT NULL, " +
//                    "`workoutJson` TEXT, " +
//                    "`updateTime` INTEGER NOT NULL)");
//        }
//    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `EGYM_TABLE` (" +
                    "`uid` INTEGER PRIMARY KEY NOT NULL, " +
                    "`workoutJson` TEXT, " +
                    "`isUploaded` INTEGER NOT NULL DEFAULT 0, " +
                    "`updateTime` INTEGER NOT NULL)");

            database.execSQL("CREATE INDEX IF NOT EXISTS `index_EGYM_TABLE_uid` ON `EGYM_TABLE` (`uid`)");
        }
    };


}
