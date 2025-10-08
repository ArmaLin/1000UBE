package com.dyaco.spirit_commercial.model.webapi.bean;

import androidx.annotation.NonNull;

public class GetUnreadMessageCountFromMachineBean {

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
            private Integer unreadCount;

            public Integer getUnreadCount() {
                return unreadCount;
            }

            public void setUnreadCount(Integer unreadCount) {
                this.unreadCount = unreadCount;
            }

            @NonNull
            @Override
            public String toString() {
                return "DataDTO{" +
                        "unreadCount=" + unreadCount +
                        '}';
            }
        }

    }

    @NonNull
    @Override
    public String toString() {
        return "GetUnreadMessageCountFromMachineBean{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorUuid='" + errorUuid + '\'' +
                ", dataMap=" + dataMap +
                '}';
    }
}
