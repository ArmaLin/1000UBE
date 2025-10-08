package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.support.CommonUtils.getAvatarSelectedFromTag;

public class AvatarBean {

    public AvatarBean(String avatarTag) {
        this.avatarTag = avatarTag;
        this.avatarId = getAvatarSelectedFromTag(avatarTag,true);
    }

    private String avatarTag;
    private boolean isChecked;

    private int avatarId;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarTag() {
        return avatarTag;
    }

    public void setAvatarTag(String avatarTag) {
        this.avatarTag = avatarTag;
    }
}
