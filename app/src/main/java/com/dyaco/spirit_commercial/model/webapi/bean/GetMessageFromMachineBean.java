package com.dyaco.spirit_commercial.model.webapi.bean;

import androidx.annotation.NonNull;

import java.util.List;

public class GetMessageFromMachineBean {

    private Boolean success;
    private DataMapDTO dataMap;

    private String msg;
    private String errorMessage;
    private String errorCode;
    private String errorUuid;

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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
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
            private String messageId;
            private String title;
            private String body;
            private Integer messageCategory;
            private String publishDate;
            private List<LstAttachFileDTO> lstAttachFile;

            public String getMessageId() {
                return messageId;
            }

            public void setMessageId(String messageId) {
                this.messageId = messageId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getBody() {
                return body;
            }

            public void setBody(String body) {
                this.body = body;
            }

            public Integer getMessageCategory() {
                return messageCategory;
            }

            public void setMessageCategory(Integer messageCategory) {
                this.messageCategory = messageCategory;
            }

            public String getPublishDate() {
                return publishDate;
            }

            public void setPublishDate(String publishDate) {
                this.publishDate = publishDate;
            }

            public List<LstAttachFileDTO> getLstAttachFile() {
                return lstAttachFile;
            }

            public void setLstAttachFile(List<LstAttachFileDTO> lstAttachFile) {
                this.lstAttachFile = lstAttachFile;
            }

            public static class LstAttachFileDTO {
                private String filename;
                private String fileExtensionName;
                private String fileUrl;

                public String getFilename() {
                    return filename;
                }

                public void setFilename(String filename) {
                    this.filename = filename;
                }

                public String getFileExtensionName() {
                    return fileExtensionName;
                }

                public void setFileExtensionName(String fileExtensionName) {
                    this.fileExtensionName = fileExtensionName;
                }

                public String getFileUrl() {
                    return fileUrl;
                }

                public void setFileUrl(String fileUrl) {
                    this.fileUrl = fileUrl;
                }
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "GetMessageFromMachineBean{" +
                "success=" + success +
                ", dataMap=" + dataMap +
                ", msg='" + msg + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorUuid='" + errorUuid + '\'' +
                '}';
    }
}
