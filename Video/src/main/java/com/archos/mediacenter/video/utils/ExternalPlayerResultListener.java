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

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.archos.mediacenter.utils.trakt.Trakt;
import com.archos.mediacenter.utils.trakt.TraktService;
import com.archos.mediacenter.utils.videodb.IndexHelper;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.browser.TorrentObserverService;
import com.archos.mediacenter.video.player.PrivateMode;
import com.archos.mediaprovider.video.VideoStore;
import com.archos.mediascraper.ScrapeDetailResult;

import static com.archos.filecorelibrary.FileUtils.removeFileSlashSlash;

/**
 * Created by alexandre on 19/09/16.
 */
public class ExternalPlayerResultListener implements ExternalPlayerWithResultStarter.ResultListener, IndexHelper.Listener {

    private static String TAG = "ExternalPlayerResultListener";
    private static final boolean DBG = false;

    private static ExternalPlayerResultListener sExternalPlayerResultListener;
    private Context mContext;
    private TraktService.Client mTraktClient;
    private final TraktService.Client.Listener mTraktListener = new TraktService.Client.Listener() {
        @Override
        public void onResult(Bundle bundle) { }
    };
    private Uri mContentUri;
    private Uri mPlayerUri;
    private IndexHelper mIndexHelper;
    private VideoDbInfo mVideoDbInfo;
    private int mDuration;

    public static class ExternalPositionExtra{
        public static String VLC_MXPLAYER_ACTION_EXTRA_position = "position";
        public static String VLC_RESULT_EXTRA_position = "extra_position";
        public static String MXPLAYER_RESULT_EXTRA_position = "position";
        public static String MXPLAYER_RESULT_EXTRA_duration = "duration";
        public static String VLC_RESULT_EXTRA_duration = "extra_duration";


        public static void setAllPositionExtras(Intent intent, int position){
            intent.putExtra(VLC_MXPLAYER_ACTION_EXTRA_position, position);
            intent.putExtra("return_result",true);//for mxplayer
        }
    }
    public static ExternalPlayerResultListener getInstance(){
        if(sExternalPlayerResultListener==null)
            sExternalPlayerResultListener = new ExternalPlayerResultListener();
        return sExternalPlayerResultListener;
    }
    public void init(Context context, Uri contentUri, Uri playerUri, VideoDbInfo videoDbInfo){
        mContext = context;
        mContentUri = contentUri;
        mPlayerUri = playerUri;
        if (DBG) Log.d(TAG, "init: playerUri=" + playerUri + ", contentUri=" + contentUri);
        mContentUri = Uri.parse(removeFileSlashSlash(mContentUri.toString())); // we need to remove "file://"
        if (!PrivateMode.isActive() && Trakt.isTraktV2Enabled(mContext, PreferenceManager.getDefaultSharedPreferences(mContext)))
            mTraktClient = new TraktService.Client(mContext, mTraktListener, false);
        else
            mTraktClient = null;
        //get video info, useful to save video state
        mIndexHelper = new IndexHelper(context, null, 0);
        if(videoDbInfo!=null){
            mVideoDbInfo = videoDbInfo;
        }
        else {
            mIndexHelper.requestVideoDb(mContentUri, -1,
                    null,
                    this, false, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DBG) Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (DBG) Log.d(TAG, "onActivityResult: mVideoDbInfo!=null " + (mVideoDbInfo!=null));
        if (DBG) Log.d(TAG, "onActivityResult: mPlayerUri " + mPlayerUri);
        if (data != null) {
            if (DBG) Log.d(TAG, "onActivityResult: data.getData()=" + data.getData());
            // for vlc data.getData() is null
            // for mxplayer mPlayerUri is content://com.archos.media.videocommunity/external/video/media/xxxx and data.getData() is content://org.courville.nova.provider/external_files/emulated/0/path/file.mkv
            //if(!PrivateMode.isActive() && resultCode== Activity.RESULT_OK && mVideoDbInfo!=null && data.getData()!= null && data.getData().equals(mPlayerUri)){
            if (!PrivateMode.isActive() && resultCode == Activity.RESULT_OK && mVideoDbInfo != null) {
                if (DBG) Log.d(TAG, "onActivityResult: process the if");
                int position = 0;
                if (DBG)
                    Log.d(TAG, "onActivityResult: MXPLAYER_RESULT_EXTRA_position=" + data.getIntExtra(ExternalPositionExtra.MXPLAYER_RESULT_EXTRA_position, -1));
                if (DBG)
                    Log.d(TAG, "onActivityResult: VLC_RESULT_EXTRA_position=" + data.getLongExtra(ExternalPositionExtra.VLC_RESULT_EXTRA_position, -1));
                if (DBG)
                    Log.d(TAG, "onActivityResult: MXPLAYER_RESULT_EXTRA_duration=" + data.getIntExtra(ExternalPositionExtra.MXPLAYER_RESULT_EXTRA_duration, -1));
                if (DBG)
                    Log.d(TAG, "onActivityResult: VLC_RESULT_EXTRA_duration=" + data.getLongExtra(ExternalPositionExtra.VLC_RESULT_EXTRA_duration, -1));
                if (DBG)
                    Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
                if (data.getIntExtra(ExternalPositionExtra.MXPLAYER_RESULT_EXTRA_position, -1) != -1) {//mxplayer
                    position = data.getIntExtra(ExternalPositionExtra.MXPLAYER_RESULT_EXTRA_position, -1);
                } else if (data.getLongExtra(ExternalPositionExtra.VLC_RESULT_EXTRA_position, -1) != -1) {//vlc
                    position = (int) data.getLongExtra(ExternalPositionExtra.VLC_RESULT_EXTRA_position, -1);
                }
                if (data.getIntExtra(ExternalPositionExtra.MXPLAYER_RESULT_EXTRA_duration, -1) > 0) {
                    mDuration = data.getIntExtra(ExternalPositionExtra.MXPLAYER_RESULT_EXTRA_duration, -1);
                } else if (data.getLongExtra(ExternalPositionExtra.VLC_RESULT_EXTRA_duration, -1) > 0) {
                    mDuration = (int) data.getLongExtra(ExternalPositionExtra.VLC_RESULT_EXTRA_duration, -1);
                }
                if (DBG)
                    Log.d(TAG, "onActivityResult: position=" + position + ", duration=" + mDuration);
                mVideoDbInfo.lastTimePlayed = Long.valueOf(System.currentTimeMillis() / 1000L);
                if (position != -1) {
                    mVideoDbInfo.resume = position;
                    mIndexHelper.writeVideoInfo(mVideoDbInfo, true);
                }
                TorrentObserverService.staticExitProcess();
                TorrentObserverService.killProcess();
                stopTrakt(getPercentProgress(position));
            }
        } else {
            // happens when hitting back before selecting the player
            if (DBG) Log.d(TAG, "onActivityResult: data is null!");
        }
    }
    private int getPercentProgress(int position) {
        int progress = 0;
        int duration = mDuration!=-1?mDuration : mVideoDbInfo.duration;
        if (position >= 0 && duration > 0 && position <= duration)
            progress = (int) (position / (double) duration * 100);

        return progress;
    }
    private void stopTrakt(int percentProgress) {
        if (mTraktClient != null) {

            if (percentProgress >= 0&&!Trakt.shouldMarkAsSeen(percentProgress)) {
                mVideoDbInfo.traktResume = -percentProgress;
                mTraktClient.watchingStop(mVideoDbInfo, percentProgress);

            } else if (Trakt.shouldMarkAsSeen(percentProgress)) {
                mTraktClient.markAs(mVideoDbInfo, Trakt.ACTION_SEEN);
            }
        }
        // We now use the DB flag ARCHOS_TRAKT_SEEN even if there is no sync with trakt
        else {
            if (mVideoDbInfo.id>=0 && Trakt.shouldMarkAsSeen(percentProgress) && !PrivateMode.isActive()) {
                final ContentValues cv = new ContentValues(1);
                cv.put(VideoStore.Video.VideoColumns.ARCHOS_TRAKT_SEEN, Trakt.TRAKT_DB_MARKED);
                String where = VideoStore.Video.VideoColumns._ID + " = ?";
                String[] whereArgs = new String[] {Long.toString(mVideoDbInfo.id)};
                mContext.getContentResolver().update(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, cv, where, whereArgs);
            }
        }
    }
    @Override
    public void onVideoDb(VideoDbInfo info, VideoDbInfo remoteInfo) {
        mVideoDbInfo = info;
    }

    @Override
    public void onScraped(ScrapeDetailResult result) {

    }
}
