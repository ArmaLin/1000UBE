package com.dyaco.spirit_commercial;

import android.app.Application;

import timber.log.Timber;

/**
 * 這個類別只會在 debug build variant 中被編譯。
 */
public class DebugInitializer {
  public static void init(Application app) {
    // 初始化 Timber for Debug
    Timber.plant(new Timber.DebugTree());
   // Timber.plant(new UiDebugTree());



    // 初始化 LeakCanary
//    LeakCanary.Config config = LeakCanary.getConfig().newBuilder()
//            .retainedVisibleThreshold(1)
//            .build();
//    LeakCanary.setConfig(config);

  }
}