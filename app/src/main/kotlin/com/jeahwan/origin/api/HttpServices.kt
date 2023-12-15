package com.jeahwan.origin.api


import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.ConcurrentHashMap


/**
 * 网络请求的契约类
 * Created by Jeah on 2017/2/4.
 */
interface HttpServices {
    @FormUrlEncoded
    @POST("api/v1/user/{userId}")
    fun myCertificate(
        @Path("userId") cerId: String,
        @FieldMap map: ConcurrentHashMap<String?, Any?>?
    )
}