package com.dyaco.spirit_commercial.model.webapi.bean;

public class NotifyUpdateConsoleSuccessBean {

    private Boolean success;
    private Object msg;
    private String errorMessage;
    private Integer errorCode;
    private String errorUuid;
    private Object dataMap;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorUuid() {
        return errorUuid;
    }

    public void setErrorUuid(String errorUuid) {
        this.errorUuid = errorUuid;
    }

    public Object getDataMap() {
        return dataMap;
    }

    public void setDataMap(Object dataMap) {
        this.dataMap = dataMap;
    }

    @Override
    public String toString() {
        return "NotifyUpdateConsoleSuccessBean{" +
                "success=" + success +
                ", msg=" + msg +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode=" + errorCode +
                ", errorUuid='" + errorUuid + '\'' +
                ", dataMap=" + dataMap +
                '}';
    }
}
