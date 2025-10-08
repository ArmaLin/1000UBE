package com.dyaco.spirit_commercial.dashboard_training;

public class RankingBean {

    private String memberName;
    private int memberNo;
    private int memberHours;
    private float memberSpeed;
    private float memberCal;
    private float memberDistance;
    private int memberAvatar;
    private boolean isMe;


    public RankingBean(String memberName, int memberNo, int memberHours, float memberSpeed, float memberCal, float memberDistance, int memberAvatar,boolean isMe) {
        this.memberName = memberName;
        this.memberNo = memberNo;
        this.memberHours = memberHours;
        this.memberSpeed = memberSpeed;
        this.memberCal = memberCal;
        this.memberDistance = memberDistance;
        this.memberAvatar = memberAvatar;
        this.isMe = isMe;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public int getMemberHours() {
        return memberHours;
    }

    public void setMemberHours(int memberHours) {
        this.memberHours = memberHours;
    }

    public float getMemberSpeed() {
        return memberSpeed;
    }

    public void setMemberSpeed(float memberSpeed) {
        this.memberSpeed = memberSpeed;
    }

    public float getMemberCal() {
        return memberCal;
    }

    public void setMemberCal(float memberCal) {
        this.memberCal = memberCal;
    }

    public float getMemberDistance() {
        return memberDistance;
    }

    public void setMemberDistance(float memberDistance) {
        this.memberDistance = memberDistance;
    }

    public int getMemberAvatar() {
        return memberAvatar;
    }

    public void setMemberAvatar(int memberAvatar) {
        this.memberAvatar = memberAvatar;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(int memberNo) {
        this.memberNo = memberNo;
    }

}
