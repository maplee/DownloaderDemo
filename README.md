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
//url 下载链接
//name 文件名称
DownloaderApi.start(url,name,);
```
### 停止下载
```
//url 下载链接
DownloaderApi.stop(url);
```
