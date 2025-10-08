package com.dyaco.spirit_commercial.support.room.fitness_test;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.dyaco.spirit_commercial.support.room.Converters;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableAirForce;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableArmy;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableCoastGuard;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableFacility;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableFacilityIndex;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableFrontGradeAd;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableMarines;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableMets;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableNavy;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TablePeb;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableRearGradeAd;
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableSettings;


@Database(entities = {
        TableAirForce.class,
        TableArmy.class,
        TableCoastGuard.class,
        TableMarines.class,
        TableMets.class,
        TableNavy.class,
        TablePeb.class,
        TableSettings.class,
        TableFacility.class,
        TableFrontGradeAd.class,
        TableRearGradeAd.class,
        TableFacilityIndex.class},
        version = 1)
@TypeConverters({Converters.class})
public abstract class FitnessTestDatabase extends RoomDatabase {

    public abstract FitnessTestDao fitnessTestDao();

//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE expense "
//                    + "ADD COLUMN itemPic BLOB");
//        }
//    };

//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemName TEXT");
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemNo TEXT");
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemQuantity INTEGER NOT NULL DEFAULT 0");
//            //當添加int 類型數據時，需要添加默認值
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemPrice INTEGER NOT NULL DEFAULT 0");
//        }
//    };

//    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            //當添加int 類型數據時，需要添加默認值
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemTotal INTEGER NOT NULL DEFAULT 0");
//        }
//    };
}
