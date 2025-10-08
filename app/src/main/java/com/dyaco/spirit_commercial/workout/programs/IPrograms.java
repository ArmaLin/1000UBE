package com.dyaco.spirit_commercial.workout.programs;

import com.dyaco.spirit_commercial.workout.WorkoutChartsFragment;
import com.dyaco.spirit_commercial.workout.WorkoutStatsFragment;

public interface IPrograms {

    /**
     * Program初始化
     */
    void initChart(WorkoutChartsFragment workoutChartsFragment);

    void initStats(WorkoutStatsFragment workoutStatsFragment);

    void init();

    void warmUp(long time);

    void calcDistance(double distanceAccumulate);
    /**
     * Workout執行期間
     */
    void runTime();

    void out();

    void target();

    void coolDown(long time);

    void cancelTimer();
}
