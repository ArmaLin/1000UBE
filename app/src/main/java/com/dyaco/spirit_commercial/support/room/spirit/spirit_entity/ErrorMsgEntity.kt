package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = ErrorMsgEntity.ERROR_MSG,
        indices = {@Index("uid")}
)
public class ErrorMsgEntity {
    public static final String ERROR_MSG = "error_msg";

    @PrimaryKey(autoGenerate = true)
    private long uid;
    private String errorCode;
    private String errorMessage;
    private Date errorDate;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "ErrorMsgEntity{" +
                "uid=" + uid +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorDate=" + errorDate +
                '}';
    }
}