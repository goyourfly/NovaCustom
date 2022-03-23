/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.archos.customizedleanback.app;

import android.os.Bundle;
import androidx.leanback.widget.BrowseFrameLayout;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnChildLaidOutListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;
import androidx.leanback.widget.ViewHolderTask;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.archos.customizedleanback.widget.MyTitleView;
import com.archos.mediacenter.video.R;

/**
 * A fragment for creating leanback vertical grids.
 *
 * <p>Renders a vertical grid of objects given a {@link VerticalGridPresenter} and
 * an {@link ObjectAdapter}.
 */
public class MyVerticalGridFragment extends MyBrandedFragment {
    private static final String TAG = "MyVerticalGridFragment";
    private static boolean DEBUG = false;

    private ObjectAdapter mAdapter;
    private VerticalGridPresenter mGridPresenter;
    private VerticalGridPresenter.ViewHolder mGridViewHolder;
    private OnItemViewSelectedListener mOnItemViewSelectedListener;
    private OnItemViewClickedListener mOnItemViewClickedListener;
    private int mSelectedPosition = -1;

    private String mEmptyMessage; // ARCHOS ADDED
    private TextView mEmptyView; // ARCHOS ADDED

    /**
     * Sets the grid presenter.
     */
    public void setGridPresenter(VerticalGridPresenter gridPresenter) {
        if (gridPresenter == null) {
            throw new IllegalArgumentException("Grid presenter may not be null");
        }
        mGridPresenter = gridPresenter;
        mGridPresenter.setOnItemViewSelectedListener(mViewSelectedListener);
        if (mOnItemViewClickedListener != null) {
            mGridPresenter.setOnItemViewClickedListener(mOnItemViewClickedListener);
        }

        // ARCHOS ADDED: update the number of columns in case the GridView is already created
        if (mGridViewHolder!=null) {
            mGridViewHolder.getGridView().setNumColumns(gridPresenter.getNumberOfColumns());
        }
    }

    /**
     * Returns the grid presenter.
     */
    public VerticalGridPresenter getGridPresenter() {
        return mGridPresenter;
    }

    /**
     * Sets the object adapter for the fragment.
     */
    public void setAdapter(ObjectAdapter adapter) {
        mAdapter = adapter;
        updateAdapter();
    }

    /**
     * Returns the object adapter.
     */
    public ObjectAdapter getAdapter() {
        return mAdapter;
    }

    final private OnItemViewSelectedListener mViewSelectedListener =
            new OnItemViewSelectedListener() {
                @Override
                public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                           RowPresenter.ViewHolder rowViewHolder, Row row) {
                    // ARCHOS ADDED: update the title in case the GridView is already created
                    if (mGridViewHolder != null) {
                        int position = mGridViewHolder.getGridView().getSelectedPosition();
                        if (DEBUG) Log.v(TAG, "grid selected position " + position);
                        gridOnItemSelected(position);
                    }
                    if (mOnItemViewSelectedListener != null) {
                        mOnItemViewSelectedListener.onItemSelected(itemViewHolder, item,
                                rowViewHolder, row);
                    }
                }
            };

    final private OnChildLaidOutListener mChildLaidOutListener =
            new OnChildLaidOutListener() {
                @Override
                public void onChildLaidOut(ViewGroup parent, View view, int position, long id) {
                    if (position == 0) {
                        showOrHideTitle();
                    }
                }
            };

    /**
     * Sets an item selection listener.
     */
    public void setOnItemViewSelectedListener(OnItemViewSelectedListener listener) {
        mOnItemViewSelectedListener = listener;
    }

    private void gridOnItemSelected(int position) {
        if (position != mSelectedPosition) {
            mSelectedPosition = position;
            showOrHideTitle();
        }
    }

    private void showOrHideTitle() {
        // ARCHOS ADDED: update the title in case the GridView is already created
        if (mGridViewHolder == null) {
            return;
        }
        if (mGridViewHolder.getGridView().findViewHolderForAdapterPosition(mSelectedPosition) == null) {
            return;
        }
        if (!mGridViewHolder.getGridView().hasPreviousViewInSameRow(mSelectedPosition)) {
            showTitle(true);
        } else {
            showTitle(false);
        }
    }

    /**
     * Sets an item clicked listener.
     */
    public void setOnItemViewClickedListener(OnItemViewClickedListener listener) {
        mOnItemViewClickedListener = listener;
        if (mGridPresenter != null) {
            mGridPresenter.setOnItemViewClickedListener(mOnItemViewClickedListener);
        }
    }

    /**
     * Returns the item clicked listener.
     */
    public OnItemViewClickedListener getOnItemViewClickedListener() {
        return mOnItemViewClickedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.lb_my_vertical_grid_fragment,
                container, false);
        setTitleView((MyTitleView) root.findViewById(R.id.browse_title_group));

        mEmptyView = (TextView)root.findViewById(R.id.empty_view); // ARCHOS ADDED
        if (mEmptyMessage!=null) { // ARCHOS ADDED
            mEmptyView.setText(mEmptyMessage); // ARCHOS ADDED
        } // ARCHOS ADDED

        return root;
    }

    /**
     * ARCHOS ADDED
     * @param message
     */
    public void setEmptyTextMessage(String message) {
        mEmptyMessage = message;
        if (mEmptyView!=null) {
            mEmptyView.setText(message);
        }
    }

    public void setEmptyViewVisiblity(Boolean visibility) {
        if (mEmptyView!=null) {
            mEmptyView.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup gridDock = (ViewGroup) view.findViewById(R.id.browse_grid_dock);
        mGridViewHolder = mGridPresenter.onCreateViewHolder(gridDock);
        gridDock.addView(mGridViewHolder.view);
        mGridViewHolder.getGridView().setOnChildLaidOutListener(mChildLaidOutListener);

        updateAdapter();
    }

    //ARCHOS ADDED
    // Need that to be able to change the Grid Presenter options dynamically
    public void updateGridViewHolder() {
        if (getView()!=null && mGridViewHolder!=null) {
            ViewGroup gridDock = (ViewGroup) getView().findViewById(R.id.browse_grid_dock);
            gridDock.removeView(mGridViewHolder.view);
            mGridViewHolder = mGridPresenter.onCreateViewHolder(gridDock);
            gridDock.addView(mGridViewHolder.view);
            mGridViewHolder.getGridView().setOnChildLaidOutListener(mChildLaidOutListener);

            updateAdapter();
        }
    }

    private void setupFocusSearchListener() {
        BrowseFrameLayout browseFrameLayout = (BrowseFrameLayout) getView().findViewById(
                R.id.grid_frame);
        browseFrameLayout.setOnFocusSearchListener(getTitleHelper().getOnFocusSearchListener());
    }

    @Override
    public void onStart() {
        super.onStart();
        setupFocusSearchListener();
        mGridViewHolder.getGridView().requestFocus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGridViewHolder = null;
    }

    /**
     * Sets the selected item position.
     */
    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        if(mGridViewHolder != null && mGridViewHolder.getGridView().getAdapter() != null) {
            mGridViewHolder.getGridView().setSelectedPositionSmooth(position, new ViewHolderTask() {
                @Override
                public void run(final RecyclerView.ViewHolder rvh) {
                    showOrHideTitle();
                }
            });
        }
    }

    public int getSelectedPosition() {
        if (mGridViewHolder != null)
            return mGridViewHolder.getGridView().getSelectedPosition();
        
        return -1;
    }

    private void updateAdapter() {
        if (mGridViewHolder != null) {
            mGridPresenter.onBindViewHolder(mGridViewHolder, mAdapter);
            if (mSelectedPosition != -1) {
                mGridViewHolder.getGridView().setSelectedPosition(mSelectedPosition);
            }
        }
    }
}
