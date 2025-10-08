package com.dyaco.spirit_commercial.model.webapi.bean;

public class EgymLoginBean {


    private String access_token;
    private Long expiration_time;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getExpiration_time() {
        return expiration_time;
    }

    public void setExpiration_time(Long expiration_time) {
        this.expiration_time = expiration_time;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
