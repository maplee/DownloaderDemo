package com.matt.downloader.inner

import com.matt.downloader.openapi.DownloadCallback
import java.io.File

/**
 * Author:Created by jiaguofeng on 2019/4/11.
 * Email:jiagfone@163.com
 */
class DownloadTask {
    /**
     * 文件下载的url
     */
    private var url: String
    /**
     * 文件的名称
     */
    private var name: String
    /**
     * 文件的大小
     */
    private var mContentLength: Long
    private var mDownloadRunnables: MutableList<DownloadRunnable>? = null
    private var mDownloadCallback: DownloadCallback


    constructor(name: String, url: String, contentLength: Long, callBack: DownloadCallback){
        this.name = name
        this.url = url
        this.mContentLength = contentLength
        this.mDownloadRunnables = mutableListOf()
        this.mDownloadCallback = callBack
        init()
    }


    private fun init() {
        var downloadRunnable =
            DownloadRunnable(name, url, mContentLength, 0L, mContentLength, object :
                DownloadCallback {
                override fun onFailure(e: Exception) {
                    mDownloadCallback.onFailure(e)
                    stopDownload()
                }

                override fun onSuccess(file: File) {
                    mDownloadCallback.onSuccess(file)
                    DownloadDispatcher.getInstance().recyclerTask(this@DownloadTask)
                }

                override fun onProgress(progress: Long, currentLength: Long) {
                    synchronized(this@DownloadTask) {
                        mDownloadCallback.onProgress(progress, currentLength)
                    }
                }

                override fun onPause(progress: Long, currentLength: Long) {
                    mDownloadCallback.onPause(progress, currentLength)
                }
            })
        DownloadDispatcher.getInstance().execute(downloadRunnable)
        mDownloadRunnables?.add(downloadRunnable)
    }


    /**
     * 停止下载
     */
    fun stopDownload() {
        for (runnable in this.mDownloadRunnables!!) {
            runnable.stop()
        }
    }

    fun getUrl(): String {
        return url
    }
}