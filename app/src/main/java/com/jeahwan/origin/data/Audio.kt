package com.jeahwan.origin.data

import com.google.gson.annotations.SerializedName

class Audio {
    @SerializedName(value = "audioCode", alternate = ["sectionId"])
    var audioCode: String? = null//音频id

    @SerializedName(value = "title", alternate = ["audioName"])
    var title: String? = null//音频名称

    @SerializedName(value = "audio", alternate = ["audioUrl"])
    var audio: String? = null

    var publishDate: String? = null
    var learnedCount: String? = null
    var lastPlayPercentage = 0.0
    var isPlaying = false
    var closePlayer = false
    var chartName: String? = null//音频所属模块名称
    var duration = 0//总时长
    var courseId: String? = null
    var lockStatus: Int = 0 // 0解锁 1未解锁
    var sectionRecord: Long = 0 // 进度单位s
}