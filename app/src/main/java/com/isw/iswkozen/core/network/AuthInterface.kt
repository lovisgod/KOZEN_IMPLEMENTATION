package com.isw.iswkozen.core.network

import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.network.models.ConfigResponse
import com.isw.iswkozen.core.utilities.simplecalladapter.Simple
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface AuthInterface
{
    @Headers("Content-Type: application/xml")
    @GET(Constants.KIMONO_MERCHANT_DETAILS_END_POINT_AUTOTest)
    fun getMerchantDetails(@Path("terminalSerialNo") terminalSerialNo: String):
            Simple<ConfigResponse>

}