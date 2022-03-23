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

package com.archos.mediacenter.video.player;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.archos.mediacenter.video.R;

public class SDKNotSupportedDialogActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        showDialog();
    }

    void showDialog() {
        DialogFragment newFragment = SDKNotSupportedDialogFragment.newInstance();
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    public static class SDKNotSupportedDialogFragment extends DialogFragment {

        public static SDKNotSupportedDialogFragment newInstance() {
            SDKNotSupportedDialogFragment frag = new SDKNotSupportedDialogFragment();
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
            .setTitle("Oops...")
            .setMessage(R.string.sdk_not_supported)
            .setPositiveButton(android.R.string.ok, null)
            .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            getActivity().finish();
        }
    }
}
