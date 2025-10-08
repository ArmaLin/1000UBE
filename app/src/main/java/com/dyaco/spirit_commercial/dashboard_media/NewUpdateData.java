package com.dyaco.spirit_commercial.dashboard_media;

public class NewUpdateData {
    public NewUpdateData(int type, int value) {
        this.type = type;
        this.value = value;
    }

    int type;
    int value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
