package com.isw.iswkozen.core.network.models

import android.os.Parcelable
import com.isw.iswkozen.core.network.models.TerminalInformationRequest
import kotlinx.android.parcel.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Parcelize
@Root(name = "tokenPassportRequest", strict = false)
data class TokenRequestModel (
    @field:Element(name = "terminalInformation", required = false)
    var terminalInformation: TerminalInformationRequest? = null
): Parcelable