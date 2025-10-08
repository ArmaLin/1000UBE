package com.dyaco.spirit_commercial.model.webapi.bean;

import java.util.List;

public class EgymTrainingPlans {

    private List<TrainerDTO> trainer;
    private List<SmartDTO> smart;
    private List<GeniusDTO> genius;

    public List<TrainerDTO> getTrainer() {
        return trainer;
    }

    public void setTrainer(List<TrainerDTO> trainer) {
        this.trainer = trainer;
    }

    public List<SmartDTO> getSmart() {
        return smart;
    }

    public void setSmart(List<SmartDTO> smart) {
        this.smart = smart;
    }

    public List<GeniusDTO> getGenius() {
        return genius;
    }

    public void setGenius(List<GeniusDTO> genius) {
        this.genius = genius;
    }

    public static class TrainerDTO {
        private AuthorDTO author;
        private String exerciseName;
        private String sessionName;
        private Long startTimestamp;
        private Long endTimestamp;
        private Integer frequency;
        private String timezone;
        private Long trainingPlanId;
        private Long trainingPlanExerciseId;
        private Integer orderNumber;
        private List<IntervalsDTO> intervals;

        public AuthorDTO getAuthor() {
            return author;
        }

        public void setAuthor(AuthorDTO author) {
            this.author = author;
        }

        public String getExerciseName() {
            return exerciseName;
        }

        public void setExerciseName(String exerciseName) {
            this.exerciseName = exerciseName;
        }

        public String getSessionName() {
            return sessionName;
        }

        public void setSessionName(String sessionName) {
            this.sessionName = sessionName;
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

        public Integer getFrequency() {
            return frequency;
        }

        public void setFrequency(Integer frequency) {
            this.frequency = frequency;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public Long getTrainingPlanId() {
            return trainingPlanId;
        }

        public void setTrainingPlanId(Long trainingPlanId) {
            this.trainingPlanId = trainingPlanId;
        }

        public Long getTrainingPlanExerciseId() {
            return trainingPlanExerciseId;
        }

        public void setTrainingPlanExerciseId(Long trainingPlanExerciseId) {
            this.trainingPlanExerciseId = trainingPlanExerciseId;
        }

        public Integer getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
        }

        public List<IntervalsDTO> getIntervals() {
            return intervals;
        }

        public void setIntervals(List<IntervalsDTO> intervals) {
            this.intervals = intervals;
        }

        public static class AuthorDTO {
            private String userId;
            private String bmaUserId;
            private String firstName;
            private String lastName;
            private ImageDTO image;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getBmaUserId() {
                return bmaUserId;
            }

            public void setBmaUserId(String bmaUserId) {
                this.bmaUserId = bmaUserId;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public ImageDTO getImage() {
                return image;
            }

            public void setImage(ImageDTO image) {
                this.image = image;
            }

            public static class ImageDTO {
                private String imageType;
                private String imageId;

                public String getImageType() {
                    return imageType;
                }

                public void setImageType(String imageType) {
                    this.imageType = imageType;
                }

                public String getImageId() {
                    return imageId;
                }

                public void setImageId(String imageId) {
                    this.imageId = imageId;
                }
            }
        }

        public static class IntervalsDTO {
            private Integer rampAngle;
            private Integer resistance;
            private Double speed;
            private Integer duration;
            private Double distance;
            private Integer heartRate;
            private Integer kiloCalories;
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

            //-99 > 30分鐘
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

            public Integer getKiloCalories() {
                return kiloCalories;
            }

            public void setKiloCalories(Integer kiloCalories) {
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

    public static class SmartDTO {
        private Long startTimestamp;
        private Integer endTimestamp;
        private Integer frequency;
        private String timezone;
        private Long trainingPlanId;
        private Long trainingPlanExerciseId;
        private String smartExerciseName;
        private String intensity;
        private String smartTrainingMethod;
        private String trainingGoal;
        private String trainingProgram;
        private List<IntervalsDTO> intervals;

        public Long getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(Long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        public Integer getEndTimestamp() {
            return endTimestamp;
        }

        public void setEndTimestamp(Integer endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        public Integer getFrequency() {
            return frequency;
        }

        public void setFrequency(Integer frequency) {
            this.frequency = frequency;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public Long getTrainingPlanId() {
            return trainingPlanId;
        }

        public void setTrainingPlanId(Long trainingPlanId) {
            this.trainingPlanId = trainingPlanId;
        }

        public Long getTrainingPlanExerciseId() {
            return trainingPlanExerciseId;
        }

        public void setTrainingPlanExerciseId(Long trainingPlanExerciseId) {
            this.trainingPlanExerciseId = trainingPlanExerciseId;
        }

        public String getSmartExerciseName() {
            return smartExerciseName;
        }

        public void setSmartExerciseName(String smartExerciseName) {
            this.smartExerciseName = smartExerciseName;
        }

        public String getIntensity() {
            return intensity;
        }

        public void setIntensity(String intensity) {
            this.intensity = intensity;
        }

        public String getSmartTrainingMethod() {
            return smartTrainingMethod;
        }

        public void setSmartTrainingMethod(String smartTrainingMethod) {
            this.smartTrainingMethod = smartTrainingMethod;
        }

        public String getTrainingGoal() {
            return trainingGoal;
        }

        public void setTrainingGoal(String trainingGoal) {
            this.trainingGoal = trainingGoal;
        }

        public String getTrainingProgram() {
            return trainingProgram;
        }

        public void setTrainingProgram(String trainingProgram) {
            this.trainingProgram = trainingProgram;
        }

        public List<IntervalsDTO> getIntervals() {
            return intervals;
        }

        public void setIntervals(List<IntervalsDTO> intervals) {
            this.intervals = intervals;
        }

        public static class IntervalsDTO {
            private Integer rampAngle;
            private Integer resistance;
            private Double speed;
            private Integer duration;
            private Double distance;
            private Integer heartRate;
            private Integer kiloCalories;
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

            public Integer getKiloCalories() {
                return kiloCalories;
            }

            public void setKiloCalories(Integer kiloCalories) {
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

    public static class GeniusDTO {
        private String exerciseName;
        private String sessionName;
        private Long startTimestamp;
        private Integer endTimestamp;
        private Integer frequency;
        private String timezone;
        private Long trainingPlanId;
        private Long trainingPlanExerciseId;
        private Integer orderNumber;
        private List<IntervalsDTO> intervals;

        public String getExerciseName() {
            return exerciseName;
        }

        public void setExerciseName(String exerciseName) {
            this.exerciseName = exerciseName;
        }

        public String getSessionName() {
            return sessionName;
        }

        public void setSessionName(String sessionName) {
            this.sessionName = sessionName;
        }

        public Long getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(Long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        public Integer getEndTimestamp() {
            return endTimestamp;
        }

        public void setEndTimestamp(Integer endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        public Integer getFrequency() {
            return frequency;
        }

        public void setFrequency(Integer frequency) {
            this.frequency = frequency;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public Long getTrainingPlanId() {
            return trainingPlanId;
        }

        public void setTrainingPlanId(Long trainingPlanId) {
            this.trainingPlanId = trainingPlanId;
        }

        public Long getTrainingPlanExerciseId() {
            return trainingPlanExerciseId;
        }

        public void setTrainingPlanExerciseId(Long trainingPlanExerciseId) {
            this.trainingPlanExerciseId = trainingPlanExerciseId;
        }

        public Integer getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
        }

        public List<IntervalsDTO> getIntervals() {
            return intervals;
        }

        public void setIntervals(List<IntervalsDTO> intervals) {
            this.intervals = intervals;
        }

        public static class IntervalsDTO {
            private Integer rampAngle;
            private Integer resistance;
            private Double speed;
            private Integer duration;
            private Double distance;
            private Integer heartRate;
            private Integer kiloCalories;
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

            public Integer getKiloCalories() {
                return kiloCalories;
            }

            public void setKiloCalories(Integer kiloCalories) {
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
}
