// Copyright 2021 Courville Software
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

package com.archos.mediacenter.video.browser.loader;

import android.content.Context;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.archos.mediaprovider.video.LoaderUtils;
import com.archos.mediaprovider.video.VideoStore;

public abstract class TvshowsNoAnimeByLoader extends CursorLoader implements CompatAndSDKCursorLoaderFactory {

    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_SUBSET_ID = "_id";
    public static final String COLUMN_LIST_OF_POSTER_FILES = "po_file_list";
    public static final String COLUMN_SUBSET_NAME = "name";
    public static final String COLUMN_LIST_OF_TVSHOWS_IDS = "list";
    public static final String COLUMN_NUMBER_OF_TVSHOWS = "number";
    protected static String COUNT = "COUNT(*) as "+COLUMN_COUNT;

    protected String mSortOrder;
    private boolean mForceHideVideos;

    private static Context mContext;

    public TvshowsNoAnimeByLoader(Context context) {
        super(context);
        mContext = context;
        setUri(VideoStore.RAW_QUERY.buildUpon().appendQueryParameter("group",
                VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID + " IS NOT NULL GROUP BY ( " + VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID + " ) "
        ).build());
        setSelectionArgs(null);

    }

    abstract protected String getSelection(Context context);

    protected String getCommonSelection() {
        StringBuilder sb = new StringBuilder();

        if (LoaderUtils.mustHideUserHiddenObjects()) {
            sb.append(" AND ");
            sb.append(LoaderUtils.HIDE_USER_HIDDEN_FILTER);
        }

        if (LoaderUtils.mustHideWatchedVideo()||mForceHideVideos) {
            sb.append(" AND ");
            sb.append(LoaderUtils.HIDE_WATCHED_FILTER);
        }

        sb.append(" AND ");
        sb.append("( " + VideoStore.Video.VideoColumns.SCRAPER_S_GENRES + " IS NULL OR " +
                VideoStore.Video.VideoColumns.SCRAPER_S_GENRES + " NOT LIKE '%" + mContext.getString(com.archos.medialib.R.string.tvshow_genre_animation) + "%' )");

        return sb.toString();
    }

    public Loader getV4CursorLoader(boolean detailed, boolean hideWatchedVideos){
        mForceHideVideos = hideWatchedVideos;
        return  new CursorLoader(getContext(),
                getUri(), getProjection(), getSelection(), getSelectionArgs(),
                getSortOrder());
    }
}
