package com.dyaco.spirit_commercial.support.mediaapp;

import static com.dyaco.spirit_commercial.MainActivity.isGMS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.APP_APK;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.APP_WEB;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.OPEN_TYPE_USE_CLS_NAME;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.OPEN_TYPE_USE_PKG_NAME;

import com.dyaco.spirit_commercial.R;

//WebView 若出現錯誤訊息，可清除Cache試試
public enum MediaAppEnum {

    NETFLIX(R.string.netflix, "com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    DISNEY_PLUS(R.string.Disney, "com.disney.disneyplus", "com.bamtechmedia.dominguez.main.MainActivity", R.drawable.icon_app_disney_80_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    HULU(R.string.Hulu, "com.hulu.plus", "com.hulu.features.splash.SplashActivity", R.drawable.icon_app__hulu_default, OPEN_TYPE_USE_CLS_NAME, APP_WEB, "https://www.hulu.com/","NO"),
    PRIME_VIDEO(R.string.prime_video, "com.amazon.avod.thirdpartyclient", "com.amazon.avod.thirdpartyclient.LauncherActivity", R.drawable.icon_app_prime_video_80_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "https://www.primevideo.com/","NO"),
    CNN_NEWS(R.string.cnn, "com.cnn.mobile.android.phone", "com.cnn.mobile.android.phone.features.splash.SplashActivity", R.drawable.icon_app_cnn_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    ESPN(R.string.ESPN, "com.espn.score_center", "com.espn.framework.ui.edition.EditionSwitchHelperActivity", R.drawable.icon_app_espn_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    BBC(R.string.BBC, "bbc.mobile.news.uk", "bbc.mobile.news.v3.ui.splash.SplashActivity", R.drawable.icon_app_abc_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    ABC(R.string.abc, "com.abc.abcnews", "com.abc.abcnews.features.onboarding.ABCOnboardingMainActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    NBC(R.string.nbc, "bbc.mobile.news.uk", "bbc.mobile.news.v3.ui.splash.SplashActivity", R.drawable.icon_app_nbc_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    FOX_NEWS(R.string.fox_sports, "com.foxnews.android", "com.foxnews.android.corenav.StartActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    FACEBOOK(R.string.FaceBook, "com.facebook.katana", "com.facebook.katana.LoginActivity", R.drawable.icon_app_fb_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    INSTAGRAM(R.string.Instagram, "com.instagram.android", "com.instagram.android.activity.MainTabActivity", R.drawable.icon_app__ig_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    TWITTER(R.string.Twitter, "com.twitter.android", "com.twitter.android.StartActivity", R.drawable.icon_app__x_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    SPOTIFY(R.string.Spotify, "com.spotify.music", "com.spotify.music.MainActivity", R.drawable.icon_app__spotify_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    IHEART(R.string.iHeart, "com.clearchannel.iheartradio.controller", "com.iheart.activities.NavDrawerActivity", R.drawable.icon_app__iheart_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    WEATHER(R.string.Weather, "com.weather.forecast.weatherchannel", "com.weather.forecast.weatherchannel.activities.SettingActivity", R.drawable.icon_app__weather_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    STUDIO(R.string.studio, "com.trystudio.studio", "com.trystudio.studio.ui.MainActivity", R.drawable.icon_app__twitter_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    GOOGLE_NEWS(R.string.netflix, "com.trystudio.studio", "com.trystudio.studio.ui.MainActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    REUTERS_NEWS(R.string.netflix, "com.trystudio.studio", "com.trystudio.studio.ui.MainActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    SOUND_CLOUD(R.string.netflix, "com.trystudio.studio", "com.trystudio.studio.ui.MainActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    YAHOO_SPORT(R.string.netflix, "com.yahoo.mobile.client.android.sportacular", "com.yahoo.mobile.ysports.activity.onboard.OnboardingActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    PEACOCK(R.string.peacock_tv, "com.peacocktv.peacockandroid", "com.nowtv.startup.StartupActivity", R.drawable.icon_app_peacock_tv_80_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    HBO_MAX(R.string.hbo_max, "com.wbd.stream", "com.wbd.stream.MainActivity", R.drawable.icon_app_max_80_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    KINOMAP(R.string.Kinomap, "com.kinomap.training.embedded", "com.kinomap.training.embedded/.MainActivity", R.drawable.icon_app_kinomap_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    YOUTUBE_TV(R.string.youtube_tv, "com.google.android.apps.youtube.unplugged", "com.google.android.apps.youtube.unplugged.features.main.MainActivity", R.drawable.icon_app_youtube_tv_80_default, OPEN_TYPE_USE_CLS_NAME, isGMS ? APP_APK : APP_WEB, "https://tv.youtube.com/","YES"),
    YOUTUBE(R.string.youtube, "com.google.android.youtube", "com.google.android.apps.youtube.app.watchwhile.WatchWhileActivity", R.drawable.icon_app_youtube_default, OPEN_TYPE_USE_PKG_NAME, isGMS ? APP_APK : APP_WEB, "https://www.youtube.com/","YES"),
    AUDIO_BOOKS(R.string.netflix, "com.audible.application", "com.audible.application.SplashScreen", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    TENCENT_VIDEO(R.string.netflix, "com.tencent.qqlive", "com.tencent.qqlive.ona.activity.SplashHomeActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    TV_HOME(R.string.netflix, "com.dianshijia.newlive", "com.dianshijia.newlive.entry.SplashActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    NETEASE_CLOUD_MUSIC(R.string.netflix, "com.netease.cloudmusic.tv", "com.netease.cloudmusic.app.LoadingActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    IQIY(R.string.netflix, "com.qiyi.video", "com.qiyi.video.WelcomeActivity", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    QQ_MUSIC(R.string.netflix, "com.tencent.qqmusic", "com.kinomap.training.embedded", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    BILI_BILI(R.string.netflix, "tv.danmaku.bili", "tv.danmaku.bili.MainActivityV2", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_CLS_NAME, APP_APK, "","NO"),
    BIT_GYM(R.string.BitGym, "com.activetheoryinc.BitGym", "com.activetheoryinc.BitGym/com.google.firebase.MessagingUnityPlayerActivity", R.drawable.icon_app_bitgym_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    PLUTO_TV(R.string.pluto_tv, "tv.pluto.android", "com.kinomap.training.embedded", R.drawable.icon_app_pluto_tv_80_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "https://pluto.tv/","NO"),
    EDGE(R.string.netflix, "com.microsoft.emmx", "com.kinomap.training.embedded", R.drawable.icon_app_netflix_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    MIDNIGHT_PULP(R.string.Midnight_Pulp, "com.dmr.midnightpulp", "com.kinomap.training.embedded", R.drawable.icon_app_midnight_pulp_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    IMDB_TV(R.string.IMDb_TV, "com.imdb.mobile", "com.kinomap.training.embedded", R.drawable.icon_app_imdb_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    CRACKLE(R.string.Crackle, "com.crackle.androidtv", "com.kinomap.training.embedded", R.drawable.icon_app_crackle_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "","NO"),
    PARAMOUNT_PLUS(R.string.Paramount, "com.cbs.app", "", R.drawable.icon_app_paramount_default, OPEN_TYPE_USE_PKG_NAME, APP_APK, "https://www.paramountplus.com","NO"),
    TUBI(R.string.tubi, "com.tubitv", "com.tubitv.activities.MainActivity", R.drawable.icon_app_tubi_80_default, OPEN_TYPE_USE_PKG_NAME, APP_WEB, "https://tubitv.com/","NO");

    String appPackageName;
    String appClassName;
    int appIcon;
    int appName;

    int openType;
    int appType;
    String appUrl;
    String isGmsNeed;

    public String getIsGmsNeed() {
        return isGmsNeed;
    }

    public void setIsGmsNeed(String isGmsNeed) {
        this.isGmsNeed = isGmsNeed;
    }

    MediaAppEnum(int appName, String appPackageName, String appClassName, int appIcon, int openType, int appType, String appUrl, String isGmsNeed) {
        this.appName = appName;
        this.appPackageName = appPackageName;
        this.appClassName = appClassName;
        this.appIcon = appIcon;
        this.openType = openType;
        this.appType = appType;
        this.appUrl = appUrl;
        this.isGmsNeed = isGmsNeed;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppClassName() {
        return appClassName;
    }

    public void setAppClassName(String appClassName) {
        this.appClassName = appClassName;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(int appIcon) {
        this.appIcon = appIcon;
    }

    public int getAppName() {
        return appName;
    }

    public void setAppName(int appName) {
        this.appName = appName;
    }

    public int getOpenType() {
        return openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public static MediaAppEnum getMediaApp(String appPackageName) {
        for (MediaAppEnum mediaAppEnum : values()) {
            if (appPackageName.equals(mediaAppEnum.getAppPackageName())) {
                return mediaAppEnum;
            }
        }
        return MediaAppEnum.getMediaApp(NETFLIX.getAppPackageName());
    }
}
