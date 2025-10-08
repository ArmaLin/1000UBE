package com.dyaco.spirit_commercial.model.webapi.bean;

public class GetGymInfo2Bean {


    private Boolean success;
    private String msg;
    private String errorMessage;
    private Object errorCode;
    private Object errorUuid;
    private DataMapDTO dataMap;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getMsg() {
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

    public Object getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Object errorCode) {
        this.errorCode = errorCode;
    }

    public Object getErrorUuid() {
        return errorUuid;
    }

    public void setErrorUuid(Object errorUuid) {
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
            private String logUrl;
            private String clubFullName;
            private String taxId;
            private String contactEmail;
            private String address;
            private ThirdPartyClientCredentialDTO thirdPartyClientCredential;

            public String getBranchId() {
                return branchId;
            }

            public void setBranchId(String branchId) {
                this.branchId = branchId;
            }

            public String getLogUrl() {
                return logUrl;
            }

            public void setLogUrl(String logUrl) {
                this.logUrl = logUrl;
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

            public ThirdPartyClientCredentialDTO getThirdPartyClientCredential() {
                return thirdPartyClientCredential;
            }

            public void setThirdPartyClientCredential(ThirdPartyClientCredentialDTO thirdPartyClientCredential) {
                this.thirdPartyClientCredential = thirdPartyClientCredential;
            }

            public static class ThirdPartyClientCredentialDTO {
                private String source_name;
                private String client_id;
                private String client_secret;

                public String getSource_name() {
                    return source_name;
                }

                public void setSource_name(String source_name) {
                    this.source_name = source_name;
                }

                public String getClient_id() {
                    return client_id;
                }

                public void setClient_id(String client_id) {
                    this.client_id = client_id;
                }

                public String getClient_secret() {
                    return client_secret;
                }

                public void setClient_secret(String client_secret) {
                    this.client_secret = client_secret;
                }
            }
        }
    }
}
