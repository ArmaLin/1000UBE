package com.dyaco.spirit_commercial.model.webapi.bean;

import androidx.annotation.NonNull;

public class MemberCheckinMachineByQRCodeBean {

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
        private DataDTO data;

        public DataDTO getData() {
            return data;
        }

        public void setData(DataDTO data) {
            this.data = data;
        }

        public static class DataDTO {
            private String userUuid;
            private String photoFileUuid;
            private String photoFileUrl;
            private String avatarId;
            private String gender;
            private String firstName;
            private String lastName;
            private String displayName;
            private Integer measurementUnit;
            private String weight;
            private String height;
            private String memberLevel;
            private Integer age;
            private String orgId;
            private String joinedGymDate;
            private Long joinedGymDateTimeMillis;
            private String membershipExpirationDate;
            private boolean isJoinedMonthlyRanking;

            public boolean isJoinedMonthlyRanking() {
                return isJoinedMonthlyRanking;
            }

            public void setJoinedMonthlyRanking(boolean joinedMonthlyRanking) {
                isJoinedMonthlyRanking = joinedMonthlyRanking;
            }

            public String getUserUuid() {
                return userUuid;
            }

            public void setUserUuid(String userUuid) {
                this.userUuid = userUuid;
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

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
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

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public Integer getMeasurementUnit() {
                return measurementUnit;
            }

            public void setMeasurementUnit(Integer measurementUnit) {
                this.measurementUnit = measurementUnit;
            }

            public String getWeight() {
                return weight;
            }

            public void setWeight(String weight) {
                this.weight = weight;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getMemberLevel() {
                return memberLevel;
            }

            public void setMemberLevel(String memberLevel) {
                this.memberLevel = memberLevel;
            }

            public Integer getAge() {
                return age;
            }

            public void setAge(Integer age) {
                this.age = age;
            }

            public String getOrgId() {
                return orgId;
            }

            public void setOrgId(String orgId) {
                this.orgId = orgId;
            }

            public String getJoinedGymDate() {
                return joinedGymDate;
            }

            public void setJoinedGymDate(String joinedGymDate) {
                this.joinedGymDate = joinedGymDate;
            }

            public Long getJoinedGymDateTimeMillis() {
                return joinedGymDateTimeMillis;
            }

            public void setJoinedGymDateTimeMillis(Long joinedGymDateTimeMillis) {
                this.joinedGymDateTimeMillis = joinedGymDateTimeMillis;
            }

            public String getMembershipExpirationDate() {
                return membershipExpirationDate;
            }

            public void setMembershipExpirationDate(String membershipExpirationDate) {
                this.membershipExpirationDate = membershipExpirationDate;
            }

            @NonNull
            @Override
            public String toString() {
                return "DataDTO{" +
                        "userUuid='" + userUuid + '\'' +
                        ", photoFileUuid='" + photoFileUuid + '\'' +
                        ", photoFileUrl='" + photoFileUrl + '\'' +
                        ", avatarId='" + avatarId + '\'' +
                        ", gender='" + gender + '\'' +
                        ", firstName='" + firstName + '\'' +
                        ", lastName='" + lastName + '\'' +
                        ", displayName='" + displayName + '\'' +
                        ", measurementUnit=" + measurementUnit +
                        ", weight='" + weight + '\'' +
                        ", height='" + height + '\'' +
                        ", memberLevel='" + memberLevel + '\'' +
                        ", age=" + age +
                        ", orgId='" + orgId + '\'' +
                        ", joinedGymDate='" + joinedGymDate + '\'' +
                        ", joinedGymDateTimeMillis=" + joinedGymDateTimeMillis +
                        ", isJoinedMonthlyRanking=" + isJoinedMonthlyRanking +
                        ", membershipExpirationDate='" + membershipExpirationDate + '\'' +
                        '}';
            }
        }
    }
}
