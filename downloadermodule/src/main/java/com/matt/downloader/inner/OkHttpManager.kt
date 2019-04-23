package com.matt.downloader.inner

import android.util.Log
import com.matt.downloader.BuildConfig
import com.matt.downloader.openapi.IGet
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * Author:Created by jiaguofeng on 2019/4/11.
 * Email:jiagfone@163.com
 */
class OkHttpManager {

    private val TAG = "OkHttpManager"
    private var mOkHttpClient: OkHttpClient

    constructor(){
        this.mOkHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .connectTimeout(6000, TimeUnit.SECONDS)
            .readTimeout(100000, TimeUnit.SECONDS)
            .writeTimeout(100000, TimeUnit.SECONDS).hostnameVerifier(HostnameVerifier { s, sslSession ->
                if(BuildConfig.DEBUG){
                    Log.i(TAG, "verify: " + s + "," + sslSession.isValid)
                }
                true
            })
            .build();
    }

    private object mHolder{
        val instance = OkHttpManager()
    }

    companion object {
        fun getInstance():OkHttpManager{
            return mHolder.instance
        }
    }

    fun asyncCall(url: String): Call {
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept-Encoding", "identity")
            .build()
        return mOkHttpClient.newCall(request)
    }

    @Throws(IOException::class)
    fun syncResponse(url: String, start: Long, end: Long): Response {
        val request = Request.Builder()
            .url(url)
            //Range 请求头格式Range: bytes=start-end
            //                .addHeader("Range", "bytes=" + start + "-" + end)
            .addHeader("Accept-Encoding", "identity")
            .build()
        return mOkHttpClient.newCall(request).execute()
    }

    fun asyncGet(url: String,get: IGet){
        val call = OkHttpManager.getInstance().asyncCall(url)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                get.onFailure(call,e)
            }

            override fun onResponse(call: Call, response: Response) {
                get.onResponse(call, response)
            }
        })
    }
}