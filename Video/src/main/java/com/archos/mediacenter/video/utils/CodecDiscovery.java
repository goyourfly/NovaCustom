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

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;

public class CodecDiscovery {


	private static String TAG = "CodecDiscovery";
	private static boolean DBG = false;

	public static boolean isCodecTypeSupported(String codecType, boolean allowSwCodec) {
		return isCodecTypeSupported(codecType, allowSwCodec, MediaCodecList.REGULAR_CODECS);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static boolean isCodecTypeSupported(String codecType, boolean allowSwCodec, int kind) {
		MediaCodecList codecList = new MediaCodecList(kind);
		MediaCodecInfo[] codecInfos = codecList.getCodecInfos();

		for (MediaCodecInfo codecInfo : codecInfos) {
			if (DBG) Log.d(TAG, "isCodecTypeSupported: codecInfo.getName " + codecInfo.getName());
			if (isCodecInfoSupported(codecInfo, codecType, allowSwCodec))
				return true;
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private static boolean isCodecInfoSupported(MediaCodecInfo codecInfo, String codecType, boolean allowSwCodec) {
		if (codecInfo.isEncoder()
				|| (!allowSwCodec && ( codecInfo.getName().startsWith("OMX.google")
				|| codecInfo.getName().toLowerCase().contains("sw")
				|| codecInfo.getName().toLowerCase().startsWith("c2.android")
		)))
			return false;

		String[] types = codecInfo.getSupportedTypes();
		for (String type : types) {
			if (type.equalsIgnoreCase(codecType))
				return true;
		}
		return false;
	}

}