package com.matt.downloader.openapi

import android.content.Context
import com.matt.downloader.inner.DownloadDispatcher
import com.matt.downloader.inner.Utils

/**
 * Author:Created by jiaguofeng on 2019/4/10.
 * Email:jiagfone@163.com
 */

class DownloaderApi {
    companion object {

        public lateinit var sConext:Context;

        fun init(context: Context, downloadPath:String, retryCount:Int){
            this.sConext = context
            Utils.DOWNLOAD_PATH = downloadPath
            Utils.RETRY_COUNT = retryCount
        }

        fun start(name: String, url: String, callback: DownloadCallback) {
            DownloadDispatcher.getInstance().startDownload(name, url, callback)
        }

        fun stop(url: String){
            DownloadDispatcher.getInstance().stopDownLoad(url)
        }

        fun containTask(url: String): Boolean {
            return DownloadDispatcher.getInstance().containTask(url)
        }
    }
}
