package com.dyaco.spirit_commercial.support.intdef;


import com.dyaco.spirit_commercial.R;

import java.util.Locale;

public enum LanguageEnum {

    //    ENGLISH(0, Locale.ENGLISH, R.string.English),
    ENGLISH(0, new Locale("en", "US"), R.string.English, new Locale("en", "US").toLanguageTag()),
    TRADITIONAL_CHINESE(1, Locale.TRADITIONAL_CHINESE, R.string.Traditional_Chinese, Locale.TRADITIONAL_CHINESE.toLanguageTag()),
    SIMPLIFIED_CHINESE(2, Locale.SIMPLIFIED_CHINESE, R.string.simplified_chinese, Locale.SIMPLIFIED_CHINESE.toLanguageTag()),
    PORTUGAL(3, new Locale("pt", "PT"), R.string.portugues, new Locale("pt", "PT").toLanguageTag()),//Português 葡萄牙
    RUSSIA(4, new Locale("ru", "RU"), R.string.Pyccknn, new Locale("ru", "RU").toLanguageTag()),//Pyccknn 俄羅斯
    FRANCE(5, Locale.FRANCE, R.string.fran_ais, Locale.FRANCE.toLanguageTag()),
    JAPAN(6, Locale.JAPAN, R.string.japanese, Locale.JAPAN.toLanguageTag()),
    KOREA(7, Locale.KOREA, R.string.Korea, Locale.KOREA.toLanguageTag()),
    GERMAN(8, Locale.GERMAN, R.string.deutsch, Locale.GERMAN.toLanguageTag()),
    ITALIAN(9, Locale.ITALIAN, R.string.Italiano, Locale.ITALIAN.toLanguageTag()),
    NEDERLANDS(10, new Locale("nl", "NL"), R.string.Nederlands, new Locale("nl", "NL").toLanguageTag()),//Nederlands 荷蘭
    NORSK(11, new Locale("nb", "NO"), R.string.norsk, new Locale("nb", "NO").toLanguageTag()),//Norsk Norge 挪威 //nn_NO  nb_NO
    SPAIN(12, new Locale("es", "ES"), R.string.Español, new Locale("es", "ES").toLanguageTag()),//Español 西班牙
    SUOMI(13, new Locale("fi", "FI"), R.string.Suomi, new Locale("fi", "FI").toLanguageTag());//芬蘭

    private int languageId;
    private String languageTag;
    private Locale locale;
    private int languageStr;

    LanguageEnum(int languageId, Locale locale, int languageStr, String languageTag) {
        this.languageId = languageId;
        this.locale = locale;
        this.languageStr = languageStr;
        this.languageTag = languageTag;
    }


    public static LanguageEnum getLanguage(int id) {
        for (LanguageEnum languageEnum : values()) {
            if (languageEnum.getLanguageId() == id) {
                return languageEnum;
            }
        }
        return LanguageEnum.getLanguage(0);
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public int getLanguageStr() {
        return languageStr;
    }

    public void setLanguageStr(int languageStr) {
        this.languageStr = languageStr;
    }
}
