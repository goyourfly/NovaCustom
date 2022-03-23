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

package com.archos.mediacenter.video.browser.filebrowsing.network.SmbBrowser;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.archos.environment.NetworkState;
import com.archos.filecorelibrary.FileEditorFactory;
import com.archos.filecorelibrary.samba.SambaDiscovery;
import com.archos.filecorelibrary.samba.Workgroup;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.browser.filebrowsing.network.UpnpSmbCommonRootFragment;
import com.archos.mediacenter.video.browser.filebrowsing.network.WorkgroupShortcutAndServerAdapter;
import com.archos.mediaprovider.NetworkScanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by alexandre on 28/05/15.
 */
public class SmbRootFragment extends UpnpSmbCommonRootFragment implements SambaDiscovery.Listener {
    private static final Logger log = LoggerFactory.getLogger(SmbRootFragment.class);
    private SambaDiscovery mSambaDiscovery;

    private AsyncTask<Void, Void, Void> mCheckShortcutAvailabilityTask;

    public SmbRootFragment(){
        super();
    }

    @Override
    protected WorkgroupShortcutAndServerAdapter getAdapter() {
        return new SmbWorkgroupShortcutAndServerAdapter(getActivity());
    }

    @Override
    protected void rescanAvailableShortcuts() {
        Cursor cursor = ShortcutDbAdapter.VIDEO.getAllShortcuts(getActivity(), ShortcutDbAdapter.KEY_PATH+" LIKE ?",new String[]{"smb%"});
        if (cursor == null) return;
        int uriIndex = cursor.getColumnIndex(ShortcutDbAdapter.KEY_PATH);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            do {
                String path = cursor.getString(uriIndex);
                if (((WorkgroupShortcutAndServerAdapter) mAdapter).getShares().contains(Uri.parse(path).getHost().toLowerCase())) {
                    NetworkScanner.scanVideos(getActivity(), path);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, R.string.refresh_servers_list, Menu.NONE, R.string.refresh_servers_list).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.string.refresh_servers_list) {
            // restart the discovery (if there is connectivity and not already discovering)
            if (NetworkState.isNetworkConnected(getActivity())&&!mSambaDiscovery.isRunning()) {
                mSambaDiscovery.start();
                checkShortcutAvailability();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated (View v, Bundle saved){
        if (saved!=null) {
            // Restart the discovery if it was running when saving the instance
            if (saved.getBoolean("isRunning")) {
                mSambaDiscovery.start();
            }
        }
        else {
            // First initialization, start the discovery (if there is connectivity)
            if (NetworkState.isNetworkConnected(getActivity())) {
                mSambaDiscovery.start();
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
         // Remember if the discovery is still running in order to restart it when restoring the fragment
        outState.putBoolean("isRunning", mSambaDiscovery.isRunning());
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        log.debug("onAttach mSambaDiscovery");
        // Instantiate the SMB discovery as soon as we get the activity context
        mSambaDiscovery = new SambaDiscovery(activity);
        mSambaDiscovery.setMinimumUpdatePeriodInMs(100);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSambaDiscovery.addListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.debug("onDestroy");
        mSambaDiscovery.removeListener(this);
        if(mCheckShortcutAvailabilityTask!=null) {
            log.debug("onDestroy: cancel mCheckShortcutAvailabilityTask");
            mCheckShortcutAvailabilityTask.cancel(true);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        log.debug("onDestroyView");
        mSambaDiscovery.abort();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        log.debug("onDetach");
        mSambaDiscovery.abort();
    }
    /**
     * Start or restart the discovery.
     * Not needed at initialization since the fragment will start it by itself (if there is connectivity)
     */
    public void startDiscovery() {
        mSambaDiscovery.start();
    }

    // SambaDiscovery.Listener implementation
    @Override
    public void onDiscoveryStart() {
        ((WorkgroupShortcutAndServerAdapter)mAdapter).setIsLoadingWorkgroups(true);
    }

    // SambaDiscovery.Listener implementation
    @Override
    public void onDiscoveryEnd() {
        ((WorkgroupShortcutAndServerAdapter)mAdapter).setIsLoadingWorkgroups(false);
    }

    // SambaDiscovery.Listener implementation
    @Override
    public void onDiscoveryUpdate(List<Workgroup> workgroups) {
        ((SmbWorkgroupShortcutAndServerAdapter)mAdapter).updateWorkgroups(workgroups);
        mAdapter.notifyDataSetChanged();
    }

    // SambaDiscovery.Listener implementation
    @Override
    public void onDiscoveryFatalError() {
        log.debug("onDiscoveryFatalError");
        ((WorkgroupShortcutAndServerAdapter)mAdapter).setIsLoadingWorkgroups(false);
    }

    private void checkShortcutAvailability(){
        if(mCheckShortcutAvailabilityTask!=null) {
            log.debug("checkShortcutAvailability: cancel non null mCheckShortcutAvailabilityTask before launching one");
            mCheckShortcutAvailabilityTask.cancel(true);
        }
        mCheckShortcutAvailabilityTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... arg0) {
                List<ShortcutDbAdapter.Shortcut> shortcuts = mAdapter.getShortcuts();
                List<String> shares = mAdapter.getAvailableShares();
                List<String> forcedShortcuts = mAdapter.getForcedEnabledShortcuts();
                if(shortcuts == null) return null;
                // FIXME: manage display/remove of shortcuts not only adding
                // below code does not do anything in terms of forcing display shortcut since it is anyway displayed and creates an issue with jcifs-ng #377
                // it is anyway displayed and creates an issue with jcifs-ng #377 : disable it for now
                // re-enable since otherwise stunnel smb://127.0.0.1:xxxx links are marked non available
                for (ShortcutDbAdapter.Shortcut shortcut : shortcuts) {
                    Uri uri = Uri.parse(shortcut.getUri());
                    log.debug("checkShortcutAvailability.doInBackground: checking " + shortcut.getUri());
                    if ((shares == null || !shares.contains(uri.getHost().toLowerCase())) // share not listed yet...
                            &&!forcedShortcuts.contains(shortcut.getUri()) // it is not a forced shortcut
                            && FileEditorFactory.getFileEditorForUrl(uri, getActivity()).exists()) { // shortcut exists
                        log.debug("checkShortcutAvailability.doInBackground: shortcut " + shortcut.getUri() + " is available, display it!");
                        mAdapter.forceShortcutDisplay(shortcut.getUri());
                    } else {
                        log.debug("checkShortcutAvailability.doInBackground: it is there, no need to check " + shortcut.getUri());
                    }
                }
                log.debug("checkShortcutAvailability.doInBackground: check finished");
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    protected void loadIndexedShortcuts() {
        Cursor cursor = ShortcutDbAdapter.VIDEO.getAllShortcuts(getActivity(), ShortcutDbAdapter.KEY_PATH+" LIKE ?",new String[]{"smb%"});
        mAdapter.updateIndexedShortcuts(cursor);
        if (cursor != null) cursor.close();
        mAdapter.notifyDataSetChanged();
        checkShortcutAvailability();
    }
}
