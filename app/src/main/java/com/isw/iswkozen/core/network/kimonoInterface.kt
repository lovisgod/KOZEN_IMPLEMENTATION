package com.isw.iswkozen.core.network

import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.network.models.*
import com.isw.iswkozen.core.utilities.simplecalladapter.Simple
import retrofit2.http.*

interface kimonoInterface {

    @POST(Constants.ISW_TOKEN_URL)
    fun getISWToken( @Body request: TokenRequestModel):
            Simple<TokenConfigResponse>

    @POST(Constants.KIMONO_END_POINT)
    fun makePurchase( @Body request: PurchaseRequest):
            Simple<PurchaseResponse>

    @POST(Constants.KIMONO_END_POINT)
    fun makeCashout(@Body request: TransferRequest, @Header("Authorization") token: String ):
            Simple<PurchaseResponse>
}