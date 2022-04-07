package com.isw.iswkozen.core.network.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "tokenPassportResponse")
data class TokenConfigResponse (
    @field:Element(name = "responseCode", required = false)
    @param:Element(name = "responseCode", required = false)
    var responseCode: String? = "",

    @field:Element(name = "responseMessage", required = false)
    @param:Element(name = "responseMessage", required = false)
    var responseMessage: String? = "",

    @field:Element(name = "token")
    @param:Element(name = "token")
    var token: String? = ""
)