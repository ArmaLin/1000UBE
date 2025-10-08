package com.dyaco.spirit_commercial.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

public class UserProfileViewModel extends ViewModel {
    public final ObservableInt userAvatar = new ObservableInt();
    public final ObservableField<String> userDisplayName = new ObservableField<>();
    public final ObservableBoolean isVip = new ObservableBoolean();
    public final ObservableInt userType = new ObservableInt();

    private double weight_metric; //公
    private double height_metric;
    private double weight_imperial; //英
    private double height_imperial;
    private String userUUID;
    private int userGender;
    private int userUnit;
    private String userLastName;
    private String userFirstName;
    private String memberLevel;
    private String orgId;
    private String joinedGymDate;
    private Long joinedGymDateTimeMillis;
    private String membershipExpirationDate;
    private String photoFileUuid;
    private String photoFileUrl;
    private String avatarId;
    private int userAge;
    private boolean isJoinedMonthlyRanking;

    public boolean isJoinedMonthlyRanking() {
        return isJoinedMonthlyRanking;
    }

    public void setJoinedMonthlyRanking(boolean joinedMonthlyRanking) {
        isJoinedMonthlyRanking = joinedMonthlyRanking;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public double getWeight_metric() {
        return weight_metric;
    }

    public void setWeight_metric(double weight_metric) {
        this.weight_metric = weight_metric;
    }

    public double getHeight_metric() {
        return height_metric;
    }

    public void setHeight_metric(double height_metric) {
        this.height_metric = height_metric;
    }

    public double getWeight_imperial() {
        return weight_imperial;
    }

    public void setWeight_imperial(double weight_imperial) {
        this.weight_imperial = weight_imperial;
    }

    public double getHeight_imperial() {
        return height_imperial;
    }

    public void setHeight_imperial(double height_imperial) {
        this.height_imperial = height_imperial;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public int getUserGender() {
        return userGender;
    }

    public void setUserGender(int userGender) {
        this.userGender = userGender;
    }

    public int getUserUnit() {
        return userUnit;
    }

    public void setUserUnit(int userUnit) {
        this.userUnit = userUnit;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getJoinedGymDate() {
        return joinedGymDate;
    }

    public void setJoinedGymDate(String joinedGymDate) {
        this.joinedGymDate = joinedGymDate;
    }

    public Long getJoinedGymDateTimeMillis() {
        return joinedGymDateTimeMillis;
    }

    public void setJoinedGymDateTimeMillis(Long joinedGymDateTimeMillis) {
        this.joinedGymDateTimeMillis = joinedGymDateTimeMillis;
    }

    public String getMembershipExpirationDate() {
        return membershipExpirationDate;
    }

    public void setMembershipExpirationDate(String membershipExpirationDate) {
        this.membershipExpirationDate = membershipExpirationDate;
    }

    public String getPhotoFileUuid() {
        return photoFileUuid;
    }

    public void setPhotoFileUuid(String photoFileUuid) {
        this.photoFileUuid = photoFileUuid;
    }

    public String getPhotoFileUrl() {
        return photoFileUrl;
    }

    public void setPhotoFileUrl(String photoFileUrl) {
        this.photoFileUrl = photoFileUrl;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }
}
