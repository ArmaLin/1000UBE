package com.dyaco.spirit_commercial.settings;

public class AvatarEntity {
    private int avatarIcon;
    private int avatarId;

    public AvatarEntity(int avatarIcon, int avatarId) {
        this.avatarIcon = avatarIcon;
        this.avatarId = avatarId;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public int getAvatarIcon() {
        return avatarIcon;
    }

    public void setAvatarIcon(int avatarIcon) {
        this.avatarIcon = avatarIcon;
    }
}
