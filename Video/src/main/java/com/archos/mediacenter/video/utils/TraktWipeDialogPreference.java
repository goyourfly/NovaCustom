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

package com.archos.mediacenter.video.utils;

import com.archos.mediacenter.utils.trakt.Trakt;

import android.content.Context;

import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import android.util.AttributeSet;

public class TraktWipeDialogPreference extends Preference {
    public TraktWipeDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TraktWipeDialogPreference(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }
    protected void onClick() {
        Trakt.setLoginPreferences(PreferenceManager.getDefaultSharedPreferences(getContext()), null, null);
        Trakt.setAccessToken(PreferenceManager.getDefaultSharedPreferences(getContext()), null);
        notifyChanged();
    }

}
