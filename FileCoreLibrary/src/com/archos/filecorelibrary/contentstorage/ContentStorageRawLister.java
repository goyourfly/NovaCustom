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

package com.archos.filecorelibrary.contentstorage;

import android.net.Uri;
import androidx.documentfile.provider.DocumentFile;

import com.archos.environment.ArchosUtils;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.filecorelibrary.RawLister;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * returns 
 * @author alexandre
 *
 */
public class ContentStorageRawLister extends RawLister {
    public ContentStorageRawLister(Uri uri) {
        super(uri);
    }

    public List<MetaFile2> getFileList(){
        String path = mUri.getPath();
        DocumentFile documentFile = null;
        try {
            documentFile = DocumentUriBuilder.getDocumentFileForUri(ArchosUtils.getGlobalContext(), mUri);

            if (!documentFile.canRead()) {

                return null;
            }
            DocumentFile[] listFiles = documentFile.listFiles();


            if (listFiles == null) {
                return null;
            }

            final List<MetaFile2> files = new ArrayList<>();
            for (DocumentFile f : listFiles) {
                files.add(new ContentFile2(f));
            }
            return files;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }
}
