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

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * Created by alexandre on 01/06/16.
 */
public class ArchosSeekBar extends AppCompatSeekBar {
    private OnEnableListener mOnEnableListener;

    public interface OnEnableListener{

        void onEnable();
    }
    public ArchosSeekBar(Context context) {
        super(context);
    }

    public ArchosSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArchosSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

   public void setOnEnableListener(OnEnableListener listener){
       mOnEnableListener = listener;
   }

    public boolean  onTouchEvent(MotionEvent event){
        if(!isEnabled()) {
            mOnEnableListener.onEnable();
            setEnabled(true);
        }
        return super.onTouchEvent(event);
    }

}
