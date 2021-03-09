package com.realtimecoding.linkedinmanager.helper

import com.google.gson.Gson
import com.realtimecoding.linkedinmanager.util.CommonInfo
import com.realtimecoding.linkedinmanager.util.ExceptionManager
import com.realtimecoding.linkedinmanager.models.LinkedInTokenValidationSuccessBean
import com.realtimecoding.linkedinmanager.events.LinkedInAccessTokenResponse
import com.realtimecoding.linkedinmanager.events.LinkedInAccessTokenValidationResponse
import com.realtimecoding.linkedinmanager.events.LinkedInEmailAddressResponse
import com.realtimecoding.linkedinmanager.events.LinkedInProfileDataResponse
import com.realtimecoding.linkedinmanager.util.AppExecutors
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

class LinkedInNetworkTask {
    private var callingURLString: String
    private var urlParameters: String? = null
    private var mode: Int
    private var linkedInEmailAddressResponse: LinkedInEmailAddressResponse? = null
    private var linkedInAccessTokenResponse: LinkedInAccessTokenResponse? = null
    private var linkedInProfileDataResponse: LinkedInProfileDataResponse? = null
    private var linkedInAccessTokenValidationResponse: LinkedInAccessTokenValidationResponse? = null
    private val gson = Gson()
    private val executors = AppExecutors()

    constructor(callingURLString: String, mode: Int, linkedInEmailAddressResponse: LinkedInEmailAddressResponse?) {
        this.callingURLString = callingURLString
        this.mode = mode
        this.linkedInEmailAddressResponse = linkedInEmailAddressResponse
    }

    constructor(callingURLString: String, mode: Int, linkedInAccessTokenResponse: LinkedInAccessTokenResponse?) {
        this.callingURLString = callingURLString
        this.mode = mode
        this.linkedInAccessTokenResponse = linkedInAccessTokenResponse
    }

    constructor(callingURLString: String, mode: Int, linkedInProfileDataResponse: LinkedInProfileDataResponse?) {
        this.callingURLString = callingURLString
        this.mode = mode
        this.linkedInProfileDataResponse = linkedInProfileDataResponse
    }

    constructor(callingURLString: String, urlParameters: String?, mode: Int, linkedInAccessTokenValidationResponse: LinkedInAccessTokenValidationResponse?) {
        this.callingURLString = callingURLString
        this.mode = mode
        this.linkedInAccessTokenValidationResponse = linkedInAccessTokenValidationResponse
        this.urlParameters = urlParameters
    }

    fun doInBackground() {
        executors.networkIO().execute {
            var bufferedReader: BufferedReader? = null
            var result = if (mode == CommonInfo.MODE_ACCESS_TOKEN_VALIDATION) {
                try {
                    val postData = urlParameters!!.toByteArray(StandardCharsets.UTF_8)
                    val postDataLength = postData.size
                    val url = URL(callingURLString)
                    val conn = url.openConnection() as HttpsURLConnection
                    conn.doOutput = true
                    conn.instanceFollowRedirects = false
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    conn.setRequestProperty("charset", "utf-8")
                    conn.setRequestProperty("Content-Length", Integer.toString(postDataLength))
                    conn.useCaches = false
                    DataOutputStream(conn.outputStream).use { wr -> wr.write(postData) }
                    bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                    val stringBuffer = StringBuilder()
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuffer.append(line)
                    }
                    stringBuffer.toString()
                } catch (e: Exception) {
                    ExceptionManager.exceptionLog(e)
                    ""
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close()
                        } catch (e: IOException) {
                            ExceptionManager.exceptionLog(e)
                        }
                    }
                }
            } else {
                val urlConn: URLConnection
                try {
                    val url = URL(callingURLString)
                    urlConn = url.openConnection()
                    bufferedReader = BufferedReader(InputStreamReader(urlConn.getInputStream()))
                    val stringBuffer = StringBuilder()
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuffer.append(line)
                    }
                    stringBuffer.toString()
                } catch (e: Exception) {
                    ExceptionManager.exceptionLog(e)
                    ""
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close()
                        } catch (e: IOException) {
                            ExceptionManager.exceptionLog(e)
                        }
                    }
                }
            }

            onPostExecute(result)
        }
    }

    private fun onPostExecute(response: String?) {
        executors.mainThread().execute {
            if (response != null) {
                when (mode) {
                    CommonInfo.MODE_EMAIL_ADDRESS_REQUEST -> try {
                        linkedInEmailAddressResponse!!.onSuccessResponse(JSONObject(response))
                    } catch (e: Exception) {
                        ExceptionManager.exceptionLog(e)
                        linkedInEmailAddressResponse!!.onFailedResponse()
                    }
                    CommonInfo.MODE_PROFILE_DATA_REQUEST -> try {
                        linkedInProfileDataResponse!!.onRequestSuccess(JSONObject(response))
                    } catch (e: Exception) {
                        ExceptionManager.exceptionLog(e)
                        linkedInProfileDataResponse!!.onRequestFailed()
                    }
                    CommonInfo.MODE_ACCESS_TOKEN_REQUEST -> try {
                        linkedInAccessTokenResponse!!.onAuthenticationSuccess(JSONObject(response))
                    } catch (e: Exception) {
                        ExceptionManager.exceptionLog(e)
                        linkedInAccessTokenResponse!!.onAuthenticationFailed()
                    }
                    CommonInfo.MODE_ACCESS_TOKEN_VALIDATION -> try {
                        val linkedInTokenValidationSuccessBean: LinkedInTokenValidationSuccessBean = gson.fromJson(response, LinkedInTokenValidationSuccessBean::class.java)
                        if (linkedInTokenValidationSuccessBean.active && linkedInTokenValidationSuccessBean.status.equals("active")) {
                            linkedInAccessTokenValidationResponse!!.onValidationSuccess()
                        } else {
                            linkedInAccessTokenValidationResponse!!.onValidationFailed()
                        }
                    } catch (e: Exception) {
                        ExceptionManager.exceptionLog(e)
                        linkedInAccessTokenValidationResponse!!.onValidationFailed()
                    }
                }
            } else {
                when (mode) {
                    CommonInfo.MODE_EMAIL_ADDRESS_REQUEST -> linkedInEmailAddressResponse!!.onFailedResponse()
                    CommonInfo.MODE_PROFILE_DATA_REQUEST -> linkedInProfileDataResponse!!.onRequestFailed()
                    CommonInfo.MODE_ACCESS_TOKEN_REQUEST -> linkedInAccessTokenResponse!!.onAuthenticationFailed()
                    CommonInfo.MODE_ACCESS_TOKEN_VALIDATION -> linkedInAccessTokenValidationResponse!!.onValidationFailed()
                }
            }
        }
    }
}