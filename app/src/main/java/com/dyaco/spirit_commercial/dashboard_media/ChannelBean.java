package com.dyaco.spirit_commercial.dashboard_media;

public class ChannelBean {

    String channelName;
    int channelNumber;
    String channelTime;
    String channelContent;
    int channelIcon;

    public ChannelBean(String channelName, int channelNumber, String channelTime, String channelContent, int channelIcon) {
        this.channelName = channelName;
        this.channelNumber = channelNumber;
        this.channelTime = channelTime;
        this.channelContent = channelContent;
        this.channelIcon = channelIcon;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public String getChannelTime() {
        return channelTime;
    }

    public void setChannelTime(String channelTime) {
        this.channelTime = channelTime;
    }

    public String getChannelContent() {
        return channelContent;
    }

    public void setChannelContent(String channelContent) {
        this.channelContent = channelContent;
    }

    public int getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(int channelIcon) {
        this.channelIcon = channelIcon;
    }
}
