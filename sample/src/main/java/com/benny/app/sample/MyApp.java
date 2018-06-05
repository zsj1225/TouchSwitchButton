package com.benny.app.sample;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import me.ele.uetool.UETool;

/**
 * Created by shengjun.zhu1 on 2018/6/5.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            private int visibleActivityCount;
            private int uetoolDismissY = 0;

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                visibleActivityCount++;
                if (visibleActivityCount == 1 && uetoolDismissY >= 0) {
                    UETool.showUETMenu(uetoolDismissY);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                visibleActivityCount--;
                if (visibleActivityCount == 0) {
                    uetoolDismissY = UETool.dismissUETMenu();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
