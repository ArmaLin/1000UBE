package com.dyaco.spirit_commercial.support.room.fitness_test;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

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

import java.util.List;

import io.reactivex.Maybe;


@Dao
public interface FitnessTestDao {
//RxJava2 features, you must add `rxjava2` artifact from Room as a dependency. androidx.room:room-rxjava2:<version>
    @Query("SELECT * FROM tableMets WHERE mets = :mets")
    Maybe<TableMets> getMets(int mets);

    @Query("SELECT * FROM tableMarines WHERE gender = :gender AND time_le >= :time_le ORDER BY time_le LIMIT 1")
    Maybe<TableMarines> getMarines(int gender, int time_le);

    @Query("SELECT * FROM tableAirForce WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_min <= :time AND  time_max >= :time LIMIT 1")
    Maybe<TableAirForce> getAirForce(int gender, int age, int time);

    @Query("SELECT * FROM tableAirForce WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND category = 2 ORDER BY time_max DESC LIMIT 1")
    Maybe<TableAirForce> getAirForcePassTime(int gender, int age);

    @Query("SELECT * FROM tableNavy WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_le >= :time ORDER BY time_le LIMIT 1")
    Maybe<TableNavy> getNavy(int gender, int age, int time);

    @Query("SELECT * FROM tableNavy WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND points = 45")
    Maybe<TableNavy> getNavyPassTime(int gender, int age);

    @Query("SELECT * FROM tableArmy WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_le >= :time ORDER BY time_le LIMIT 1")
    Maybe<TableArmy> getArmy(int gender, int age, int time);

    @Query("SELECT * FROM tableArmy WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND points = 50")
    Maybe<TableArmy> getArmyPassTime(int gender, int age);

    @Query("SELECT * FROM tableCoastGuard WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_min <= :time AND  time_max >= :time LIMIT 1")
    Maybe<TableCoastGuard> getCoastGuard(int gender, int age, int time);

    @Query("SELECT * FROM tablePeb WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND time_le >= :time ORDER BY time_le LIMIT 1")
    Maybe<TablePeb> getPeb(int gender, int age, int time);

    @Query("SELECT * FROM tablePeb WHERE gender = :gender AND age_min <= :age AND age_max >= :age AND percentiles = 75 ORDER BY time_le DESC LIMIT 1")
    Maybe<TablePeb> getPebPassTime(int gender, int age);

    @Query("SELECT * FROM tableSettings")
    TableSettings getSettings();

    @Query("SELECT * FROM tableFacility WHERE profile_id = :profileId ORDER BY profile_type, profile_index ASC")
    List<TableFacility> getFacilityByProfileId(int profileId);

    @Query("SELECT * FROM tablefacilityindex WHERE profile_remove != 0 ORDER BY profile_alias ASC")
    List<TableFacilityIndex> getFacilityIndexValidList();

    @Query("SELECT * FROM tablefacilityindex WHERE profile_id = :profileId")
    List<TableFacilityIndex> getFacilityIndexByProfileId(int profileId);

    @Query("SELECT * FROM tableAirForce ")
    Maybe<List<TableAirForce>> getAirForceAll();

    @Query("SELECT * FROM tablefacilityindex WHERE profile_remove == 1 ORDER BY profile_id LIMIT 1")
    List<TableFacilityIndex> getFacilityIndexAvailable();

    @Query("SELECT * FROM tableFrontGradeAd ORDER BY grade ASC")
    List<TableFrontGradeAd> getFrontGradeAd();

    @Query("SELECT * FROM tableRearGradeAd ORDER BY grade DESC")
    List<TableRearGradeAd> getRearGradeAd();

    @Update
    Integer updateSettings(TableSettings... tableSettings);

    @Update
    Integer updateFacility(List<TableFacility> tableFacilities);

    @Update
    Integer updateFrontGradeAd(List<TableFrontGradeAd> tableFrontGradeAd);

    @Update
    Integer updateRearGradeAd(List<TableRearGradeAd> tableRearGradeAd);
}
