package com.isw.iswkozen.core.network.models

import org.simpleframework.xml.Root

@Root(name = "purchaseRequest", strict = false)
class PurchaseRequest: TransactionRequest()
class TransferRequest: TransactionRequest()