package com.matt.downloader.inner

import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Author:Created by jiaguofeng on 2019/4/11.
 * Email:jiagfone@163.com
 */
class Utils {

    companion object {
        val PATCH = ".patch";
        val JSON = ".json";
        val APK = ".apk";
        var DOWNLOAD_PATH = "/matt/download"
        var RETRY_COUNT = 1
        private fun getRootFile(): String {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        }

        private fun getDownloadPath(): File {
            val parent = File(getRootFile() + DOWNLOAD_PATH)
            if (!parent.exists()) {
                parent.mkdirs()
            }
            return parent
        }

        fun getApkFile(name: String): String {
            //final String filename = url.substring(url.lastIndexOf("/") + 1);
            val parent = getDownloadPath()

            val file = File(parent.absolutePath + "/" + name)

            return file.absolutePath
        }

        fun getPatchFile(name: String): String {

            val parent = getDownloadPath()
            var nameTemp = name.substring(0,name.indexOf(".")+1);

            val file = File(parent.absolutePath + "/" + nameTemp+ PATCH)

            return file.absolutePath
        }

        fun isDownloadPatch(name: String,md5Value:String): Boolean {
            val parent = getDownloadPath()
            var nameTemp = name.substring(0,name.indexOf(".")+1);
            val file = File(parent.absolutePath + "/" + nameTemp+ APK)
            return file.exists()
        }

        fun getFileMD5(file: File): String? {
            if (!file.isFile) {
                return null
            }
            var digest: MessageDigest? = null
            var mFileInputStream: FileInputStream? = null
            val buffer = ByteArray(1024)
            var len: Int = -1
            try {
                digest = MessageDigest.getInstance("MD5")
                mFileInputStream = FileInputStream(file)

                while (mFileInputStream.read(buffer).also { len = it } != -1) {
                    digest?.update(buffer, 0, len)
                }
                mFileInputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            val bigInt = BigInteger(1, digest?.digest())
            return bigInt.toString(16)
        }

        /**
         * 获取json地址
         */
        fun getJsonUrl(url: String): String {
            var nameTemp = url.substring(0,url.indexOf(".")+1);
            return nameTemp+ JSON
        }

    }


}