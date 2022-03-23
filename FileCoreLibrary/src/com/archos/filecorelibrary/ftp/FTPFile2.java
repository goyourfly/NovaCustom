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

package com.archos.filecorelibrary.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.net.Uri;

import com.archos.filecorelibrary.FileEditor;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.filecorelibrary.RawLister;

public class FTPFile2 extends MetaFile2 {

    private static final Logger log = LoggerFactory.getLogger(FTPFile2.class);

    private static final long serialVersionUID = 2L;

    private final String mUriString;
    private final String mName;
    private final boolean mIsDirectory;
    private final boolean mIsFile;
    private final long mLastModified;
    private final boolean mCanRead;
    private final boolean mCanWrite;
    private final long mLength;

    public FTPFile2(FTPFile file, Uri uri) {

        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }

        mUriString = uri.toString();
        String name = file.getName();
        mIsDirectory = file.isDirectory();
        mIsFile = file.isFile();
        if (file.getTimestamp() != null)
            mLastModified = file.getTimestamp().getTimeInMillis();
        else
            mLastModified = 0;
        mCanRead = file.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION);
        mCanWrite = file.hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION);
        mLength = file.getSize();
        // remove the '/' at the end of directory name
        if (mIsDirectory && name.endsWith("/")) {
            mName = name.substring(0, name.length()-1);
        } else {
            mName = name;
        }
        log.trace("FTPFile2 uri: " + uri + ", isFile=" + mIsFile + ", isDirectory=" + mIsDirectory);
    }

    @SuppressWarnings("unused")
    private FTPFile2() {
        throw new IllegalArgumentException("Unauthorized to create a FTPFile2 from nothing! Can only be created from a org.apache.commons.net.ftp.FTPFile and an android.net.Uri");
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isDirectory() {
        return mIsDirectory;
    }

    @Override
    public boolean isFile() {
        return mIsFile;
    }

    @Override
    public long lastModified() {
        return mLastModified;
    }

    @Override
    public long length() {
        return mLength;
    }

    @Override
    public boolean canRead() {
        return mCanRead;
    }

    @Override
    public boolean canWrite() {
        return mCanWrite;
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FTPFile2) {
            return getUri().equals(((FTPFile2)other).getUri());
        } else {
            return false;
        }
    }

    @Override
    public Uri getUri() {
        return Uri.parse(this.mUriString);
    }

    public RawLister getRawListerInstance() {
        return new FTPRawLister(getUri()) ;
    }

    @Override
    public FileEditor getFileEditorInstance(Context ct) {
        return new FtpFileEditor(getUri());
    }
    /**
     * get metafile2 object from a uri (please use this only if absolutely necessary
     *
     */
    public static MetaFile2 fromUri(Uri uri) throws Exception {
        log.debug("fromUri: " + uri);
        if (uri.getScheme().equals("ftps")) {
            // ftpClient is not thread safe: using a new instance (need to close afterwards)
            FTPSClient ftp = Session.getInstance().getNewFTPSClient(uri, FTP.BINARY_FILE_TYPE);
            if (ftp.featureValue("MLST") == null) log.error("fromUri: ftp server does not support MLST!!!");
            FTPFile ftpFile = ftp.mlistFile(uri.getPath());
            Session.closeNewFTPSClient(ftp);
            if (ftpFile != null) return new FTPFile2(ftpFile,uri);
            else log.warn("fromUri: ftps detected but ftpfile is null!");
        } else {
            FTPClient ftp = Session.getInstance().getNewFTPClient(uri, FTP.BINARY_FILE_TYPE);
            if (ftp.featureValue("MLST") == null) log.warn("fromUri: ftp server does not support MLST!!!");
            FTPFile ftpFile = ftp.mlistFile(uri.getPath());
            Session.closeNewFTPClient(ftp);
            if (ftpFile != null) return new FTPFile2(ftpFile,uri);
            else log.warn("fromUri: ftp detected but ftpfile is null!");
        }
        log.warn("fromUri: uh! returning null!!!");
        return null;
    }
}
