package com.matt.downloader.inner

import android.util.Log
import com.matt.downloader.BuildConfig
import com.matt.downloader.openapi.DownloadCallback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Author:Created by jiaguofeng on 2019/4/11.
 * Email:jiagfone@163.com
 */
open class SyncRunnable {

    private val TAG = "SyncRunnable"

    private val STATUS_DOWNLOADING = 1
    private val STATUS_STOP = 2
    /**
     * 线程的状态
     */
    private var mStatus = STATUS_DOWNLOADING


    /**
     * 文件下载的url
     */
    private var url: String
    /**
     * 文件的名称
     */
    private var name: String
    /**
     * 每个线程下载开始的位置
     */
    private var start: Long
    /**
     * 每个线程下载结束的位置
     */
    private var end: Long
    /**
     * 每个线程的下载进度
     */
    private var mProgress: Long = 0
    /**
     * 文件的总大小 content-length
     */
    private var mTotal: Long
    private var mDownloadCallback: DownloadCallback

    private var count = 0

    constructor(
        name: String,
        url: String,
        currentLength: Long,
        start: Long,
        end: Long,
        downloadCallback: DownloadCallback
    ) {
        this.name = name
        this.url = url
        this.mTotal = currentLength
        this.start = start
        this.end = end
        this.mDownloadCallback = downloadCallback
    }

    suspend fun run(): String? {
        count++
        if (count > 1) {
            Log.i(TAG, "失败后重试下载: " + (count - 1))
        }
        var inputStream: InputStream? = null
        var fos: FileOutputStream? = null
        try {
            var response = OkHttpManager.getInstance().syncResponse(url, start, end)
            Log.i(
                TAG,
                "fileName=" + name + "下载文件大小=" + response.body()?.contentLength()
                        + " 开始位置start=" + start + "结束位置end=" + end
            )
            inputStream = response.body()?.byteStream()
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "run: " + inputStream?.available())
            }
            //保存文件的路径
            var file = File(Utils.getApkFile(name))
            try {
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            fos = FileOutputStream(file)

            var bytes = ByteArray(10 * 1024)
            var length: Int = -1
            inputStream.use { input ->
                fos.use {
                    while (input?.read(bytes).also { length = it!! } != -1) {
                        Log.i(TAG, "length: $length")
                        if (mStatus == STATUS_STOP) {
                            Log.i(TAG, "run: $mStatus")
                            mDownloadCallback.onPause(mProgress, mTotal)
                            break
                        }
                        //写入
                        it.write(bytes, 0, length)
                        //保存下进度
                        mProgress = mProgress + length
                        //实时去更新下进度条，将每次写入的length传出去
                        mDownloadCallback.onProgress(mProgress, mTotal)
                    }
                    it.flush()
                }
            }
            return file.absolutePath
//            mDownloadCallback.onSuccess(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "", e)
            if (count <= Utils.RETRY_COUNT) {
                return run()
            }

            mDownloadCallback.onFailure(e)
            return null
        } finally {
            try {
                if (null != inputStream) {
                    inputStream!!.close()
                }
                if (null != fos) {
                    fos!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "", e)
            }

        }
        return null
    }

    fun stop() {
        mStatus = STATUS_STOP
    }
}