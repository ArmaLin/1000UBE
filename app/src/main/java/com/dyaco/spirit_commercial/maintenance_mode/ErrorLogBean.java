package com.dyaco.spirit_commercial.maintenance_mode;

public class ErrorLogBean {

    String errorLogDate;
    String errorLogTime;
    String errorLogCode;
    String errorLogMessage;

    public ErrorLogBean(String errorLogDate, String errorLogTime, String errorLogCode, String errorLogMessage) {
        this.errorLogDate = errorLogDate;
        this.errorLogTime = errorLogTime;
        this.errorLogCode = errorLogCode;
        this.errorLogMessage = errorLogMessage;
    }

    public String getErrorLogDate() {
        return errorLogDate;
    }

    public void setErrorLogDate(String errorLogDate) {
        this.errorLogDate = errorLogDate;
    }

    public String getErrorLogTime() {
        return errorLogTime;
    }

    public void setErrorLogTime(String errorLogTime) {
        this.errorLogTime = errorLogTime;
    }

    public String getErrorLogCode() {
        return errorLogCode;
    }

    public void setErrorLogCode(String errorLogCode) {
        this.errorLogCode = errorLogCode;
    }

    public String getErrorLogMessage() {
        return errorLogMessage;
    }

    public void setErrorLogMessage(String errorLogMessage) {
        this.errorLogMessage = errorLogMessage;
    }
}
