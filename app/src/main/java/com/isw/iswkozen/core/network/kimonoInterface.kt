package com.isw.iswkozen.core.network

import com.interswitchng.smartpos.simplecalladapter.Simple
import com.isw.iswkozen.core.network.models.TerminalInformationRequest
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.network.models.ConfigResponse
import com.isw.iswkozen.core.network.models.TokenConfigResponse
import com.isw.iswkozen.core.network.models.TokenRequestModel
import retrofit2.http.*

interface kimonoInterface {

    @POST(Constants.ISW_TOKEN_URL)
    fun getISWToken( @Body request: TokenRequestModel):
            Simple<TokenConfigResponse>
}