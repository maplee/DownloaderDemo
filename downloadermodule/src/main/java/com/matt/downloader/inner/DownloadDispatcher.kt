package com.matt.downloader.inner

import android.text.TextUtils
import android.util.Log
import com.matt.downloader.BuildConfig
import com.matt.downloader.openapi.DownloadCallback
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.*

/**
 * Author:Created by jiaguofeng on 2019/4/10.
 * Email:jiagfone@163.com
 */
class DownloadDispatcher private constructor(){


    private val TAG = "DownloadDispatcher"

    private var mExecutorService: ExecutorService? = null

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val THREAD_SIZE = Math.max(3, Math.min(CPU_COUNT - 1, 5))
    /**
     * 核心线程数
     */
    private val CORE_POOL_SIZE = THREAD_SIZE

    private val runningTasks = ArrayDeque<DownloadTask>()

    private object mHolder{
        val instance = DownloadDispatcher()
    }

    companion object {
        fun getInstance():DownloadDispatcher{
            return mHolder.instance
        }
    }


    /**
     * 创建线程池
     *
     * @return mExecutorService
     */
    @Synchronized
    fun execute(runnable: Runnable) {
        if (mExecutorService == null) {
            mExecutorService = ThreadPoolExecutor(CORE_POOL_SIZE, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                SynchronousQueue(), ThreadFactory { r ->
                    val thread = Thread(r)
                    thread.isDaemon = false
                    thread
                })
        }
        mExecutorService?.execute(runnable);
    }


    /**
     * @param name     文件名
     * @param url      下载的地址
     * @param callBack 回调接口
     */
    @Throws(Exception::class)
    fun startDownload(name: String, url: String, callBack: DownloadCallback) {
        val call = OkHttpManager.getInstance().asyncCall(url)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callBack.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                //获取文件的大小
                var contentLength = response.body()?.contentLength()

                if (BuildConfig.DEBUG){
                    Log.i(TAG, "文件大小=$contentLength")
                }
                if (contentLength == null || contentLength <= -1) {
                    callBack.onFailure(RuntimeException("length is null"))
                    return
                }
                var downloadTask = contentLength?.let { DownloadTask(name, url, it, callBack) }
                runningTasks.add(downloadTask)
            }
        })
    }

    /**
     * 停止下载
     *
     * @param url
     */
    fun stopDownLoad(url: String) {
        for (runningTask in runningTasks) {
            if (runningTask.getUrl().equals(url)) {
                runningTask.stopDownload()
            }
        }
    }

    /**
     * @param downLoadTask 下载任务
     */
    fun recyclerTask(downLoadTask: DownloadTask) {
        if (runningTasks == null){
            return
        }
        runningTasks.remove(downLoadTask)
    }


    fun containTask(url: String): Boolean {
        if (runningTasks == null || runningTasks.isEmpty()) {
            return false
        }
        val it = runningTasks.iterator()
        var downloadTask: DownloadTask? = null
        while (it.hasNext()) {
            downloadTask = it.next()
            if (downloadTask == null) {
                continue
            }
            if (TextUtils.equals(downloadTask.getUrl(), url)) {
                return true
            }
        }
        return false
    }





}