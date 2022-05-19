package com.isw.iswkozen.core.network.CardLess


import com.isw.iswkozen.core.data.utilsData.Constants.AUTH_END_POINT
import com.isw.iswkozen.core.utilities.simplecalladapter.Simple
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

internal interface IAuthService {

    @FormUrlEncoded
    @POST(AUTH_END_POINT)
    fun getToken(
            @Field("grant_type") grantType: String,
            @Field("scope") scope: String): Simple<AuthToken?>
}