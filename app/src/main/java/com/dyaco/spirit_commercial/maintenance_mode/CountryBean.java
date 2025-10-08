package com.dyaco.spirit_commercial.maintenance_mode;

import com.corestar.libs.device.DeviceTvTuner;

public class CountryBean {

   String countryName;
   int countryCode;
   DeviceTvTuner.TV_COUNTRY tvCountry;

    public CountryBean(String countryName, int countryCode, DeviceTvTuner.TV_COUNTRY tvCountry) {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.tvCountry = tvCountry;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public DeviceTvTuner.TV_COUNTRY getTvCountry() {
        return tvCountry;
    }

    public void setTvCountry(DeviceTvTuner.TV_COUNTRY tvCountry) {
        this.tvCountry = tvCountry;
    }
}
