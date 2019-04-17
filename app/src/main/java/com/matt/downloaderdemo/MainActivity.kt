package com.matt.downloaderdemo

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.matt.downloader.openapi.DownloadCallback
import com.matt.downloader.openapi.DownloaderApi
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : View.OnClickListener, AppCompatActivity() {


    private val TAG = "MainActivity"
    private val URL = "http://inno72.oss.72solo.com/apk/test/test_potLimit.apk"
    private val list = listOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    ).toTypedArray();



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_start.setOnClickListener(this)
        btn_pause.setOnClickListener(this)
        btn_app.setOnClickListener(this)
        var flag = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
        if (!flag) {
            requestPermissions(list, 1)
        }
    }

    private fun start() {
        DownloaderApi.start(
            "test.apk",
            URL,
            object : DownloadCallback {
                override fun onFailure(e: Exception) {
                    Log.e(TAG, "e:", e)
                }

                override fun onProgress(progress: Long, total: Long) {
                    Log.i(TAG, "onProgress-progress:" + progress + ",total:" + total)
                }

                override fun onPause(progress: Long, total: Long) {
                    Log.i(TAG, "onPause--progress:" + progress + ",total:" + total)
                }

                override fun onSuccess(file: File) {
                    Log.i(TAG, "onSuccess--progress:" + file.absolutePath)
                }

            })
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_start -> consume {
                Log.i(TAG, "onClick--start")
                start()
            }
            R.id.btn_pause -> consume {
                DownloaderApi.stop(URL)
            }
            R.id.btn_app -> consume {
                var list:MutableList<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
                list.forEach {
                    Log.i(TAG,"packageName:"+it.applicationInfo.packageName+",sourceDir:"+it.applicationInfo.sourceDir)
                }
            }
        }
    }

    inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
