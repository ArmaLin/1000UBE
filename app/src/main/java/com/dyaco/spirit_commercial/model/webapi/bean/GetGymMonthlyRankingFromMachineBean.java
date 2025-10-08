package com.dyaco.spirit_commercial.model.webapi.bean;

import androidx.annotation.NonNull;

import java.util.List;

public class GetGymMonthlyRankingFromMachineBean {

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
            private String photoFileUuid;
            private String memberId;
            private String userId;
            private String displayName;
            private String photoFileUrl;
            private Object avatarId;
            private Boolean isSelf;
            private Double performance;

            public Integer getRanking() {
                return ranking;
            }

            public void setRanking(Integer ranking) {
                this.ranking = ranking;
            }

            public String getPhotoFileUuid() {
                return photoFileUuid;
            }

            public void setPhotoFileUuid(String photoFileUuid) {
                this.photoFileUuid = photoFileUuid;
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

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public String getPhotoFileUrl() {
                return photoFileUrl;
            }

            public void setPhotoFileUrl(String photoFileUrl) {
                this.photoFileUrl = photoFileUrl;
            }

            public Object getAvatarId() {
                return avatarId;
            }

            public void setAvatarId(Object avatarId) {
                this.avatarId = avatarId;
            }

            public Boolean getIsSelf() {
                return isSelf;
            }

            public void setIsSelf(Boolean isSelf) {
                this.isSelf = isSelf;
            }

            public Double getPerformance() {
                return performance;
            }

            public void setPerformance(Double performance) {
                this.performance = performance;
            }

            @NonNull
            @Override
            public String toString() {
                return "DataDTO{" +
                        "ranking=" + ranking +
                        ", photoFileUuid='" + photoFileUuid + '\'' +
                        ", memberId='" + memberId + '\'' +
                        ", userId='" + userId + '\'' +
                        ", displayName='" + displayName + '\'' +
                        ", photoFileUrl='" + photoFileUrl + '\'' +
                        ", avatarId=" + avatarId +
                        ", isSelf=" + isSelf +
                        ", performance=" + performance +
                        '}';
            }
        }
    }
}
