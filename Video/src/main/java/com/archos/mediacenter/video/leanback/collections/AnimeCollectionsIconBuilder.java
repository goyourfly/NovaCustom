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

package com.archos.mediacenter.video.leanback.collections;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.PlayerActivity;
import com.archos.mediaprovider.video.LoaderUtils;
import com.archos.mediaprovider.video.ScraperStore;
import com.archos.mediaprovider.video.VideoStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Build a bitmap composed of 8 anime collection posters to be used in the main leanback activity
 */
public class AnimeCollectionsIconBuilder {

    final static String[] PROJECTION = {
            VideoStore.Video.VideoColumns.SCRAPER_C_POSTER_LARGE_FILE
    };

    // limitation: cannot remove animation genre because ScraperStore.MovieCollections has no genre
    // and we are dealing with collection posters here i.e. no video posters
    static String SELECTION;

    private static final String TAG = "AnimeCollectionsIconBuilder";
    private static final Boolean DBG = false;
    final Context mContext;
    final int mWidth;
    final int mHeight;

    public AnimeCollectionsIconBuilder(Context context) {
        mContext = context;
        SELECTION = VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER + "=0 AND " +
                VideoStore.Video.VideoColumns.SCRAPER_MOVIE_ID + " IS NOT NULL AND " +
                VideoStore.Video.VideoColumns.SCRAPER_COVER + " IS NOT NULL AND " +
                VideoStore.Video.VideoColumns.SCRAPER_C_ID + " > '0' AND " +
                VideoStore.Video.VideoColumns.SCRAPER_C_POSTER_LARGE_FILE + " IS NOT NULL AND " +
                VideoStore.Video.VideoColumns.SCRAPER_M_GENRES + " LIKE '%" + mContext.getString(com.archos.medialib.R.string.movie_genre_animation) + "%'" +
                ") GROUP BY (" + VideoStore.Video.VideoColumns.SCRAPER_C_ID;
        mWidth  = context.getResources ().getDimensionPixelSize(R.dimen.all_collections_icon_width);
        mHeight  = context.getResources ().getDimensionPixelSize(R.dimen.all_collections_icon_height);
    }

    public Bitmap buildNewBitmap() {
        long start = SystemClock.elapsedRealtime();
        long startThread = SystemClock.currentThreadTimeMillis();

        Bitmap bitmap=null;
        // catch all exception because we REALLY don't want to crash the whole application in case there is any issue with this cosmetic feature
        try {
            bitmap = buildIconBitmap(mContext.getContentResolver());
        } catch (Exception ex) {
            Log.e(TAG, "buildIconBitmap exception ", ex);
        }

        long endThread = SystemClock.currentThreadTimeMillis();
        long end = SystemClock.elapsedRealtime();
        if (DBG) Log.d(TAG, "buildNewBitmap took "+(endThread-startThread)+" | "+(end-start));
        return bitmap;
    }

    private Bitmap buildIconBitmap(ContentResolver cr) {
        List<String> posters = getPostersList(cr);

        if (posters.size() == 0) {
            if (DBG) Log.d(TAG, "not enough collections with poster to build the icon");
            return null;
        }

        return composeBitmap(posters);
    }

    private List<String> getPostersList(ContentResolver cr) {
        Cursor c = cr.query(VideoStore.Video.Media.EXTERNAL_CONTENT_URI,
                PROJECTION, SELECTION, null,
                "RANDOM() LIMIT 12"); // get 12 random movies (8 + 4 in case some posters are invalid for any reason)

        if (c.getCount()==0) {
            c.close();
            return Collections.emptyList();
        }

        final int coverIndex = c.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_C_POSTER_LARGE_FILE);
        c.moveToFirst();

        ArrayList<String> list = new ArrayList<>(c.getCount());

        while (!c.isAfterLast()) {
            list.add(c.getString(coverIndex));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    private Bitmap composeBitmap(List<String> posters) {

        View compositionView  = LayoutInflater.from(mContext).inflate(R.layout.all_collections_icon, null);

        // Get the 8 ImagesViews from the layout
        ArrayList<ImageView> imageViews = new ArrayList<>(8);
        for (int id : new int[] {R.id.poster1, R.id.poster2, R.id.poster3, R.id.poster4, R.id.poster5, R.id.poster6, R.id.poster7, R.id.poster8}) {
            imageViews.add((ImageView) compositionView.findViewById(id));
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; // posters are way larger than needed here, we can sub-sample
        options.inScaled = false; // no need to take care of the screen density at this stage

        // Decode the posters and assign to the image views
        Iterator<String> poster = posters.iterator();
        for (ImageView iv : imageViews) {
            if (!poster.hasNext())
                poster = posters.iterator();
            
            // try next posters in case the first one fails to decode
            while (poster.hasNext()) {
                Bitmap b = BitmapFactory.decodeFile(poster.next(), options);
                if (b!=null) {
                    iv.setImageBitmap(b);
                    break;
                }
            }
        }

        // measureAndLayout
        compositionView.measure(View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.EXACTLY));
        compositionView.layout(0, 0, mWidth, mHeight);

        // make the bitmap
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap( mWidth, mHeight, Bitmap.Config.ARGB_8888 );
        canvas.setBitmap(bitmap);
        compositionView.draw(canvas);
        return bitmap;
    }
}
