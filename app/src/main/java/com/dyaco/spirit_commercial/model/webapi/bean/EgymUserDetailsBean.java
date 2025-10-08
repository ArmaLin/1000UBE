package com.dyaco.spirit_commercial.model.webapi.bean;

public class EgymUserDetailsBean {

    private String userId;
    private String firstName;
    private String lastName;
    private ImageDTO image;
    private String gender;
    private String dateOfBirth;
    private Double height;
    private Double weight;
    private String locale;
    private String unitSystem;
    private OnboardingChecklistDTO onboardingChecklist;
    private Boolean termsAndConditionsAccepted;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ImageDTO getImage() {
        return image;
    }

    public void setImage(ImageDTO image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUnitSystem() {
        return unitSystem;
    }

    public void setUnitSystem(String unitSystem) {
        this.unitSystem = unitSystem;
    }

    public OnboardingChecklistDTO getOnboardingChecklist() {
        return onboardingChecklist;
    }

    public void setOnboardingChecklist(OnboardingChecklistDTO onboardingChecklist) {
        this.onboardingChecklist = onboardingChecklist;
    }

    public Boolean getTermsAndConditionsAccepted() {
        return termsAndConditionsAccepted;
    }

    public void setTermsAndConditionsAccepted(Boolean termsAndConditionsAccepted) {
        this.termsAndConditionsAccepted = termsAndConditionsAccepted;
    }

    public static class ImageDTO {
        private String imageType;
        private String imageId;

        public String getImageType() {
            return imageType;
        }

        public void setImageType(String imageType) {
            this.imageType = imageType;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }
    }

    public static class OnboardingChecklistDTO {
        private PreferencesDTO preferences;
        private StrengthTestDTO strengthTest;
        private BodyMeasurementDTO bodyMeasurement;
        private MachineSettingsMeasurementsDTO machineSettingsMeasurements;

        public PreferencesDTO getPreferences() {
            return preferences;
        }

        public void setPreferences(PreferencesDTO preferences) {
            this.preferences = preferences;
        }

        public StrengthTestDTO getStrengthTest() {
            return strengthTest;
        }

        public void setStrengthTest(StrengthTestDTO strengthTest) {
            this.strengthTest = strengthTest;
        }

        public BodyMeasurementDTO getBodyMeasurement() {
            return bodyMeasurement;
        }

        public void setBodyMeasurement(BodyMeasurementDTO bodyMeasurement) {
            this.bodyMeasurement = bodyMeasurement;
        }

        public MachineSettingsMeasurementsDTO getMachineSettingsMeasurements() {
            return machineSettingsMeasurements;
        }

        public void setMachineSettingsMeasurements(MachineSettingsMeasurementsDTO machineSettingsMeasurements) {
            this.machineSettingsMeasurements = machineSettingsMeasurements;
        }

        public static class PreferencesDTO {
            private String state;
            private Object expiresAt;

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public Object getExpiresAt() {
                return expiresAt;
            }

            public void setExpiresAt(Object expiresAt) {
                this.expiresAt = expiresAt;
            }
        }

        public static class StrengthTestDTO {
            private String state;
            private Object expiresAt;

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public Object getExpiresAt() {
                return expiresAt;
            }

            public void setExpiresAt(Object expiresAt) {
                this.expiresAt = expiresAt;
            }
        }

        public static class BodyMeasurementDTO {
            private String state;
            private Double expiresAt;

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public Double getExpiresAt() {
                return expiresAt;
            }

            public void setExpiresAt(Double expiresAt) {
                this.expiresAt = expiresAt;
            }
        }

        public static class MachineSettingsMeasurementsDTO {
            private String state;
            private Object expiresAt;

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public Object getExpiresAt() {
                return expiresAt;
            }

            public void setExpiresAt(Object expiresAt) {
                this.expiresAt = expiresAt;
            }
        }
    }
}
