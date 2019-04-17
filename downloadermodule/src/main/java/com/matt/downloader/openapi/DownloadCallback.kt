package com.matt.downloader.openapi

import java.io.File

/**
 * Author:Created by jiaguofeng on 2019/4/11.
 * Email:jiagfone@163.com
 */
interface DownloadCallback {
    /**
     * 下载成功
     *
     * @param file
     */
    abstract fun onSuccess(file: File)

    /**
     * 下载失败
     *
     * @param e
     */
    abstract fun onFailure(e: Exception)

    /**
     * 下载进度
     *
     * @param progress
     */
    abstract fun onProgress(progress: Long, total: Long)

    /**
     * 暂停
     *
     * @param progress
     * @param currentLength
     */
    abstract fun onPause(progress: Long, total: Long)
}