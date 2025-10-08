package com.dyaco.spirit_commercial.model.repository;

import com.corestar.libs.device.DeviceTvTuner;
import com.dyaco.spirit_commercial.maintenance_mode.CountryBean;

import java.util.ArrayList;
import java.util.List;

public class CountryRepo implements IRepo<CountryBean> {

    @Override
    public void getData(int id, RepoCallback<CountryBean> repoCallback) {

        List<CountryBean> countryBeanList = addCountry();

        if (countryBeanList.size() > 0) {
            repoCallback.onSuccess(countryBeanList);
        } else {
            repoCallback.onFail("NO DATA");
        }
    }

    private List<CountryBean> addCountry() {

        List<CountryBean> countryBeanList = new ArrayList<>(84);
        for (int i = 0; i <= 84; i++) {
            countryBeanList.add(new CountryBean("", -1, null));
        }

        countryBeanList.set(59, new CountryBean("", -1, null));
        countryBeanList.set(65, new CountryBean("", -1, null));
        countryBeanList.set(71, new CountryBean("", -1, null));

        int i = 0;
        String countryName;
        for (DeviceTvTuner.TV_COUNTRY tvCountry : DeviceTvTuner.TV_COUNTRY.values()) {
            countryName = tvCountry.name();
            switch (tvCountry) {
                case Albania:
                    i = 0;
                    break;
                case Argentina:
                    i = 6;
                    break;
                case Australia:
                    i = 12;
                    break;
                case Austria:
                    i = 18;
                    break;
                case Azerbaijan:
                    i = 24;
                    break;
                case Belarus:
                    i = 30;
                    break;
                case Belgium:
                    i = 36;
                    break;
                case Bolivia:
                    i = 42;
                    break;
                case Brazil:
                    i = 48;
                    break;
                case Brunei:
                    i = 54;
                    break;
                case Bulgaria:
                    i = 60;
                    break;
                case Canada:
                    i = 66;
                    break;
                case Chile:
                    i = 72;
                    break;
                case China:
                    countryName = "China (Mainland)";
                    i = 78;
                    break;
                case China_Hong_Kong:
                    countryName = "China (Hong Kong)";
                    i = 1;
                    break;
                case Columbia:
                    i = 7;
                    break;
                case CostaRica:
                    countryName = "Costa Rica";
                    i = 13;
                    break;
                case Croatia:
                    i = 19;
                    break;
                case Cyprus:
                    i = 25;
                    break;
                case CzechRepublic:
                    countryName = "Czech Republic";
                    i = 31;
                    break;
                case Denmark:
                    i = 37;
                    break;
                case DominicanRepublic:
                    countryName = "Dominican Republic";
                    i = 43;
                    break;
                case Ecuador:
                    i = 49;
                    break;
                case Egypt:
                    i = 55;
                    break;
                case Estonia:
                    i = 61;
                    break;
                case Finland:
                    i = 67;
                    break;
                case France:
                    i = 73;
                    break;
                case Germany:
                    i = 79;
                    break;
                case Greece:
                    i = 2;
                    break;
                case Guatemala:
                    i = 8;
                    break;
                case Hungary:
                    i = 14;
                    break;
                case Iceland:
                    i = 20;
                    break;
                case India:
                    i = 26;
                    break;
                case Indonesia:
                    i = 32;
                    break;
                case Ireland:
                    i = 38;
                    break;
                case Israel:
                    i = 44;
                    break;
                case Italy:
                    i = 50;
                    break;
                case Japan:
                    i = 56;
                    break;
                case Kazakhstan:
                    i = 62;
                    break;
                case Kuwait:
                    i = 68;
                    break;
                case Latvia:
                    i = 74;
                    break;
                case Lithuania:
                    i = 80;
                    break;
                case Luxembourg:
                    i = 3;
                    break;
                case Macedonia:
                    i = 9;
                    break;
                case Malaysia:
                    i = 15;
                    break;
                case Maldives:
                    i = 21;
                    break;
                case Montenegro:
                    i = 27;
                    break;
                case Netherlands:
                    i = 33;
                    break;
                case Mexico:
                    i = 39;
                    break;
                case NewZealand:
                    countryName = "New Zealand";
                    i = 45;
                    break;
                case Norway:
                    i = 51;
                    break;
                case Panama:
                    i = 57;
                    break;
                case Peru:
                    i = 63;
                    break;
                case Philippines:
                    i = 69;
                    break;
                case Poland:
                    i = 75;
                    break;
//                case Portugal:
//                    i = 0;
//                    break;
                case PuertoRico:
                    countryName = "Puerto Rico";
                    i = 81;
                    break;
                case Qatar:
                    i = 4;
                    break;
                case Romania:
                    i = 10;
                    break;
                case Russia:
                    i = 16;
                    break;
                case SaudiArabia:
                    countryName = "Saudi Arabia";
                    i = 22;
                    break;
                case Serbia:
                    i = 28;
                    break;
                case Singapore:
                    i = 34;
                    break;
                case Slovakia:
                    i = 40;
                    break;
                case Slovenia:
                    i = 46;
                    break;
                case SouthKorea:
                    countryName = "South Korea";
                    i = 52;
                    break;
                case Suriname:
                    i = 58;
                    break;
                case Spain:
                    i = 64;
                    break;
                case Sweden:
                    i = 70;
                    break;
                case Switzerland:
                    i = 76;
                    break;
                case Taiwan:
                    i = 82;
                    break;
                case Thailand:
                    i = 5;
                    break;
                case Turkey:
                    i = 11;
                    break;
                case Turkmenistan:
                    i = 17;
                    break;
                case UnitedArabEmirates:
                    countryName = "UAE";
                    i = 23;
                    break;
                case UnitedKingdom:
                    countryName = "UK";
                    i = 29;
                    break;
                case Uruguay:
                    i = 35;
                    break;
                case USA:
                    i = 41;
                    break;
                case Venezuela:
                    i = 47;
                    break;
                case Vietnam:
                    i = 53;
                    break;
                case Unknown:
                    continue;

            }

            countryBeanList.set(i, new CountryBean(countryName, tvCountry.code, tvCountry));
        }

        return countryBeanList;
    }
}
