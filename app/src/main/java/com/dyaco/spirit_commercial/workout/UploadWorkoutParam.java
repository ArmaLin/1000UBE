package com.dyaco.spirit_commercial.workout;

import java.util.List;


public class UploadWorkoutParam {
    /**
     * clientWorkoutUuid   由Client端針對每一筆workout產生ID 使用 UUID uuid = UUID.randomUUID();
     *  machineCategoryType 0 = Treadmill, 1 = Bike, 11 = Recumbent Bike, 12 = Upright Bike, 13 = Flywheel Bike, 2 = Elliptical, 3 = Stepper, 4= Rower
     *  machineName         若是app跟機器藍芽對接(沒透過QRCode掃描),用app上傳運動資料時,此欄位為app讀取到的機器藍芽名稱<br>
     *                            console上傳的機器藍芽名稱為nickname，因各家錶廠對變更藍芽名稱的支援度不同，所以都統一傳nickname<br>
     *  machineMac          用QRCode連機器藍芽FTMS,或是機器console上傳的才會有
     *  machineModelName    機器的名稱
     *  startTime           開始的時間
     *  endTime             結束的時間
     *  startTimeMillis     開始的時間 Date.getTime
     *  endTimeMillis       結束的時間 Date.getTime
     *  programName         @CloudData.ProgramName 專案名稱
     *  programId           @CloudData.ProgramId 專案的ID
     *  avgIncline          平均揚升
     *  avgLevel            平均等級
     *  avgHeartRate        平均心跳
     *  avgMet              平均Met
     *  avgSpeed            平均速度 km/hr
     *  avgWatt             平均Watt
     *  avgCadence          平均節奏
     *                            stepper : spm (steps per minute)
     *                            bike : rpm (revolutions per minute)
     *  totalTime           經過的總時間
     *  totalDistance       移動的總距離
     *  totalCalories       消耗的總卡路里
     *  totalSteps          走的總步數
     *  totalElevation      上升的總高度 m公尺
     *  workoutRawData      實做一個 workoutRawData的List
     *                            每三秒產生 一筆workoutRawData
     *                            不足三秒 當成三秒
     */

    private FormDTO form;

    public FormDTO getForm() {
        return form;
    }

    public void setForm(FormDTO form) {
        this.form = form;
    }


    public static class FormDTO {
        private String orgId;
        private Long timestamp;
        private String clientWorkoutUuid;
        private Integer machineCategoryType;
        private String machineName;
        private String machineUuid;
        private String machineMac;
        private String machineModelName;
        private String startTime;
        private String endTime;
        private Long startTimeMillis;
        private Long endTimeMillis;
        private String programName;
        private Integer programId;
        private Double avgIncline;
        private Double avgLevel;
        private Double avgHeartRate;
        private Double avgMet;
        private Double avgSpeed;
        private Double avgWatt;
        private Double avgCadence;
        private Integer totalTime;
        private Double totalDistance;
        private Integer totalCalories;
        private Integer totalSteps;
        private Integer totalElevation;
        private List<RawDataListDTO> rawDataList;
        private String userUuid;
        private String timeZone;

        public String getOrgId() {
            return orgId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getClientWorkoutUuid() {
            return clientWorkoutUuid;
        }

        public void setClientWorkoutUuid(String clientWorkoutUuid) {
            this.clientWorkoutUuid = clientWorkoutUuid;
        }

        public Integer getMachineCategoryType() {
            return machineCategoryType;
        }

        public void setMachineCategoryType(Integer machineCategoryType) {
            this.machineCategoryType = machineCategoryType;
        }

        public String getMachineName() {
            return machineName;
        }

        public void setMachineName(String machineName) {
            this.machineName = machineName;
        }

        public String getMachineUuid() {
            return machineUuid;
        }

        public void setMachineUuid(String machineUuid) {
            this.machineUuid = machineUuid;
        }

        public String getMachineMac() {
            return machineMac;
        }

        public void setMachineMac(String machineMac) {
            this.machineMac = machineMac;
        }

        public String getMachineModelName() {
            return machineModelName;
        }

        public void setMachineModelName(String machineModelName) {
            this.machineModelName = machineModelName;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public Long getStartTimeMillis() {
            return startTimeMillis;
        }

        public void setStartTimeMillis(Long startTimeMillis) {
            this.startTimeMillis = startTimeMillis;
        }

        public Long getEndTimeMillis() {
            return endTimeMillis;
        }

        public void setEndTimeMillis(Long endTimeMillis) {
            this.endTimeMillis = endTimeMillis;
        }

        public String getProgramName() {
            return programName;
        }

        public void setProgramName(String programName) {
            this.programName = programName;
        }

        public Integer getProgramId() {
            return programId;
        }

        public void setProgramId(Integer programId) {
            this.programId = programId;
        }

        public Double getAvgIncline() {
            return avgIncline;
        }

        public void setAvgIncline(Double avgIncline) {
            this.avgIncline = avgIncline;
        }

        public Double getAvgLevel() {
            return avgLevel;
        }

        public void setAvgLevel(Double avgLevel) {
            this.avgLevel = avgLevel;
        }

        public Double getAvgHeartRate() {
            return avgHeartRate;
        }

        public void setAvgHeartRate(Double avgHeartRate) {
            this.avgHeartRate = avgHeartRate;
        }

        public Double getAvgMet() {
            return avgMet;
        }

        public void setAvgMet(Double avgMet) {
            this.avgMet = avgMet;
        }

        public Double getAvgSpeed() {
            return avgSpeed;
        }

        public void setAvgSpeed(Double avgSpeed) {
            this.avgSpeed = avgSpeed;
        }

        public Double getAvgWatt() {
            return avgWatt;
        }

        public void setAvgWatt(Double avgWatt) {
            this.avgWatt = avgWatt;
        }

        public Double getAvgCadence() {
            return avgCadence;
        }

        public void setAvgCadence(Double avgCadence) {
            this.avgCadence = avgCadence;
        }

        public Integer getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(Integer totalTime) {
            this.totalTime = totalTime;
        }

        public Double getTotalDistance() {
            return totalDistance;
        }

        public void setTotalDistance(Double totalDistance) {
            this.totalDistance = totalDistance;
        }

        public Integer getTotalCalories() {
            return totalCalories;
        }

        public void setTotalCalories(Integer totalCalories) {
            this.totalCalories = totalCalories;
        }

        public Integer getTotalSteps() {
            return totalSteps;
        }

        public void setTotalSteps(Integer totalSteps) {
            this.totalSteps = totalSteps;
        }

        public Integer getTotalElevation() {
            return totalElevation;
        }

        public void setTotalElevation(Integer totalElevation) {
            this.totalElevation = totalElevation;
        }

        public List<RawDataListDTO> getRawDataList() {
            return rawDataList;
        }

        public void setRawDataList(List<RawDataListDTO> rawDataList) {
            this.rawDataList = rawDataList;
        }

        public String getUserUuid() {
            return userUuid;
        }

        public void setUserUuid(String userUuid) {
            this.userUuid = userUuid;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        public static class RawDataListDTO {
            private Integer totalWorkoutTime;
            private Integer totalTimeLeft;
            private Integer nowHr;
            private Double totalDistance;
            private Integer totalCalorie;
            private Object nowSpeed;
            private Object nowIncline;
            private Object nowLevel;
            private Object nowWatt;
            private Object avgSpmRower;
            private Object totalStrokes;
            private Double avgRpm;
            private Object totalFloor;
            private Object totalElevation;
            private Object totalSteps;
            private Object curSpmStepper;
            private Object avgSpmStepper;

            public Integer getTotalWorkoutTime() {
                return totalWorkoutTime;
            }

            public void setTotalWorkoutTime(Integer totalWorkoutTime) {
                this.totalWorkoutTime = totalWorkoutTime;
            }

            public Integer getTotalTimeLeft() {
                return totalTimeLeft;
            }

            public void setTotalTimeLeft(Integer totalTimeLeft) {
                this.totalTimeLeft = totalTimeLeft;
            }

            public Integer getNowHr() {
                return nowHr;
            }

            public void setNowHr(Integer nowHr) {
                this.nowHr = nowHr;
            }

            public Double getTotalDistance() {
                return totalDistance;
            }

            public void setTotalDistance(Double totalDistance) {
                this.totalDistance = totalDistance;
            }

            public Integer getTotalCalorie() {
                return totalCalorie;
            }

            public void setTotalCalorie(Integer totalCalorie) {
                this.totalCalorie = totalCalorie;
            }

            public Object getNowSpeed() {
                return nowSpeed;
            }

            public void setNowSpeed(Object nowSpeed) {
                this.nowSpeed = nowSpeed;
            }

            public Object getNowIncline() {
                return nowIncline;
            }

            public void setNowIncline(Object nowIncline) {
                this.nowIncline = nowIncline;
            }

            public Object getNowLevel() {
                return nowLevel;
            }

            public void setNowLevel(Object nowLevel) {
                this.nowLevel = nowLevel;
            }

            public Object getNowWatt() {
                return nowWatt;
            }

            public void setNowWatt(Object nowWatt) {
                this.nowWatt = nowWatt;
            }

            public Object getAvgSpmRower() {
                return avgSpmRower;
            }

            public void setAvgSpmRower(Object avgSpmRower) {
                this.avgSpmRower = avgSpmRower;
            }

            public Object getTotalStrokes() {
                return totalStrokes;
            }

            public void setTotalStrokes(Object totalStrokes) {
                this.totalStrokes = totalStrokes;
            }

            public Double getAvgRpm() {
                return avgRpm;
            }

            public void setAvgRpm(Double avgRpm) {
                this.avgRpm = avgRpm;
            }

            public Object getTotalFloor() {
                return totalFloor;
            }

            public void setTotalFloor(Object totalFloor) {
                this.totalFloor = totalFloor;
            }

            public Object getTotalElevation() {
                return totalElevation;
            }

            public void setTotalElevation(Object totalElevation) {
                this.totalElevation = totalElevation;
            }

            public Object getTotalSteps() {
                return totalSteps;
            }

            public void setTotalSteps(Object totalSteps) {
                this.totalSteps = totalSteps;
            }

            public Object getCurSpmStepper() {
                return curSpmStepper;
            }

            public void setCurSpmStepper(Object curSpmStepper) {
                this.curSpmStepper = curSpmStepper;
            }

            public Object getAvgSpmStepper() {
                return avgSpmStepper;
            }

            public void setAvgSpmStepper(Object avgSpmStepper) {
                this.avgSpmStepper = avgSpmStepper;
            }
        }
    }
}
