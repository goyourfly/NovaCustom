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

package com.archos.mediacenter.video.leanback.overlay;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.archos.mediacenter.video.R;
import com.archos.mediaprovider.ImportState;
import com.archos.mediaprovider.video.NetworkScannerReceiver;
import com.archos.mediascraper.AutoScrapeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vapillon on 16/06/15.
 */
public class ScannerAndScraperProgress {

    private static final Logger log = LoggerFactory.getLogger(ScannerAndScraperProgress.class);

    // For now i'm doing some basic polling...
    final static int REPEAT_PERIOD_MS = 1000;

    final private View mProgressGroup;
    final private ProgressBar mProgressWheel;
    final private TextView mCount;
    final private String mInitialScanMessage;
    final Handler mRepeatHandler = new Handler();


    /** the visibility due to the general state of the fragment */
    private int mGeneralVisibility = View.GONE;

    /** the visibility due to the scanner and scraper state */
    private int mStatusVisibility = View.GONE;

    public ScannerAndScraperProgress(Context context, View overlayContainer) {
        mProgressGroup = overlayContainer.findViewById(R.id.progress_group);
        mProgressWheel = (ProgressBar) mProgressGroup.findViewById(R.id.progress);
        mCount = (TextView) mProgressGroup.findViewById(R.id.count);
        log.debug("ScannerAndScraperProgress: creation");
        mInitialScanMessage = context.getString(R.string.initial_scan);
        mRepeatHandler.post(mRepeatRunnable);
    }

    public void destroy() {
        log.debug("destroy");
        // all things that need to be stopped are stopped in pause() already
    }

    public void resume() {
        log.debug("resume: view visible");
        mGeneralVisibility = View.VISIBLE;
        updateCount();
        updateVisibility();
        mRepeatHandler.post(mRepeatRunnable);
    }

    public void pause() {
        log.debug("pause: view gone");
        mGeneralVisibility = View.GONE;
        updateVisibility();
        mRepeatHandler.removeCallbacks(mRepeatRunnable);
    }

    private Runnable mRepeatRunnable = new Runnable() {
        @Override
        public void run() {
            boolean scanningOnGoing = NetworkScannerReceiver.isScannerWorking() || AutoScrapeService.isScraping() || ImportState.VIDEO.isInitialImport() || ImportState.VIDEO.isRegularImport();
            mStatusVisibility = scanningOnGoing ? View.VISIBLE : View.GONE;
            log.trace("mRepeatRunnable: visibility " + mStatusVisibility + " because scanningOngoing " + scanningOnGoing +
                    " due to networkScanner " + NetworkScannerReceiver.isScannerWorking() +
                    " due to autoScrapeService " + AutoScrapeService.isScraping() +
                    " due to isInitialImport " + ImportState.VIDEO.isInitialImport() +
                    " due to isRegularImport " + ImportState.VIDEO.isRegularImport());
            updateCount();
            updateVisibility();
            mRepeatHandler.postDelayed(this, REPEAT_PERIOD_MS);
        }
    };


    /** Compute the visibility of the progress group. Both mGeneralVisibility and mStatusVisibility must be VISIBLE for the view to be visible */
    private void updateVisibility() {
        log.trace("updateVisibility: (0 visible, 8 gone) mGeneralVisibility " + mGeneralVisibility + ", mStatusVisibility " + mStatusVisibility);
        if ((mGeneralVisibility == View.VISIBLE) && (mStatusVisibility == View.VISIBLE)) {
            mProgressGroup.setVisibility(View.VISIBLE);
        } else {
            mProgressGroup.setVisibility(View.GONE);
        }
    }

    /** update the counter TextView */
    private void updateCount() {
        String msg = String.valueOf("");
        int count = 0;

        // First check initial import count
        if (ImportState.VIDEO.isInitialImport()) {
            msg = mInitialScanMessage+"\n";
            count = ImportState.VIDEO.getNumberOfFilesRemainingToImport();
            log.trace("updateCount: initial import count " + count);
        }
        // If not initial import count, check autoscraper count
        if (count==0) {
            count = AutoScrapeService.getNumberOfFilesRemainingToProcess();
            log.trace("updateCount: not initial import count " + count);
        }

        // Display count only if greater than zero
        if (count > 0) {
            log.trace("updateCount: visible " + count);
            mCount.setText(msg+Integer.toString(count));
            mCount.setVisibility(View.VISIBLE);
        } else {
            log.trace("updateCount: invisible");
            mCount.setVisibility(View.INVISIBLE);
        }
    }
}
