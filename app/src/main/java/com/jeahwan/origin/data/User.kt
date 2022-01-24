package com.jeahwan.origin.data

class User {
    var customerData: CustomerData? = null
    var status = 0//登录下一步要进行的动作: 0直接进入 1手机号留资 2微信授权 -1账号异常 ,
    var mobile: String? = null
    var unionId: String? = null

    class CustomerData {
        var token = "" //token 给默认值 避免请求headers中报错
        var mobile: String? = null
        var learnNumber: String? = null
        var wechatName: String? = null
        var wechatAvatar: String? = null
        var unionId: String? = null
        var channelAlertCourseId: String? = null
        var vipStatus = 0//-1 过期 0 不是 1 黑金 99 待领取
        var vipInvalidTime: String? = null
        var isAppPutIn = false
        var appPutUrl: String? = null
        var authorId: String? = null // 作者id，如果非空则是作者能发帖
    }
}