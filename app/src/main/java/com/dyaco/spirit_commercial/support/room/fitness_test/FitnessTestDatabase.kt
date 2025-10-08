package com.dyaco.spirit_commercial.support.room.fitness_test

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dyaco.spirit_commercial.support.room.Converters
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableAirForce
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableArmy
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableCoastGuard
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableFacility
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableFacilityIndex
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableFrontGradeAd
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableMarines
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableMets
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableNavy
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TablePeb
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableRearGradeAd
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableSettings

@Database(
    entities = [
        TableAirForce::class,
        TableArmy::class,
        TableCoastGuard::class,
        TableMarines::class,
        TableMets::class,
        TableNavy::class,
        TablePeb::class,
        TableSettings::class,
        TableFacility::class,
        TableFrontGradeAd::class,
        TableRearGradeAd::class,
        TableFacilityIndex::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class FitnessTestDatabase : RoomDatabase() {

    abstract fun fitnessTestDao(): FitnessTestDao

    // Migrations can be kept here if needed in the future
    // companion object {
    //     val MIGRATION_1_2 = object : Migration(1, 2) { ... }
    // }
}