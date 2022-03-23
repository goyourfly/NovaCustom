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

import android.net.Uri;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceFragmentCompat;

import com.archos.mediacenter.video.R;

public class VideoPreferencesLicencesFragment extends VideoPreferencesFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences_licences);
    }


    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        try {
            Uri uri = preference.getIntent().getData();
            if (uri.getScheme().startsWith("http")) {
                WebUtils.openWebLink(getActivity(), uri.toString());
                return true;
            }
        }
        catch (NullPointerException npe) {}

        return super.onPreferenceTreeClick(preference);
    }
}
