package com.dyaco.spirit_commercial.model.webapi.bean;

import java.util.List;

public class CreateWorkoutParam {

    private String uniqueExerciseId;
    private Long startTimestamp;
    private Long endTimestamp;
    private String timezone;
    private Integer frequency;
    private String exerciseName;
    private Integer kiloCalories;
    private Integer averageHeartRate;
    private Long trainingPlanId;
    private Double averagePace;
    private Long trainingPlanExerciseId;
    private MetadataDTO metadata;
    private List<IntervalsDTO> intervals;

    public String getUniqueExerciseId() {
        return uniqueExerciseId;
    }

    public void setUniqueExerciseId(String uniqueExerciseId) {
        this.uniqueExerciseId = uniqueExerciseId;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getKiloCalories() {
        return kiloCalories;
    }

    public void setKiloCalories(Integer kiloCalories) {
        this.kiloCalories = kiloCalories;
    }

    public Integer getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(Integer averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public Long getTrainingPlanId() {
        return trainingPlanId;
    }

    public void setTrainingPlanId(Long trainingPlanId) {
        this.trainingPlanId = trainingPlanId;
    }

    public Double getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(Double averagePace) {
        this.averagePace = averagePace;
    }

    public Long getTrainingPlanExerciseId() {
        return trainingPlanExerciseId;
    }

    public void setTrainingPlanExerciseId(Long trainingPlanExerciseId) {
        this.trainingPlanExerciseId = trainingPlanExerciseId;
    }

    public MetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataDTO metadata) {
        this.metadata = metadata;
    }

    public List<IntervalsDTO> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<IntervalsDTO> intervals) {
        this.intervals = intervals;
    }

    public static class MetadataDTO {
        private String property1;
        private String property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }
    }

    public static class IntervalsDTO {
        private Integer rampAngle;
        private Integer resistance;
        private Double speed;
        private Integer duration;
        private Double distance;
        private Integer heartRate;
        private Double kiloCalories;
        private Integer stepsPerMinute;
        private Integer steps;
        private Integer stepHeight;
        private Integer strideLengthZone;
        private Integer rotations;
        private Double incline;
        private Integer floors;
        private Integer watts;

        public Integer getRampAngle() {
            return rampAngle;
        }

        public void setRampAngle(Integer rampAngle) {
            this.rampAngle = rampAngle;
        }

        public Integer getResistance() {
            return resistance;
        }

        public void setResistance(Integer resistance) {
            this.resistance = resistance;
        }

        public Double getSpeed() {
            return speed;
        }

        public void setSpeed(Double speed) {
            this.speed = speed;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Integer getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(Integer heartRate) {
            this.heartRate = heartRate;
        }

        public Double getKiloCalories() {
            return kiloCalories;
        }

        public void setKiloCalories(Double kiloCalories) {
            this.kiloCalories = kiloCalories;
        }

        public Integer getStepsPerMinute() {
            return stepsPerMinute;
        }

        public void setStepsPerMinute(Integer stepsPerMinute) {
            this.stepsPerMinute = stepsPerMinute;
        }

        public Integer getSteps() {
            return steps;
        }

        public void setSteps(Integer steps) {
            this.steps = steps;
        }

        public Integer getStepHeight() {
            return stepHeight;
        }

        public void setStepHeight(Integer stepHeight) {
            this.stepHeight = stepHeight;
        }

        public Integer getStrideLengthZone() {
            return strideLengthZone;
        }

        public void setStrideLengthZone(Integer strideLengthZone) {
            this.strideLengthZone = strideLengthZone;
        }

        public Integer getRotations() {
            return rotations;
        }

        public void setRotations(Integer rotations) {
            this.rotations = rotations;
        }

        public Double getIncline() {
            return incline;
        }

        public void setIncline(Double incline) {
            this.incline = incline;
        }

        public Integer getFloors() {
            return floors;
        }

        public void setFloors(Integer floors) {
            this.floors = floors;
        }

        public Integer getWatts() {
            return watts;
        }

        public void setWatts(Integer watts) {
            this.watts = watts;
        }
    }
}
