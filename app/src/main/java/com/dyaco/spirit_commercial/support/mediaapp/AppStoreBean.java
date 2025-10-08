package com.dyaco.spirit_commercial.support.mediaapp;

import static com.dyaco.spirit_commercial.maintenance_mode.MaintenanceAppManagerFragment.UPDATE_X;

import java.util.List;

public class AppStoreBean {


    private List<AppUpdateBeansDTO> appUpdateBeans;

    public List<AppUpdateBeansDTO> getAppUpdateBeans() {
        return appUpdateBeans;
    }

    public void setAppUpdateBeans(List<AppUpdateBeansDTO> appUpdateBeans) {
        this.appUpdateBeans = appUpdateBeans;
    }

    public static class AppUpdateBeansDTO {
        private String comment;
        private String type;
        private String appName;
        private String gmsNeeded;
        private String forceUpdates;
        private String packageName;
        private String version;
        private Integer versionCode;
        private List<IconDTO> icon;
        private List<ApkDTO> apk;
        private List<WebDTO> web;
        private int isUpdate = UPDATE_X;
        private int sort = 99; //讓排序在最後面

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getIsUpdate() {
            return isUpdate;
        }

        public void setIsUpdate(int isUpdate) {
            this.isUpdate = isUpdate;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getGmsNeeded() {
            return gmsNeeded;
        }

        public void setGmsNeeded(String gmsNeeded) {
            this.gmsNeeded = gmsNeeded;
        }

        public String getForceUpdates() {
            return forceUpdates;
        }

        public void setForceUpdates(String forceUpdates) {
            this.forceUpdates = forceUpdates;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Integer getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(Integer versionCode) {
            this.versionCode = versionCode;
        }

        public List<IconDTO> getIcon() {
            return icon;
        }

        public void setIcon(List<IconDTO> icon) {
            this.icon = icon;
        }

        public List<ApkDTO> getApk() {
            return apk;
        }

        public void setApk(List<ApkDTO> apk) {
            this.apk = apk;
        }

        public List<WebDTO> getWeb() {
            return web;
        }

        public void setWeb(List<WebDTO> web) {
            this.web = web;
        }

        public static class IconDTO {
            private String appIconSmallUrl;
            private String appIconMediumUrl;
            private String appIconLargeUrl;

            public String getAppIconSmallUrl() {
                return appIconSmallUrl;
            }

            public void setAppIconSmallUrl(String appIconSmallUrl) {
                this.appIconSmallUrl = appIconSmallUrl;
            }

            public String getAppIconMediumUrl() {
                return appIconMediumUrl;
            }

            public void setAppIconMediumUrl(String appIconMediumUrl) {
                this.appIconMediumUrl = appIconMediumUrl;
            }

            public String getAppIconLargeUrl() {
                return appIconLargeUrl;
            }

            public void setAppIconLargeUrl(String appIconLargeUrl) {
                this.appIconLargeUrl = appIconLargeUrl;
            }
        }

        public static class ApkDTO {
            private String md5;
            private String downloadUrl;
            private String path;

            public String getMd5() {
                return md5;
            }

            public void setMd5(String md5) {
                this.md5 = md5;
            }

            public String getDownloadUrl() {
                return downloadUrl;
            }

            public void setDownloadUrl(String downloadUrl) {
                this.downloadUrl = downloadUrl;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }
        }

        public static class WebDTO {
            private String webUrl;

            public String getWebUrl() {
                return webUrl;
            }

            public void setWebUrl(String webUrl) {
                this.webUrl = webUrl;
            }
        }
    }
}
