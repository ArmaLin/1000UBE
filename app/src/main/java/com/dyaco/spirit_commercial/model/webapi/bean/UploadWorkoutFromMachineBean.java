package com.dyaco.spirit_commercial.model.webapi.bean;

import androidx.annotation.NonNull;

public class UploadWorkoutFromMachineBean {


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
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @NonNull
        @Override
        public String toString() {
            return "DataMapDTO{" +
                    "data='" + data + '\'' +
                    '}';
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "UploadWorkoutFromMachineBean{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorUuid='" + errorUuid + '\'' +
                ", dataMap=" + dataMap +
                '}';
    }
}
