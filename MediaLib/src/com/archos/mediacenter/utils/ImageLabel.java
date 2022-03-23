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

package com.archos.mediacenter.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageLabel extends ImageView {


	public ImageLabel(Context context) {
		super(context);
	}

	public ImageLabel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageLabel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public void setEnabled(boolean enabled) {
		this.setAlpha(enabled ? 1.0f : 0.1f);
	}
}
