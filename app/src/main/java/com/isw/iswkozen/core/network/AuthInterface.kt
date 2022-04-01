package com.isw.iswkozen.core.network

import com.interswitchng.smartpos.simplecalladapter.Simple
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.network.models.ConfigResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface AuthInterface
{
    @Headers("Content-Type: application/xml")
    @GET(Constants.KIMONO_MERCHANT_DETAILS_END_POINT_AUTO)
    fun getMerchantDetails(@Path("terminalSerialNo") terminalSerialNo: String):
            Simple<ConfigResponse>

}