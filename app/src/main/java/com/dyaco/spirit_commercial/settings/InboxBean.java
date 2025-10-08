package com.dyaco.spirit_commercial.settings;

public class InboxBean {

    private int inboxIcon;
    private String inboxTitle;
    private String inboxDate;
    private String inboxContent;
    private String inboxFrom;
    private boolean isRead;


    public InboxBean(int inboxIcon, String inboxTitle, String inboxDate, String inboxContent, String inboxFrom, boolean isRead) {
        this.inboxIcon = inboxIcon;
        this.inboxTitle = inboxTitle;
        this.inboxDate = inboxDate;
        this.inboxContent = inboxContent;
        this.inboxFrom = inboxFrom;
        this.isRead = isRead;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getInboxIcon() {
        return inboxIcon;
    }

    public void setInboxIcon(int inboxIcon) {
        this.inboxIcon = inboxIcon;
    }

    public String getInboxTitle() {
        return inboxTitle;
    }

    public void setInboxTitle(String inboxTitle) {
        this.inboxTitle = inboxTitle;
    }

    public String getInboxDate() {
        return inboxDate;
    }

    public void setInboxDate(String inboxDate) {
        this.inboxDate = inboxDate;
    }

    public String getInboxContent() {
        return inboxContent;
    }

    public void setInboxContent(String inboxContent) {
        this.inboxContent = inboxContent;
    }

    public String getInboxFrom() {
        return inboxFrom;
    }

    public void setInboxFrom(String inboxFrom) {
        this.inboxFrom = inboxFrom;
    }
}
