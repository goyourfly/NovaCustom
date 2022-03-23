package com.archos.mediacenter.video.browser.loader;

import android.content.Context;

import com.archos.mediacenter.video.browser.loader.VideoLoader;
import com.archos.mediaprovider.video.VideoStore;

public class WatchingUpNextLoader extends VideoLoader {

    public WatchingUpNextLoader(Context context) {
        super(context);
        init();
    }

    @Override
    public String getSelection() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getSelection());

        if (builder.length() > 0)
            builder.append(" AND ");

        builder.append(
            "(bookmark > 0 OR e_id in (" +
                "SELECT n.e_id " +
                "FROM video n " +
                "INNER JOIN video w " +
                "ON n.s_id = w.s_id AND (" +
                    "CASE " +
                        "WHEN w.e_episode = (" +
                            "SELECT MAX(e_episode) FROM video WHERE e_id IS NOT NULL AND s_id = w.s_id AND e_season = w.e_season" +
                        ") " +
                        "THEN n.e_season = w.e_season + 1 AND n.e_episode = (" +
                            "SELECT MIN(e_episode) FROM video WHERE e_id IS NOT NULL AND s_id = n.s_id AND e_season = n.e_season" +
                        ") " +
                        "ELSE n.e_season = w.e_season AND n.e_episode = w.e_episode + 1 " +
                    "END" +
                ") " +
                "WHERE n.e_id IS NOT NULL AND n.Archos_lastTimePlayed = 0 AND w.e_id IS NOT NULL AND w.Archos_lastTimePlayed != 0" +
            "))"
        );

        return builder.toString();
    }

    @Override
    public String getSortOrder() {
        return "CASE WHEN " + VideoStore.Video.VideoColumns.BOOKMARK + " > 0 THEN 0 ELSE 1 END, " + VideoStore.Video.VideoColumns.ARCHOS_LAST_TIME_PLAYED + " DESC, " + VideoLoader.DEFAULT_SORT + " LIMIT 100";
    }
}