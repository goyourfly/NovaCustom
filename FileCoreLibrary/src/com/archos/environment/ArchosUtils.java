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

package com.archos.environment;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class ArchosUtils {
    private static final String TAG = "ArchosUtils";
    private static final boolean DBG = false;

    private static Context globalContext;

    public static boolean isAmazonApk() {
       return android.os.Build.MANUFACTURER.toLowerCase().equals("amazon");
    }

    public static boolean isInstalledfromPlayStore(Context context) {
        final List<String> playStoreInstallerPackageNames = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installerPackageName = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        if (DBG) Log.d(TAG, "isInstalledfromPlayStore: installerPackageName = " + installerPackageName);
        return installerPackageName != null && playStoreInstallerPackageNames.contains(installerPackageName);
    }

    public static String getNameWithoutExtension(String filenameWithExtension) {
        int dotPos = filenameWithExtension.lastIndexOf('.');
        if (dotPos >= 0 && dotPos < filenameWithExtension.length()) {
            return filenameWithExtension.substring(0, dotPos);
        } else {
            return filenameWithExtension;
        }
    }

    public static String getExtension(String filename) {
        if (filename == null)
            return null;
        int dotPos = filename.lastIndexOf('.');
        if (dotPos >= 0 && dotPos < filename.length()) {
            return filename.substring(dotPos + 1).toLowerCase();
        }
        return null;
    }

    public static void setGlobalContext(Context globalContext) {
        ArchosUtils.globalContext = globalContext;
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

}
