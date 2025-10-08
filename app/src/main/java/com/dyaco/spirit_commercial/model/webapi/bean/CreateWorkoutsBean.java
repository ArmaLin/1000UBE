package com.dyaco.spirit_commercial.model.webapi.bean;

public class CreateWorkoutsBean {

    private Long timestamp;
    private String path;
    private Integer status;
    private String error;
    private String requestId;
    private String message;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CreateWorkoutsBean{" +
                "timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", requestId='" + requestId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
