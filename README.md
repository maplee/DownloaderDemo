# DownloaderModule

简易下载模块

添加仓库

```
allprojects {
    repositories {
        google()
        jcenter()
        maven {url 'https://raw.github.com/maplee/mvn-repo/master'}
    }
}

```

添加依赖

```
implementation 'com.matt.module:downloader:1.0.0'

```


集成模块

## 初始化
在Application中的onCreate中添加

```
DownloaderApi.init(getApplicationContext());

```

## 使用

### 开始下载
```
//name 文件名称
//url 下载链接
DownloaderApi.start(
            name,
            url,
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
```
### 停止下载
```
//url 下载链接
DownloaderApi.stop(url);
```
