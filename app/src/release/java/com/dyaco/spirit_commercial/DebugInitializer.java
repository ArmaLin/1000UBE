package com.dyaco.spirit_commercial;

import android.app.Application;

/**
 * 這個類別只會在 release build variant 中被編譯。
 */
public class DebugInitializer {
  public static void init(Application app) {
    //  Timber.plant(new ReleaseTree());


    // TODO: 開log
      //  Timber.plant(new Timber.DebugTree());
  }
}