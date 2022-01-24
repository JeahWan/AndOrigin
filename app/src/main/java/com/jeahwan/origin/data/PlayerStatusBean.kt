package com.jeahwan.origin.data

import java.io.Serializable

data class PlayerStatusBean(

    var type: Int = 0, // 0 音频，1 视频
    var action: String? = null,
    var progress: Long = 0,
    var id: String? = null,
) : Serializable