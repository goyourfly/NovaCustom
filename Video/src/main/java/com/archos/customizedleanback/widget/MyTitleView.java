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
package com.archos.customizedleanback.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.SearchOrbView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.widget.HintView;

import java.util.ArrayList;

/**
 * Title view for a leanback fragment.
 */
public class MyTitleView extends RelativeLayout {

    private ImageView mBadgeView;
    private TextView mTextView;
    private SearchOrbView mSearchOrbView;
    private SearchOrbView mSearchOrbView2; // ARCHOS added
    private SearchOrbView mSearchOrbView3; // ARCHOS added
    private SearchOrbView mSearchOrbView4; // ARCHOS added
    private SearchOrbView mSearchOrbView5; // ARCHOS added

    private HintView mHintView; // ARCHOS added
    private TextView mOrb1Description; // ARCHOS added
    private TextView mOrb2Description; // ARCHOS added
    private TextView mOrb3Description; // ARCHOS added
    private TextView mOrb4Description; // ARCHOS added
    private TextView mOrb5Description; // ARCHOS added

    private SearchOrbView mLastOrb;

    public MyTitleView(Context context) {
        this(context, null);
    }

    public MyTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.browseTitleViewStyle);
    }

    public MyTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.lb_my_title_view, this);

        mBadgeView = (ImageView) rootView.findViewById(R.id.title_badge);
        mTextView = (TextView) rootView.findViewById(R.id.title_text);
        mSearchOrbView = (SearchOrbView) rootView.findViewById(R.id.title_orb);
        mSearchOrbView2 = (SearchOrbView) rootView.findViewById(R.id.title_orb2); // ARCHOS added
        mSearchOrbView3 = (SearchOrbView) rootView.findViewById(R.id.title_orb3); // ARCHOS added
        mSearchOrbView4 = (SearchOrbView) rootView.findViewById(R.id.title_orb4); // ARCHOS added
        mSearchOrbView4.setVisibility(GONE);
        mSearchOrbView5 = (SearchOrbView) rootView.findViewById(R.id.title_orb5); // ARCHOS added
        mSearchOrbView5.setVisibility(GONE);
        mHintView = (HintView) rootView.findViewById(R.id.hint_view); // ARCHOS added

        mOrb1Description = (TextView) rootView.findViewById(R.id.orb1_description); // ARCHOS added
        mOrb2Description = (TextView) rootView.findViewById(R.id.orb2_description); // ARCHOS added
        mOrb3Description = (TextView) rootView.findViewById(R.id.orb3_description); // ARCHOS added
        mOrb4Description = (TextView) rootView.findViewById(R.id.orb4_description); // ARCHOS added
        mOrb5Description = (TextView) rootView.findViewById(R.id.orb5_description); // ARCHOS added

        mSearchOrbView.setOnFocusChangeListener(mOrbsFocusListener);
        mSearchOrbView2.setOnFocusChangeListener(mOrbsFocusListener);
        mSearchOrbView3.setOnFocusChangeListener(mOrbsFocusListener);
        mSearchOrbView4.setOnFocusChangeListener(mOrbsFocusListener);
        mSearchOrbView5.setOnFocusChangeListener(mOrbsFocusListener);

        setClipToPadding(false);
        setClipChildren(false);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (mLastOrb != null && mLastOrb.getVisibility() == View.VISIBLE && mLastOrb.requestFocus())
            return true;

        ArrayList<SearchOrbView> visibleOrbs = new ArrayList<>();

        if (mSearchOrbView.getVisibility() == View.VISIBLE)
            visibleOrbs.add(mSearchOrbView);
        if (mSearchOrbView2.getVisibility() == View.VISIBLE)
            visibleOrbs.add(mSearchOrbView2);
        if (mSearchOrbView3.getVisibility() == View.VISIBLE)
            visibleOrbs.add(mSearchOrbView3);
        if (mSearchOrbView4.getVisibility() == View.VISIBLE)
            visibleOrbs.add(mSearchOrbView4);
        if (mSearchOrbView5.getVisibility() == View.VISIBLE)
            visibleOrbs.add(mSearchOrbView5);

        if (visibleOrbs.size() == 1)
            return visibleOrbs.get(0).requestFocus();
        else if (visibleOrbs.size() == 2)
            return visibleOrbs.get(0).requestFocus();
        else if (visibleOrbs.size() == 3)
            return visibleOrbs.get(1).requestFocus();
        else if (visibleOrbs.size() == 4)
            return visibleOrbs.get(1).requestFocus();
        else if (visibleOrbs.size() == 5)
            return visibleOrbs.get(2).requestFocus();
        
        return false;
    }

    /**
     * Sets the title text.
     */
    public void setTitle(String titleText) {
        mTextView.setText(titleText);
    }

    /**
     * Returns the title text.
     */
    public CharSequence getTitle() {
        return mTextView.getText();
    }

    /**
     * Sets the badge drawable.
     * If non-null, the drawable is displayed instead of the title text.
     */
    public void setBadgeDrawable(Drawable drawable) {
        mBadgeView.setImageDrawable(drawable);
        if (drawable != null) {
            mBadgeView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        } else {
            mBadgeView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Returns the badge drawable.
     */
    public Drawable getBadgeDrawable() {
        return mBadgeView.getDrawable();
    }

    /**
     * Sets the listener to be called when the search affordance is clicked.
     */
    public void setOnSearchClickedListener(View.OnClickListener listener) {
        mSearchOrbView.setOnOrbClickedListener(listener);
    }

    public void setOnOrb1ClickedListener(View.OnClickListener listener) {
        mSearchOrbView.setOnOrbClickedListener(listener);
        mSearchOrbView.setVisibility(listener != null ? View.VISIBLE : View.GONE);
    }
    public void setOnOrb2ClickedListener(View.OnClickListener listener) {
        mSearchOrbView2.setOnOrbClickedListener(listener);
        mSearchOrbView2.setVisibility(listener != null ? View.VISIBLE : View.GONE);
    }

    public void setOnOrb3ClickedListener(View.OnClickListener listener) {
        mSearchOrbView3.setOnOrbClickedListener(listener);
        mSearchOrbView3.setVisibility(listener != null ? View.VISIBLE : View.GONE);
    }

    public void setOnOrb4ClickedListener(View.OnClickListener listener) {
        mSearchOrbView4.setOnOrbClickedListener(listener);
        mSearchOrbView4.setVisibility(listener != null ? View.VISIBLE : View.GONE);
    }

    public void setOnOrb5ClickedListener(View.OnClickListener listener) {
        mSearchOrbView5.setOnOrbClickedListener(listener);
        mSearchOrbView5.setVisibility(listener != null ? View.VISIBLE : View.GONE);
    }

    /**
     * ARCHOS added
     * Set a short description displayed below the orb when is is selected
     */
    public void setOnOrb1Description(CharSequence description) {
        mOrb1Description.setTextColor(Color.WHITE);
        mOrb1Description.setText(description);

        // Avoid description changing without animation (in case it was displayed already)
        mOrb1Description.setAlpha(0);
        if (mSearchOrbView.isFocused()) {
            animateDescription(mOrb1Description, true);
        }
    }

    /**
     * ARCHOS added
     * * Set a short description displayed below the orb when is is selected
     */
    public void setOnOrb2Description(CharSequence description) {
        mOrb2Description.setTextColor(Color.WHITE);
        mOrb2Description.setText(description);

        // Avoid description changing without animation (in case it was displayed already)
        mOrb2Description.setAlpha(0);
        if (mSearchOrbView2.isFocused()) {
            animateDescription(mOrb2Description, true);
        }
    }

    /**
     * ARCHOS added
     * * Set a short description displayed below the orb when is is selected
     */
    public void setOnOrb3Description(CharSequence description) {
        mOrb3Description.setTextColor(Color.WHITE);
        mOrb3Description.setText(description);

        // Avoid description changing without animation (in case it was displayed already)
        mOrb3Description.setAlpha(0);
        if (mSearchOrbView3.isFocused()) {
            animateDescription(mOrb3Description, true);
        }
    }

    /**
     * ARCHOS added
     * * Set a short description displayed below the orb when is is selected
     */
    public void setOnOrb4Description(CharSequence description) {
        mOrb4Description.setTextColor(Color.WHITE);
        mOrb4Description.setText(description);

        // Avoid description changing without animation (in case it was displayed already)
        mOrb4Description.setAlpha(0);
        if (mSearchOrbView4.isFocused()) {
            animateDescription(mOrb4Description, true);
        }
    }

    /**
     * ARCHOS added
     * * Set a short description displayed below the orb when is is selected
     */
    public void setOnOrb5Description(CharSequence description) {
        mOrb5Description.setTextColor(Color.WHITE);
        mOrb5Description.setText(description);

        // Avoid description changing without animation (in case it was displayed already)
        mOrb5Description.setAlpha(0);
        if (mSearchOrbView5.isFocused()) {
            animateDescription(mOrb5Description, true);
        }
    }

    /**
     *  Returns the view for the search affordance.
     */
    public View getSearchAffordanceView() {
        return mSearchOrbView;
    }

    /**
     * Sets the {@link SearchOrbView.Colors} used to draw the search affordance.
     */
    public void setSearchAffordanceColors(SearchOrbView.Colors colors) {
        mSearchOrbView.setOrbColors(colors);
        mSearchOrbView2.setOrbColors(colors);
        mSearchOrbView3.setOrbColors(colors);
        mSearchOrbView4.setOrbColors(colors);
        mSearchOrbView5.setOrbColors(colors);
    }

    public void setOrb1IconResId(int iconResId) {
        mSearchOrbView.setOrbIcon(ContextCompat.getDrawable(getContext(), iconResId));
    }

    public void setOrb2IconResId(int iconResId) {
        mSearchOrbView2.setOrbIcon(ContextCompat.getDrawable(getContext(), iconResId));
    }

    public void setOrb3IconResId(int iconResId) {
        mSearchOrbView3.setOrbIcon(ContextCompat.getDrawable(getContext(), iconResId));
    }

    public void setOrb4IconResId(int iconResId) {
        mSearchOrbView4.setOrbIcon(ContextCompat.getDrawable(getContext(), iconResId));
    }

    public void setOrb5IconResId(int iconResId) {
        mSearchOrbView5.setOrbIcon(ContextCompat.getDrawable(getContext(), iconResId));
    }

    /**
     * Returns the {@link SearchOrbView.Colors} used to draw the search affordance.
     */
    public SearchOrbView.Colors getSearchAffordanceColors() {
        return mSearchOrbView.getOrbColors();
    }

    /**
     * Enables or disables any view animations.
     */
    public void enableAnimation(boolean enable) {
        mSearchOrbView.enableOrbColorAnimation(enable && mSearchOrbView.hasFocus());
        mSearchOrbView2.enableOrbColorAnimation(enable && mSearchOrbView2.hasFocus());
        mSearchOrbView3.enableOrbColorAnimation(enable && mSearchOrbView3.hasFocus());
        mSearchOrbView4.enableOrbColorAnimation(enable && mSearchOrbView4.hasFocus());
        mSearchOrbView5.enableOrbColorAnimation(enable && mSearchOrbView5.hasFocus());
    }

    /**
     * Set the message and show the hint view
     * ARCHOS added
     * @param message
     */
    public void setAndShowHintMessage(String message) {
        mHintView.setMessage(message);
        mHintView.setVisibility(View.VISIBLE);
    }

    /**
     * ARCHOS added
     */
    public void hideHintMessage() {
        mHintView.setVisibility(View.GONE);
    }

    /**
     * ARCHOS added
     */
    OnFocusChangeListener mOrbsFocusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean focus) {
            View descriptionView = null;
            if (view.equals(mSearchOrbView)) {
                descriptionView = mOrb1Description;
                mLastOrb = mSearchOrbView;
            } else if (view.equals(mSearchOrbView2)) {
                descriptionView = mOrb2Description;
                mLastOrb = mSearchOrbView2;
            } else if (view.equals(mSearchOrbView3)) {
                descriptionView = mOrb3Description;
                mLastOrb = mSearchOrbView3;
            } else if (view.equals(mSearchOrbView4)) {
                descriptionView = mOrb4Description;
                mLastOrb = mSearchOrbView4;
            } else if (view.equals(mSearchOrbView5)) {
                descriptionView = mOrb5Description;
                mLastOrb = mSearchOrbView5;
            } else {
                mLastOrb = null;
            }

            if (descriptionView != null) {
                animateDescription(descriptionView, focus);
            }
        }
    };

    private void animateDescription(View v, boolean focus) {
        float alpha = focus ? 1f : 0f;
        long startDelay = focus ? 300 : 0; // delay the display, not the hide
        v.animate().alpha(alpha).setStartDelay(startDelay);
    }

    public void resetLastOrb() {
        mLastOrb = null;
    }
}
