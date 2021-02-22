package com.udacity

enum class DownloadOptions(val title: String, val url: String) {
    NONE("NONE","none"),
    GLIDE("GLIDE", "https://github.com/bumptech/glide"),
    RETROFIT("RETROFIT","https://github.com/square/retrofit"),
    UDACITY( "UDACITY","https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip")
}