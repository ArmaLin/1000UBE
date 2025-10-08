package com.dyaco.spirit_commercial.model.webapi;

import androidx.annotation.IntDef;

public class CloudData {
    public static final int MESSAGE_CATEGORY_NEWS = 0;
    public static final int MESSAGE_CATEGORY_REGULATIONS = 1;
    public static final int MESSAGE_CATEGORY_PROMO = 2;

    public static final String UPDATE_FIELD = "updateField";
    public static final String UPDATE_FIELD_AGE = "age";
    public static final String UPDATE_FIELD_WEIGHT = "weight";
    public static final String UPDATE_FIELD_HEIGHT = "height";
    public static final String UPDATE_FIELD_AVATARID = "avatarId";


    public static final int TREADMILL = 0;
    public static final int BIKE = 1;
    public static final int RECUMBENT_BIKE = 11;
    public static final int UPRIGHT_BIKE = 12;
    public static final int ELLIPTICAL = 2;
    public static final int STEPPER = 3;
    public static final int ROWER = 4;

    @IntDef({TREADMILL, BIKE, RECUMBENT_BIKE, UPRIGHT_BIKE, ELLIPTICAL, STEPPER, ROWER
    })
    public @interface CategoryType {

    }
}
