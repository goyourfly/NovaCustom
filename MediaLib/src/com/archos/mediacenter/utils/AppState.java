// Copyright 2017 Archos SA
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


package com.archos.mediacenter.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Keeps track of App being foreground or not
 * Any Activity between onStart / onStop will trigger that
 */
public class AppState {
    private static final Logger log = LoggerFactory.getLogger(AppState.class);

    private static final HashSet<Integer> sStartedActivities = new HashSet<Integer>();
    private static boolean sChangingConfiguration = false;

    public static boolean isForeGround() {
        synchronized (sStartedActivities) {
            log.debug("isForeGround=" + (sStartedActivities.size() > 0));
            return sStartedActivities.size() > 0;
        }
    }

    // Callback when foreground state changes - used from VideoStoreImportService
    public interface OnForeGroundListener {
        void onForeGroundState(Context applicationContext, boolean foreground);
    }

    private static final WeakHashMap<OnForeGroundListener, String> sListeners = new WeakHashMap<OnForeGroundListener, String>();
    public static void addOnForeGroundListener(OnForeGroundListener listener) {
        synchronized (sListeners) {
            sListeners.put(listener, "1");
        }
    }

    public static void removeOnForeGroundListener(OnForeGroundListener listener) {
        synchronized (sListeners) {
            sListeners.remove(listener);
        }
    }

    private static void notifyListener(Activity activity, boolean foreground) {
        log.debug("notifyListener: " + foreground);
        // copy keyset into an array to allow removing of listener from listener.
        OnForeGroundListener[] onForegroundListenerArray = null;
        synchronized (sListeners) {
            if (sListeners.size() > 0) {
                int i = 0;
                onForegroundListenerArray = new OnForeGroundListener[sListeners.size()];
                for (OnForeGroundListener listener : sListeners.keySet())
                    onForegroundListenerArray[i++] = listener;
            }
        }
        if (onForegroundListenerArray != null) {
            for (OnForeGroundListener listener : onForegroundListenerArray) {
                if (listener != null) listener.onForeGroundState(activity.getApplicationContext(), foreground);
            }
        }
    }

    public static final Application.ActivityLifecycleCallbacks sCallbackHandler =
            new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityStopped(Activity activity) {
                    log.debug("onActivityStopped:" + activity);
                    int activityKey = System.identityHashCode(activity);
                    synchronized (sStartedActivities) {
                        boolean foregroundOld = isForeGround();
                        sStartedActivities.remove(activityKey);
                        sChangingConfiguration = activity.isChangingConfigurations();
                        if (!sChangingConfiguration) {
                            boolean foregroundNew = isForeGround();
                            if (foregroundNew != foregroundOld)
                                notifyListener(activity, foregroundNew);
                            log.debug("onActivityStopped isForeGround: " + isForeGround());
                        }
                    }
                }
                private boolean isActuallyForeground(Context context) {
                    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
                    if (runningAppProcesses != null) {
                        int importance = runningAppProcesses.get(0).importance;
                        // higher importance has lower number (?)
                        return importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                    }
                    return false;
                }
                @Override
                public void onActivityStarted(Activity activity) {
                    log.debug("onActivityStarted:" + activity);
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                        if (!isActuallyForeground(activity))
                            return;
                    }
                    int activityKey = System.identityHashCode(activity);
                    synchronized (sStartedActivities) {
                        boolean foregroundOld = isForeGround();
                        sStartedActivities.add(activityKey);
                        if (!sChangingConfiguration) {
                            boolean foregroundNew = isForeGround();
                            if (foregroundNew != foregroundOld)
                                notifyListener(activity, foregroundNew);
                            log.debug("onActivityStarted isForeGround: " + isForeGround());
                        } else {
                            sChangingConfiguration = false;
                        }
                    }
                }
                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    log.debug("onActivitySaveInstanceState:" + activity);
                }
                @Override
                public void onActivityResumed(Activity activity) {
                    log.debug("onActivityResumed:" + activity);
                }
                @Override
                public void onActivityPaused(Activity activity) {
                    log.debug("onActivityPaused:" + activity);
                }
                @Override
                public void onActivityDestroyed(Activity activity) {
                    log.debug("onActivityDestroyed:" + activity);
                }
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    log.debug("onActivityCreated:" + activity);
                }
            };
}
