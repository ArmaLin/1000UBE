package com.dyaco.spirit_commercial.support;

public class RegexPattern {
    public final static String REGISTER_ACCOUNT = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$";

    public final static String LOGIN_ACCOUNT = "";

    public final static String CELLPHONE = "^[0-9]{11,11}$";

    public final static String PASSWORD = "^[0-9a-zA-Z]{6,12}$";

    public final static String FORGET_ACCOUNT = "";


    public final static String FUNDS_PASSWORD = "^[0-9a-zA-Z]{6,12}$";

    public final static String EMAIL = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";

    public final static String VERIFY_CODE = "^[0-9]{4}$";

    public final static String PHONE_AND_EMAIL_VERIFY_CODE = "^[0-9]{6}$";

    public final static String BANKCARD = "^[0-9]{13,19}$";

    public final static String NAME = "^[0-9a-zA-Z\\u4e00-\\u9fa5]{2,30}$";
}
