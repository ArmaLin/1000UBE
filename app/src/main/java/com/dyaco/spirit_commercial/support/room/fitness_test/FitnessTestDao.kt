package com.dyaco.spirit_commercial.support.room.fitness_test

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface FitnessTestDao {

    // --- 查詢 (Queries) ---

    @Query("SELECT * FROM tableMets WHERE mets = :mets")
    suspend fun getMets(mets: Int): TableMets?

    @Query("SELECT * FROM tableMarines WHERE gender = :gender AND time_le >= :time_le ORDER BY time_le LIMIT 1")
    suspend fun getMarines(gender: Int, time_le: Int): TableMarines?

    @Query("SELECT * FROM tableAirForce WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_min <= :time AND  time_max >= :time LIMIT 1")
    suspend fun getAirForce(gender: Int, age: Int, time: Int): TableAirForce?

    @Query("SELECT * FROM tableAirForce WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND category = 2 ORDER BY time_max DESC LIMIT 1")
    suspend fun getAirForcePassTime(gender: Int, age: Int): TableAirForce?

    @Query("SELECT * FROM tableNavy WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_le >= :time ORDER BY time_le LIMIT 1")
    suspend fun getNavy(gender: Int, age: Int, time: Int): TableNavy?

    @Query("SELECT * FROM tableNavy WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND points = 45")
    suspend fun getNavyPassTime(gender: Int, age: Int): TableNavy?

    @Query("SELECT * FROM tableArmy WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_le >= :time ORDER BY time_le LIMIT 1")
    suspend fun getArmy(gender: Int, age: Int, time: Int): TableArmy?

    @Query("SELECT * FROM tableArmy WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND points = 50")
    suspend fun getArmyPassTime(gender: Int, age: Int): TableArmy?

    @Query("SELECT * FROM tableCoastGuard WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_min <= :time AND  time_max >= :time LIMIT 1")
    suspend fun getCoastGuard(gender: Int, age: Int, time: Int): TableCoastGuard?

    @Query("SELECT * FROM tablePeb WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_le >= :time ORDER BY time_le LIMIT 1")
    suspend fun getPeb(gender: Int, age: Int, time: Int): TablePeb?

    @Query("SELECT * FROM tablePeb WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND percentiles = 75 ORDER BY time_le DESC LIMIT 1")
    suspend fun getPebPassTime(gender: Int, age: Int): TablePeb?

    @Query("SELECT * FROM tableSettings")
    suspend fun getSettings(): TableSettings? // 同步改 suspend

    @Query("SELECT * FROM tableFacility WHERE profile_id = :profileId ORDER BY profile_type, profile_index ASC")
    suspend fun getFacilityByProfileId(profileId: Int): List<TableFacility>

    @Query("SELECT * FROM tablefacilityindex WHERE profile_remove != 0 ORDER BY profile_alias ASC")
    suspend fun getFacilityIndexValidList(): List<TableFacilityIndex>

    @Query("SELECT * FROM tablefacilityindex WHERE profile_id = :profileId")
    suspend fun getFacilityIndexByProfileId(profileId: Int): List<TableFacilityIndex>

    @Query("SELECT * FROM tableAirForce")
    suspend fun getAirForceAll(): List<TableAirForce>?

    @Query("SELECT * FROM tablefacilityindex WHERE profile_remove == 1 ORDER BY profile_id LIMIT 1")
    suspend fun getFacilityIndexAvailable(): List<TableFacilityIndex>

    @Query("SELECT * FROM tableFrontGradeAd ORDER BY grade ASC")
    suspend fun getFrontGradeAd(): List<TableFrontGradeAd>

    @Query("SELECT * FROM tableRearGradeAd ORDER BY grade DESC")
    suspend fun getRearGradeAd(): List<TableRearGradeAd>

    // --- 更新 (Updates) ---

    @Update
    suspend fun updateSettings(vararg tableSettings: TableSettings): Int

    @Update
    suspend fun updateFacility(tableFacilities: List<TableFacility>): Int

    @Update
    suspend fun updateFrontGradeAd(tableFrontGradeAd: List<TableFrontGradeAd>): Int

    @Update
    suspend fun updateRearGradeAd(tableRearGradeAd: List<TableRearGradeAd>): Int
}