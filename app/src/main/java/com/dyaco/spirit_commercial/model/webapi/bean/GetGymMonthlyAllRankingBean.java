package com.dyaco.spirit_commercial.model.webapi.bean;

import androidx.annotation.NonNull;

import java.util.List;

public class GetGymMonthlyAllRankingBean {


    private Boolean success;
    private String msg;
    private String errorMessage;
    private String errorCode;
    private String errorUuid;
    private DataMapDTO dataMap;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorUuid() {
        return errorUuid;
    }

    public void setErrorUuid(String errorUuid) {
        this.errorUuid = errorUuid;
    }

    public DataMapDTO getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMapDTO dataMap) {
        this.dataMap = dataMap;
    }

    public static class DataMapDTO {
        private List<DataDTO> data;

        public List<DataDTO> getData() {
            return data;
        }

        public void setData(List<DataDTO> data) {
            this.data = data;
        }

        public static class DataDTO {
            private Integer ranking;
            private String memberId;
            private String userId;
            private boolean isSelf;
            private Double performanceTime;
            private Double performanceSpeed;
            private Double performanceDistance;
            private Double performancePower;
            private Integer performanceCalories;
            private String displayName;
            private String photoFileUuid;
            private String photoFileUrl;
            private String avatarId;

            public Double getPerformancePower() {
                return performancePower;
            }

            public void setPerformancePower(Double performancePower) {
                this.performancePower = performancePower;
            }

            public Integer getRanking() {
                return ranking;
            }

            public void setRanking(Integer ranking) {
                this.ranking = ranking;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public boolean isSelf() {
                return isSelf;
            }

            public void setSelf(boolean self) {
                isSelf = self;
            }

            public Double getPerformanceTime() {
                return performanceTime;
            }

            public void setPerformanceTime(Double performanceTime) {
                this.performanceTime = performanceTime;
            }

            public Double getPerformanceSpeed() {
                return performanceSpeed;
            }

            public void setPerformanceSpeed(Double performanceSpeed) {
                this.performanceSpeed = performanceSpeed;
            }

            public Double getPerformanceDistance() {
                return performanceDistance;
            }

            public void setPerformanceDistance(Double performanceDistance) {
                this.performanceDistance = performanceDistance;
            }

            public Integer getPerformanceCalories() {
                return performanceCalories;
            }

            public void setPerformanceCalories(Integer performanceCalories) {
                this.performanceCalories = performanceCalories;
            }

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public String getPhotoFileUuid() {
                return photoFileUuid;
            }

            public void setPhotoFileUuid(String photoFileUuid) {
                this.photoFileUuid = photoFileUuid;
            }

            public String getPhotoFileUrl() {
                return photoFileUrl;
            }

            public void setPhotoFileUrl(String photoFileUrl) {
                this.photoFileUrl = photoFileUrl;
            }

            public String getAvatarId() {
                return avatarId;
            }

            public void setAvatarId(String avatarId) {
                this.avatarId = avatarId;
            }

            @NonNull
            @Override
            public String toString() {
                return "DataDTO{" +
                        "ranking=" + ranking +
                        ", memberId='" + memberId + '\'' +
                        ", userId='" + userId + '\'' +
                        ", is_self=" + isSelf +
                        ", performanceTime=" + performanceTime +
                        ", performanceSpeed=" + performanceSpeed +
                        ", performanceDistance=" + performanceDistance +
                        ", performanceCalories=" + performanceCalories +
                        ", performancePower=" + performancePower +
                        ", displayName='" + displayName + '\'' +
                        ", photoFileUuid='" + photoFileUuid + '\'' +
                        ", photoFileUrl='" + photoFileUrl + '\'' +
                        ", avatarId='" + avatarId + '\'' +
                        '}';
            }
        }

    }

    @NonNull
    @Override
    public String toString() {
        return "GetGymMonthlyAllRankingBean{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorUuid='" + errorUuid + '\'' +
                ", dataMap=" + dataMap +
                '}';
    }
}
