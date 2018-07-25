package com.cherryperry.amiami.model.update

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AmiamiJsonApi {

    companion object {
        const val BASE_URL = "https://api.amiami.com/api/v1.0/"
        const val HEADER_LANGUAGE = "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4"
        const val HEADER_USER_AGENT = "User-Agent: Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36" +
            "(KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36"
        const val HEADER_AUTH = "x-user-key: amiami_dev"
    }

    @GET("items?s_sortkey=preowned&s_st_condition_flg=1&lang=eng")
    @Headers(value = [HEADER_LANGUAGE, HEADER_USER_AGENT, HEADER_AUTH])
    fun items(
        @Query("s_cate_tag") category: Int,
        @Query("pagemax") perPage: Int,
        @Query("pagecnt") page: Int
    ): Single<AmiamiApiListResponse>

    @GET("item?lang=eng")
    @Headers(value = [HEADER_LANGUAGE, HEADER_USER_AGENT, HEADER_AUTH])
    fun item(@Query("gcode") itemId: String): Single<AmiamiApiItemResponse>
}
