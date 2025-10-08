package com.dyaco.spirit_commercial.model.webapi.bean;

import androidx.annotation.NonNull;

public class GetGymInfoBean {

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
            private String branchId;
            private String logoUrl;
            private String clubFullName;
            private String taxId;
            private String contactEmail;
            private String address;

            public String getBranchId() {
                return branchId;
            }

            public void setBranchId(String branchId) {
                this.branchId = branchId;
            }

            public String getLogoUrl() {
                return logoUrl;
            }

            public void setLogoUrl(String logoUrl) {
                this.logoUrl = logoUrl;
            }

            public String getClubFullName() {
                return clubFullName;
            }

            public void setClubFullName(String clubFullName) {
                this.clubFullName = clubFullName;
            }

            public String getTaxId() {
                return taxId;
            }

            public void setTaxId(String taxId) {
                this.taxId = taxId;
            }

            public String getContactEmail() {
                return contactEmail;
            }

            public void setContactEmail(String contactEmail) {
                this.contactEmail = contactEmail;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            @NonNull
            @Override
            public String toString() {
                return "DataDTO{" +
                        "branchId='" + branchId + '\'' +
                        ", logoUrl='" + logoUrl + '\'' +
                        ", clubFullName='" + clubFullName + '\'' +
                        ", taxId='" + taxId + '\'' +
                        ", contactEmail='" + contactEmail + '\'' +
                        ", address='" + address + '\'' +
                        '}';
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "GetGymInfoBean{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorUuid='" + errorUuid + '\'' +
                ", dataMap=" + dataMap +
                '}';
    }
}
