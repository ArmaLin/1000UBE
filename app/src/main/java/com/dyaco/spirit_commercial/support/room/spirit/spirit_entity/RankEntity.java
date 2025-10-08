package com.dyaco.spirit_commercial.support.room.spirit.spirit_entity;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = RankEntity.RANKS,
        indices = {@Index("uid")}
)

public class RankEntity {
    public static final String RANKS = "ranks";

    @PrimaryKey(autoGenerate = true)
    private long uid;
    private String memberName;
    private int memberNo;
    private int memberHours;
    private float memberSpeed;
    private float memberCal;
    private float memberDistance;
    private byte[] memberAvatar;
    private boolean isMe;

    public RankEntity(String memberName, int memberNo, int memberHours, float memberSpeed, float memberCal, float memberDistance, byte[] memberAvatar, boolean isMe) {
        this.memberName = memberName;
        this.memberNo = memberNo;
        this.memberHours = memberHours;
        this.memberSpeed = memberSpeed;
        this.memberCal = memberCal;
        this.memberDistance = memberDistance;
        this.memberAvatar = memberAvatar;
        this.isMe = isMe;
    }

    @Ignore
    public RankEntity() {
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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

    public byte[] getMemberAvatar() {
        return memberAvatar;
    }

    public void setMemberAvatar(byte[] memberAvatar) {
        this.memberAvatar = memberAvatar;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}