package com.isw.iswkozen.core.network.IsoCommunicator

/**
 * This interface provides operations that target the ISO-8385 message specification,
 * it provides functionality operations that require the ISO layer's method for communication.
 */
internal interface IsoService {

    /**
     * Uses the provided terminalId to perform key exchange with the EPMS server
     *
     * @param terminalId  a string representing the configured terminal id
     * @param ip the ip address to download terminal parameters from
     * @param port the port number for the ip address
     * @return     boolean expression indicating the success or failure status of the key exchange
     */
    fun downloadKey(terminalId: String, ip: String, port: Int): Boolean {
        return false
    }

    /**
     * Uses the provided terminalId to download the terminal information, like name and location.
     *
     * @param terminalId  a string representing the configured terminal id
     * @param ip the ip address to download terminal parameters from
     * @param port the port number for the ip address
     * @return  boolean expression indicating the success or failure of the terminal info download
     * @see TerminalInfo
     */
    fun downloadTerminalParameters(terminalId: String, ip: String, port: Int): Boolean {
        return false
    }
}
